package edu.ntnu.idatt2106.krisefikser.service.storage;

import edu.ntnu.idatt2106.krisefikser.api.dto.item.ItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.storage.StorageItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.household.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.item.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.storage.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.user.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.household.HouseholdRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.item.ItemRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.storage.StorageItemRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing storage items in a household. This includes adding, removing,
 * updating, and retrieving storage items. It also provides methods to filter items by type and
 * expiration date.
 */
@Service
public class StorageService {

  private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

  private final StorageItemRepository storageItemRepository;
  private final HouseholdRepository householdRepository;
  private final ItemRepository itemRepository;
  private final UserRepository userRepository;

  /**
   * Constructor for StorageService.
   *
   * @param storageItemRepository The repository for storage item operations.
   * @param householdRepository   The repository for household operations.
   * @param itemRepository        The repository for item operations.
   */
  public StorageService(StorageItemRepository storageItemRepository,
      HouseholdRepository householdRepository,
      ItemRepository itemRepository, UserRepository userRepository) {
    this.storageItemRepository = storageItemRepository;
    this.householdRepository = householdRepository;
    this.itemRepository = itemRepository;
    logger.info("StorageService instantiated.");
    this.userRepository = userRepository;
  }

  /**
   * Get all storage items for a specific household.
   *
   * @return A list of StorageItemResponseDto.
   */
  public List<StorageItemResponseDto> getStorageItemsByHousehold() {
    logger.info("Fetching storage items for current household");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("No user logged in"));
    Household household = user.getHousehold();
    List<StorageItemResponseDto> items = storageItemRepository
        .findByHouseholdId(household.getId())
        .stream()
        .map(storageItem -> {
          logger.debug("Mapping StorageItem id={} to DTO", storageItem.getId());
          return new StorageItemResponseDto(
              storageItem.getId(),
              new ItemResponseDto(
                  storageItem.getItem().getId(),
                  storageItem.getItem().getName(),
                  storageItem.getItem().getCaloricAmount(),
                  storageItem.getItem().getItemType()
              ),
              storageItem.getHousehold().getId(),
              storageItem.getUnit(),
              storageItem.getAmount(),
              storageItem.getExpirationDate()
          );
        })
        .toList();
    logger.info("Found {} storage items for householdId={}", items.size(), household.getId());
    return items;
  }

  /**
   * Get storage items for a specific household filtered by item type.
   *
   * @param itemType The type of items to filter by.
   * @return A list of StorageItem entities.
   */
  public List<StorageItem> getStorageItemsByHouseholdAndType(ItemType itemType) {
    logger.info("Fetching storage items for with itemType={}",
        itemType);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Household household = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("No user logged in"))
        .getHousehold();

    List<StorageItem> items =
        storageItemRepository.findByHouseholdIdAndItemItemType(household.getId(),
            itemType);
    logger.info("Found {} items of type {} for householdId={}", items.size(), itemType,
        household.getId());
    return items;
  }

  /**
   * Get items that will expire before a specific date.
   *
   * @param before The date before which items will expire.
   * @return A list of StorageItem entities that will expire before the specified date.
   */
  public List<StorageItem> getExpiringItems(LocalDateTime before) {
    logger.info("Fetching items expiring before {}", before);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("No user logged in"));
    Household household = user.getHousehold();

    List<StorageItem> items = storageItemRepository.findByHouseholdIdAndExpirationDateBefore(
        household.getId(), before);
    logger.info("Found {} expiring items for householdId={}", items.size(), household.getId());
    return items;
  }

  /**
   * Get items that have already expired.
   *
   * @param householdId The ID of the household.
   * @return A list of StorageItem entities that have already expired.
   */
  public List<StorageItem> getExpiredItems(String householdId) {
    LocalDateTime now = LocalDateTime.now();
    logger.info("Fetching items already expired as of {} for householdId={}", now, householdId);
    List<StorageItem> items = storageItemRepository.findByHouseholdIdAndExpirationDateBefore(
        householdId, now);
    logger.info("Found {} expired items for householdId={}", items.size(), householdId);
    return items;
  }

  /**
   * Adds an item to the storage of a household.
   *
   * @param itemId         The ID of the item to be added.
   * @param unit           The unit of measurement for the item.
   * @param amount         The amount of the item to be added.
   * @param expirationDate The expiration date of the item.
   * @return The newly created StorageItem entity.
   */
  @Transactional
  public StorageItem addItemToStorage(Long itemId,
      String unit, Integer amount,
      LocalDateTime expirationDate) {
    logger.info(
        "Adding item to storage: itemId={}, amount={}, unit={}, expiration={}",
        itemId, amount, unit, expirationDate);

    // Get the currently authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("No user logged in"));
    Household household = user.getHousehold();

    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> {
          logger.error("Item not found: {}", itemId);
          return new IllegalArgumentException("Item not found");
        });

    StorageItem storageItem = new StorageItem();
    storageItem.setHousehold(household);
    storageItem.setItem(item);
    storageItem.setUnit(unit);
    storageItem.setAmount(amount);
    storageItem.setExpirationDate(expirationDate);
    storageItem.setDateAdded(LocalDateTime.now());

    StorageItem saved = storageItemRepository.save(storageItem);
    logger.info("StorageItem created with id={}", saved.getId());
    return saved;
  }

  /**
   * Removes an item from storage.
   *
   * @param storageItemId The ID of the storage item to be removed.
   */
  @Transactional
  public void removeItemFromStorage(Long storageItemId) {
    logger.info("Removing storage item with id={}", storageItemId);
    storageItemRepository.deleteById(storageItemId);
    logger.info("Storage item {} removed", storageItemId);
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
    logger.info("Updating storage item id={} with unit={}, amount={}, expiration={}",
        storageItemId, unit, amount, expirationDate);

    StorageItem storageItem = storageItemRepository.findById(storageItemId)
        .orElseThrow(() -> {
          logger.error("Storage item not found: {}", storageItemId);
          return new IllegalArgumentException("Storage item not found");
        });

    if (unit != null) {
      storageItem.setUnit(unit);
      logger.debug(" - unit set to {}", unit);
    }

    if (amount != null) {
      storageItem.setAmount(amount);
      logger.debug(" - amount set to {}", amount);
    }

    storageItem.setExpirationDate(expirationDate);
    logger.debug(" - expirationDate set to {}", expirationDate);

    StorageItem updated = storageItemRepository.save(storageItem);
    logger.info("Storage item {} updated successfully", storageItemId);
    return updated;
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
    logger.info("Updating amount of storage item id={} to {}", storageItemId, newAmount);

    StorageItem storageItem = storageItemRepository.findById(storageItemId)
        .orElseThrow(() -> {
          logger.error("Storage item not found: {}", storageItemId);
          return new IllegalArgumentException("Storage item not found");
        });

    storageItem.setAmount(newAmount);
    StorageItem saved = storageItemRepository.save(storageItem);
    logger.info("Storage item {} amount updated to {}", storageItemId, newAmount);
    return saved;
  }
}
