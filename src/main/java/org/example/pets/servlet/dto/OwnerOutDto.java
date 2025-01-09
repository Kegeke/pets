package org.example.pets.servlet.dto;

import java.util.List;

public class OwnerOutDto {
    private Integer id;
    private String name;
    private List<PetOutDto> pets;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PetOutDto> getPets() {
        return pets;
    }

    public void setPets(List<PetOutDto> pets) {
        this.pets = pets;
    }
}
