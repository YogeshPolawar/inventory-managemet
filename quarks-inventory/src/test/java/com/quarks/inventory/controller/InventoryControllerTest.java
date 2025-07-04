package com.quarks.inventory.controller;

import com.quarks.inventory.model.Item;
import com.quarks.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class InventoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventoryService service;

    @InjectMocks
    private InventoryController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testAddItem() throws Exception {
        Item item = new Item(null, "ItemA", 10, 0);
        Item savedItem = new Item(1L, "ItemA", 10, 0);
        when(service.createItem(any())).thenReturn(savedItem);

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("ItemA"));
    }

    @Test
    void testAddStock() throws Exception {
        Item updatedItem = new Item(1L, "ItemA", 15, 0);
        when(service.addStock(1L, 5)).thenReturn(updatedItem);

        mockMvc.perform(post("/api/items/1/stock")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStock").value(15));
    }

    @Test
    void testReserveStock() throws Exception {
        Item reservedItem = new Item(1L, "ItemA", 20, 5);
        when(service.reserveStock(1L, 5)).thenReturn(reservedItem);

        mockMvc.perform(post("/api/items/1/reserve")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservedStock").value(5));
    }

    @Test
    void testCancelReservation() throws Exception {
        Item canceledItem = new Item(1L, "ItemA", 20, 2);
        when(service.cancelReservation(1L, 3)).thenReturn(canceledItem);

        mockMvc.perform(post("/api/items/1/cancel")
                        .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservedStock").value(2));
    }

    @Test
    void testGetAvailability() throws Exception {
        when(service.getAvailableStock(1L)).thenReturn(10);

        mockMvc.perform(get("/api/items/1/available"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }
}
