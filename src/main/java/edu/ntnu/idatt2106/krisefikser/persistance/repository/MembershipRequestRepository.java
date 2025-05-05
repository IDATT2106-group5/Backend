package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.MembershipRequest;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestStatus;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for MembershipRequest entities.
 */
public interface MembershipRequestRepository extends JpaRepository<MembershipRequest, Long> {

  @Transactional
  @Modifying
  @Query("UPDATE MembershipRequest m SET m.status = :status WHERE m.id = :id")
  void updateStatusById(Long id, RequestStatus status);

  List<MembershipRequest> findAllByReceiverAndTypeAndStatus(User receiver,
      RequestType type, RequestStatus status);

  List<MembershipRequest> findAllBySenderAndTypeAndStatus(User receiver,
      RequestType type, RequestStatus status);

  List<MembershipRequest> findAllByHouseholdIdAndTypeAndStatus(Long householdId,
      RequestType type, RequestStatus status);
}