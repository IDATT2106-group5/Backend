package edu.ntnu.idatt2106.krisefikser.api.controller.item;

import edu.ntnu.idatt2106.krisefikser.api.dto.item.ItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.service.item.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling item related requests.
 */
@Tag(name = "Items", description = "Endpoints for item related requests")
@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ItemController {

  private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
  private final ItemService itemService;

  /**
   * Constructor for ItemController.
   *
   * @param itemService the service for handling item related requests
   */
  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  /**
   * Get all items.
   *
   * @return a list of items as DTOs
   */
  @Operation(summary = "Gets all items",
      description = "Gets all items from the database")
  @GetMapping
  public ResponseEntity<List<ItemResponseDto>> getAllItems() {
    try {
      List<ItemResponseDto> items = itemService.getAllItems();
      logger.info("Fetched all items");
      return ResponseEntity.ok(items);
    } catch (Exception e) {
      logger.error("Error fetching items: {}", e.getMessage());
      return ResponseEntity.status(500).build();
    }
  }

  /**
   * Gets an item by id.
   *
   * @param itemId the id of the item
   * @return the item
   */
  @Operation(summary = "Gets an item",
      description = "Gets an item by a given id")
  @GetMapping("/{itemId}")
  public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Long itemId) {
    try {
      ItemResponseDto item = itemService.getItemById(itemId);
      return ResponseEntity.ok(item);
    } catch (IllegalArgumentException e) {
      logger.warn("Error fetching item {}: {}", itemId, e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      logger.error("Error fetching item {}: {}", itemId, e.getMessage());
      return ResponseEntity.status(500).build();
    }
  }

  /**
   * Gets items by type.
   *
   * @param itemType the type of item.
   * @return a list of items of the given type.
   */

  @Operation(summary = "Gets items by type",
      description = "Gets items of a give type")
  @GetMapping("/type/{itemType}")
  public ResponseEntity<List<ItemResponseDto>> getItemsByType(@PathVariable String itemType) {
    try {
      List<ItemResponseDto> items = itemService.getItemsByType(itemType);
      return ResponseEntity.ok(items);
    } catch (IllegalArgumentException e) {
      logger.warn("Error fetching items of type {}: {}", itemType, e.getMessage());
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      logger.error("Error fetching items of type {}: {}", itemType, e.getMessage());
      return ResponseEntity.status(500).build();
    }
  }

  /**
   * Gets paginated items.
   *
   * @param page   the page number
   * @param size   the page size
   * @param search the search term
   * @return a paginated list of items
   */

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
