package com.quarks.inventory.controller;

import com.quarks.inventory.model.Item;
import com.quarks.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public Item addItem(@RequestBody Item item) {
        return inventoryService.createItem(item);
    }

    @PostMapping("/{id}/stock")
    public Item addStock(@PathVariable Long id, @RequestParam int quantity) {
        return inventoryService.addStock(id, quantity);
    }

    @PostMapping("/{id}/reserve")
    public Item reserve(@PathVariable Long id, @RequestParam int quantity) {
        return inventoryService.reserveStock(id, quantity);
    }

    @PostMapping("/{id}/cancel")
    public Item cancel(@PathVariable Long id, @RequestParam int quantity) {
        return inventoryService.cancelReservation(id, quantity);
    }

    @GetMapping("/{id}/available")
    public int getAvailability(@PathVariable Long id) {
        return inventoryService.getAvailableStock(id);
    }
}
