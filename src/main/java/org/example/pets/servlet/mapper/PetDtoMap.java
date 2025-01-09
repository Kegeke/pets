package org.example.pets.servlet.mapper;

import org.example.pets.model.Owner;
import org.example.pets.model.Pet;
import org.example.pets.servlet.dto.PetInDto;
import org.example.pets.servlet.dto.PetOutDto;

public class PetDtoMap {
    public Pet map(PetInDto petInDto) {
        Owner owner = new Owner();
        owner.setId(petInDto.getOwnerId());
        owner.setName(petInDto.getOwnerName());

        Pet pet = new Pet();
        pet.setId(petInDto.getId());
        pet.setName(petInDto.getName());
        pet.setAge(petInDto.getAge());
        pet.setOwner(owner);

        return pet;
    }

    public PetOutDto map(Pet pet) {
        PetOutDto petOutDto = new PetOutDto();
        petOutDto.setId(pet.getId());
        petOutDto.setName(pet.getName());
        petOutDto.setAge(pet.getAge());

        Owner owner = pet.getOwner();

        if (owner != null) {
            petOutDto.setOwnerId(owner.getId());
            petOutDto.setOwnerName(owner.getName());
        }

        return petOutDto;
    }
}
