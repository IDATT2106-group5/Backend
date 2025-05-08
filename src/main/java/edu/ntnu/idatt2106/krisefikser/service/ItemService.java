package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.ItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ItemRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing items. This class provides methods to retrieve items from the database
 * and convert them to DTOs.
 */

@Service
public class ItemService {

  private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
  private final ItemRepository itemRepository;

  public ItemService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
    logger.info("ItemService initialized");
  }

  /**
   * Retrieves all items from the database and converts them to DTOs.
   *
   * @return a list of ItemResponseDto objects representing all items
   */

  public List<ItemResponseDto> getAllItems() {
    logger.info("Fetching all items");
    List<Item> items = itemRepository.findAll();
    logger.debug("Retrieved {} items from database", items.size());

    return items.stream()
        .map(this::mapToDto)
        .toList();
  }

  private ItemResponseDto mapToDto(Item item) {
    logger.trace("Mapping item to DTO: id={}, name={}", item.getId(), item.getName());
    return new ItemResponseDto(
        item.getId(),
        item.getName(),
        item.getCaloricAmount(),
        item.getItemType()
    );
  }

  /**
   * Retrieves an item by its ID and converts it to a DTO.
   *
   * @param itemId The ID of the item to retrieve.
   * @return An ItemResponseDto object representing the item.
   */
  public ItemResponseDto getItemById(Long itemId) {
    logger.info("Fetching item with ID: {}", itemId);

    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> {
          logger.warn("Item not found with ID: {}", itemId);
          return new IllegalArgumentException("Item not found with ID: " + itemId);
        });

    logger.debug("Found item: {} (ID: {})", item.getName(), item.getId());
    return mapToDto(item);
  }

  /**
   * Retrieves all items of a specific type.
   *
   * @param itemType the type of items to retrieve
   * @return a list of items matching the specified type
   * @throws IllegalArgumentException if the item type is invalid
   */
  public List<ItemResponseDto> getItemsByType(String itemType) {
    logger.info("Fetching items by type: {}", itemType);

    try {
      // Convert the string to enum (will throw IllegalArgumentException if invalid)
      ItemType type = ItemType.valueOf(itemType.toUpperCase());
      logger.debug("Converted item type string to enum: {}", type);

      // Use the repository to find items by type
      List<Item> items = itemRepository.findByItemType(type);
      logger.debug("Found {} items of type {}", items.size(), type);

      // Convert to DTOs and return
      return items.stream()
          .map(this::mapToDto)
          .toList();
    } catch (IllegalArgumentException e) {
      logger.warn("Invalid item type provided: {}", itemType);
      throw new IllegalArgumentException("Invalid item type: " + itemType);
    }
  }

  /**
   * Retrieves a paginated list of items, optionally filtered by a search term.
   *
   * @param page   The page number to retrieve (0-indexed).
   * @param size   The number of items per page.
   * @param search The search term to filter items by name (optional).
   * @return A Page of ItemResponseDto objects representing the items.
   */
  public Page<ItemResponseDto> getPaginatedItems(int page, int size, String search) {
    logger.info("Fetching paginated items: page={}, size={}, search={}", page, size, search);

    Pageable pageable = PageRequest.of(page, size);
    logger.debug("Created pageable request for page {} with size {}", page, size);

    Page<Item> items;
    if (search != null && !search.isEmpty()) {
      logger.debug("Searching for items with name containing: '{}'", search);
      items = itemRepository.findByNameContainingIgnoreCase(search, pageable);
    } else {
      logger.debug("No search term provided, fetching all items with pagination");
      items = itemRepository.findAll(pageable);
    }

    Page<ItemResponseDto> result = items.map(this::mapToDto);
    logger.debug("Retrieved page {} of {} with {} items",
        result.getNumber() + 1,
        result.getTotalPages(),
        result.getNumberOfElements());

    return result;
  }
}