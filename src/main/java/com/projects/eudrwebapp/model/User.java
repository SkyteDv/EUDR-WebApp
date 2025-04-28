package com.projects.eudrwebapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String userType;
    private String osapiensID;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_associate",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private List<User> associates = new ArrayList<>();

    public List<User> getAssociates() {
        return associates;
    }

    public void setAssociates(List<User> associates) {
        this.associates = associates;
    }

    public User() {}

    public User(String username, String password, String userType, String osapiensID) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.osapiensID = osapiensID;
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

    public void setUsername(String username) {
        this.username = username;
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
