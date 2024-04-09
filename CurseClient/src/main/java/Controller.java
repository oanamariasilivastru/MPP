import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.Cursa;
import model.Rezervare;
import model.User;
import service.IObserver;
import service.IService;
import service.ServerException;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable, IObserver {
    IService service;
    Stage stage;
    User user;
    ObservableList<Pair<String, String>> model = FXCollections.observableArrayList();

    @FXML
    private TextField destinationField;

    @FXML
    private DatePicker departureDate;

    @FXML
    private TextField departureTime;

    @FXML
    private TableView<Pair<String, String>> resultTable;

    @FXML
    private TableColumn<String, String> numarLoc;

    @FXML
    private TableColumn<String, String> numeClient;

    @FXML
    private TableView<Cursa> cursaTable;

    @FXML
    private TableColumn<String, String> destinatie;

    @FXML
    private TableColumn<Cursa, String> data;

    @FXML
    private TableColumn<Cursa, String> locuriLibere;
    @FXML
    private TextField nameField;
    @FXML
    private TextField numarLocuri;

    private Iterable<Cursa> cursaIterable;

    @FXML
    ObservableList<Cursa> cursa = FXCollections.observableArrayList();
    List<String> clienti = new ArrayList();
    public void setService(IService myService, Stage stage, User user){
        this.service = myService;
        this.stage = stage;
        this.user = user;
        initialize();
    }

    public void initialize() {
            numarLoc.setCellValueFactory(new PropertyValueFactory<>("key"));
            numeClient.setCellValueFactory(new PropertyValueFactory<>("value"));
            destinatie.setCellValueFactory(new PropertyValueFactory<>("destinatie"));
            data.setCellValueFactory(new PropertyValueFactory<>("data"));
            initModel();
    }

    public void setCursa(){
        try {
            cursaIterable = service.getAllCurse();
        }catch (ServerException ex){
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void search() {
            String destination = destinationField.getText();
            LocalDate date = departureDate.getValue();
            String timeStr = departureTime.getText();

            if (destination.isEmpty() || destination.trim().equals("")) {
                MessageAlert.showErrorMessage(null, "Te rog sa introduci o destinatie valida.");
                return;
            }

            if (date == null || date.isBefore(LocalDate.now())) {
                MessageAlert.showErrorMessage(null, "Te rog sa selectezi o data valida in viitor.");
                return;
            }

            try {
                LocalTime time = LocalTime.parse(timeStr);
            } catch (DateTimeParseException e) {
                MessageAlert.showErrorMessage(null, "Te rog sa introduci o ora valida (HH:mm).");
                return;
            }
            try {
                LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.parse(timeStr));
                Cursa cursa = service.getCursaByDestAndTime(destination, dateTime);
                if (cursa == null) {
                    MessageAlert.showErrorMessage(null, "Nu exista curse disponibile pentru destinatia si ora selectata.");
                    return;
                }

                clienti = service.getNumeClientiCuRezervariMultiple(cursa.getId());
                System.out.println(clienti);
                System.out.println(clienti.size());
                seeSeats();
            }catch(ServerException ex){
                ex.getMessage();
            }



    }

    @Override
    public void rezervareReceived(Rezervare rezervare) throws ServerException {
       initModel();
    }

    private void seeSeats(){
        System.out.println("In see seats");
        List<Pair<String, String>> perechi = new ArrayList<>();
        int numarLocuri = 18;
        for (int i = 1; i <= numarLocuri; i++) {
            String numeClient;
            if (i <= clienti.size()) {
                numeClient = clienti.get(i - 1);
            } else {
                numeClient = "-";
            }
            perechi.add(new Pair<>(String.valueOf(i), numeClient));
        }
        for (Pair<String, String> pair : perechi) {
            System.out.println(pair.getKey() + " : " + pair.getValue());
        }

        Platform.runLater(() -> {
            model.setAll(perechi);
            resultTable.setItems(model);
        });

        System.out.println("Iesim din see seats");

    }

    private void initModel() {
        Platform.runLater(() -> {
            setCursa();
            System.out.println("HEI SUNTEM IN INITMODEL NICIUN RASPUNS");
            cursaTable.getItems().clear();
            try {

                System.out.println("In controller");
                System.out.println(cursaIterable);

                List<Cursa> cursaList = new ArrayList<>();
                cursaIterable.forEach(cursaList::add);
                cursa.addAll(cursaList);
                cursaTable.setItems(cursa);
                List<Integer> listaValori = service.getLocuriLibere();

                locuriLibere.setCellValueFactory(cellData -> {

                        System.out.println("Aici in controller");
                        int index = cellData.getTableView().getItems().indexOf(cellData.getValue());
                        if (index >= 0 && index < listaValori.size()) {
                            Integer valoare = listaValori.get(index);
                            return new SimpleStringProperty(String.valueOf(valoare));
                        } else {
                            return new SimpleStringProperty("");
                        }
                });

            } catch (ServerException ex) {
                ex.getMessage();
            }
        });

    }


    @FXML
    private void add(){
        Cursa selectedCursa = cursaTable.getSelectionModel().getSelectedItem();
        if (selectedCursa == null) {
            MessageAlert.showErrorMessage(null, "Te rog sa selectezi o cursa din tabel inainte de a face o rezervare.");
            return;
        }
        String numeClient = nameField.getText();
        String numarLocuriText = numarLocuri.getText();

        if (numeClient.isEmpty() || numeClient.trim().equals("")) {
            MessageAlert.showErrorMessage(null, "Te rog sa introduci un nume de client valid.");
            return;
        }


        int nrLocuri;
        try {
            nrLocuri = Integer.parseInt(numarLocuriText);
            if (nrLocuri <= 0) {
                MessageAlert.showErrorMessage(null, "Te rog sa introduci un numar valid de locuri (mai mare decat zero).");
                return;
            }
        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "Te rog sa introduci un numar valid de locuri (intreg pozitiv).");
            return;
        }
        try {
            Rezervare rezervare = new Rezervare(selectedCursa, numeClient, nrLocuri);
            service.save(rezervare);
            initModel();
        }catch (ServerException ex){
            ex.getMessage();
        }
    }

    @FXML
    private void loggoutButton() throws IOException {
        try {
            this.service.logout(this.user, this);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            VBox loginPane = loader.load();

            LoginController loginController = loader.getController();
            loginController.setService(service);

            Scene loginScene = new Scene(loginPane, 300, 150);
            stage.setScene(loginScene);
            stage.show();
        } catch (ServerException ex) {
            ex.getMessage();
        }
    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {

        System.out.println("INIT");

        System.out.println("END INIT!!!!!!!!!");
    }

}
