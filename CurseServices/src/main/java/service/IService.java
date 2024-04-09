package service;

import model.Cursa;
import model.Rezervare;
import model.User;
import org.codehaus.groovy.tools.shell.IO;


import java.time.LocalDateTime;
import java.util.List;

public interface IService {

    void login(User user, IObserver client) throws ServerException;

    Cursa getCursaByDestAndTime(String destination, LocalDateTime date) throws ServerException;

    List<String> getNumeClientiCuRezervariMultiple(Long id) throws ServerException;

    Iterable<Cursa> getAllCurse() throws ServerException;

    List<Integer> getLocuriLibere() throws ServerException;

    void save(Rezervare rezervare) throws ServerException;

    User findUser(String username, String password);

    void logout(User user, IObserver client) throws ServerException;
}
