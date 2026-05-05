package eltons.books.controllers;

import eltons.books.DTOs.UserDTO;
import eltons.books.DTOs.UserRegisterDTO;
import eltons.books.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userS;

    public UserController(UserService userS) { this.userS = userS; }

    @PostMapping("/newUser")
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

    @GetMapping("/fromId")
    public ResponseEntity<UserDTO> showUserFromId(@RequestBody Long request) {
        UserDTO userDTO = userS.showUserFromId(request);
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/fromName")
    public ResponseEntity<UserDTO> showUserFromName(@RequestBody String request) {
        UserDTO userDTO = userS.showUserFromName(request);
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(userDTO);
    }
}
