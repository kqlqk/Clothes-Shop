package me.kqlqk.shop.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import me.kqlqk.shop.dto.OrderDTO;
import me.kqlqk.shop.model.enums.Colors;
import me.kqlqk.shop.model.enums.Sizes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CookieUtilTest {
    @Mock
    private HttpServletRequest request;

    @Test
    public void containsOrderDTO_shouldReturnTrueIfContains() {
        Cookie cookie = new Cookie("product", "1/1/BLACK/L.2/2/GRAY/L.3/2/WHITE/XL.");

        assertThat(CookieUtil.containsOrderDTO(cookie)).isTrue();

        cookie.setValue("aaaa");
        assertThat(CookieUtil.containsOrderDTO(cookie)).isFalse();

        assertThat(CookieUtil.containsOrderDTO(null)).isFalse();
    }

    @Test
    public void getCookieByName_shouldReturnCookieByName() {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("product", "value")});

        Cookie cookie = CookieUtil.getCookieByName("product", request);

        assertThat(cookie.getName()).isEqualTo("product");
        assertThat(cookie.getValue()).isEqualTo("value");

        assertThat(CookieUtil.getCookieByName("invalid", request)).isNull();
    }


    @Test
    public void getLastIdFromOrderDTOs_shouldReturnLastIdFromOrderDTOs() {
        Cookie cookie = new Cookie("product", "1/1/BLACK/L.2/2/GRAY/L.3/2/WHITE/XL.");

        assertThat(CookieUtil.getLastIdFromOrderDTOs(cookie)).isEqualTo(3);

        assertThat(CookieUtil.getLastIdFromOrderDTOs(null)).isEqualTo(0);
    }

    @Test
    public void getOrderDTOs_shouldReturnOrderDTOs() {
        Cookie cookie = new Cookie("product", "1/1/BLACK/L.2/2/GRAY/L.3/2/WHITE/XL.");

        List<OrderDTO> orderDTOs = CookieUtil.getOrderDTOs(cookie);

        assertThat(orderDTOs).asList().hasSize(3);
        assertThat(orderDTOs).asList().hasOnlyElementsOfType(OrderDTO.class);
        assertThat(orderDTOs.get(0).getId()).isEqualTo(1);
        assertThat(orderDTOs.get(0).getProductId()).isEqualTo(1);
        assertThat(orderDTOs.get(0).getColorName()).isEqualTo(Colors.BLACK);
        assertThat(orderDTOs.get(0).getSizeName()).isEqualTo(Sizes.L);


        cookie.setValue("a");

        assertThat(CookieUtil.getOrderDTOs(cookie)).asList().isEmpty();
    }

    @Test
    public void deleteOrderDTO_shouldDeleteOrderDTO() {
        Cookie cookie = new Cookie("product", "1/1/BLACK/L.2/2/GRAY/L.3/2/WHITE/XL.");
        OrderDTO orderDTO = new OrderDTO(1, 1, Colors.BLACK, Sizes.L, false);

        CookieUtil.deleteOrderDTO(cookie, orderDTO);

        assertThat(cookie.getValue()).isEqualTo("2/2/GRAY/L.3/2/WHITE/XL.");
    }
}
