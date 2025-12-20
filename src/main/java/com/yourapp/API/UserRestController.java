package com.yourapp.API;

import com.yourapp.dto.UserSettingsDTO;
import com.yourapp.model.User;
import com.yourapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser() {
        // En vrai, récupère depuis session/token
        User user = userService.getUserByEmail("boudifatima450@gmail.com");
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<UserSettingsDTO> updateSettings(
            @PathVariable Long id,
            @RequestBody UserSettingsDTO request) {
        User updated = userService.updateUserSettings(
                id,
                request.getEmailAlerts(),
                request.getAuditReminders()
        );
        if (updated != null) {
            return ResponseEntity.ok(new UserSettingsDTO(
                    updated.getId(),
                    updated.getEmail(),
                    updated.getEmailAlerts(),
                    updated.getAuditReminders()
            ));
        }
        return ResponseEntity.notFound().build();
    }
}