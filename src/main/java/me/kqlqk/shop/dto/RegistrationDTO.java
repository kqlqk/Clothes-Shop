package me.kqlqk.shop.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    @Pattern(regexp = "^[^\\s@]{3,}@[^\\s@]{2,}\\.[^\\s@]{2,}$", message = "Email should be valid")
    private String email;

    @Pattern(regexp = "[a-zA-Z]{2,50}", message = "Name should contains only letters, between 8 and 50 characters")
    private String name;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,50}$",
            message = "Password should be between 8 and 50 characters, minimum 1 number, both lower and uppercase letters")
    private String password;
}
