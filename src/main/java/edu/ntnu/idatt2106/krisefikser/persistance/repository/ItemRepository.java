package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Item repository. This interface extends JpaRepository and provides methods to
 * perform CRUD operations on Item entities. It also includes custom query methods to find items by
 * name and item type.
 */

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

  /**
   * Find items by type.
   *
   * @param itemType the item type
   * @return the list of items of the given type
   */
  List<Item> findByItemType(ItemType itemType);

  /**
   * Find items by name containing the given string, ignoring case.
   *
   * @param name     the name to search for
   * @param pageable the pagination information
   * @return a page of items that match the search criteria
   */
  Page<Item> findByNameContainingIgnoreCase(String name, Pageable pageable);
}