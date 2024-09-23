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
import java.util.ArrayList;
import java.util.List;

public class ClientRpcProto implements Runnable, IObserver {
    private IService service;
    private Socket connection;

    private InputStream input;

    private OutputStream output;

    private volatile boolean connected;

    private static Response okResponse;

    public ClientRpcProto(IService service, Socket connection){
        this.service = service;
        this.connection = connection;
        try{
            output = connection.getOutputStream();
            output.flush();
            input = connection.getInputStream();
            connected = true;
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void rezervareReceived(Rezervare rezervare) throws ServerException {
        Protobufs.Response response = ProtoUtils.rezervareReceived(rezervare);
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
                //Object request=input.readObject();
                Protobufs.Request request = Protobufs.Request.parseDelimitedFrom(input);
                Protobufs.Response response=handleRequest(request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException e) {
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

    private void sendResponse(Protobufs.Response response) throws IOException{
        System.out.println("sending response "+response);
        synchronized (output) {
            //output.writeObject(response);
            response.writeDelimitedTo(output);
            output.flush();
        }
    }

    private  synchronized Protobufs.Response handleRequest(Protobufs.Request request) {
        System.out.println("Suntem in handleRequest la Client");
        Protobufs.Response response = null;
        User user = ProtoUtils.getUser(request.getUser());
        if (request.getType() == Protobufs.Request.RequestType.LOGIN) {
            System.out.println("Login request ..." + request.getType());


            try {
                this.service.login(user, this);
                return ProtoUtils.createOkResponse().build();
            } catch (ServerException var7) {
                this.connected = false;
                return ProtoUtils.createErrorResponse(var7.getMessage());
            }
        }
            if (request.getType() == Protobufs.Request.RequestType.GET_LOCURI_LIBERE) {
                System.out.println("Get Curse request ...." + request.getType());
                try {
                    List<Integer> nrLocuri = this.service.getLocuriLibere();
                    System.out.println("Aici in client nr locuri");
                    Protobufs.Response.Builder responseBuilder = ProtoUtils.createOkResponse();
                    responseBuilder.addAllNrLocuri(nrLocuri);
                    return responseBuilder.build();
                } catch (ServerException e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }

            if (request.getType() == Protobufs.Request.RequestType.GET_ALL_CURSE){
            System.out.println("Get all curse request ...." + request.getType());
            try{
                Iterable<Cursa> curse = this.service.getAllCurse();
                List<Cursa> curseList = new ArrayList<>();
                List<Protobufs.Cursa> protocursaList = new ArrayList<>();
                for (Cursa cursa : curse) {
                    System.out.println(cursa);
                    Protobufs.Cursa cursaProto = ProtoUtils.getProtoCursa(cursa);
                    protocursaList.add(cursaProto);
                }
                System.out.println(curseList.size());

                Protobufs.Response.Builder responseBuilder = ProtoUtils.createOkResponse();
                responseBuilder.addAllListaCurse(protocursaList);
                return responseBuilder.build();
            } catch (ServerException e){
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        if(request.getType() == Protobufs.Request.RequestType.GET_CURSA){
            System.out.println("Get cursa request ...." + request.getType());
            try{
                Cursa cursa = ProtoUtils.getCursa(request.getCursa());
                Cursa cursaFound = this.service.getCursaByDestAndTime(cursa.getDestinatie(), cursa.getData());
                Protobufs.Cursa cursafoundProto = ProtoUtils.getProtoCursa(cursaFound);
                return  ProtoUtils.createOkResponse().setCursa(cursafoundProto).build();
            }catch (ServerException e){
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        if(request.getType() == Protobufs.Request.RequestType.GET_LOCURI_MULTIPLE){
            System.out.println("Suntem la client");
            System.out.println("Get locuri request ...." + request.getType());
            try{
                Long id = request.getId();
                List<String> clients = service.getNumeClientiCuRezervariMultiple(id);
                System.out.println("Suntem la serverul ala");
                Protobufs.Response.Builder responseBuilder = ProtoUtils.createOkResponse();
                responseBuilder.addAllClientNames(clients);
//                for (int i = 0; i< clients.size(); i++) {
//                    responseBuilder.setClientNames(i, clients.get(i));
//                }
                return responseBuilder.build();
            }catch (ServerException e){
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        if(request.getType() == Protobufs.Request.RequestType.ADD){
            System.out.println("Get add request ...." + request.getType());
            try{
                Rezervare rezervare = ProtoUtils.getRezervare(request.getRezervare());
                service.save(rezervare);
                return ProtoUtils.createOkResponse().build();
            }catch (ServerException e){
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        if(request.getType() == Protobufs.Request.RequestType.LOGOUT){
            System.out.println("Get logout request");
            Long id = request.getId();

            try{
                service.logout(user, this);
                connected = false;
                return ProtoUtils.createOkResponse().build();
            }catch (ServerException e) {
                return ProtoUtils.createErrorResponse(e.getMessage());
            }
        }
        else {
            return response;
        }
    }


    static {
        okResponse = (new Response.Builder()).type(ResponseType.OK).build();
    }

}
