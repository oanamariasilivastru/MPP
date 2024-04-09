import model.Cursa;
import model.Rezervare;
import model.User;
import service.IObserver;
import service.IService;
import service.ServerException;
import utils.Request;
import utils.RequestType;
import utils.Response;
import utils.ResponseType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientRpcWorker implements Runnable, IObserver {
    private IService service;
    private Socket connection;

    private ObjectInputStream input;

    private ObjectOutputStream output;

    private volatile boolean connected;

    private static Response okResponse;

    public ClientRpcWorker(IService service, Socket connection){
        this.service = service;
        this.connection = connection;
        try{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void rezervareReceived(Rezervare rezervare) throws ServerException{
        Response response = new Response.Builder().type(ResponseType.NEW_MESSAGE).data(rezervare).build();
        System.out.println("Rezervare received " + rezervare);
        try{
            sendResponse(response);
        }catch (IOException e){
            throw new ServerException("Sending error: " +  e.getMessage());
        }
    }

    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response "+response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    private  synchronized Response handleRequest(Request request) {
        System.out.println("Suntem in handleRequest la Client");
        Response response = null;
        User user;
        if (request.type() == RequestType.LOGIN) {
            System.out.println("Login request ..." + request.type());
            user = (User) request.data();

            try {
                this.service.login(user, this);
                return okResponse;
            } catch (ServerException var7) {
                this.connected = false;
                return (new Response.Builder()).type(ResponseType.ERROR).data(var7.getMessage()).build();
            }
        }
        if (request.type() == RequestType.GET_LOCURI_LIBERE){
            System.out.println("Get Curse request ...." + request.type());
            try{
                List<Integer> nrLocuri = this.service.getLocuriLibere();
                System.out.println("Aici in client nr locuri");
                return new Response.Builder().type(ResponseType.OK).data(nrLocuri).build();
            } catch (ServerException e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }

        }
        if (request.type() == RequestType.GET_ALL_CURSE){
            System.out.println("Get all curse request ...." + request.type());
            try{
                Iterable<Cursa> curse = this.service.getAllCurse();
                return new Response.Builder().type(ResponseType.OK).data(curse).build();
            } catch (ServerException e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if(request.type() == RequestType.GET_CURSA){
            System.out.println("Get cursa request ...." + request.type());
            try{
                Cursa cursa = (Cursa) request.data();
                Cursa cursaFound = this.service.getCursaByDestAndTime(cursa.getDestinatie(), cursa.getData());
                return new Response.Builder().type(ResponseType.OK).data(cursaFound).build();
            }catch (ServerException e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if(request.type() == RequestType.GET_LOCURI_MULTIPLE){
            System.out.println("Suntem la client");
            System.out.println("Get locuri request ...." + request.type());
            try{
                Long id = (Long) request.data();
                List<String> clients = service.getNumeClientiCuRezervariMultiple(id);
                System.out.println("Suntem la serverul ala");
                return new Response.Builder().type(ResponseType.OK).data(clients).build();
            }catch (ServerException e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if(request.type() == RequestType.ADD){
            System.out.println("Get add request ...." + request.type());
            try{
                Rezervare rezervare = (Rezervare) request.data();
                service.save(rezervare);
                return new Response.Builder().type(ResponseType.OK).build();
            }catch (ServerException e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if(request.type() == RequestType.LOGOUT){
            System.out.println("Get logout request");
            user = (User) request.data();
            try{
                service.logout(user, this);
                connected = false;
                return okResponse;
            }catch (ServerException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        else {
            return (Response) response;
        }
    }


    static {
        okResponse = (new Response.Builder()).type(ResponseType.OK).build();
    }

}
