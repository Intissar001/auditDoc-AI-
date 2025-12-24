package com.yourapp.services;

import com.yourapp.DAO.PasswordResetTokenRepository;
import com.yourapp.DAO.UserRepository;
import com.yourapp.dto.*;
import com.yourapp.model.PasswordResetToken;
import com.yourapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Register a new user
     */
    public AuthResponseDto register(SignUpRequestDto request) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return AuthResponseDto.failure("Un compte avec cet email existe déjà");
            }

            // Create new user
            User user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setRole("USER"); // Default role
            user.setEmailAlerts(true);
            user.setAuditReminders(true);

            // Save user
            user = userRepository.save(user);

            System.out.println("✅ User registered successfully: " + user.getEmail());

            return AuthResponseDto.success(
                    "Compte créé avec succès",
                    user.getId(),
                    user.getEmail(),
                    user.getRole()
            );

        } catch (Exception e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            e.printStackTrace();
            return AuthResponseDto.failure("Erreur lors de la création du compte");
        }
    }

    /**
     * Login user
     */
    public AuthResponseDto login(LoginRequestDto request) {
        try {
            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

            if (userOpt.isEmpty()) {
                return AuthResponseDto.failure("Email ou mot de passe incorrect");
            }

            User user = userOpt.get();

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                return AuthResponseDto.failure("Email ou mot de passe incorrect");
            }

            System.out.println("✅ User logged in successfully: " + user.getEmail());

            return AuthResponseDto.success(
                    "Connexion réussie",
                    user.getId(),
                    user.getEmail(),
                    user.getRole()
            );

        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            return AuthResponseDto.failure("Erreur lors de la connexion");
        }
    }

    /**
     * Request password reset
     */
    public AuthResponseDto requestPasswordReset(PasswordResetRequestDto request) {
        try {
            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

            if (userOpt.isEmpty()) {
                // For security, always return success message
                return AuthResponseDto.success(
                        "Si un compte existe avec cet email, vous recevrez un lien de réinitialisation",
                        null, null, null
                );
            }

            User user = userOpt.get();

            // Delete any existing unused tokens for this user
            tokenRepository.deleteByUser(user);

            // Generate reset token
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, user);
            tokenRepository.save(resetToken);

            // TODO: Send email with reset link
            // For now, we'll just log it
            System.out.println("🔑 Password reset token for " + user.getEmail() + ": " + token);
            System.out.println("📧 Reset link: http://localhost:8080/reset-password?token=" + token);

            return AuthResponseDto.success(
                    "Un lien de réinitialisation a été envoyé à votre email",
                    null, null, null
            );

        } catch (Exception e) {
            System.err.println("❌ Password reset request error: " + e.getMessage());
            e.printStackTrace();
            return AuthResponseDto.failure("Erreur lors de la demande de réinitialisation");
        }
    }

    /**
     * Reset password with token
     */
    public AuthResponseDto resetPassword(String token, String newPassword) {
        try {
            // Find token
            Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

            if (tokenOpt.isEmpty()) {
                return AuthResponseDto.failure("Token invalide ou expiré");
            }

            PasswordResetToken resetToken = tokenOpt.get();

            // Check if token is expired or already used
            if (resetToken.isExpired() || resetToken.isUsed()) {
                return AuthResponseDto.failure("Token invalide ou expiré");
            }

            // Update user password
            User user = resetToken.getUser();
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Mark token as used
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);

            System.out.println("✅ Password reset successfully for: " + user.getEmail());

            return AuthResponseDto.success(
                    "Mot de passe réinitialisé avec succès",
                    user.getId(),
                    user.getEmail(),
                    user.getRole()
            );

        } catch (Exception e) {
            System.err.println("❌ Password reset error: " + e.getMessage());
            e.printStackTrace();
            return AuthResponseDto.failure("Erreur lors de la réinitialisation du mot de passe");
        }
    }

    /**
     * Validate password strength
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
        }

        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Clean up expired tokens (scheduled task)
     */
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        System.out.println("🧹 Cleaned up expired password reset tokens");
    }
}