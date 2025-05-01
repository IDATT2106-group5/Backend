package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.ItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.StorageItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.HouseholdRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ItemRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.StorageItemRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing storage items in a household. This includes adding, removing,
 * updating, and retrieving storage items. It also provides methods to filter items by type and
 * expiration date.
 */
@Service
public class StorageService {

  private final StorageItemRepository storageItemRepository;
  private final HouseholdRepository householdRepository;
  private final ItemRepository itemRepository;

  /**
   * Constructor for StorageService.
   *
   * @param storageItemRepository The repository for storage item operations.
   * @param householdRepository   The repository for household operations.
   * @param itemRepository        The repository for item operations.
   */
  public StorageService(StorageItemRepository storageItemRepository,
      HouseholdRepository householdRepository,
      ItemRepository itemRepository) {
    this.storageItemRepository = storageItemRepository;
    this.householdRepository = householdRepository;
    this.itemRepository = itemRepository;
  }

  /**
   * Get all storage items for a specific household.
   *
   * @param householdId The ID of the household.
   * @return A list of StorageItem entities.
   */
  public List<StorageItemResponseDto> getStorageItemsByHousehold(Long householdId) {
    return storageItemRepository.findByHouseholdId(householdId).stream().map(
        storageItem -> new StorageItemResponseDto(
            new ItemResponseDto(
                storageItem.getItem().getName(),
                storageItem.getItem().getCaloricAmount(),
                storageItem.getItem().getItemType()
                ),
            storageItem.getHousehold().getId(),
            storageItem.getUnit(),
            storageItem.getAmount(),
            storageItem.getExpirationDate()
        )).toList(
    );
  }

  /**
   * Get storage items for a specific household filtered by item type.
   *
   * @param householdId The ID of the household.
   * @param itemType    The type of items to filter by.
   * @return A list of StorageItem entities.
   */
  public List<StorageItem> getStorageItemsByHouseholdAndType(Long householdId, ItemType itemType) {
    return storageItemRepository.findByHouseholdIdAndItemItemType(householdId, itemType);
  }

  /**
   * Get items that will expire before a specific date.
   *
   * @param householdId The ID of the household.
   * @param before      The date before which items will expire.
   * @return A list of StorageItem entities that will expire before the specified date.
   */
  public List<StorageItem> getExpiringItems(Long householdId, LocalDateTime before) {
    return storageItemRepository.findByHouseholdIdAndExpirationDateBefore(householdId, before);
  }

  /**
   * Get items that have already expired.
   *
   * @param householdId The ID of the household.
   * @return A list of StorageItem entities that have already expired.
   */
  public List<StorageItem> getExpiredItems(Long householdId) {
    return storageItemRepository.findByHouseholdIdAndExpirationDateBefore(
        householdId, LocalDateTime.now());
  }

  /**
   * Adds an item to the storage of a household. This method creates a new StorageItem entity and
   * associates it with the specified household and item. It also sets the unit, amount, and
   * expiration date for the storage item.
   *
   * @param householdId    The ID of the household to which the item will be added.
   * @param itemId         The ID of the item to be added.
   * @param unit           The unit of measurement for the item.
   * @param amount         The amount of the item to be added.
   * @param expirationDate The expiration date of the item.
   * @return The newly created StorageItem entity.
   */
  @Transactional
  public StorageItem addItemToStorage(Long householdId, Long itemId,
      String unit, Integer amount,
      LocalDateTime expirationDate) {
    Household household = householdRepository.findById(householdId)
        .orElseThrow(() -> new IllegalArgumentException("Household not found"));

    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    StorageItem storageItem = new StorageItem();
    storageItem.setHousehold(household);
    storageItem.setItem(item);
    storageItem.setUnit(unit);
    storageItem.setAmount(amount);
    storageItem.setExpirationDate(expirationDate);
    storageItem.setDateAdded(LocalDateTime.now());

    return storageItemRepository.save(storageItem);
  }

  /**
   * Removes an item from storage.
   *
   * @param storageItemId The ID of the storage item to be removed.
   */
  @Transactional
  public void removeItemFromStorage(Long storageItemId) {
    storageItemRepository.deleteById(storageItemId);
  }

  /**
   * Updates a storage item with new values.
   *
   * @param storageItemId  The ID of the storage item to update.
   * @param unit           The new unit of measurement.
   * @param amount         The new amount.
   * @param expirationDate The new expiration date.
   * @return The updated StorageItem entity.
   */
  @Transactional
  public StorageItem updateStorageItem(Long storageItemId, String unit, Integer amount,
      LocalDateTime expirationDate) {
    StorageItem storageItem = storageItemRepository.findById(storageItemId)
        .orElseThrow(() -> new IllegalArgumentException("Storage item not found"));

    if (unit != null) {
      storageItem.setUnit(unit);
    }

    if (amount != null) {
      storageItem.setAmount(amount);
    }

    storageItem.setExpirationDate(expirationDate);

    return storageItemRepository.save(storageItem);
  }

  /**
   * Updates the amount of a storage item in the household's storage.
   *
   * @param storageItemId The ID of the storage item to be updated.
   * @param newAmount     The new amount to set for the storage item.
   * @return The updated StorageItem entity.
   */
  @Transactional
  public StorageItem updateItemAmount(Long storageItemId, Integer newAmount) {
    StorageItem storageItem = storageItemRepository.findById(storageItemId)
        .orElseThrow(() -> new IllegalArgumentException("Storage item not found"));

    storageItem.setAmount(newAmount);
    return storageItemRepository.save(storageItem);
  }
}