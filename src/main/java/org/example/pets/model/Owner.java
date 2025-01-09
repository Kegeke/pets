package org.example.pets.model;

import java.util.List;

public class Owner {
    private Integer id;
    private String name;
    private List<Pet> pets;

    public Owner() {
    }

    public Owner(Integer id, String name, List<Pet> pets) {
        this.id = id;
        this.name = name;
        this.pets = pets;
    }

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

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }
}
