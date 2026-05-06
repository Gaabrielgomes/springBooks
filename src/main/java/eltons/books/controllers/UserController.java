package eltons.books.controllers;

import eltons.books.DTOs.UserDTO;
import eltons.books.DTOs.UserRegisterDTO;
import eltons.books.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userS;
//
//    public UserController(UserService userS) { this.userS = userS; }

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
