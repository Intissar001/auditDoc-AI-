package com.yourapp.services;

import com.yourapp.DAO.UserRepository;
import com.yourapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User updateUserSettings(Long userId, Boolean emailAlerts, Boolean auditReminders) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEmailAlerts(emailAlerts);
            user.setAuditReminders(auditReminders);
            return userRepository.save(user);
        }
        return null;
    }
}