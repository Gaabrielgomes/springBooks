package eltons.books.controllers;

import eltons.books.DTOs.BookDTO;
import eltons.books.models.Book;
import eltons.books.models.BookcaseEntry;
import eltons.books.services.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

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

    @GetMapping("/search/bytitle")
    public ResponseEntity<List<BookDTO>> searchBookByTitle(@RequestParam String title) {
        try {
            List<Book> foundBooks = bookS.searchBookByTitle(title);
            return ResponseEntity.ok(bookS.showFoundBooksAsDTO(foundBooks));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/search/byauthor")
    public ResponseEntity<List<BookDTO>> searchBooksByAuthor(@RequestParam String authorName) {
        List<Book> foundBooks = bookS.searchBooksByAuthor(authorName);
        return ResponseEntity.ok(bookS.showFoundBooksAsDTO(foundBooks));
    }

    @GetMapping("/mainbookcase/bytitle")
    public ResponseEntity<BookDTO> getBookByTitle(@RequestParam String title) {
        BookDTO foundBook = bookS.getBookByTitleFromDatabase(title);
        return ResponseEntity.ok(foundBook);
    }

    @PostMapping("/savebook")
    public ResponseEntity<BookDTO> saveBook(@RequestBody BookDTO dto) {
        try {
            Book saved = bookS.saveBook(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bookS.showFoundBooksAsDTO(Collections.singletonList(saved)).getFirst());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
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
