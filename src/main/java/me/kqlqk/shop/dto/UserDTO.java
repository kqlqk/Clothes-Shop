package me.kqlqk.shop.dto;

import lombok.Data;
import me.kqlqk.shop.model.OrderHistory;
import me.kqlqk.shop.model.User;
import me.kqlqk.shop.util.Formatter;

import java.util.List;

@Data
public class UserDTO {
    private String name;
    private String email;
    private String country;
    private String city;
    private String street;
    private String home;
    private String flat;
    private String postalCode;
    private List<OrderHistory> orderHistoryList;

    public User convertToUser(User userDb) {
        User user = new User();
        user.setName(name == null ? userDb.getName() : name);
        user.setEmail(email == null ? userDb.getEmail() : email);

        String[] addressDb = null;
        if (userDb.getAddress() != null) {
            addressDb = userDb.getAddress().split(";"); // TODO: 10/07/2023 fix addresses
        }

        if (addressDb == null || addressDb.length < 7 ||
                addressDb[0] == null || addressDb[0].isBlank() ||
                addressDb[1] == null || addressDb[1].isBlank() ||
                addressDb[2] == null || addressDb[2].isBlank() ||
                addressDb[3] == null || addressDb[3].isBlank() ||
                addressDb[4] == null || addressDb[4].isBlank() ||
                addressDb[5] == null || addressDb[5].isBlank()) {
            if (country != null && !country.isBlank() ||
                    city != null && !city.isBlank() ||
                    street != null && !street.isBlank() ||
                    home != null && !home.isBlank() ||
                    flat != null && !flat.isBlank() ||
                    postalCode != null && !postalCode.isBlank()) {
                user.setAddress(Formatter.formatAddressToSave(country, city, street, home, flat, postalCode));
            }
        }
        else {
            user.setAddress(Formatter.formatAddressToSave(
                    country == null || country.isBlank() ? addressDb[0] : country,
                    city == null || city.isBlank() ? addressDb[1] : city,
                    street == null || street.isBlank() ? addressDb[2] : street,
                    home == null || home.isBlank() ? addressDb[3] : home,
                    flat == null || flat.isBlank() ? addressDb[4] : flat,
                    postalCode == null || postalCode.isBlank() ? addressDb[5] : postalCode));
        }

        return user;
    }
}
