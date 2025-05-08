package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.MembershipRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest.MembershipRequestResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.MembershipRequest;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestStatus;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestType;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.HouseholdRepository;
import edu.ntnu.idatt2106.krisefikser.service.HouseholdService;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.MembershipRequestRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * The type Membership request service.
 */
@Service
public class MembershipRequestService {

  private final MembershipRequestRepository membershipRequestRepository;
  private final HouseholdRepository householdRepository;
  private final HouseholdService householdService;
  private final UserRepository userRepository;
  private final NotificationService notificationService;


  /**
   * Instantiates a new Membership request service.
   *
   * @param membershipRequestRepository the membership request repository
   * @param householdRepository         the household repository
   * @param userRepository              the user repository
   */
  public MembershipRequestService(MembershipRequestRepository membershipRequestRepository,
      HouseholdRepository householdRepository,
      UserRepository userRepository,
      NotificationService notificationService,
      HouseholdService householdService) {
    this.membershipRequestRepository = membershipRequestRepository;
    this.householdRepository = householdRepository;
    this.userRepository = userRepository;
    this.notificationService = notificationService;
    this.householdService = householdService;
  }

  /**
   * Send an invitation to a user to join a household.
   *
   * @param email       the email
   * @param householdId the household id
   */
  public void sendInvitation(String email, String householdId) {
    User receiver = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));

    Household household = householdRepository.findById(householdId)
        .orElseThrow(
            () -> new IllegalArgumentException("Household not found with ID: " + householdId));

    MembershipRequest membershipRequest = new MembershipRequest();
    membershipRequest.setHousehold(household);
    membershipRequest.setSender(household.getOwner());
    membershipRequest.setReceiver(receiver);
    membershipRequest.setType(RequestType.INVITATION);
    membershipRequest.setStatus(RequestStatus.PENDING);
    membershipRequest.setCreated_at(new Timestamp(System.currentTimeMillis()));

    membershipRequestRepository.save(membershipRequest);

    // Send a notification to the receiver
    NotificationDto notificationDto = new NotificationDto(
        NotificationType.MEMBERSHIP_REQUEST,
        receiver.getId(),
        LocalDateTime.now(),
        false,
        "You have received an invitation to join the household: " + household.getName()
    );

    notificationService.saveNotification(notificationDto);
    notificationService.sendPrivateNotification(receiver.getId(), notificationDto);
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

    // Send a notification to the receiver
    NotificationDto notificationDto = new NotificationDto(
        NotificationType.MEMBERSHIP_REQUEST,
        household.getOwner().getId(),
        LocalDateTime.now(),
        false,
        sender.getFullName() + " has requested to join the household: " + household.getName()
    );

    // Add these missing lines to save and send the notification
    notificationService.saveNotification(notificationDto);
    notificationService.broadcastNotification(notificationDto);
  }

  /**
   * Accept a membership join request.
   *
   * @param requestId the request id
   */
  public void acceptJoinRequest(Long requestId) {
    MembershipRequest request = membershipRequestRepository.findById(requestId)
        .orElseThrow(() -> new IllegalArgumentException("Request not found"));

    if (request.getStatus() != RequestStatus.PENDING) {
      throw new IllegalArgumentException("Request is not pending");
    }

    request.setStatus(RequestStatus.ACCEPTED);
    membershipRequestRepository.save(request);

    UserHouseholdAssignmentRequestDto assignment = new UserHouseholdAssignmentRequestDto();
    assignment.setUserId(request.getSender().getId());
    assignment.setHouseholdId(request.getHousehold().getId());

    householdService.addUserToHousehold(assignment);
  }

  /**
   * Accept an invitation request.
   *
   * @param requestId the request id
   */
  public void acceptInvitationRequest(Long requestId) {
    MembershipRequest request = membershipRequestRepository.findById(requestId)
        .orElseThrow(() -> new IllegalArgumentException("Request not found"));

    if (request.getStatus() != RequestStatus.PENDING) {
      throw new IllegalArgumentException("Request is not pending");
    }

    request.setStatus(RequestStatus.ACCEPTED);
    membershipRequestRepository.save(request);

    UserHouseholdAssignmentRequestDto assignment = new UserHouseholdAssignmentRequestDto();
    assignment.setUserId(request.getReceiver().getId());
    assignment.setHouseholdId(request.getHousehold().getId());

    householdService.addUserToHousehold(assignment);
  }



  /**
   * Cancel a membership request.
   *
   * @param requestId the request id
   */
  public void cancelRequest(Long requestId) {
    // Check if the request exists
    if (!membershipRequestRepository.existsById(requestId)) {
      throw new IllegalArgumentException("Request not found");
    }

    // Update the request status to "canceled"
    membershipRequestRepository.updateStatusById(requestId, RequestStatus.CANCELED);
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
  public List<MembershipRequestResponseDto> getSentInvitationsByUser(String userId) {
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
            invitation.getHousehold().getName(),
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
  public List<MembershipRequestResponseDto> getReceivedInvitationsByUser(String userId) {
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
            invitation.getHousehold().getName(),
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
   * Get active join requests by householdID.
   *
   * @param householdId the household id
   * @return the active join requests by user
   */
  public List<MembershipRequestResponseDto> getReceivedJoinRequestsByHousehold(String householdId) {
    List<MembershipRequest> requests =
        membershipRequestRepository.findAllByHouseholdIdAndTypeAndStatus(householdId,
            RequestType.JOIN_REQUEST,
            RequestStatus.PENDING);

    return requests.stream().map(request ->
        new MembershipRequestResponseDto(
            request.getId(),
            request.getHousehold().getId(),
            request.getHousehold().getName(),
            new UserResponseDto(request.getSender().getId(), request.getSender().getEmail(),
                request.getSender().getFullName(), request.getSender().getTlf(),
                request.getSender().getRole()),
            new UserResponseDto(request.getReceiver().getId(),
                request.getReceiver().getEmail(), request.getReceiver().getFullName(),
                request.getReceiver().getTlf(), request.getReceiver().getRole()),
            request.getType(),
            request.getStatus(),
            request.getCreated_at()
        )
    ).toList();
  }

  /**
   * Gets accepted received join requests by a household.
   *
   * @param householdId the household id
   * @return the accepted received join requests by household
   */
  public List<MembershipRequestResponseDto> getAcceptedReceivedJoinRequestsByHousehold(
      String householdId) {
    List<MembershipRequest> requests =
        membershipRequestRepository.findAllByHouseholdIdAndTypeAndStatus(householdId,
            RequestType.JOIN_REQUEST,
            RequestStatus.ACCEPTED);

    return requests.stream().map(request ->
        new MembershipRequestResponseDto(
            request.getId(),
            request.getHousehold().getId(),
            request.getHousehold().getName(),
            new UserResponseDto(request.getSender().getId(), request.getSender().getEmail(),
                request.getSender().getFullName(), request.getSender().getTlf(),
                request.getSender().getRole()),
            new UserResponseDto(request.getReceiver().getId(),
                request.getReceiver().getEmail(), request.getReceiver().getFullName(),
                request.getReceiver().getTlf(), request.getReceiver().getRole()),
            request.getType(),
            request.getStatus(),
            request.getCreated_at()
        )
    ).toList();
  }

  /**
   * Get invitations sent by a household, regardless of status (e.g., PENDING, ACCEPTED).
   *
   * @param householdId the ID of the household
   * @return list of membership invitations sent from the household
   */
  public List<MembershipRequestResponseDto> getInvitationsSentByHousehold(String householdId) {
    if (!householdRepository.existsById(householdId)) {
      throw new IllegalArgumentException("Household not found");
    }

    List<RequestStatus> statuses = List.of(RequestStatus.PENDING, RequestStatus.ACCEPTED);

    List<MembershipRequest> invitations =
        membershipRequestRepository.findAllByHouseholdIdAndTypeAndStatusIn(
            householdId, RequestType.INVITATION, statuses
        );

    return invitations.stream().map(invitation ->
        new MembershipRequestResponseDto(
            invitation.getId(),
            invitation.getHousehold().getId(),
            invitation.getHousehold().getName(),
            new UserResponseDto(
                invitation.getSender().getId(),
                invitation.getSender().getEmail(),
                invitation.getSender().getFullName(),
                invitation.getSender().getTlf(),
                invitation.getSender().getRole()
            ),
            new UserResponseDto(
                invitation.getReceiver().getId(),
                invitation.getReceiver().getEmail(),
                invitation.getReceiver().getFullName(),
                invitation.getReceiver().getTlf(),
                invitation.getReceiver().getRole()
            ),
            invitation.getType(),
            invitation.getStatus(),
            invitation.getCreated_at()
        )
    ).toList();
  }

}