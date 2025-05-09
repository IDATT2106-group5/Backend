package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.MapIcon;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.MapIconType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Map icon repository.
 */
@Repository
public interface MapIconRepository extends JpaRepository<MapIcon, Long> {

  /**
   * Find all map icons by type.
   *
   * @param type the type of the map icon
   * @return a list of map icons
   */
  List<MapIcon> findByType(MapIconType type);
}
