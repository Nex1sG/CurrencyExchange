package main.currencyexchange.repositories;


import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {

    Optional<T> findById(long id);

    List<T> findAll();

    void save(T entity);

    boolean update(T entity);

    boolean delete(long id);

}
