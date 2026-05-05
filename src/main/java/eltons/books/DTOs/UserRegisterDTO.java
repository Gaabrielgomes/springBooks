package eltons.books.DTOs;

import lombok.*;


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
