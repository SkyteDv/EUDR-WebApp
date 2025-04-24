package com.projects.eudrwebapp.repository;

import com.projects.eudrwebapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}

