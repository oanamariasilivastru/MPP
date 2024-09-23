package protobufprotocol;

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

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ProtoChatProxy implements IService {
    private String host;
    private int port;

    private IObserver client;
    private InputStream input;
    //private ObjectInputStream input;
    private OutputStream output;
    //private ObjectOutputStream output;
    private Socket connection;

    private BlockingDeque<Protobufs.Response> qresponses;
    private volatile boolean finished;

    public ProtoChatProxy(String host, int port){
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<Protobufs.Response>();
    }

    @Override
    public synchronized void login(User user, IObserver client) throws service.ServerException{
        initializeConnection();
        Protobufs.Request request = ProtoUtils.createLoginRequest(user);
        this.sendRequest(request);
//        Request req = (new Request.Builder()).type(RequestType.LOGIN).data(user).build();
//        this.sendRequest(req);
//        Response response = this.readResponse();
        Protobufs.Response response = readResponse();
        if (response.getType() == Protobufs.Response.ResponseType.OK) {
            this.client = client;
        }
        else if (response.getType() == Protobufs.Response.ResponseType.ERROR) {
            String err = ProtoUtils.getError(response);
            this.closeConnection();
            throw new ServerException(err);
        }
    }
    public synchronized Cursa getCursaByDestAndTime(String destination, LocalDateTime date) throws ServerException{
        Cursa cursa = new Cursa(destination, date);
//        Request req = new Request.Builder().type(RequestType.GET_CURSA).data(cursa).build();
//        this.sendRequest(req);
//        Response response = this.readResponse();
        System.out.println("Aici in getCursaByDestAndTime");
        Protobufs.Request request = ProtoUtils.createCursaByDestDate(cursa);
        System.out.println("Se trimite request" + request);
        this.sendRequest(request);

        Protobufs.Response response = readResponse();
        if (response.getType() == Protobufs.Response.ResponseType.OK) {
            return ProtoUtils.getCursa(response.getCursa());
        } else if (response.getType() == Protobufs.Response.ResponseType.ERROR) {
            String err = ProtoUtils.getError(response);
            System.out.println(err);
            throw new ServerException(err);
        }
        return null;
    }


    public synchronized List<String> getNumeClientiCuRezervariMultiple(Long id) throws ServerException {
//        Request req = new Request.Builder().type(RequestType.GET_LOCURI_MULTIPLE).data(id).build();
//        this.sendRequest(req);
//        Response response = this.readResponse();
        Protobufs.Request request = ProtoUtils.createGetNumeClientiCuRezervariMultipleRequest(id);
        System.out.println("In getNumeClienticuRezervariMultiple Se trimite requestul: " + request);
        this.sendRequest(request);
        Protobufs.Response response = this.readResponse();
        if (response.getType() == Protobufs.Response.ResponseType.OK) {
            return ProtoUtils.getclientNames(response);
        } else if (response.getType() == Protobufs.Response.ResponseType.ERROR) {
            String err = ProtoUtils.getError(response);
            throw new ServerException(err);
        }
        return null;
    }

    public synchronized List<Integer> getLocuriLibere() throws ServerException {
//        Request req = new Request.Builder().type(RequestType.GET_LOCURI_LIBERE).build();
//        this.sendRequest(req);
//        System.out.println("Aici suntem in rcpProxy!!!!!");
//        Response response = this.readResponse();
        Protobufs.Request req = ProtoUtils.createGetLocuriLibereRequest();
        this.sendRequest(req);
        System.out.println("Aici suntem in rpcProxy!!!");
        Protobufs.Response response = this.readResponse();
        if (response.getType()== Protobufs.Response.ResponseType.OK) {
            return ProtoUtils.getLocuriLibere(response);
        } else if (response.getType() == Protobufs.Response.ResponseType.ERROR) {
            String err = ProtoUtils.getError(response);
            throw new ServerException(err);
        }
        return null;
    }

    public synchronized void save(Rezervare rezervare) throws ServerException{
//        Request req = new Request.Builder().type(RequestType.ADD).data(rezervare).build();
//        this.sendRequest(req);
//        Response response = this.readResponse();
        Protobufs.Request req = ProtoUtils.createSaveRequest(rezervare);

        this.sendRequest(req);
        System.out.println("Aici suntem in save!!");
        Protobufs.Response response = this.readResponse();
        if (response.getType() == Protobufs.Response.ResponseType.OK) {
            System.out.println(response);
        } else if (response.getType() == Protobufs.Response.ResponseType.ERROR) {
            String err = ProtoUtils.getError(response);
            throw new ServerException(err);
        }
    }

    public User findUser(String username, String password){
        return null;
    }
    public synchronized List<Cursa> getAllCurse() throws ServerException{
//        Request req = new Request.Builder().type(RequestType.GET_ALL_CURSE).build();
//        this.sendRequest(req);
//        Response response = this.readResponse();
        Protobufs.Request req = ProtoUtils.createGetAllCurseRequest();
        this.sendRequest(req);
        System.out.println("Aici suntem in getAllCurse");
        Protobufs.Response response = this.readResponse();
        if (response.getType() == Protobufs.Response.ResponseType.OK) {
            return ProtoUtils.getAllCurse(response);
        } else if (response.getType() == Protobufs.Response.ResponseType.ERROR) {
            String err = ProtoUtils.getError(response);
            throw new ServerException(err);
        }
        return null;
    }



    private void initializeConnection() throws ServerException {
        try {
            connection=new Socket(host,port);
            //output=new ObjectOutputStream(connection.getOutputStream());
            //output.flush();
            output = connection.getOutputStream();
            input=connection.getInputStream();
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

    private boolean isUpdate(Protobufs.Response response){
        return response.getType() == Protobufs.Response.ResponseType.FRIEND_LOGGED_OUT || response.getType()== Protobufs.Response.ResponseType.FRIEND_LOGGED_IN || response.getType()== Protobufs.Response.ResponseType.NEW_MESSAGE;
    }

    public void logout(User user, IObserver client)throws ServerException{
//        Request req=new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(ProtoUtils.createLogoutRequest(user));

        Protobufs.Response response=readResponse();
        closeConnection();
        if (response.getType()== Protobufs.Response.ResponseType.ERROR){
            String err= ProtoUtils.getError(response);
            throw new ServerException(err);
        }
    }
    private void handleUpdate(Protobufs.Response response){
        if(response.getType()== Protobufs.Response.ResponseType.NEW_MESSAGE){
            Rezervare rezervare = ProtoUtils.getRezervare(response.getRezervare());
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
                    //Object response=input.readObject();
                    Protobufs.Response response = Protobufs.Response.parseDelimitedFrom(input);
                    System.out.println("response received "+response);
                    if (isUpdate(response)){
                        handleUpdate(response);
                    }else{

                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                }
//                catch (ClassNotFoundException e) {
//                    System.out.println("Reading error "+e);
//                }
            }
        }
    }

    private void sendRequest(Protobufs.Request request)throws ServerException {
        try {
            //output.writeObject(request);
            request.writeDelimitedTo(output);
            output.flush();
        } catch (IOException e) {
            throw new ServerException("Error sending object "+e);
        }

    }

    private Protobufs.Response readResponse() throws ServerException {
        Protobufs.Response response=null;
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
