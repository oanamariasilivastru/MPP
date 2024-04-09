package repository;


import model.Cursa;

import java.time.LocalDateTime;

public interface CursaRepository extends Repository<Long, Cursa>{
    public Cursa findByDestData(String destinatie, LocalDateTime time);

    public int getNumarLocuriRezervateByCursaId(long idCursa);

}
