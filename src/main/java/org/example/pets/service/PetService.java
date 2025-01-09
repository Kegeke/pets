package org.example.pets.service;

import org.example.pets.model.Pet;
import org.example.pets.repository.Repository;
import org.example.pets.repository.RepositoryFactory;
import org.example.pets.servlet.dto.PetInDto;
import org.example.pets.servlet.dto.PetOutDto;
import org.example.pets.servlet.mapper.PetDtoMap;

import java.util.List;
import java.util.Optional;

public class PetService {
    private Repository<Pet, Integer> petRepository;
    private final PetDtoMap petDtoMap;

    public PetService() {
        petRepository = RepositoryFactory.getRepository(Pet.class, Integer.class);
        petDtoMap = new PetDtoMap();
    }

    public Repository<Pet, Integer> getPetRepository() {
        return petRepository;
    }

    public void setPetRepository(Repository<Pet, Integer> petRepository) {
        this.petRepository = petRepository;
    }

    public PetOutDto createPet(PetInDto petInDto) {
        validatePetData(petInDto);

        return mapToOutDto(petRepository.save(mapFromInDto(petInDto)));
    }

    public List<PetOutDto> getPets() {
        return petRepository.findAll().stream().map(this::mapToOutDto).toList();
    }

    public Optional<PetOutDto> getPetById(int id) {
        Optional<Pet> optionalPet = petRepository.findById(id);

        return optionalPet.isEmpty() ? Optional.empty()
                : Optional.of(mapToOutDto(optionalPet.get()));
    }

    public PetOutDto updatePet(PetInDto pet) {
        if (pet.getId() == null) {
            throw new RuntimeException("id питомца не найден");
        }

        validatePetData(pet);

        return mapToOutDto(petRepository.update(mapFromInDto(pet)).orElseThrow());
    }

    public boolean deletePet(int id) {
        return petRepository.deleteById(id);
    }

    private void validatePetData(PetInDto pet) {
        if (pet.getName() == null) {
            throw new RuntimeException("Должно быть имя питомца");
        }
        if (pet.getAge() == null) {
            throw new RuntimeException("Должен быть возраст питомца");
        }
        if (pet.getAge() < 0) {
            throw new RuntimeException("Возраст питомна не может быть меньше 0");
        }
        if (pet.getOwnerId() == null && pet.getOwnerName() == null) {
            throw new RuntimeException("Должен быть id и имя хозяина");
        }
    }

    private PetOutDto mapToOutDto(Pet pet) {
        return petDtoMap.map(pet);
    }

    private Pet mapFromInDto(PetInDto pet) {
        return petDtoMap.map(pet);
    }
}
