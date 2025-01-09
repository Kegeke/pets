package org.example.pets.model;

public class Pet {
    private Integer id;
    private String name;
    private Integer age;
    private Owner owner;

    public Pet() {
    }

    public Pet(Integer id, String name, Integer age, Owner owner) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.owner = owner;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
}
