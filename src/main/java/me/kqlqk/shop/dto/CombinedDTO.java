package me.kqlqk.shop.dto;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class CombinedDTO {
    @Valid
    private UserDTO userDTO;

    @Valid
    private AddressDTO addressDTO;

    public boolean allFieldsAreNullOrBlank() {
        return (userDTO.getEmail() == null || userDTO.getEmail().isBlank()) &&
                (userDTO.getName() == null || userDTO.getName().isBlank()) &&
                (addressDTO.getCountry() == null || addressDTO.getCountry().isBlank()) &&
                (addressDTO.getCity() == null || addressDTO.getCity().isBlank()) &&
                (addressDTO.getStreet() == null || addressDTO.getStreet().isBlank()) &&
                (addressDTO.getHouse() == null || addressDTO.getHouse().isBlank()) &&
                (addressDTO.getPostalCode() == null || addressDTO.getPostalCode().isBlank());
    }

}
