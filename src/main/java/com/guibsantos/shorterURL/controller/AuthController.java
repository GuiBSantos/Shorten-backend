package com.guibsantos.shorterURL.controller;

import com.guibsantos.shorterURL.controller.docs.AuthControllerDocs;
import com.guibsantos.shorterURL.controller.dto.request.*;
import com.guibsantos.shorterURL.controller.dto.response.CheckAvailabilityResponse;
import com.guibsantos.shorterURL.controller.dto.response.GoogleLoginResponse;
import com.guibsantos.shorterURL.controller.dto.response.LoginResponse;
import com.guibsantos.shorterURL.controller.dto.response.MessageResponse;
import com.guibsantos.shorterURL.controller.dto.response.UserResponse;
import com.guibsantos.shorterURL.entity.UserEntity;
import com.guibsantos.shorterURL.repository.UserRepository;
import com.guibsantos.shorterURL.service.AuthService;
import com.guibsantos.shorterURL.security.TokenService;
import com.guibsantos.shorterURL.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final UserService userService;
    private final AuthService authService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody @Valid RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok(new MessageResponse("Usuário registrado com sucesso!"));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(request.email(), request.password());
            var auth = authenticationManager.authenticate(usernamePassword);

            var user = (UserEntity) auth.getPrincipal();
            var token = tokenService.generateToken(user);

            return ResponseEntity.ok(new LoginResponse(token));

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            throw e;
        }
    }

    @Override
    @PostMapping("/google")
    public ResponseEntity<GoogleLoginResponse> googleLogin(@RequestBody @Valid GoogleLoginRequest request) {
        var response = authService.loginWithGoogle(request.token());
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(new MessageResponse("Senha alterada com sucesso!"));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
            UserEntity user = (UserEntity) authentication.getPrincipal();

            return ResponseEntity.ok(new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getAvatarUrl()
            ));
        }
        return ResponseEntity.status(403).build();
    }

    @Override
    @GetMapping("/check-username/{username}")
    public ResponseEntity<CheckAvailabilityResponse> checkUsername(@PathVariable String username) {
        boolean exists = userRepository.existsByUsername(username);
        String message = exists ? "Nome de usuário já está em uso." : "Nome de usuário disponível.";
        return ResponseEntity.ok(new CheckAvailabilityResponse(!exists, message));
    }

    @Override
    @GetMapping("/check-email")
    public ResponseEntity<CheckAvailabilityResponse> checkEmail(@RequestParam("value") String email) {
        boolean exists = userRepository.existsByEmail(email);
        String message = exists ? "Este e-mail já está cadastrado." : "E-mail disponível.";
        return ResponseEntity.ok(new CheckAvailabilityResponse(!exists, message));
    }

    @Override
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request.email());
        return ResponseEntity.ok(new MessageResponse("Código de recuperação enviado para o email, caso ele exista."));
    }

    @Override
    @PatchMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request.email(), request.code(), request.newPassword());
        return ResponseEntity.ok(new MessageResponse("Senha redefinida com sucesso!"));
    }

    @Override
    @PostMapping("/validate-code")
    public ResponseEntity<MessageResponse> validateCode(@RequestBody @Valid ValidateCodeRequest request) {
        authService.validateRecoveryCode(request.email(), request.code());
        return ResponseEntity.ok(new MessageResponse("Código válido."));
    }

    @Override
    @PatchMapping("/update-username")
    public ResponseEntity<MessageResponse> updateUsername(@RequestBody @Valid UpdateUsernameRequest request) {
        authService.updateUsername(request.newUsername());
        return ResponseEntity.ok(new MessageResponse("Username atualizado com sucesso!"));
    }

    @Override
    @DeleteMapping("/delete-account")
    public ResponseEntity<MessageResponse> deleteAccount(@RequestBody @Valid DeleteAccountRequest request) {
        authService.deleteAccount(request.password());
        return ResponseEntity.ok(new MessageResponse("Conta excluída com sucesso!"));
    }
}
