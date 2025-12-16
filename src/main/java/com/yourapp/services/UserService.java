package com.yourapp.services;

import com.yourapp.model.User;

public class UserService {

    public User getCurrentUser() {
        return new User("Intissar AIT HSSAIN", "Administrateur");
    }
}
