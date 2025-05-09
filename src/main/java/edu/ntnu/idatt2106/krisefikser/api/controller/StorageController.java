package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.StorageItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import edu.ntnu.idatt2106.krisefikser.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling storage related requests.
 */
@Tag(name = "Storage", description = "Endpoints for storage related requests")
@RestController
@RequestMapping("/api/storage")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class StorageController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageController.class);
  private final StorageService storageService;

  /**
   * Constructor for StorageController.
   *
   * @param storageService the service for handling storage related requests
   */
  public StorageController(StorageService storageService) {
    this.storageService = storageService;
  }

  /**
   * Get all storage items for a given household.
   *
   * @return a list of storage items
   */
  @Operation(summary = "Gets a households storage items",
      description = "Gets all storage items for a household with a given id")
  @GetMapping("/household")
  public ResponseEntity<List<StorageItemResponseDto>> getStorageItemsByHousehold() {
    List<StorageItemResponseDto> storageItems = storageService.getStorageItemsByHousehold();
    return ResponseEntity.ok(storageItems);
  }

  /**
   * Get all storage items of a given type for a given household.
   *
   * @param householdId the id of the household
   * @param itemType    the type of the item
   * @return a list of storage items
   */
  @Operation(summary = "Gets storage items by type for a household",
      description = "Gets all storage items of a given type for a given household")
  @GetMapping("/household/type/{itemType}")
  public ResponseEntity<List<StorageItem>> getStorageItemsByHouseholdAndType(
      @PathVariable ItemType itemType) {
    List<StorageItem> storageItems = storageService.getStorageItemsByHouseholdAndType(itemType);
    return ResponseEntity.ok(storageItems);
  }

  /**
   * Get all storage items that are expiring before a given date.
   *
   * @param before      the date to check for expiration
   * @return a list of expiring storage items
   */
  @Operation(summary = "Gets expiring items for a household",
      description = "Gets a household's storage items that are expiring before a given date")
  @GetMapping("/household/expiring")
  public ResponseEntity<List<StorageItem>> getExpiringItems(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
    List<StorageItem> expiringItems = storageService.getExpiringItems(before);
    return ResponseEntity.ok(expiringItems);
  }

  /**
   * Add an item to the storage of a household.
   *
   * @param householdId the id of the household
   * @param itemId      the id of the item
   * @param request     the request body containing item details
   * @return the added storage item
   */
  @Operation(summary = "Adds an item to the storage",
      description = "Adds an item to the storage of a household with a given id")
  @PostMapping("/household/item/{itemId}")
  public ResponseEntity<StorageItem> addItemToStorage(
      @PathVariable Long itemId,
      @RequestBody Map<String, Object> request) {

    LOGGER.info("Adding item to storage itemId={}, request={}", itemId, request);

    String unit = (String) request.get("unit");
    Integer amount = Integer.valueOf(request.get("amount").toString());
    LocalDateTime expirationDate = null;

    if (request.get("expirationDate") != null) {
      expirationDate = LocalDateTime.parse(request.get("expirationDate").toString());
    }

    StorageItem storageItem = storageService.addItemToStorage(itemId, unit, amount, expirationDate);

    return ResponseEntity.ok(storageItem);
  }

  /**
   * Remove an item from the storage of a household.
   *
   * @param storageItemId the id of the storage item
   * @return a response entity indicating the result of the operation
   */
  @Operation(summary = "Removes an item from the storage",
      description = "Removes an item from the storage of a household with a given id")
  @PostMapping("/{storageItemId}")
  public ResponseEntity<Void> removeItemFromStorage(@PathVariable Long storageItemId) {
    storageService.removeItemFromStorage(storageItemId);
    return ResponseEntity.ok().build();
  }

  /**
   * Update an existing storage item.
   *
   * @param storageItemId the id of the storage item
   * @param request       the request body containing updated item details
   * @return the updated storage item
   */
  @Operation(summary = "Updates a storage item",
      description = "Updates an existing storage item with a given id.")
  @PutMapping("/{storageItemId}")
  public ResponseEntity<?> updateStorageItem(
      @PathVariable Long storageItemId,
      @RequestBody Map<String, Object> request) {

    try {
      // Extract values from request body
      String unit = (String) request.get("unit");
      Integer amount = request.get("amount") != null ?
          Integer.valueOf(request.get("amount").toString()) : null;

      // Handle expiration date (could be null)
      LocalDateTime expirationDate = null;
      if (request.get("expirationDate") != null) {
        expirationDate = LocalDateTime.parse(request.get("expirationDate").toString());
      }

      // Update existing storage item
      StorageItem updatedItem = storageService.updateStorageItem(
          storageItemId, unit, amount, expirationDate);

      return ResponseEntity.ok(updatedItem);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(Map.of("error", "Failed to update storage item"));
    }
  }
}