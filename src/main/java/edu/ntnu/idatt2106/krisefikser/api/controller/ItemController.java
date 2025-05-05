package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.ItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.service.ItemService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ItemController {

  private final ItemService itemService;

  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  @GetMapping
  public ResponseEntity<List<ItemResponseDto>> getAllItems() {
    List<ItemResponseDto> items = itemService.getAllItems();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{itemId}")
  public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Long itemId) {
    ItemResponseDto item = itemService.getItemById(itemId);
    return ResponseEntity.ok(item);
  }

  @GetMapping("/type/{itemType}")
  public ResponseEntity<List<ItemResponseDto>> getItemsByType(@PathVariable String itemType) {
    List<ItemResponseDto> items = itemService.getItemsByType(itemType);
    return ResponseEntity.ok(items);
  }
  @GetMapping("/paginated")
  public ResponseEntity<?> getPaginatedItems(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "20") int size,
          @RequestParam(required = false) String search) {
      try {
          Page<ItemResponseDto> paginatedItems = itemService.getPaginatedItems(page, size, search);
          Map<String, Object> response = new HashMap<>();
          response.put("items", paginatedItems.getContent());
          response.put("currentPage", paginatedItems.getNumber());
          response.put("totalItems", paginatedItems.getTotalElements());
          response.put("totalPages", paginatedItems.getTotalPages());

          return ResponseEntity.ok(response);
      } catch (IllegalArgumentException e) {
          return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
      } catch (Exception e) {
          return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
      }
  }
}
