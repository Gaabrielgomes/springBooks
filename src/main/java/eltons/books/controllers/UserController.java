package eltons.books.controllers;

import eltons.books.DTOs.BookDTO;
import eltons.books.DTOs.UserDTO;
import eltons.books.models.Book;
import eltons.books.models.User;
import eltons.books.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userS;

    public UserController(UserService userService) {
        this.userS = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(userS.showUserFromName(user.getName()));
    }

    @GetMapping("/me/bookcase")
    public ResponseEntity<List<BookDTO>> getMyBookcase() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(userS.showUserBookcase(user));
    }

    @PostMapping("/me/bookcase/add/{bookId}")
    public ResponseEntity<String> addBookToBookcase(@PathVariable Long bookId) {
        User user = getAuthenticatedUser();
        userS.addBookToBookcase(user, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Book added to bookcase.");
    }

    @PutMapping("/me/bookcase/{bookId}/review")
    public ResponseEntity<String> addReview(@PathVariable Long bookId,
                                            @RequestBody String review) {
        User user = getAuthenticatedUser();
        userS.addReview(user, bookId, review);
        return ResponseEntity.status(HttpStatus.CREATED).body("Review added.");
    }

    @DeleteMapping("/me/shelf/{bookId}")
    public ResponseEntity<String> removeBookFromShelf(@PathVariable Long bookId) {
        User user = getAuthenticatedUser();
        userS.removeFromBookcase(user, bookId);
        return ResponseEntity.ok("Book removed from bookcase.");
    }

    private User getAuthenticatedUser() {
        return (User) Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                        .getPrincipal();
    }
}
