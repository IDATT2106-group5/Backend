package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

  Optional<Item> findByName(String name);

  List<Item> findByItemType(ItemType itemType);
}