package me.kqlqk.shop.util;

import me.kqlqk.shop.dto.AddressDTO;
import me.kqlqk.shop.exception.AddressException;
import me.kqlqk.shop.model.user.Address;

public class Formatter {

    public static Address convertToSave(AddressDTO addressDTO, Address addressDb) {
        if (addressDb == null) {
            if (addressDTO.getCountry() == null || addressDTO.getCountry().isBlank() ||
                    addressDTO.getCity() == null || addressDTO.getCity().isBlank() ||
                    addressDTO.getStreet() == null || addressDTO.getStreet().isBlank() ||
                    addressDTO.getHouse() == null || addressDTO.getHouse().isBlank() ||
                    addressDTO.getPostalCode() == null || addressDTO.getPostalCode().isBlank()) {
                throw new AddressException("AddressDTO cannot contains null fields");
            }

            return new Address(addressDTO.getCountry(),
                    addressDTO.getCity(),
                    addressDTO.getStreet(),
                    addressDTO.getHouse(),
                    addressDTO.getPostalCode());
        }

        Address address = new Address();
        address.setId(addressDb.getId());
        address.setCountry(addressDTO.getCountry() == null || addressDTO.getCountry().isBlank() ? addressDb.getCountry() : addressDTO.getCountry());
        address.setCity(addressDTO.getCity() == null || addressDTO.getCity().isBlank() ? addressDb.getCity() : addressDTO.getCity());
        address.setStreet(addressDTO.getStreet() == null || addressDTO.getStreet().isBlank() ? addressDb.getStreet() : addressDTO.getStreet());
        address.setHouse(addressDTO.getHouse() == null || addressDTO.getHouse().isBlank() ? addressDb.getHouse() : addressDTO.getHouse());
        address.setPostalCode(addressDTO.getPostalCode() == null || addressDTO.getPostalCode().isBlank() ? addressDb.getPostalCode() : addressDTO.getPostalCode());

        return address;
    }
}
