package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

List<Notification> findAllByUserIdOrderByTimestampDesc(Long userId);

}
