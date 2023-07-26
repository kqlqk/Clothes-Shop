package me.kqlqk.shop.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserDTO {
    @Pattern(regexp = "^(|^[^\\s@]{3,}@[^\\s@]{2,}\\.[^\\s@]{2,})$", message = "Email should be valid")
    private String email;

    @Pattern(regexp = "(|[a-zA-Z]{2,50})", message = "Name should contains only letters between 8 and 50 characters")
    private String name;
}
