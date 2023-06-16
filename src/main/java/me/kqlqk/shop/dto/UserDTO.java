package me.kqlqk.shop.dto;

import lombok.Data;
import me.kqlqk.shop.model.OrderHistory;
import me.kqlqk.shop.model.User;
import me.kqlqk.shop.util.Formatter;

import java.util.List;

@Data
public class UserDTO {
    private String name;
    private String country;
    private String city;
    private String street;
    private String home;
    private String flat;
    private String postalCode;
    private List<OrderHistory> orderHistoryList;

    public User convertToUser(User userDb) {
        User user = new User();
        user.setName(name);

        String[] addressDb = userDb.getAddress().split(";");

        user.setAddress(Formatter.formatAddressToSave(
                country == null || country.isBlank() ? addressDb[0] : country,
                city == null || city.isBlank() ? addressDb[1] : city,
                street == null || street.isBlank() ? addressDb[2] : street,
                home == null || home.isBlank() ? addressDb[3] : home,
                flat == null || flat.isBlank() ? addressDb[4] : flat,
                postalCode == null || postalCode.isBlank() ? addressDb[5] : postalCode));

        return user;
    }
}
