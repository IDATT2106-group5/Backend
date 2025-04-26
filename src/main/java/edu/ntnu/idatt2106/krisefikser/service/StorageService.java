package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.ItemType;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.HouseholdRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ItemRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.StorageItemRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StorageService {

  private final StorageItemRepository storageItemRepository;
  private final HouseholdRepository householdRepository;
  private final ItemRepository itemRepository;

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

  @Transactional
  public StorageItem updateItemAmount(Long storageItemId, Integer newAmount) {
    StorageItem storageItem = storageItemRepository.findById(storageItemId)
        .orElseThrow(() -> new IllegalArgumentException("Storage item not found"));

    storageItem.setAmount(newAmount);
    return storageItemRepository.save(storageItem);
  }
}