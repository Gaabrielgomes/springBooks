package eltons.books.controllers;

import eltons.books.DTOs.BookDTO;
import eltons.books.DTOs.BookSaveRequestDTO;
import eltons.books.models.Book;
import eltons.books.services.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.MissingRequestValueException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookS;

    public BookController(BookService bookService) {
        this.bookS = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookS.showAllBooks());
    }

    @GetMapping("/search/byTitle")
    public ResponseEntity<List<BookDTO>> searchBookByTitle(@RequestParam String title) {
        List<Book> foundBooks = bookS.searchBookByTitle(title);
        return ResponseEntity.ok(bookS.showFoundBooksAsDTO(foundBooks));
    }

    @GetMapping("/search/byAuthor")
    public ResponseEntity<List<BookDTO>> searchBooksByAuthor(@RequestParam String authorName) {
        List<Book> foundBooks = bookS.searchBooksByAuthor(authorName);
        return ResponseEntity.ok(bookS.showFoundBooksAsDTO(foundBooks));
    }

    @GetMapping("/byTitle")
    public ResponseEntity<BookDTO> getBookByTitle(@RequestParam String title) {
        BookDTO foundBook = bookS.getBookByTitleFromDatabase(title);
        return ResponseEntity.ok(foundBook);
    }

    @Transactional
    @PostMapping("/save/byLastSearchedBooksIndex")
    public ResponseEntity<String> saveBook(@RequestBody BookSaveRequestDTO dto) {
        try {
            Book saved = bookS.saveSelectedBook(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Book saved! -> " + saved);
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid book index.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save book.");
        }
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBookById(@PathVariable Long id) {
        try {
            bookS.deleteBookById(id);
            return ResponseEntity.ok("Book deleted!");

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
