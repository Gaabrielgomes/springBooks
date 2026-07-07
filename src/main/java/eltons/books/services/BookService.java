package eltons.books.services;

import eltons.books.DTOs.BookDTO;
import eltons.books.DTOs.BookSavedDTO;
import eltons.books.DTOs.BookSearchDTO;
import eltons.books.components.ApiGetter;
import eltons.books.components.DataConverter;
import eltons.books.models.*;
import eltons.books.repositories.AuthorRepository;
import eltons.books.repositories.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookR;
    private final AuthorRepository authorR;
    private final ApiGetter apiGetter;
    private final DataConverter converter;
    private final String apiUrl;
    private final String apiKey;

    public BookService(BookRepository bookR,
                       AuthorRepository authorR,
                       ApiGetter apiGetter,
                       DataConverter converter,
                       @Value("${GOOGLE_BOOKS_BASE_URL}") String apiUrl,
                       @Value("${GOOGLE_BOOKS_API_KEY}") String apiKey) {
        this.bookR = bookR;
        this.authorR = authorR;
        this.apiGetter = apiGetter;
        this.converter = converter;

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

    public BookDTO getBookByTitleFromDatabase(String title) {
        return bookR.findByTitleIgnoreCase(title)
                .map(this::convertBookToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));
    }

    public Book saveBook(BookDTO dto) {
        return bookR.findByTitleIgnoreCaseAndAuthorNameIgnoreCase(dto.title(), dto.author())
                .orElseGet(() -> bookR.save(convertBookDTOToBook(dto)));
    }

    public void deleteBookById(Long id) {
        bookR.deleteById(id);
    }

    public List<BookDTO> searchBooksWithFilters(BookSearchDTO bookSDTO) {

        StringBuilder parameters = new StringBuilder();

        if (!bookSDTO.intitle().isEmpty()) {
            String encodedTitle = "intitle:" + bookSDTO.intitle().toLowerCase().replace(" ", "+");
            parameters.append(encodedTitle);
        }
        if (!bookSDTO.inauthor().isEmpty()) {
            parameters.append("+inauthor:")
                      .append(bookSDTO.inauthor().toLowerCase().replace(" ", "+"));
        }
        if (!bookSDTO.inpublisher().isEmpty()) parameters.append("+inpublisher:").append(bookSDTO.inpublisher());
        if (!bookSDTO.subject().isEmpty()) parameters.append("+subject:").append(bookSDTO.subject());
        if (bookSDTO.isbn() != 0L) parameters.append("+isbn:").append(bookSDTO.isbn());

        if (parameters.toString().startsWith("+")) parameters.deleteCharAt(0);

        String requestString = apiUrl + parameters + "&printType=books" + "&maxResults=40" + apiKey;

        var json = apiGetter.getData(requestString);

        BookResponse bookResponse = converter.getData(json, BookResponse.class);

        if (bookResponse != null) {
            if (bookResponse.getItems() != null && !bookResponse.getItems().isEmpty()) {
                List<Book> convertedBooks = convertToBook(bookResponse);
                return showFoundBooksAsDTO(convertedBooks);
            }
            throw new EntityNotFoundException("No books found for the search.");
        }
        throw new EntityNotFoundException("No books found for the search.");
    }

    public List<BookDTO> showFoundBooksAsDTO(List<Book> books) {
        return books.stream()
                .map(this::convertBookToDTO)
                .toList();
    }

    public BookSavedDTO showSavedBook(Book b) {
        return new BookSavedDTO(
                b.getId(),
                b.getTitle(),
                b.getAuthor().getName(),
                b.getCoverLink()
        );
    }

    private BookDTO convertBookToDTO(Book b) {
        return new BookDTO(
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
