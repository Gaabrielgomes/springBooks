package eltons.books.controllers;

import eltons.books.DTOs.UserRegisterDTO;
import eltons.books.services.JwtService;
import eltons.books.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserService userS;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> newUser(@Validated @RequestBody UserRegisterDTO newUserDTO) {
        try {
            Boolean newUser = userS.newUser(newUserDTO);

            if (Boolean.TRUE.equals(newUser)) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Welcome to Eltons' Books, %s!".formatted(newUserDTO.getName()));
            }

        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid birth date.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Gender does not exists.");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while creating new User.");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
    }

    @PostMapping("/login")
    public String login(@RequestBody UserRegisterDTO request) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getName(),
                        request.getPassword()
                )
        );

        UserDetails user = (UserDetails) authentication.getPrincipal();

        assert user != null;
        return jwtService.generateToken(user);
    }
}