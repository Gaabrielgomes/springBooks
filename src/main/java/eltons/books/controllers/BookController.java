package eltons.books.controllers;

import eltons.books.DTOs.BookDTO;
import eltons.books.models.Book;
import eltons.books.services.BookService;
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

    public BookController(BookService bookService) { this.bookS = bookService; }

    @GetMapping("/search/byTitle")
    public ResponseEntity<List<BookDTO>> searchBookByTitle(@RequestBody String title) {
        List<Book> foundBooks = bookS.searchBookByTitle(title);
        return ResponseEntity.ok(bookS.showFoundBooksAsDTO(foundBooks));
    }

    @GetMapping("/search/byAuthor")
    public ResponseEntity<List<BookDTO>> searchBooksByAuthor(@RequestBody String authorName) {
        List<Book> foundBooks = bookS.searchBooksByAuthor(authorName);
        return ResponseEntity.ok(bookS.showFoundBooksAsDTO(foundBooks));
    }

    @GetMapping("/getBook/all")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.status(HttpStatus.OK).body(bookS.showAllBooks());
    }

    @GetMapping("/getBook/byTitle")
    public ResponseEntity<BookDTO> getBookByTitle(@RequestBody String request) {
        try {
            BookDTO foundBook = bookS.getBookByTitleFromDatabase(request);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .body(foundBook);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    @Transactional
    @PostMapping("/save/byLastSearchedBooksIndex")
    public ResponseEntity<String> saveBook(@RequestBody Integer index) {
        try {
            Book bookToBeSaved = bookS.saveSelectedBook(index);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Book saved! -> " + bookToBeSaved.toString());
        } catch (MissingRequestValueException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing book index.");
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid book index.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save book.");
        }
    }

    @Transactional
    @DeleteMapping("/delete/byId")
    public ResponseEntity<String> deleteBookById(@RequestBody Long id) {
        try {
            Optional<Book> bookToDelete = bookS.getBookById(id);
            if (bookToDelete.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("Book deleted!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error to be described");
            }
        } catch (DataAccessException e) {
            throw e;
        }
    }
}
