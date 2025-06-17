package com.projects.eudrwebapp.model;

import com.projects.eudrwebapp.model.Enum.Country;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Harbour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Country country;

    @ManyToMany(mappedBy = "harbours")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "harbour")
    private List<StorageUnit> storageUnits = new ArrayList<>();


    public Harbour() {}

    public Harbour(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public List<StorageUnit> getStorageUnits() {
        return storageUnits;
    }

    public void setStorageUnits(List<StorageUnit> storageUnits) {
        this.storageUnits = storageUnits;
    }
}

