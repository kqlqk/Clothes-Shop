package me.kqlqk.shop.controller;

import me.kqlqk.shop.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ControllerTest
public class MainControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getMainPage_shouldReturnMainPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("MainPage"));
    }
}
