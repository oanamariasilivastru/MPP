package repository;

import model.Rezervare;

public interface RezervareRepository extends Repository<Long, Rezervare>{
    public Iterable<Rezervare> findAllByCursaId(Long idCursa);
}
