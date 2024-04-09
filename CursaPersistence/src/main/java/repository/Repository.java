package repository;

import model.Entity;

import java.util.List;

public interface Repository<ID,E extends Entity<ID>> {

    E findOne(ID id);
    Iterable<E> findAll();
    void save(E elem);
    void delete(ID id);
    void update(E elem);

}
