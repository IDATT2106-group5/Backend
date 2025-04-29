package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.MembershipRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.MembershipRequest;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestStatus;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestType;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.HouseholdRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.MembershipRequestRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * The type Membership request service.
 */
@Service
public class MembershipRequestService {
  private final MembershipRequestRepository membershipRequestRepository;
  private final HouseholdRepository householdRepository;
  private final UserRepository userRepository;


  /**
   * Instantiates a new Membership request service.
   *
   * @param membershipRequestRepository the membership request repository
   * @param householdRepository         the household repository
   * @param userRepository              the user repository
   */
  public MembershipRequestService(MembershipRequestRepository membershipRequestRepository,
                                  HouseholdRepository householdRepository,
                                  UserRepository userRepository) {
    this.membershipRequestRepository = membershipRequestRepository;
    this.householdRepository = householdRepository;
    this.userRepository = userRepository;
  }

  /**
   * Send an invitation to a user to join a household.
   *
   * @param request the request
   */
  public void sendInvitation(MembershipRequestDto request) {
    // Check if the user and household exist
    if (!userRepository.existsByEmail(request.getUserEmail())) {
      throw new IllegalArgumentException("User not found");
    }
    if (!householdRepository.existsByName(request.getHouseholdName())) {
      throw new IllegalArgumentException("Household not found");
    }
    User receiver = userRepository.findByEmail(request.getUserEmail()).orElse(null);
    Household household = householdRepository.findByName(request.getHouseholdName()).orElse(null);

    // Create and save the membership request
    MembershipRequest membershipRequest = new MembershipRequest();
    membershipRequest.setHousehold(household);
    membershipRequest.setSender(household.getOwner());
    membershipRequest.setReceiver(receiver);
    membershipRequest.setType(RequestType.INVITATION);
    membershipRequest.setStatus(RequestStatus.PENDING);
    membershipRequest.setCreated_at(new Timestamp(System.currentTimeMillis()));

    membershipRequestRepository.save(membershipRequest);
  }

  /**
   * Send a request to join a household.
   *
   * @param request the request
   */
  public void sendJoinRequest(MembershipRequestDto request) {
    // Check if the user and household exist
    if (!userRepository.existsByEmail(request.getUserEmail())) {
      throw new IllegalArgumentException("User not found");
    }
    if (!householdRepository.existsByName(request.getHouseholdName())) {
      throw new IllegalArgumentException("Household not found");
    }
    User sender = userRepository.findByEmail(request.getUserEmail()).orElse(null);
    Household household = householdRepository.findByName(request.getHouseholdName()).orElse(null);

    // Create and save the membership request
    MembershipRequest membershipRequest = new MembershipRequest();
    membershipRequest.setHousehold(household);
    membershipRequest.setSender(sender);
    membershipRequest.setReceiver(household.getOwner());
    membershipRequest.setType(RequestType.JOIN_REQUEST);
    membershipRequest.setStatus(RequestStatus.PENDING);
    membershipRequest.setCreated_at(new Timestamp(System.currentTimeMillis()));

    membershipRequestRepository.save(membershipRequest);
  }

  /**
   * Accept a membership request.
   *
   * @param requestId the request id
   */
  public void acceptRequest(Long requestId) {
    // Check if the request exists
    if (!membershipRequestRepository.existsById(requestId)) {
      throw new IllegalArgumentException("Request not found");
    }

    // Update the request status to "accepted"
    membershipRequestRepository.updateStatusById(requestId, RequestStatus.ACCEPTED);
  }

  /**
   * Reject a membership request.
   *
   * @param requestId the request id
   */
  public void declineRequest(Long requestId) {
    // Check if the request exists
    if (!membershipRequestRepository.existsById(requestId)) {
      throw new IllegalArgumentException("Request not found");
    }

    // Update the request status to "rejected"
    membershipRequestRepository.updateStatusById(requestId, RequestStatus.REJECTED);
  }

  /**
   * Get active requests by user.
   *
   * @param email the users email
   */
  public List<MembershipRequest> getActiveRequestsByUser(String email) {
    // Check if the user exists
    if (!userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("User not found");
    }

    // Get the user
    User user = userRepository.findByEmail(email).orElse(null);

    // Get the active requests for the user
    return membershipRequestRepository.findAllByReceiverAndStatus(user, RequestStatus.PENDING);
  }
}