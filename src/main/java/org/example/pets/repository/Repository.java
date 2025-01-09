package org.example.pets.repository;

import org.example.pets.db.ConnectionManager;

import java.util.List;
import java.util.Optional;

public interface Repository<T, K> {
    T save(T t);

    List<T> findAll();

    Optional<T> findById(K id);

    boolean deleteById(K id);

    Optional<T> update(T t);

    public void setConnectionManager(ConnectionManager connectionManager);

    void initDB();
}
