package com.quarks.inventory.service;

import com.quarks.inventory.model.Item;
import com.quarks.inventory.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ItemRepository repository;
    private final RedisTemplate<String, Integer> redisTemplate;

    private String getRedisKey(Long itemId) {
        return "stock:available:" + itemId;
    }

    @Transactional
    public Item createItem(Item item) {
        return repository.save(item);
    }

    @Transactional
    public Item addStock(Long id, int quantity) {
        Item item = repository.findByIdInventoryItem(id);
        item.setTotalStock(item.getTotalStock() + quantity);
        redisTemplate.opsForValue().set(getRedisKey(id), item.getTotalStock() - item.getReservedStock());
        return repository.save(item);
    }

    @Transactional
    public Item reserveStock(Long id, int quantity) {
        Item item = repository.findByIdInventoryItem(id);
        int available = item.getTotalStock() - item.getReservedStock();
        if (available < quantity) {
            throw new RuntimeException("Insufficient stock");
        }
        item.setReservedStock(item.getReservedStock() + quantity);
        redisTemplate.opsForValue().set(getRedisKey(id), available - quantity);
        return repository.save(item);
    }

    @Transactional
    public Item cancelReservation(Long id, int quantity) {
        Item item = repository.findByIdInventoryItem(id);
        int reserved = item.getReservedStock();
        if (quantity > reserved) {
            throw new RuntimeException("Cannot cancel more than reserved");
        }
        item.setReservedStock(reserved - quantity);
        redisTemplate.opsForValue().set(getRedisKey(id), item.getTotalStock() - item.getReservedStock());
        return repository.save(item);
    }

    public int getAvailableStock(Long id) {
        String key = getRedisKey(id);
        Integer cached = redisTemplate.opsForValue().get(key);
        if (cached != null) return cached;

        Optional<Item> itemOpt = repository.findById(id);
        if (itemOpt.isEmpty()) throw new RuntimeException("Item not found");

        Item item = itemOpt.get();
        int available = item.getTotalStock() - item.getReservedStock();
        redisTemplate.opsForValue().set(key, available);
        return available;
    }
}
