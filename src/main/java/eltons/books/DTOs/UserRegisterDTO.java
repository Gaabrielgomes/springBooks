package eltons.books.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UserRegisterDTO (
    @NotBlank(message = "Name cannot be null or blank")
    String name,
    @NotBlank(message = "Birth date cannot be null or blank")
    String birth,
    @NotBlank(message = "Gender cannot be null or blank")
    String gender,
    String selfDescription,
    @NotBlank(message = "Password cannot be null or blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,
    String role
){
    public UserRegisterDTO {
        selfDescription = (selfDescription == null || selfDescription.isEmpty()) ? "" : selfDescription;
        role = (role == null || role.isEmpty()) ? "" : role;
    }
}
