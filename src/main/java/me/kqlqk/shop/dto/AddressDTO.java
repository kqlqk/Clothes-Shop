package me.kqlqk.shop.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    @Pattern(regexp = "(|.{1,200})", message = "Country should be between 1 and 200 characters")
    private String country;

    @Pattern(regexp = "(|.{1,200})", message = "City should be between 1 and 200 characters")
    private String city;

    @Pattern(regexp = "(|.{1,200})", message = "Street should be between 1 and 200 characters")
    private String street;

    @Pattern(regexp = "(|.{1,200})", message = "House should be between 1 and 200 characters")
    private String house;

    @Pattern(regexp = "(|.{1,200})", message = "Postal code should be between 1 and 200 characters")
    private String postalCode;
}
