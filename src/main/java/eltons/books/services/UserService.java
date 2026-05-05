package eltons.books.services;

import eltons.books.DTOs.UserDTO;
import eltons.books.DTOs.UserRegisterDTO;
import eltons.books.components.ApiGetter;
import eltons.books.components.DataConverter;
import eltons.books.enums.Gender;
import eltons.books.models.User;
import eltons.books.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userR;
    @Autowired
    private ApiGetter apiGetter;
    @Autowired
    private DataConverter converter;
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
                .password(userRegisterDTO.getPassword())
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

    public UserDTO showUserFromName(String name) {
        Optional<User> userToBeShown = userR.findByName(name);
        if (userToBeShown.isPresent()) {
            return convertToUserDTO(userToBeShown.get());
        }
        return convertToUserDTO(new User());
    }

    public String saveBookToUser(Long userId, Long bookId) {
        return "";
    }

    private UserDTO convertToUserDTO(User u) {
        return new UserDTO(
                u.getName(),
                u.getBirth().toString(),
                u.getGender().toString(),
                u.getSelfDescription()
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
