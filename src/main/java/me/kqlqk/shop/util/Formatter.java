package me.kqlqk.shop.util;

public class Formatter {

    public static String formatAddressToShow(String address) {//Country;City;Street;Home;flat;postal-code
        String[] data = address.split(";");

        return "Country: " + data[0] + ";City: " + data[1] +
                ";Street: " + data[2] + ";House: " + data[3] +
                ";Flat: " + data[4] + ";Postal-code: " + data[5];
    }
}
