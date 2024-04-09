import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.User;
import service.IService;
import service.ServerException;

import java.io.IOException;

public class LoginController {
    IService service;

    private Controller controller;
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    public void setService(IService myService){
        service = myService;
    }

    @FXML
    public void loginButtonClicked(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        User user = new User(username, password);
        if(username.length() < 2 || username.length() > 15) {
            MessageAlert.showErrorMessage(null, "Username-ul trebuie sa aiba intre 2 " + "si 15 caractere.");
        }
        if(password.length() < 2 || password.length() > 15) {
            MessageAlert.showErrorMessage(null, "Parola trebuie sa aiba intre 2 " + "si 15 caractere.");
            return;
        }
        try{
            System.out.println("s a dus in controller");
            System.out.println("a iesit din controller");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/main.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            Stage dialogStage1 = new Stage();

            Controller controller = loader.getController();
            controller.setService(service, dialogStage1, user);

            service.login(user, controller);
            System.out.println(controller);
            dialogStage1.setTitle("Window for " + user.getUsername());
            dialogStage1.initModality(Modality.WINDOW_MODAL);


            Scene scene = new Scene(root);
            dialogStage1.setScene(scene);


            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();

            dialogStage1.show();
        }catch (ServerException e) {
            MessageAlert.showErrorMessage(null,  e.getMessage());
        }
        catch (IOException e) {
            e.getMessage();
        }
    }

    }

