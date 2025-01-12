package org.example.pets.repository;

import org.example.pets.model.Owner;
import org.example.pets.model.Pet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PetRepositoryTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    private PetRepository petRepository;
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
        petRepository = (PetRepository) RepositoryFactory.getRepository(Pet.class, Integer.class);
        RepositoryFactory.setConnectionManager(connectionManager);
        petRepository.findAll().forEach(pet -> petRepository.deleteById(pet.getId()));
    }

    @Test
    void shouldGetPets() {
        Owner owner = new Owner(null, "David", null);
        petRepository.save(new Pet(null, "Riki", 2, owner));
        petRepository.save(new Pet(null, "Vasya", 1, owner));
        List<Pet> pets = petRepository.findAll();
        assertEquals(2, pets.size());
    }

    @Test
    void shouldGetPetById() {
        Owner owner = new Owner(null, "David", null);
        petRepository.save(new Pet(null, "Riki", 2, owner));
        petRepository.save(new Pet(null, "Vasya", 1, owner));
        List<Pet> pets = petRepository.findAll();
        Pet petToFind = pets.get(0);
        Pet foundPet = petRepository.findById(petToFind.getId()).get();
        assertEquals(petToFind.getId(), foundPet.getId());
        assertEquals(petToFind.getName(), foundPet.getName());
        assertEquals(petToFind.getAge(), foundPet.getAge());
    }

    @Test
    void shouldDeletePetById() {
        Owner owner = new Owner(null, "David", null);
        petRepository.save(new Pet(null, "Riki", 2, owner));
        petRepository.save(new Pet(null, "Vasya", 1, owner));
        List<Pet> pets = petRepository.findAll();
        Pet petToDelete = pets.get(0);

        boolean resultStatus = petRepository.deleteById(petToDelete.getId());
        Optional<Pet> foundPet = petRepository.findById(petToDelete.getId());
        assertTrue(resultStatus);
        assertTrue(foundPet.isEmpty());
    }

    @Test
    void shouldReturnCorrectStatusIfDeleteWithInvalidId() {
        boolean resultStatus = petRepository.deleteById(0);
        assertFalse(resultStatus);
    }

    @Test
    void shouldUpdatePet() {
        Owner owner = new Owner(null, "David", null);
        petRepository.save(new Pet(null, "Riki", 2, owner));
        List<Pet> pets = petRepository.findAll();
        Pet petToUpdate = pets.stream().filter(pet -> "Riki".equals(pet.getName()))
                .findFirst().get();
        petToUpdate.setName("Vasya");
        petToUpdate.setAge(1);
        petRepository.update(petToUpdate);
        Pet foundPet = petRepository.findById(petToUpdate.getId()).get();
        assertEquals(petToUpdate.getName(), foundPet.getName());
        assertEquals(petToUpdate.getAge(), foundPet.getAge());
    }

    @Test
    void shouldUpdatePetIfInputContainsOwnerNameButNoOwnerId() {
        Owner owner = new Owner(null, "David", null);
        petRepository.save(new Pet(null, "Riki", 2, owner));
        List<Pet> pets = petRepository.findAll();
        Pet petToUpdate = pets.stream().filter(pet -> "Riki".equals(pet.getName()))
                .findFirst().get();
        petToUpdate.setName("Vasya");
        petToUpdate.setAge(2012);
        petToUpdate.getOwner().setId(null);
        petRepository.update(petToUpdate);
        Pet foundPet = petRepository.findById(petToUpdate.getId()).get();
        assertEquals(petToUpdate.getName(), foundPet.getName());
        assertEquals(petToUpdate.getAge(), foundPet.getAge());
    }

    @Test
    void shouldThrowExceptionIfUpdateWithInvalidId() {
        Owner owner = new Owner(null, "David", null);
        Pet petToUpdateWithInvalidId = new Pet(0, "Riki", 2, owner);
        assertThrows(RuntimeException.class,
                () -> petRepository.update(petToUpdateWithInvalidId));
    }

    @Test
    void shouldThrowExceptionIfUpdateWithInconsistentIdOfOwner() {
        Owner owner = new Owner(null, "David", null);
        petRepository.save(new Pet(null, "Riki", 2, owner));
        List<Pet> pets = petRepository.findAll();
        Pet petToUpdate = pets.stream().filter(pet -> "Riki".equals(pet.getName()))
                .findFirst().get();

        Owner owner2 = new Owner(null, "Anna", null);
        petRepository.save(new Pet(null, "Kitty", 3, owner2));
        pets = petRepository.findAll();
        Pet petWithAnotherOwner = pets.stream().filter(d -> "Kitty".equals(d.getName()))
                .findFirst().get();

        petToUpdate.getOwner().setId(petWithAnotherOwner.getOwner().getId());
        assertThrows(RuntimeException.class,
                () -> petRepository.update(petToUpdate));
    }

    @Test
    void shouldThrowExceptionIfSaveWithInconsistentIdOfDirector() {
        Owner owner = new Owner(null, "David", null);
        petRepository.save(new Pet(null, "Riki", 2, owner));

        owner.setId(0);
        Pet petToSave = new Pet(null, "Kitty", 3, owner);

        assertThrows(RuntimeException.class,
                () -> petRepository.save(petToSave));
    }

    @Test
    void shouldThrowExceptionIfSaveWithInvalidIdOfOwner() {
        Owner owner = new Owner(null, "David", null);
        petRepository.save(new Pet(null, "Rikki", 2, owner));

        owner.setId(0);
        owner.setName(null);
        Pet petToSave = new Pet(null, "Kitty", 3, owner);

        assertThrows(RuntimeException.class,
                () -> petRepository.save(petToSave));
    }

    @Test
    void shouldDeletePetsByOwnerId() {
        Owner owner = new Owner(null, "David", null);
        petRepository.save(new Pet(null, "Riki", 2, owner));
        petRepository.save(new Pet(null, "Kitty", 3, owner));
        List<Pet> pets = petRepository.findAll();
        assertEquals(2, pets.size());
        Integer ownerId = pets.get(0).getOwner().getId();
        petRepository.deleteByOwnerID(ownerId);
        pets = petRepository.findAll();
        assertEquals(0, pets.size());
    }

    @Test
    void shouldReturnPetsByOwnerId() {
        Owner owner = new Owner(null, "David", null);
        petRepository.save(new Pet(null, "Riki", 2, owner));
        petRepository.save(new Pet(null, "Kitty", 3, owner));
        List<Pet> pets = petRepository.findAll();
        assertEquals(2, pets.size());
        Integer directorId = pets.get(0).getOwner().getId();
        pets = petRepository.findByOwnerId(directorId);
        assertEquals(2, pets.size());
    }

    @Test
    void shouldReturnConnectionManager() {
        assertEquals(connectionManager, petRepository.getConnectionManager());
    }

    @Test
    void shouldReturnMovieRepository() {
        assertNotNull(petRepository.getOwnerRepository());
    }
}
