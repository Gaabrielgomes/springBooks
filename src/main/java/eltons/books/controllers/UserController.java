package eltons.books.controllers;

import eltons.books.DTOs.UserDTO;
import eltons.books.models.BookcaseEntry;
import eltons.books.DTOs.BookcaseEntryDTO;
import eltons.books.models.User;
import eltons.books.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping
    public ResponseEntity<UserDTO> getMyProfile() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(userS.showUserFromId(user.getId()));
    }

    @GetMapping("/bookcase")
    public ResponseEntity<List<BookcaseEntryDTO>> getMyBookcase() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(userS.showUserBookcase(user));
    }

    @GetMapping("/bookcase/showbook/{bookId}")
    public ResponseEntity<BookcaseEntryDTO> showBookFromUserBookcase(@PathVariable Long bookId) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(userS.showBookFromUserBookcase(user, bookId));
    }

    @Transactional
    @PostMapping("/bookcase/addbook/{bookId}")
    public ResponseEntity<BookcaseEntry> addBookToBookcase(@PathVariable Long bookId) {
        User user = getAuthenticatedUser();
        userS.addBookToBookcase(user, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body();
    }

    @Transactional
    @PutMapping("/bookcase/addreview/{bookId}")
    public ResponseEntity<String> addReview(@PathVariable Long bookId,
                                            @RequestBody String review) {
        User user = getAuthenticatedUser();
        userS.addReview(user, bookId, review);
        return ResponseEntity.status(HttpStatus.CREATED).body("Review added.");
    }

    @Transactional
    @DeleteMapping("/bookcase/removebook/{bookId}")
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
