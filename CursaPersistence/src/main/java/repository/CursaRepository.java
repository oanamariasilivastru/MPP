package repository;


import model.Cursa;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@org.springframework.stereotype.Repository
public interface CursaRepository extends Repository<Long, Cursa>{
    public Cursa findByDestData(String destinatie, LocalDateTime time);

    public int getNumarLocuriRezervateByCursaId(long idCursa);

}
