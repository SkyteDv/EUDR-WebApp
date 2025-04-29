package com.projects.eudrwebapp.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    public boolean isLoggedIn(HttpSession session) {
        String userId = String.valueOf(session.getAttribute("userId"));
        return userId != null;
    }

    public String getSessionUserId(HttpSession session) {
        return (String) session.getAttribute("userId");
    }
}
