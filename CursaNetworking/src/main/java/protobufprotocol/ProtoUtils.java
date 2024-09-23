package protobufprotocol;

import com.google.protobuf.Timestamp;
import model.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ProtoUtils {

    public static Protobufs.User getProtoUser(User user){
        return Protobufs.User.newBuilder()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build();
    }

    public static Protobufs.Rezervare getProtoRezervare(Rezervare rezervare) {
        return Protobufs.Rezervare.newBuilder()
                .setCursa(getProtoCursa(rezervare.getCursa()))
                .setNumeClient(rezervare.getNumeClient())
                .setNrLocuri(rezervare.getNrLocuri())
                .build();
    }

    public static Protobufs.Cursa getProtoCursa(Cursa cursa) {
        System.out.println("In protobuf cursa proto" + cursa);

        Protobufs.Cursa.Builder builder = Protobufs.Cursa.newBuilder()
                .setDestinatie(cursa.getDestinatie())
                .setData(getProtoTimestamp(cursa.getData()));

        if (cursa.getId() != null) {
            builder.setId(cursa.getId());
        }

        return builder.build();
    }

    public static Timestamp getProtoTimestamp(LocalDateTime dateTime) {
        long seconds = dateTime.toEpochSecond(ZoneOffset.UTC);
        int nanos = dateTime.getNano();
        return Timestamp.newBuilder().setSeconds(seconds).setNanos(nanos).build();
    }

    public static String getError(Protobufs.Response response){
        return response.getError();
    }

    public static User getUser(Protobufs.User protoUser){
        User user = new User(protoUser.getUsername(), protoUser.getPassword());
        user.setId(protoUser.getId());
        return user;
    }

    public static Cursa getCursa(Protobufs.Cursa protoCursa) {
        LocalDateTime data = LocalDateTime.ofEpochSecond(protoCursa.getData().getSeconds(), protoCursa.getData().getNanos(), ZoneOffset.UTC);
        Cursa cursa = new Cursa(protoCursa.getDestinatie(), data);

        if (protoCursa.getId() != 0) {
            cursa.setId(protoCursa.getId());
        }

        return cursa;
    }


    public static Rezervare getRezervare(Protobufs.Rezervare protoRezervare) {
        Cursa cursa = getCursa(protoRezervare.getCursa());
        Rezervare rezervare = new Rezervare(cursa, protoRezervare.getNumeClient(), protoRezervare.getNrLocuri());
        rezervare.setId(protoRezervare.getId());
        return rezervare;
    }

    public static Collection<String> getNumeClienti(Collection<Protobufs.Rezervare> rezervari){
        List<String> clientNames = new ArrayList<>();
        for(Protobufs.Rezervare rezervare : rezervari){
            clientNames.add(rezervare.getNumeClient());
        }

        return clientNames;
    }

    public static List<String> getclientNames(Protobufs.Response response){
        List<String> clientNames = new ArrayList<>();
        for(int i = 0; i < response.getClientNamesCount(); i++){
            String name = response.getClientNames(i);
            clientNames.add(name);
        }
        return clientNames;
    }



    public static Protobufs.Response setUser(Protobufs.Response.Builder response, User user){
        return response.setUser(getProtoUser(user)).build();
    }

    public static Protobufs.Response setCursa(Protobufs.Response.Builder response, Cursa cursa){
        return response.setCursa(getProtoCursa(cursa)).build();
    }

    public static Protobufs.Response setRezervare(Protobufs.Response.Builder response, Rezervare rezervare){
        return response.setRezervare(getProtoRezervare(rezervare)).build();
    }

    //  REQUEST

    public static Protobufs.Request createLoginRequest(User user){
        System.out.println("createLoginRequest");
        Protobufs.User protoUser = getProtoUser(user);

        return Protobufs.Request.newBuilder()
                .setType(Protobufs.Request.RequestType.LOGIN)
                .setUser(protoUser)
                .build();
    }

    public static Protobufs.Request createGetLocuriLibereRequest() {
        return Protobufs.Request.newBuilder()
                .setType(Protobufs.Request.RequestType.GET_LOCURI_LIBERE)
                .build();
    }

    public static Protobufs.Request createLogoutRequest(User user) {
        Protobufs.Request.Builder requestBuilder = Protobufs.Request.newBuilder()
                .setType(Protobufs.Request.RequestType.LOGOUT);

        requestBuilder.setUser(ProtoUtils.getProtoUser(user));
        return requestBuilder.build();
    }


    public  synchronized static Protobufs.Request createCursaByDestDate(Cursa cursa){
        System.out.println("create getCursaByDestDate");
        Protobufs.Cursa protoCursa = getProtoCursa(cursa);
        System.out.println("Cursa domain din getProtoCursa" + cursa);
        System.out.println("Cursa domain din getProtoCursa" + cursa);
        return Protobufs.Request.newBuilder()
                //.setId(cursa.getId())
                .setType(Protobufs.Request.RequestType.GET_CURSA)
                .setCursa(protoCursa)
                .build();

    }

    public synchronized static Protobufs.Response rezervareReceived(Rezervare rezervare){
        Protobufs.Rezervare protoRezervare = getProtoRezervare(rezervare);
        Protobufs.Response response = Protobufs.Response.newBuilder()
                .setType(Protobufs.Response.ResponseType.NEW_MESSAGE)
                .setRezervare(protoRezervare)
                .build();
        return response;

    }

    public static Protobufs.Request createGetNumeClientiCuRezervariMultipleRequest(Long id) {
        return Protobufs.Request.newBuilder()
                .setType(Protobufs.Request.RequestType.GET_LOCURI_MULTIPLE)
                .setId(id)
                .build();
    }

    public static Protobufs.Request createSaveRequest(Rezervare rezervare) {
        return Protobufs.Request.newBuilder()
                .setType(Protobufs.Request.RequestType.ADD)
                .setRezervare(getProtoRezervare(rezervare))
                .build();
    }


    public static Protobufs.Request createGetAllCurseRequest() {
        return Protobufs.Request.newBuilder()
                .setType(Protobufs.Request.RequestType.GET_ALL_CURSE)
                .build();
    }
    //  RESPONSES

    public static List<Cursa> getAllCurse(Protobufs.Response response) {
        List<Cursa> curse = new ArrayList<>();
        for (int i = 0; i < response.getListaCurseCount(); i++) {
            Protobufs.Cursa protoCursa = response.getListaCurse(i);
            Cursa cursa = ProtoUtils.getCursa(protoCursa);
            curse.add(cursa);
        }
        return curse;
    }

    public static List<Integer> getLocuriLibere(Protobufs.Response response){
        List<Integer> locuriLibere = new ArrayList<>();
        for(int i = 0; i<response.getNrLocuriCount(); i++){
            Integer locuri = response.getNrLocuri(i);
            locuriLibere.add(locuri);
        }
        return locuriLibere;
    }
    public static Protobufs.Response.Builder createOkResponse(){
        return Protobufs.Response.newBuilder()
                .setType(Protobufs.Response.ResponseType.OK);
    }

    public static  Protobufs.Response createErrorResponse(String error){
        return Protobufs.Response.newBuilder()
                .setType(Protobufs.Response.ResponseType.ERROR)
                .setError(error)
                .build();
    }






}
