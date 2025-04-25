package com.projects.eudrwebapp.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    public boolean isLoggedIn(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userId != null;
    }

    public Long getSessionUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }
}
