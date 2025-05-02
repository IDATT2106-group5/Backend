package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.ItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ItemRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

  private final ItemRepository itemRepository;

  public ItemService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public List<ItemResponseDto> getAllItems() {
    return itemRepository.findAll().stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  private ItemResponseDto mapToDto(Item item) {
    return new ItemResponseDto(
        item.getId(),
        item.getName(),
        item.getCaloricAmount(),
        item.getItemType()
    );
  }

  public ItemResponseDto getItemById(Long itemId) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));
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
    try {
      // Convert the string to enum (will throw IllegalArgumentException if invalid)
      ItemType type = ItemType.valueOf(itemType.toUpperCase());

      // Use the repository to find items by type
      List<Item> items = itemRepository.findByItemType(type);

      // Convert to DTOs and return
      return items.stream()
          .map(this::mapToDto) // Use the existing mapToDto method
          .collect(Collectors.toList());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid item type: " + itemType);
    }
  }
}
