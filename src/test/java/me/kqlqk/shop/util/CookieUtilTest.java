package me.kqlqk.shop.util;

import jakarta.servlet.http.Cookie;
import me.kqlqk.shop.dto.ProductBuyingDTO;
import me.kqlqk.shop.model.enums.Colors;
import me.kqlqk.shop.model.enums.Sizes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class CookieUtilTest {

    @Test
    public void containsProductBuyingDTO_shouldReturnTrueIfContains() {
        Cookie cookie = new Cookie("product", "1/1/BLACK/L.2/2/GRAY/L.3/2/WHITE/XL.");

        assertThat(CookieUtil.containsProductBuyingDTO(cookie)).isTrue();

        cookie.setValue("aaaa");
        assertThat(CookieUtil.containsProductBuyingDTO(cookie)).isFalse();

        assertThat(CookieUtil.containsProductBuyingDTO(null)).isFalse();
    }

    @Test
    public void getLastIdFromProductBuyingDTOs_shouldReturnLastIdFromProductBuyingDTOs() {
        Cookie cookie = new Cookie("product", "1/1/BLACK/L.2/2/GRAY/L.3/2/WHITE/XL.");

        assertThat(CookieUtil.getLastIdFromProductBuyingDTOs(cookie)).isEqualTo(3);

        assertThat(CookieUtil.getLastIdFromProductBuyingDTOs(null)).isEqualTo(-1);
    }

    @Test
    public void getProductBuyingDTOs_shouldReturnProductBuyingDTOs() {
        Cookie cookie = new Cookie("product", "1/1/BLACK/L.2/2/GRAY/L.3/2/WHITE/XL.");

        List<ProductBuyingDTO> productBuyingDTOs = CookieUtil.getProductBuyingDTOs(cookie);

        assertThat(productBuyingDTOs).asList().hasSize(3);
        assertThat(productBuyingDTOs).asList().hasOnlyElementsOfType(ProductBuyingDTO.class);
        assertThat(productBuyingDTOs.get(0).getId()).isEqualTo(1);
        assertThat(productBuyingDTOs.get(0).getProductId()).isEqualTo(1);
        assertThat(productBuyingDTOs.get(0).getColor()).isEqualTo(Colors.BLACK);
        assertThat(productBuyingDTOs.get(0).getSize()).isEqualTo(Sizes.L);

    }

    @Test
    public void deleteProductBuyingDTO_shouldDeleteProductBuyingDTO() {
        Cookie cookie = new Cookie("product", "1/1/BLACK/L.2/2/GRAY/L.3/2/WHITE/XL.");
        ProductBuyingDTO productBuyingDTO = new ProductBuyingDTO(1, 1, Colors.BLACK, Sizes.L);

        CookieUtil.deleteProductBuyingDTO(cookie, productBuyingDTO);

        assertThat(cookie.getValue()).isEqualTo("2/2/GRAY/L.3/2/WHITE/XL.");
    }
}
