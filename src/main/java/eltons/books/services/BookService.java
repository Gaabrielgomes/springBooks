package eltons.books.services;

import eltons.books.DTOs.BookDTO;
import eltons.books.DTOs.BookSaveRequestDTO;
import eltons.books.components.ApiGetter;
import eltons.books.components.DataConverter;
import eltons.books.models.Author;
import eltons.books.models.Book;
import eltons.books.models.BookResponse;
import eltons.books.models.ImageLinks;
import eltons.books.repositories.AuthorRepository;
import eltons.books.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepository bookR;
    @Autowired
    private AuthorRepository authorR;
    @Autowired
    private ApiGetter apiGetter;
    @Autowired
    private DataConverter converter;

    private String APIURL = System.getenv("GOOGLE_BOOKS_BASE_URL");
    private String APIKEY = System.getenv("GOOGLE_BOOKS_API_KEY");


    public List<BookDTO> showAllBooks() {
        return bookR.getAllBooks();
    }

    public Optional<Book> getBookById(Long id) {
        return bookR.findById(id);
    }

    public BookDTO getBookByTitleFromDatabase(String title) {
        Optional<Book> bookToBeShown = bookR.findByTitleIgnoreCase(title);
        if (bookToBeShown.isPresent()) {
            return convertBookToDTO(bookToBeShown.get());
        }
        return null;
    }

    public Book saveSelectedBook(BookSaveRequestDTO dto) {
        Optional<Book> existing = bookR.findByTitle(dto.title());
        if (existing.isPresent()) {
            return existing.get();
        }

        Book book = new Book(
            dto.title(),
            verifyAuthor(dto.author()),
            dto.description(),
            parsePublishedDate(dto.publishedDate()),
            dto.pagesNumber(),
            dto.coverLink()
        );

        return bookR.save(book);
    }

    public List<Book> searchBookByTitle(String title) {
        String titleWithPlusSign = title.toLowerCase().replace(" ", "+");
        String encodedTitle = URLEncoder.encode(titleWithPlusSign, StandardCharsets.UTF_8);
        var json = apiGetter.getData(APIURL + encodedTitle + APIKEY);
        BookResponse foundBooks = converter.getData(json, BookResponse.class);
        return convertToBook(foundBooks);
    }

    public List<Book> searchBooksByAuthor(String authorName) {
        var json = apiGetter.getData(APIURL + "inauthor:" + authorName + APIKEY);
        BookResponse bookResponse = converter.getData(json, BookResponse.class);
        List<Book> books = convertToBook(bookResponse);

        if (books == null || books.isEmpty()) {
            return null;
        }

        return books;
    }

    public List<BookDTO> showFoundBooksAsDTO(List<Book> books) {
        return books.stream()
                .map(b -> convertBookToDTO(b))
                .collect(Collectors.toList());
    }

    private BookDTO convertBookToDTO(Book b) {
        return new BookDTO(
                b.getTitle(),
                b.getAuthor().getName(),
                b.getDescription(),
                b.getPublishedDate(),
                b.getPagesNumber(),
                b.getCoverLink()
        );
    }

    private List<BookDTO> convertToBookDTO(BookResponse books) {
        return books.getItems().stream()
                .map(items -> items.getVolumeInfo())
                .map(volume -> new BookDTO(
                        verifyTitle(volume.getTitle()),
                        verifyAuthorString(getFirstAuthor(volume.getAuthors())),
                        verifyDescription(volume.getDescription()),
                        verifyPublishedDate(volume.getPublishedDate()),
                        verifyPagesNumber(volume.getPageCount()),
                        verifyCoverLink(volume.getImageLinks())
                ))
                .collect(Collectors.toList());
    }

    private List<Book> convertToBook(BookResponse books) {
        return books.getItems().stream()
                .map(item -> item.getVolumeInfo())
                .map(volume -> new Book(
                        verifyTitle(volume.getTitle()),
                        verifyAuthor(getFirstAuthor(volume.getAuthors())),
                        verifyDescription(volume.getDescription()),
                        verifyPublishedDate(volume.getPublishedDate()),
                        verifyPagesNumber(volume.getPageCount()),
                        verifyCoverLink(volume.getImageLinks())
                ))
                .collect(Collectors.toList());
    }

    private String verifyTitle(String title) {
        if (title == null || title.isBlank()) {
            return "No title found";
        }
        return title;
    }

    private Author verifyAuthor(String author) {
        if (author == null || author.isBlank()) {
            return new Author();
        }
        Optional<Author> a = authorR.findByName(author);

        if (a.isPresent()) {
            return a.get();
        } else {
            return authorR.save(new Author(author));
        }
    }

    private String verifyAuthorString(String author) {
        if (author == null || author.isBlank()) {
            return "No author found";
        }

        Optional<Author> a = authorR.findByName(author);

        if (a.isPresent()) {
            return a.get().getName();
        } else {
            return "No author found.";
        }
    }

    private String verifyDescription(String description) {
        if (description == null || description.isBlank()) {
            return "No description found.";
        }
        return description;
    }

    private LocalDate verifyPublishedDate(String publishedDate) {
        if (publishedDate == null) {
            return LocalDate.now();
        }
        return parsePublishedDate(publishedDate);
    }

    private Integer verifyPagesNumber(Integer pagesNumber) {
        if (pagesNumber == null || pagesNumber <= 0) {
            return 1;
        }
        return pagesNumber;
    }

    private String verifyCoverLink(ImageLinks imageLinks) {
        if (imageLinks == null) {
            return "No cover link found.";
        }
        return imageLinks.getThumbnail();
    }

    private String getFirstAuthor(List<String> authors) {
        if (authors == null || authors.isEmpty()) {
            return "Unknown Author";
        }
        return authors.getFirst();
    }

    private LocalDate parsePublishedDate(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        try {
            if (date.length() == 4) {
                return LocalDate.of(Integer.parseInt(date), 1, 1);
            }

            if (date.length() == 7) {
                return LocalDate.parse(date + "-01");
            }

            return LocalDate.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteBookById(Long id) {
        bookR.deleteById(id);
    }
}
