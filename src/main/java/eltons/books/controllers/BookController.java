package eltons.books.controllers;

import eltons.books.DTOs.BookDTO;
import eltons.books.DTOs.BookSavedDTO;
import eltons.books.DTOs.BookSearchDTO;
import eltons.books.models.Book;
import eltons.books.services.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/mainbookcase/bytitle")
    public ResponseEntity<BookDTO> getBookByTitle(@RequestParam String title) {
        BookDTO foundBook = bookS.getBookByTitleFromDatabase(title);
        return ResponseEntity.ok(foundBook);
    }

    @GetMapping("/search/withfilters")
    public ResponseEntity<List<BookDTO>> searchBooksWithFilters(
            @RequestParam(required = false, defaultValue = "") String intitle,
            @RequestParam(required = false, defaultValue = "") String inauthor,
            @RequestParam(required = false, defaultValue = "") String inpublisher,
            @RequestParam(required = false, defaultValue = "") String subject,
            @RequestParam(required = false) Long isbn
    ) {
        try {
            BookSearchDTO bookSDTO = new BookSearchDTO(intitle, inauthor, inpublisher, subject, isbn);
            return ResponseEntity.ok(bookS.searchBooksWithFilters(bookSDTO));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/savebook")
    public ResponseEntity<BookSavedDTO> saveBook(@RequestBody BookDTO dto) {
        try {
            Book saved = bookS.saveBook(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bookS.showSavedBook(saved));
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
