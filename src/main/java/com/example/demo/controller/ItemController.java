package com.example.demo.controller;

import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Simple health check endpoint confirming the API is reachable.
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "springboot-postgres-api");
        response.put("timestamp", Instant.now().toString());
        response.put("itemCount", itemRepository.count());
        return response;
    }

    /**
     * Retrieve all items stored in the PostgreSQL database.
     */
    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /**
     * Retrieve a specific item by its primary key ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Persist a new item to the database.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item createItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }

    /**
     * Delete an item by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
