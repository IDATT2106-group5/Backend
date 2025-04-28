package edu.ntnu.idatt2106.krisefikser.service;

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

  public List<StorageItem> getHouseholdStorage(Long householdId) {
    return storageItemRepository.findByHouseholdId(householdId);
  }

  public List<StorageItem> getHouseholdStorageByItemType(Long householdId, ItemType itemType) {
    return storageItemRepository.findByHouseholdIdAndItemItemType(householdId, itemType);
  }

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

    return storageItemRepository.save(storageItem);
  }

  @Transactional
  public void removeItemFromStorage(Long storageItemId) {
    storageItemRepository.deleteById(storageItemId);
  }

  /**
   * Updates the amount of a storage item in the household's storage. This method retrieves the
   * StorageItem entity by its ID, updates the amount, and saves the changes to the database.
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