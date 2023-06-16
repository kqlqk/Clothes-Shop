package me.kqlqk.shop.util;

public class Formatter {

    public static String formatAddressToShow(String address) {
        String[] data = address.split(";");

        return "Country: " + data[0] + ";City: " + data[1] +
                ";Street: " + data[2] + ";House: " + data[3] +
                ";Flat: " + data[4] + ";Postal-code: " + data[5];
    }

    public static String formatAddressToSave(String country, String city, String street,
                                             String home, String flat, String postalCode) {
        return country + ";" +
                city + ";" +
                street + ";" +
                home + ";" +
                flat + ";" +
                postalCode;
    }
}
