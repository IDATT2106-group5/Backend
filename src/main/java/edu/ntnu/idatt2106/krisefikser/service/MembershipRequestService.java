package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.MembershipRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.MembershipRequestResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
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
    User receiver = userRepository.findById(request.getUserId()).orElseThrow(
        () -> new IllegalArgumentException("User not found"));
    Household household = householdRepository.findById(request.getHouseholdId()).orElseThrow(
        () -> new IllegalArgumentException("Household not found"));

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
    User sender = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    Household household = householdRepository.findById(request.getHouseholdId())
        .orElseThrow(() -> new IllegalArgumentException("Household not found"));

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
   * Get sent invitations by user.
   *
   * @param userId the user id
   * @return the active invitations by user
   */
  public List<MembershipRequestResponseDto> getSentInvitationsByUser(Long userId) {
    // Check if the user exists
    if (!userRepository.existsById(userId)) {
      throw new IllegalArgumentException("User not found");
    }

    // Get the user
    User user = userRepository.findById(userId).orElse(null);

    // Get the active requests for the user
    return membershipRequestRepository.findAllBySenderAndTypeAndStatus(user,
        RequestType.INVITATION, RequestStatus.PENDING).stream().map(invitation ->
        new MembershipRequestResponseDto(
            invitation.getId(),
            invitation.getHousehold().getId(),
            new UserResponseDto(invitation.getSender().getId(), invitation.getSender().getEmail(),
                invitation.getSender().getFullName(), invitation.getSender().getTlf(),
                invitation.getSender().getRole()),
            new UserResponseDto(invitation.getReceiver().getId(),
                invitation.getReceiver().getEmail(), invitation.getReceiver().getFullName(),
                invitation.getReceiver().getTlf(), invitation.getReceiver().getRole()),
            invitation.getType(),
            invitation.getStatus(),
            invitation.getCreated_at()
            )
    ).toList();
  }

  /**
   * Get received invitations by user.
   *
   * @param userId the user id
   * @return the active invitations by user
   */
  public List<MembershipRequestResponseDto> getReceivedInvitationsByUser(Long userId) {
    // Check if the user exists
    if (!userRepository.existsById(userId)) {
      throw new IllegalArgumentException("User not found");
    }

    // Get the user
    User user = userRepository.findById(userId).orElse(null);

    // Get the active requests for the user
    return membershipRequestRepository.findAllByReceiverAndTypeAndStatus(user,
        RequestType.INVITATION, RequestStatus.PENDING).stream().map(invitation ->
        new MembershipRequestResponseDto(
            invitation.getId(),
            invitation.getHousehold().getId(),
            new UserResponseDto(invitation.getSender().getId(), invitation.getSender().getEmail(),
                invitation.getSender().getFullName(), invitation.getSender().getTlf(),
                invitation.getSender().getRole()),
            new UserResponseDto(invitation.getReceiver().getId(),
                invitation.getReceiver().getEmail(), invitation.getReceiver().getFullName(),
                invitation.getReceiver().getTlf(), invitation.getReceiver().getRole()),
            invitation.getType(),
            invitation.getStatus(),
            invitation.getCreated_at()
        )
    ).toList();
  }
}