package me.kqlqk.shop.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.kqlqk.shop.dto.OrderDTO;
import me.kqlqk.shop.model.enums.Colors;
import me.kqlqk.shop.model.enums.Sizes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class CookieUtil {
    public static final String OBJECT_DELIMITER = ".";
    public static final String VALUE_DELIMITER = "/";


    public static boolean containsOrderDTO(Cookie cookie) {
        if (cookie == null || cookie.getValue() == null) {
            return false;
        }

        return cookie.getValue().matches("\\d+" + VALUE_DELIMITER +
                "\\d+" + VALUE_DELIMITER +
                "\\w+" + VALUE_DELIMITER +
                "\\w+\\" + OBJECT_DELIMITER +
                "(\\d+" + VALUE_DELIMITER +
                "\\d+" + VALUE_DELIMITER +
                "\\w+" + VALUE_DELIMITER +
                "\\w+\\" + OBJECT_DELIMITER + ")*");
    }

    public static Cookie getCookieByName(@NonNull String name, @NonNull HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie c : request.getCookies()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }

        return null;
    }

    public static int getLastIdFromOrderDTOs(Cookie cookie) {
        if (!containsOrderDTO(cookie)) {
            return 0;
        }

        List<OrderDTO> orderDTOs = getOrderDTOs(cookie);

        OrderDTO noOrderDTO = new OrderDTO();
        noOrderDTO.setId(0);

        OrderDTO orderDTO = orderDTOs.stream()
                .reduce((a, b) -> a.getId() > b.getId() ? a : b)
                .orElse(noOrderDTO);

        return (int) orderDTO.getId();
    }

    public static List<OrderDTO> getOrderDTOs(Cookie cookie) {
        if (!containsOrderDTO(cookie)) {
            return Collections.emptyList();
        }

        List<OrderDTO> orders = new ArrayList<>();
        String[] orderDTOsString = cookie.getValue().split("\\" + OBJECT_DELIMITER);

        for (String orderDTOString : orderDTOsString) {
            String[] values = orderDTOString.split(VALUE_DELIMITER);
            OrderDTO orderDTO = new OrderDTO(Integer.parseInt(values[0]),
                    Long.parseLong(values[1]),
                    Colors.valueOf(values[2]),
                    Sizes.valueOf(values[3]),
                    false);
            orders.add(orderDTO);
        }

        return orders;
    }

    public static void deleteOrderDTO(Cookie cookie, OrderDTO orderDTO) {
        if (!containsOrderDTO(cookie)) {
            log.info("Cookie was not delete, because there is no cookie with that productBuyingDTO");
            return;
        }

        String value = cookie.getValue();

        String[] words = value.split(orderDTO.getId() + VALUE_DELIMITER +
                orderDTO.getProductId() + VALUE_DELIMITER +
                orderDTO.getColorName() + VALUE_DELIMITER +
                orderDTO.getSizeName() + "\\" + OBJECT_DELIMITER);

        StringBuilder newValue = new StringBuilder();

        Arrays.stream(words).forEach(newValue::append);

        cookie.setValue(newValue.toString());
        cookie.setPath("/");
        cookie.setMaxAge(36000);
    }
}
