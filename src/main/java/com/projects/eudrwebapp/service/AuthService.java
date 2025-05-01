package com.projects.eudrwebapp.service;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getSessionUserId(HttpSession session) {
        return (String) session.getAttribute("userId");
    }

    public boolean validateUserAuth(HttpSession sesh, String validateForType) {
        String userId = String.valueOf(sesh.getAttribute("userId"));

        if (userId.isEmpty()) {
            return false;
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            System.out.println("User with Id: " + userId + " not found!");
            return false;
        }

        User validUser = user.get();

        if (!validUser.getUserType().equalsIgnoreCase(validateForType)) {
            System.out.println("User: " + userId + " does not have the valid user type to access this!");
            return false;
        }

        System.out.println("User Validated");
        return true;
    }
}
