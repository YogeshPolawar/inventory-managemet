package com.quarks.inventory.service;

import com.quarks.inventory.model.Item;
import com.quarks.inventory.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock
    private ItemRepository repository;

    @Mock
    private RedisTemplate<String, Integer> redisTemplate;

    @InjectMocks
    private InventoryService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateItem() {
        Item item = new Item(null, "ItemA", 100, 0);
        when(repository.save(item)).thenReturn(new Item(1L, "ItemA", 100, 0));
        Item saved = service.createItem(item);
        assertNotNull(saved.getId());
        assertEquals("ItemA", saved.getName());
    }

    @Test
    void testAddStock() {
        Item item = new Item(1L, "ItemA", 10, 2);
        when(repository.findByIdInventoryItem(1L)).thenReturn(item);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        service.addStock(1L, 5);
        assertEquals(15, item.getTotalStock());
    }

    @Test
    void testReserveStock() {
        Item item = new Item(1L, "ItemA", 20, 5);
        when(repository.findByIdInventoryItem(1L)).thenReturn(item);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        Item reserved = service.reserveStock(1L, 5);
        assertEquals(10, reserved.getReservedStock());
        assertThrows(RuntimeException.class, () -> service.reserveStock(1L, 100));
    }

    @Test
    void testCancelReservation() {
        Item item = new Item(1L, "ItemA", 20, 5);
        when(repository.findByIdInventoryItem(1L)).thenReturn(item);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        Item canceled = service.cancelReservation(1L, 3);
        assertEquals(2, canceled.getReservedStock());
        assertThrows(RuntimeException.class, () -> service.cancelReservation(1L, 10));
    }

    @Test
    void testGetAvailableStock() {
        when(redisTemplate.opsForValue().get("stock:available:1")).thenReturn(null);
        Item item = new Item(1L, "ItemA", 20, 5);
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(redisTemplate.opsForValue().set("stock:available:1", 15)).thenReturn(null);

        int available = service.getAvailableStock(1L);
        assertEquals(15, available);
    }
}
