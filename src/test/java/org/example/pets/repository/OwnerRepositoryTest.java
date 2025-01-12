package org.example.pets.repository;


import org.example.pets.model.Owner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OwnerRepositoryTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    private OwnerRepository ownerRepository;
    private DBConnectionProvider connectionManager;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void init() {
        connectionManager = new DBConnectionProvider(postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword());
        ownerRepository = (OwnerRepository) RepositoryFactory.getRepository(Owner.class, Integer.class);
        RepositoryFactory.setConnectionManager(connectionManager);
    }

    @Test
    void shouldGetOwners() {
        List<Owner> owners = ownerRepository.findAll();
        owners.forEach(o -> ownerRepository.deleteById(o.getId()));
        ownerRepository.save(new Owner(null, "Slava", null));
        ownerRepository.save(new Owner(null, "Vasya", null));
        owners = ownerRepository.findAll();
        assertEquals(2, owners.size());
    }

    @Test
    void shouldGetOwnerById() {
        ownerRepository.save(new Owner(null, "Slava", null));
        ownerRepository.save(new Owner(null, "Vasya", null));
        List<Owner> directors = ownerRepository.findAll();
        Owner ownerToFind = directors.getFirst();
        Owner foundOwner = ownerRepository.findById(ownerToFind.getId()).get();
        assertEquals(ownerToFind.getId(), foundOwner.getId());
        assertEquals(ownerToFind.getName(), foundOwner.getName());
    }

    @Test
    void shouldReturnCorrectStatusIfDeleteWithInvalidId() {
        boolean resultStatus = ownerRepository.deleteById(0);
        assertFalse(resultStatus);
    }

    @Test
    void shouldUpdateDirector() {
        ownerRepository.save(new Owner(1, "Slava", null));
        List<Owner> owners = ownerRepository.findAll();
        Owner ownerToUpdate = owners.stream().filter(owner -> "Slava".equals(owner.getName()))
                .findFirst().get();
        ownerToUpdate.setName("Igor");
        ownerRepository.update(ownerToUpdate);
        Owner foundOwner = ownerRepository.findById(ownerToUpdate.getId()).get();
        assertEquals(ownerToUpdate.getName(), foundOwner.getName());
    }

    @Test
    void shouldThrowExceptionIfUpdateWithInvalidId() {
        Owner ownerToUpdateWithInvalidId = new Owner(0, "Slava", null);
        assertThrows(RuntimeException.class,
                () -> ownerRepository.update(ownerToUpdateWithInvalidId));
    }

    @Test
    void shouldReturnConnectionManager() {
        assertEquals(connectionManager, ownerRepository.getConnectionManager());
    }
}
