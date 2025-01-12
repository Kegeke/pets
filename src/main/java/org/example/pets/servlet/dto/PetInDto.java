package org.example.pets.servlet.dto;

public class PetInDto {
    private Integer id;
    private String name;
    private Integer age;
    private Integer ownerId;
    private String ownerName;

    public PetInDto() {
    }

    public PetInDto(Integer id, String name, Integer age, Integer ownerId, String ownerName) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
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

    public PetInDto setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
