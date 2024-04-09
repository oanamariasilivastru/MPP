import model.Cursa;
import model.Rezervare;
import model.User;
import service.IObserver;
import service.IService;
import service.ServerException;
import utils.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Provider;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ServicesRpcProxy implements IService {
    private String host;
    private int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingDeque<Response> qresponses;
    private volatile boolean finished;

    public ServicesRpcProxy(String host, int port){
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<Response>();
    }

    @Override
    public synchronized void login(User user, IObserver client) throws service.ServerException{
        initializeConnection();
        Request req = (new Request.Builder()).type(RequestType.LOGIN).data(user).build();
        this.sendRequest(req);
        Response response = this.readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = client;
        }
        else if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            this.closeConnection();
            throw new ServerException(err);
        }
    }
    public synchronized Cursa getCursaByDestAndTime(String destination, LocalDateTime date) throws ServerException{
        Cursa cursa = new Cursa(destination, date);
        Request req = new Request.Builder().type(RequestType.GET_CURSA).data(cursa).build();
        this.sendRequest(req);
        Response response = this.readResponse();
        if (response.type() == ResponseType.OK) {
            return (Cursa) response.data();
        } else if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            System.out.println(err);
            throw new ServerException(err);
        }
        return null;
    }


    public synchronized List<String> getNumeClientiCuRezervariMultiple(Long id) throws ServerException {
        Request req = new Request.Builder().type(RequestType.GET_LOCURI_MULTIPLE).data(id).build();
        this.sendRequest(req);
        Response response = this.readResponse();
        if (response.type() == ResponseType.OK) {
            return (List<String>) response.data();
        } else if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServerException(err);
        }
        return null;
    }

    public synchronized List<Integer> getLocuriLibere() throws ServerException {
        Request req = new Request.Builder().type(RequestType.GET_LOCURI_LIBERE).build();
        this.sendRequest(req);
        System.out.println("Aici suntem in rcpProxy!!!!!");
        Response response = this.readResponse();
        if (response.type() == ResponseType.OK) {
            return (List<Integer>) response.data();
        } else if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServerException(err);
        }
        return null;
    }

    public synchronized void save(Rezervare rezervare) throws ServerException{
        Request req = new Request.Builder().type(RequestType.ADD).data(rezervare).build();
        this.sendRequest(req);
        Response response = this.readResponse();
        if (response.type() == ResponseType.OK) {
            System.out.println(response);
        } else if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServerException(err);
        }
    }

    public User findUser(String username, String password){
        return null;
    }
    public synchronized List<Cursa> getAllCurse() throws ServerException{
        Request req = new Request.Builder().type(RequestType.GET_ALL_CURSE).build();
        this.sendRequest(req);
        Response response = this.readResponse();
        if (response.type() == ResponseType.OK) {
            return (List<Cursa>) response.data();
        } else if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServerException(err);
        }
        return null;
    }

    private void initializeConnection() throws ServerException {
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }

    private boolean isUpdate(Response response){
        return response.type()== ResponseType.FRIEND_LOGGED_OUT || response.type()== ResponseType.FRIEND_LOGGED_IN || response.type()== ResponseType.NEW_MESSAGE;
    }

    public void logout(User user, IObserver client)throws ServerException{
        Request req=new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(req);
        Response response=readResponse();
        closeConnection();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new ServerException(err);
        }
    }
    private void handleUpdate(Response response){
        if(response.type() == ResponseType.NEW_MESSAGE){
            Rezervare rezervare = (Rezervare) response.data();
            try{
                client.rezervareReceived(rezervare);
            } catch (ServerException e){
                e.printStackTrace();
            }
        }
    }
    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    System.out.println("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{

                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                } catch (ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }

    private void sendRequest(Request request)throws ServerException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new ServerException("Error sending object "+e);
        }

    }

    private Response readResponse() throws ServerException {
        Response response=null;
        try{

            response=qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
