package service;

import model.Cursa;
import model.Rezervare;

public interface IObserver {
//    void destinatieReceived(Cursa cursa) throws ServerException;
//
    void rezervareReceived(Rezervare rezervare) throws ServerException;
}
