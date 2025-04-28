package com.projects.eudrwebapp.repository;

import com.projects.eudrwebapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);
    List<User> findByUserType(String userType);
    List<User> findByAssociates_Id(Long associateId);
    Optional<User> findByOsapiensID(String osapiensID);

}

