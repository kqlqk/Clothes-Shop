package me.kqlqk.shop.util;

import jakarta.servlet.http.Cookie;
import me.kqlqk.shop.dto.ProductBuyingDTO;
import me.kqlqk.shop.model.enums.Colors;
import me.kqlqk.shop.model.enums.Sizes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CookieUtil {
    public static final String OBJECT_DELIMITER = ".";
    public static final String VALUES_DELIMITER = "/";


    public static boolean containsProductBuyingDTO(Cookie cookie) { // TODO add @NonNUll
        if (cookie == null || cookie.getValue() == null) {
            return false; // TODO throw exception
        }

        return cookie.getValue().matches("\\d+" + VALUES_DELIMITER +
                "\\d+" + VALUES_DELIMITER +
                "\\w+" + VALUES_DELIMITER +
                "\\w+\\" + OBJECT_DELIMITER +
                "(\\d+" + VALUES_DELIMITER +
                "\\d+" + VALUES_DELIMITER +
                "\\w+" + VALUES_DELIMITER +
                "\\w+\\" + OBJECT_DELIMITER + ")*");
    }

    public static int getLastIdFromProductBuyingDTOs(Cookie cookie) {
        if (!containsProductBuyingDTO(cookie)) {
            return -1; // TODO throw exception
        }

        List<ProductBuyingDTO> productBuyingDTOs = getProductBuyingDTOs(cookie);

        ProductBuyingDTO noProductBuyingDTO = new ProductBuyingDTO();
        noProductBuyingDTO.setId(0);

        ProductBuyingDTO productBuyingDTO = productBuyingDTOs.stream()
                .reduce((a, b) -> a.getId() > b.getId() ? a : b)
                .orElse(noProductBuyingDTO);

        return productBuyingDTO.getId();
    }

    public static List<ProductBuyingDTO> getProductBuyingDTOs(Cookie cookie) {
        if (!containsProductBuyingDTO(cookie)) {
            return Collections.emptyList();
        }

        List<ProductBuyingDTO> productBuyingDTOs = new ArrayList<>();
        String[] productBuyingDTOsString = cookie.getValue().split("\\" + OBJECT_DELIMITER);

        for (String productBuyingDTOString : productBuyingDTOsString) {
            String[] values = productBuyingDTOString.split(VALUES_DELIMITER);
            ProductBuyingDTO productBuyingDTO = new ProductBuyingDTO(Integer.parseInt(values[0]), Long.parseLong(values[1]), Colors.valueOf(values[2]), Sizes.valueOf(values[3]));
            productBuyingDTOs.add(productBuyingDTO);
        }

        return productBuyingDTOs;
    }

    public static void deleteProductBuyingDTO(Cookie cookie, ProductBuyingDTO productBuyingDTO) {
        if (!containsProductBuyingDTO(cookie)) {
            return; // TODO throw exception
        }

        String value = cookie.getValue();

        String[] words = value.split(productBuyingDTO.getId() + VALUES_DELIMITER +
                productBuyingDTO.getProductId() + VALUES_DELIMITER +
                productBuyingDTO.getColor() + VALUES_DELIMITER +
                productBuyingDTO.getSize() + "\\" + OBJECT_DELIMITER);

        StringBuilder newValue = new StringBuilder();

        Arrays.stream(words).forEach(newValue::append);

        cookie.setValue(newValue.toString());
        cookie.setPath("/");
        cookie.setMaxAge(36000);
    }
}
