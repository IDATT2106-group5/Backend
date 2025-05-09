package edu.ntnu.idatt2106.krisefikser.persistance.repository.storage;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.storage.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * The interface StorageItem repository. This interface extends JpaRepository and provides methods
 * to perform CRUD operations on StorageItem entities. It also includes custom query methods to find
 * storage items by household ID, item type, and expiration date.
 */
@Repository
public interface StorageItemRepository extends JpaRepository<StorageItem, Long> {

  /**
   * Find by household id list.
   *
   * @param householdId the household id
   * @return the list
   */
  List<StorageItem> findByHouseholdId(String householdId);

  /**
   * Find a list of items by household id and item type.
   *
   * @param householdId the household id
   * @param itemType    the item type
   * @return the list
   */
  List<StorageItem> findByHouseholdIdAndItemItemType(String householdId, ItemType itemType);

  /**
   * Find by household id and expiration date before.
   *
   * @param householdId the household id
   * @param date        the date
   * @return the list
   */
  List<StorageItem> findByHouseholdIdAndExpirationDateBefore(String householdId,
      LocalDateTime date);

  /**
   * Finds all storage items that expire between the given dates and haven't had a notification sent
   * yet.
   *
   * @param startDate The start date
   * @param endDate   The end date
   * @return List of storage items expiring between start and end dates that haven't been notified
   */
  @Query("SELECT s FROM StorageItem s WHERE s.expirationDate BETWEEN :startDate AND :endDate")
  List<StorageItem> findExpiringItems(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );
}