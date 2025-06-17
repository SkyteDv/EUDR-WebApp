package com.projects.eudrwebapp.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.projects.eudrwebapp.model.Enum.Country;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String email;
    private String password;
    private String userType;
    private String osapiensID;

    @Enumerated(EnumType.STRING)
    private Country location;

    @ManyToMany
    @JoinTable(
            name = "user_harbour",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "harbour_id")
    )
    private Set<Harbour> harbours = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<StorageUnit> storageUnits = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_associate", joinColumns = @JoinColumn(name = "customer_id"), inverseJoinColumns = @JoinColumn(name = "supplier_id"))
    private List<User> associates = new ArrayList<>();

    public List<User> getAssociates() {
        return associates;
    }

    public void setAssociates(List<User> associates) {
        this.associates = associates;
    }

    public User() {
    }

    public User(String username, String password, String userType, String osapiensID, Country location) {
        this.username = username;
        
        this.password = password;
        this.userType = userType;
        this.osapiensID = osapiensID;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public String getOsapiensID() {
        return osapiensID;
    }

    public void setOsapiensID(String osapiensID) {
        this.osapiensID = osapiensID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Country getLocation() {
        return location;
    }

    public void setLocation(Country location) {
        this.location = location;
    }

    public List<StorageUnit> getStorageUnits() {
        return storageUnits;
    }

    public void setStorageUnits(List<StorageUnit> storageUnits) {
        this.storageUnits = storageUnits;
    }

    public Set<Harbour> getHarbours() {
        return harbours;
    }

    public void setHarbours(Set<Harbour> harbours) {
        this.harbours = harbours;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", userType='" + userType + '\'' +
                ", osapiensID='" + osapiensID + '\'' +
                ", associates=" + associates +
                '}';
    }
}
