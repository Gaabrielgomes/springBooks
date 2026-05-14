package eltons.books.services;

import eltons.books.DTOs.BookDTO;
import eltons.books.DTOs.UserDTO;
import eltons.books.DTOs.UserRegisterDTO;
import eltons.books.components.ApiGetter;
import eltons.books.components.DataConverter;
import eltons.books.models.Book;
import eltons.books.models.BookcaseEntry;
import eltons.books.DTOs.BookcaseEntryDTO;
import eltons.books.models.enums.Gender;
import eltons.books.models.User;
import eltons.books.models.enums.ReadingStatus;
import eltons.books.repositories.BookRepository;
import eltons.books.repositories.BookcaseEntryRepository;
import eltons.books.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userR;
    private final BookRepository bookR;
    private final BookcaseEntryRepository bookCER;
    private final ApiGetter apiGetter;
    private final DataConverter converter;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userR, BookRepository bookR,
                       BookcaseEntryRepository bookCER, ApiGetter apiGetter,
                       DataConverter converter, PasswordEncoder passwordEncoder) {
        this.userR = userR;
        this.bookR = bookR;
        this.bookCER = bookCER;
        this.apiGetter = apiGetter;
        this.converter = converter;
        this.passwordEncoder = passwordEncoder;
    }

    private String APIURL = System.getenv("GOOGLE_BOOKS_BASE_URL");
    private String APIKEY = System.getenv("GOOGLE_BOOKS_API_KEY");

    public Boolean newUser(UserRegisterDTO userRegisterDTO) {
        Optional<User> userToBeSaved = userR.findByName(userRegisterDTO.getName());
        if (userToBeSaved.isPresent()) {
            return false;
        }

        User user = User.builder()
                .name(userRegisterDTO.getName())
                .birth(parseBirthDate(userRegisterDTO.getBirth()))
                .gender(Gender.fromString(userRegisterDTO.getGender()))
                .selfDescription(userRegisterDTO.getSelfDescription())
                .password(passwordEncoder.encode(userRegisterDTO.getPassword()))
                .role(userRegisterDTO.getRole())
                .build();
        
        userR.save(user);
        return true;
    }

    public UserDTO showUserFromId(Long id) {
        Optional<User> userToBeShown = userR.findById(id);
        if (userToBeShown.isPresent()) {
            return convertToUserDTO(userToBeShown.get());
        }
        return convertToUserDTO(new User());
    }

    @Transactional(readOnly = true)
    public List<BookcaseEntryDTO> showUserBookcase(User user) {
        return bookCER.findAllByUser(user).stream()
                .map(entry -> new BookcaseEntryDTO(
                        entry.getId(),
                        convertBookToDTO(entry.getBook()),
                        entry.getReadingStatus().name(),
                        entry.getReview(),
                        entry.getAddedAt()
                ))
                .toList();
    }

    public BookcaseEntryDTO showBookFromUserBookcase(User user, Long entryId) {
        return user.getBookcase().stream()
                .filter(entry -> entry.getId().equals(entryId))
                .findFirst()
                .map(entry -> new BookcaseEntryDTO(
                        entry.getId(),
                        convertBookToDTO(entry.getBook()),
                        entry.getReadingStatus().name(),
                        entry.getReview(),
                        entry.getAddedAt()
                ))
                .orElseThrow(() -> new EntityNotFoundException("Book not found in your bookcase."));
    }

    @Transactional
    public BookcaseEntry addBookToBookcase(User u, Long bookId) {
        Book b = bookR.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));

        boolean alreadyInBookcase = bookCER.existsByUserAndBook(u, b);
        if (alreadyInBookcase) {
            throw new IllegalStateException("Book already in your bookcase.");
        }

        BookcaseEntry entry = new BookcaseEntry();
        entry.setUser(u);
        entry.setBook(b);
        entry.setReadingStatus(ReadingStatus.WANT_TO_READ);

        return bookCER.save(entry);
    }

    @Transactional
    public Boolean addReview(User u, Long bookId, String review) {
        Book b = bookR.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));

        boolean entryExistance = bookCER.existsByUserAndBook(u, b);

        if (entryExistance) {
            BookcaseEntry bookcaseEntry = bookCER.getByUserAndBook(u, b);
            bookcaseEntry.setReview(review);
            return true;
        }

        throw new EntityNotFoundException("Book not found in your bookcase.");
    }

    @Transactional
    public boolean removeFromBookcase(User u, Long bookId) {
        Book b = bookR.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));

        boolean entryExistance = bookCER.existsByUserAndBook(u, b);

        if (entryExistance) {
            BookcaseEntry bookcaseEntry = bookCER.getByUserAndBook(u, b);
            bookCER.delete(bookcaseEntry);
            return true;
        }

        throw new EntityNotFoundException("Book not found in your bookcase.");
    }

    private UserDTO convertToUserDTO(User u) {
        return new UserDTO(
                u.getName(),
                u.getBirth().toString(),
                u.getGender().toString(),
                u.getSelfDescription()
        );
    }

    private BookDTO convertBookToDTO(Book b) {
        return new BookDTO(
                b.getId(),
                b.getTitle(),
                b.getAuthor().getName(),
                b.getDescription(),
                b.getPublishedDate(),
                b.getPagesNumber(),
                b.getCoverLink()
        );
    }

    private LocalDate parseBirthDate(String date) {
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
}
