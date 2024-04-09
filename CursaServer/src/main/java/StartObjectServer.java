import repository.RepoCursa;
import repository.RepoRezervare;
import repository.RepoUser;
import server.Service;
import service.IService;
import utils.AbstractServer;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.Properties;

public class StartObjectServer {

    private static int defaultPort=55555;

    public static void main(String[] args){
        Properties serverProps = new Properties();
        try{
            serverProps.load(StartObjectServer.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set.  ");
            serverProps.list(System.out);
        } catch (IOException e){
            System.err.println("Cannot find server.properties " + e);
            return;
        }
        RepoUser repoUser = new RepoUser(serverProps);
        RepoCursa repoCursa = new RepoCursa(serverProps);
        RepoRezervare repoRezervare = new RepoRezervare(serverProps);
        IService service = new Service(repoCursa, repoRezervare, repoUser);
        int serverPort = defaultPort;
        try{
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong Port Number " +nef.getMessage());
            System.err.println("Using default port " + defaultPort);
        }
        System.out.println("Starting server on port: " + serverPort);
        AbstractServer server = new RpcConcurrentServer(serverPort, service);
        try{
            server.start();
        } catch (ServerException e){
            System.err.println("Error starting the server " + e.getMessage());
        }

    }

}
