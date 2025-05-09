package edu.ntnu.idatt2106.krisefikser.persistance.repository.membershiprequest;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.membershiprequest.MembershipRequest;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.user.User;
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

  /**
   * Updates the status of a MembershipRequest by its ID.
   *
   * @param id     the id of the MembershipRequest
   * @param status the new status to set
   */
  @Transactional
  @Modifying
  @Query("UPDATE MembershipRequest m SET m.status = :status WHERE m.id = :id")
  void updateStatusById(Long id, RequestStatus status);

  /**
   * Finds all MembershipRequests by the given receiver, type, and status.
   *
   * @param receiver the receiver of the request
   * @param type     the type of the request
   * @param status   the status of the request
   * @return a list of MembershipRequests matching the criteria
   */
  List<MembershipRequest> findAllByReceiverAndTypeAndStatus(User receiver, RequestType type,
      RequestStatus status);

  /**
   * Finds all MembershipRequests by the given household, type, and status.
   *
   * @param householdId the ID of the household
   * @param type        the type of the request
   * @param status      the status of the request
   * @return a list of MembershipRequests matching the criteria
   */
  List<MembershipRequest> findAllByHouseholdIdAndTypeAndStatus(
      String householdId, RequestType type, RequestStatus status);

  /**
   * Finds all MembershipRequests by the given household, type, and list of statuses.
   *
   * @param householdId the ID of the household
   * @param type        the type of the request
   * @param statuses    the list of statuses of the request
   * @return a list of MembershipRequests matching the criteria
   */
  List<MembershipRequest> findAllByHouseholdIdAndTypeAndStatusIn(
      String householdId, RequestType type, List<RequestStatus> statuses
  );

  /**
   * Deletes all MembershipRequests associated with the given household ID.
   *
   * @param householdId the ID of the household
   */
  @Modifying
  @Query("DELETE FROM MembershipRequest m WHERE m.household.id = :householdId")
  void deleteAllByHouseholdId(String householdId);
}