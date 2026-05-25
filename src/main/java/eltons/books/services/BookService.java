package eltons.books.services;

import eltons.books.DTOs.BookDTO;
import eltons.books.components.ApiGetter;
import eltons.books.components.DataConverter;
import eltons.books.models.*;
import eltons.books.repositories.AuthorRepository;
import eltons.books.repositories.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookR;
    private final AuthorRepository authorR;
    private final ApiGetter apiGetter;
    private final DataConverter converter;
    private final String apiUrl;
    private final String apiKey;

    public BookService(BookRepository bookR, AuthorRepository authorR,
                       ApiGetter apiGetter, DataConverter converter) {
        this.bookR = bookR;
        this.authorR = authorR;
        this.apiGetter = apiGetter;
        this.converter = converter;

        String apiUrl = System.getenv("GOOGLE_BOOKS_BASE_URL");
        String apiKey = System.getenv("GOOGLE_BOOKS_API_KEY");

        if (apiUrl == null || apiUrl.isBlank())
            throw new IllegalStateException("Variable GOOGLE_BOOKS_BASE_URL not configured.");
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalStateException("Variable GOOGLE_BOOKS_API_KEY not configured.");

        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public List<BookDTO> showAllBooks() {
        return bookR.getAllBooks();
    }

    public Optional<Book> getBookById(Long id) {
        return bookR.findById(id);
    }

    public BookDTO getBookByTitleFromDatabase(String title) {
        return bookR.findByTitleIgnoreCase(title)
                .map(this::convertBookToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));
    }

    public Book saveBook(BookDTO dto) {
        return bookR.findByTitle(dto.title())
                .orElseGet(() -> bookR.save(convertBookDTOToBook(dto)));
    }

    public void deleteBookById(Long id) {
        bookR.deleteById(id);
    }

    public List<Book> searchBookByTitle(String title) {
        String encodedTitle = URLEncoder.encode(
                title.toLowerCase().replace(" ", "+"), StandardCharsets.UTF_8
        );
        var json = apiGetter.getData(apiUrl + encodedTitle + apiKey);
        BookResponse bookResponse = converter.getData(json, BookResponse.class);
        List<Book> convertedBooks = convertToBook(bookResponse);

        if (bookResponse == null) {
            List<Item> items = bookResponse.getItems();
            if (items != null || !items.isEmpty()) {
                throw new EntityNotFoundException("No books found for the search.");
            }
        }

        return convertedBooks;
    }

    public List<Book> searchBooksByAuthor(String authorName) {
        String correctedAuthorName = authorName.toLowerCase().replace(" ", "+");
        var json = apiGetter.getData(apiUrl + "+inauthor:" + correctedAuthorName + apiKey);
        BookResponse bookResponse = converter.getData(json, BookResponse.class);

        if (bookResponse == null) {
            List<Item> items = bookResponse.getItems();
            if (items != null || !items.isEmpty()) {
                throw new EntityNotFoundException("No books found for this author.");
            }
        }
        return convertToBook(bookResponse);
    }

    public List<BookDTO> showFoundBooksAsDTO(List<Book> books) {
        return books.stream()
                .map(this::convertBookToDTO)
                .toList();
    }

    private BookDTO convertBookToDTO(Book b) {
        return new BookDTO(
                b.getId(),
                b.getIsbn(),
                b.getTitle(),
                b.getAuthor().getName(),
                b.getDescription(),
                b.getPublishedDate(),
                b.getPagesNumber(),
                b.getCoverLink()
        );
    }

    private Book convertBookDTOToBook(BookDTO dto) {
        return new Book(
                dto.isbn(),
                verifyTitle(dto.title()),
                verifyAuthor(dto.author()),
                verifyDescription(dto.description()),
                dto.publishedDate(),
                verifyPagesNumber(dto.pagesNumber()),
                dto.coverLink()
        );
    }

    private List<Book> convertToBook(BookResponse books) {
        return books.getItems().stream()
                .map(Item::getVolumeInfo)
                .map(volume -> new Book(
                        verifyIsbn(volume.getIndustryIdentifiers()),
                        verifyTitle(volume.getTitle()),
                        verifyAuthor(getFirstAuthor(volume.getAuthors())),
                        verifyDescription(volume.getDescription()),
                        verifyPublishedDate(volume.getPublishedDate()),
                        verifyPagesNumber(volume.getPageCount()),
                        verifyCoverLink(volume.getImageLinks())
                ))
                .collect(Collectors.toList());
    }

    private Long verifyIsbn(List<Identifiers> identifiers) {
        if (identifiers == null || identifiers.isEmpty()) return null;

        return identifiers.stream()
                .filter(i -> i.getType().equalsIgnoreCase("isbn_13"))
                .findFirst()
                .map(i -> Long.parseLong(i.getIdentifier()))
                .orElse(null);
    }

    private String verifyTitle(String title) {
        if (title == null || title.isBlank()) return "No title found.";
        return title;
    }

    private Author verifyAuthor(String author) {
        if (author == null || author.isBlank()) return new Author();
        return authorR.findByName(author)
                .orElseGet(() -> authorR.save(new Author(author)));
    }

    private String verifyDescription(String description) {
        if (description == null || description.isBlank()) return "No description found.";
        return description;
    }

    private LocalDate verifyPublishedDate(String publishedDate) {
        if (publishedDate == null) return LocalDate.now();
        return parsePublishedDate(publishedDate);
    }

    private Integer verifyPagesNumber(Integer pagesNumber) {
        if (pagesNumber == null || pagesNumber <= 0) return 1;
        return pagesNumber;
    }

    private String verifyCoverLink(ImageLinks imageLinks) {
        if (imageLinks == null) return "No cover link found.";
        return imageLinks.getThumbnail();
    }

    private String getFirstAuthor(List<String> authors) {
        if (authors == null || authors.isEmpty()) return "Unknown Author";
        return authors.getFirst();
    }

    private LocalDate parsePublishedDate(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            if (date.length() == 4) return LocalDate.of(Integer.parseInt(date), 1, 1);
            if (date.length() == 7) return LocalDate.parse(date + "-01");
            return LocalDate.parse(date);
        } catch (Exception e) {
            return null;
        }
    }
}
