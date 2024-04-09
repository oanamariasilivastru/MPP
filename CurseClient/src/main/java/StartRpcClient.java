import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.IService;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.Properties;

public class StartRpcClient extends Application {
    private Stage primaryStage;
    private static int defaultChatPort = 55555;

    private String defaultServer = "localhost";

    public void start(Stage primaryStage) throws Exception {
        System.out.println("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartRpcClient.class.getResourceAsStream("client.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find client.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IService server = new ServicesRpcProxy(serverIP, serverPort);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/login.fxml"));
        VBox pane = loader.load();

        LoginController ctrl =
                loader.<LoginController>getController();
        ctrl.setService(server);
        primaryStage.setTitle("Curse MPP");
        primaryStage.setScene(new Scene(pane, 300, 400));
        primaryStage.show();



    }
}
