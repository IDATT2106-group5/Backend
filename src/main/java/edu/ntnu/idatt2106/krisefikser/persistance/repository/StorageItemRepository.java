package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface StorageItem repository. This interface extends JpaRepository and provides methods
 * to perform CRUD operations on StorageItem entities. It also includes custom query methods to find
 * storage items by household ID, item type, and expiration date.
 */

@Repository
public interface StorageItemRepository extends JpaRepository<StorageItem, Long> {

  List<StorageItem> findByHouseholdId(Long householdId);

  List<StorageItem> findByHouseholdIdAndItemItemType(Long householdId, ItemType itemType);

  List<StorageItem> findByHouseholdIdAndExpirationDateBefore(Long householdId, LocalDateTime date);

  void deleteByHouseholdIdAndItemId(Long householdId, Long itemId);
}