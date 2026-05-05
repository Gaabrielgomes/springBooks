package eltons.books.DTOs;

import eltons.books.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    @NonNull
    private String name;
    private String birth;
    private String gender;
    private String selfDescription;
    @NonNull
    private String password;
    private String role;
}
