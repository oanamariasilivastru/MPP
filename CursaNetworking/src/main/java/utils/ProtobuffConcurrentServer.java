package utils;

import protobufprotocol.ClientRpcProto;
import protobufprotocol.ProtoChatProxy;
import service.IService;

import java.net.Socket;

public class ProtobuffConcurrentServer extends AbsConcurrentServer {
    private IService chatServer;
    public ProtobuffConcurrentServer(int port, IService chatServer) {
        super(port);
        this.chatServer = chatServer;
        System.out.println("Chat- ChatProtobuffConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientRpcProto worker=new ClientRpcProto(chatServer,client);
        Thread tw=new Thread(worker);
        return tw;
    }
}
