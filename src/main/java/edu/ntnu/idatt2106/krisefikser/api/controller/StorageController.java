package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.StorageItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import edu.ntnu.idatt2106.krisefikser.service.StorageService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/storage")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class StorageController {

  private final StorageService storageService;
  private static final Logger LOGGER = LoggerFactory.getLogger(StorageController.class);

  public StorageController(StorageService storageService) {
    this.storageService = storageService;
  }

  @GetMapping("/household/{householdId}")
  public ResponseEntity<List<StorageItemResponseDto>> getStorageItemsByHousehold(
      @PathVariable Long householdId) {
    List<StorageItemResponseDto> storageItems = storageService.getStorageItemsByHousehold(householdId);
    return ResponseEntity.ok(storageItems);
  }

  @GetMapping("/household/{householdId}/type/{itemType}")
  public ResponseEntity<List<StorageItem>> getStorageItemsByHouseholdAndType(
      @PathVariable Long householdId,
      @PathVariable ItemType itemType) {
    List<StorageItem> storageItems = storageService.getStorageItemsByHouseholdAndType(householdId,
        itemType);
    return ResponseEntity.ok(storageItems);
  }

  @GetMapping("/household/{householdId}/expiring")
  public ResponseEntity<List<StorageItem>> getExpiringItems(
      @PathVariable Long householdId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
    List<StorageItem> expiringItems = storageService.getExpiringItems(householdId, before);
    return ResponseEntity.ok(expiringItems);
  }

  @PostMapping("/household/{householdId}/item/{itemId}")
  public ResponseEntity<StorageItem> addItemToStorage(
      @PathVariable Long householdId,
      @PathVariable Long itemId,
      @RequestBody Map<String, Object> request) {

    LOGGER.info("Adding item to storage: householdId={}, itemId={}, request={}",
        householdId, itemId, request);

    String unit = (String) request.get("unit");
    Integer amount = Integer.valueOf(request.get("amount").toString());
    LocalDateTime expirationDate = null;

    if (request.get("expirationDate") != null) {
      expirationDate = LocalDateTime.parse(request.get("expirationDate").toString());
    }

    StorageItem storageItem = storageService.addItemToStorage(
        householdId, itemId, unit, amount, expirationDate);

    return ResponseEntity.ok(storageItem);
  }

  @PostMapping("/{storageItemId}")
  public ResponseEntity<Void> removeItemFromStorage(@PathVariable Long storageItemId) {
    storageService.removeItemFromStorage(storageItemId);
    return ResponseEntity.ok().build();
  }

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