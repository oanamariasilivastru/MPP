package repository;

import model.Entity;
import org.springframework.stereotype.Component;

import java.util.List;

@org.springframework.stereotype.Repository
public interface Repository<ID,E extends Entity<ID>> {

    E findOne(ID id);
    Iterable<E> findAll();
    E save(E elem);
    void delete(ID id);
    void update(E elem);

}
