package server;

import model.Cursa;
import model.Rezervare;
import model.User;
import repository.CursaRepository;
import repository.RepoCursa;
import repository.RepoRezervare;
import repository.RepoUser;
import service.IObserver;
import service.IService;
import service.ServerException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements IService {
    private RepoCursa repoCursa;
    private RepoRezervare repoRezervare;
    private RepoUser repoUser;

    private Map<Long, IObserver> loggedClients;

    public Service(RepoCursa repoCursa, RepoRezervare repoRezervare, RepoUser repoUser) {
        this.repoCursa = repoCursa;
        this.repoRezervare = repoRezervare;
        this.repoUser = repoUser;
        loggedClients = new ConcurrentHashMap<>();
    }
    public synchronized void login(User user, IObserver client) throws ServerException{
        System.out.println("Suntem in login de la service");
        User userFound = repoUser.findByUsernamePassword(user.getUsername(), user.getPassword());
        if(userFound != null){
            if(loggedClients.get(userFound.getId())!=null)
                throw new ServerException("User already logged in. ");
            loggedClients.put(userFound.getId(), client);
        }else{
            throw new ServerException("Authentication failed. ");
        }
    }
    public synchronized Cursa getCursaByDestAndTime(String destination, LocalDateTime date) {
        Cursa cursa = repoCursa.findByDestData(destination, date);
        return cursa;
    }

    public synchronized List<String> getNumeClientiCuRezervariMultiple(Long id) {
        Iterable<Rezervare> rezervari = repoRezervare.findAllByCursaId(id);
        List<String> numeClienti = new ArrayList<>();
        for (Rezervare rezervare : rezervari) {
            int nrLocuri = rezervare.getNrLocuri();
            String numeClient = rezervare.getNumeClient();
            for (int i = 0; i < nrLocuri; i++) {
                numeClienti.add(numeClient);
            }
        }
        return numeClienti;
    }

    public synchronized Iterable<Cursa> getAllCurse(){
        System.out.println("Suntem in getAllCurse de la serviciul vechi");
        return repoCursa.findAll();
    }

    public synchronized List<Integer> getLocuriLibere(){
        System.out.println("Suntem in service getLocuriLibere");
        Iterable<Cursa> curse = repoCursa.findAll();
        List<Integer> locuriLibere = new ArrayList<>();
        for(Cursa cursa : curse){
            int locuri = 18;
            int numarLocuriLibere = locuri - repoCursa.getNumarLocuriRezervateByCursaId(cursa.getId());
            locuriLibere.add(numarLocuriLibere);
        }
        return locuriLibere;
    }

    private final int defaultThreadsNo=5;
    public synchronized void save(Rezervare rezervare){

        repoRezervare.save(rezervare);
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver observer : loggedClients.values()){
            try{
                observer.rezervareReceived(rezervare);
            }catch (ServerException e){
                System.out.println("Eror: " + e.getMessage());
            }
        }

        executor.shutdown();


    }

    public synchronized User findUser(String username, String password){
        return repoUser.findByUsernamePassword(username, password);
    }

    public synchronized void logout(User user, IObserver client) throws ServerException{
        User userFound = repoUser.findByUsernamePassword(user.getUsername(), user.getPassword());
        IObserver localClient = loggedClients.remove(userFound.getId());
        if(localClient == null)
            throw new ServerException("User " + userFound.getId() + " is not logged in. ");
    }

}
