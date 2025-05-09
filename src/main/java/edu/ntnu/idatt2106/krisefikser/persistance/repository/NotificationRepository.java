package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The interface Notification repository.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  /**
   * Find all notifications by user id order by timestamp descending.
   *
   * @param userId the user id
   * @return the list of notifications
   */
  List<Notification> findAllByUserIdOrderByTimestampDesc(String userId);

}
