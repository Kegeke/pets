package org.example.pets.service;

import org.example.pets.model.Owner;
import org.example.pets.model.Pet;
import org.example.pets.repository.PetRepository;
import org.example.pets.servlet.dto.PetInDto;
import org.example.pets.servlet.dto.PetOutDto;
import org.example.pets.servlet.mapper.PetDtoMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PetServiceTest {
    private PetService petService;
    private PetRepository petRepository;
    private PetDtoMap map;

    @BeforeEach
    void init() {
        petRepository = Mockito.mock(PetRepository.class);
        petService = new PetService();
        petService.setPetRepository(petRepository);
        map = new PetDtoMap();
    }

    @Test
    void shouldCorrectlyCreatePet() {
        PetInDto incomingDto = new PetInDto(1, "Riki", 2, 1,
                "David");
        Mockito.doReturn(
                        new Pet(1, "Riki", 2, new Owner(1, "David", null)))
                .when(petRepository).save(Mockito.any(Pet.class));

        PetOutDto outgoingDto = petService.createPet(incomingDto);
        assertEquals(incomingDto.getName(), outgoingDto.getName());
        assertEquals(incomingDto.getAge(), outgoingDto.getAge());
        assertEquals(incomingDto.getOwnerId(), outgoingDto.getOwnerId());
        assertEquals(incomingDto.getOwnerName(), outgoingDto.getOwnerName());
    }

    @Test
    void shouldCorrectlyFindAllPet() {
        Owner owner = new Owner(1, "David", null);
        List<Pet> petFromMockRepository = List.of(new Pet(1, "Rikki", 2, owner),
                new Pet(2, "Kitty", 1, owner));
        Mockito.when(petRepository.findAll()).thenReturn(petFromMockRepository);
        List<PetOutDto> pets = petService.getPets();
        assertEquals(2, pets.size());
    }

    @Test
    void shouldReturnOptionalEmptyIfRepositoryReturnOptionalEmpty() {
        Mockito.doReturn(Optional.empty()).when(petRepository).findById(Mockito.anyInt());
        Optional<PetOutDto> pet = petService.getPetById(0);
        assertTrue(pet.isEmpty());
    }

    @Test
    void shouldReturnPetById() {
        Mockito.doReturn(Optional.of(new Pet(1, "Rikki", 2, null)))
                .when(petRepository).findById(Mockito.anyInt());
        Optional<PetOutDto> movie = petService.getPetById(1);
        assertTrue(movie.isPresent());
        assertEquals("Rikki", movie.get().getName());
    }

    @Test
    void shouldThrowExceptionIfUpdateWithoutId() {
        PetInDto petToUpdate = new PetInDto(null, "Kitty", 1, 1, "David");
        assertThrows(RuntimeException.class,
                () -> petService.updatePet(petToUpdate));
    }

    @Test
    void shouldThrowExceptionIfUpdateWithoutTitle() {
        PetInDto petInDto = new PetInDto(1, null, 3, 1, "David");
        assertThrows(RuntimeException.class,
                () -> petService.updatePet(petInDto));
    }

    @Test
    void shouldThrowExceptionIfUpdateWithoutReleaseYear() {
        PetInDto petToUpdate = new PetInDto(1, "Rikki", null, 1, "David");
        assertThrows(RuntimeException.class,
                () -> petService.updatePet(petToUpdate));
    }

    @Test
    void shouldThrowExceptionIfUpdateWithInvalidAge() {
        PetInDto movieToUpdate = new PetInDto(1, "Kitty", -1, 1, "David");
        assertThrows(RuntimeException.class,
                () -> petService.updatePet(movieToUpdate));
    }

    @Test
    void shouldThrowExceptionIfUpdateWithoutOwnerIdAndTheirName() {
        PetInDto petToUpdate = new PetInDto(1, "Baclajan", 4, null, null);
        assertThrows(RuntimeException.class,
                () -> petService.updatePet(petToUpdate));
    }

    @Test
    void shouldCorrectlyUpdate() {
        PetInDto petToUpdate = new PetInDto(1, "Baclajan", 4, 1, "Egor");
        Mockito.doReturn(Optional.of(map.map(petToUpdate))).when(petRepository)
                .update(Mockito.any(Pet.class));
        PetOutDto pet = petService.updatePet(petToUpdate);
        assertEquals(petToUpdate.getName(), pet.getName());
    }

    @Test
    void shouldReturnTheSameBooleanValueAsRepositoryWhenDelete() {
        Mockito.doReturn(true).when(petRepository).deleteById(Mockito.anyInt());
        assertTrue(petService.deletePet(1));
        Mockito.doReturn(false).when(petRepository).deleteById(Mockito.anyInt());
        assertFalse(petService.deletePet(1));
    }

    @Test
    void shouldReturnOwnerRepository() {
        assertEquals(petRepository, petService.getPetRepository());
    }

}
