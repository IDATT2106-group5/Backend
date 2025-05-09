package edu.ntnu.idatt2106.krisefikser.service;

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
import edu.ntnu.idatt2106.krisefikser.persistance.repository.MembershipRequestRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * The type Membership request service.
 */
@Service
public class MembershipRequestService {

  private static final Logger logger = LoggerFactory.getLogger(MembershipRequestService.class);
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
   * @param notificationService         the notification service
   * @param householdService            the household service
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
    logger.info("MembershipRequestService initialized");
  }

  /**
   * Send an invitation to a user to join a household.
   *
   * @param email the email
   */
  public void sendInvitation(String email) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User currentUser = userRepository.findByEmail(currentUserEmail)
        .orElseThrow(() -> {
          logger.warn("Current user not found with email: {}", currentUserEmail);
          return new IllegalArgumentException("Current user not found");
        });
    Household household = currentUser.getHousehold();
    String householdId = household.getId();

    logger.info("Sending invitation to user with email: {} for household: {}", email, householdId);

    logger.debug("Looking up user by email: {}", email);
    User receiver = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.warn("User not found with email: {}", email);
          return new IllegalArgumentException("User with email not found: " + email);
        });
    logger.debug("Found user: id={}", receiver.getId());

    MembershipRequest membershipRequest = new MembershipRequest();
    membershipRequest.setHousehold(household);
    membershipRequest.setSender(household.getOwner());
    membershipRequest.setReceiver(receiver);
    membershipRequest.setType(RequestType.INVITATION);
    membershipRequest.setStatus(RequestStatus.PENDING);
    membershipRequest.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    logger.debug("Created membership request: type={}, status={}",
        membershipRequest.getType(), membershipRequest.getStatus());

    membershipRequestRepository.save(membershipRequest);
    logger.debug("Saved membership request with ID: {}", membershipRequest.getId());

    // Send a notification to the receiver
    NotificationDto notificationDto = new NotificationDto(
        NotificationType.MEMBERSHIP_REQUEST,
        receiver.getId(),
        LocalDateTime.now(),
        false,
        "You have received an invitation to join the household: " + household.getName()
    );
    logger.debug("Created notification for receiver: {}", receiver.getId());

    notificationService.saveNotification(notificationDto);
    notificationService.sendPrivateNotification(receiver.getId(), notificationDto);
    logger.info("Invitation sent successfully to user: {} for household: {}", email,
        household.getName());
  }

  /**
   * Send a request to join a household.
   */
  public void sendJoinRequest(String householdId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    User sender = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.warn("User not found with email: {}", email);
          return new IllegalArgumentException("User not found");
        });

    logger.info("Processing join request for household: {} from user: {}",
        householdId, sender.getFullName());

    logger.debug("Looking up household with ID: {}", householdId);
    Household household = householdRepository.findById(householdId)
        .orElseThrow(() -> {
          logger.warn("Household not found with ID: {}", householdId);
          return new IllegalArgumentException("Household not found");
        });
    logger.debug("Found household: {}", household.getName());

    MembershipRequest membershipRequest = new MembershipRequest();
    membershipRequest.setHousehold(household);
    membershipRequest.setSender(sender);
    membershipRequest.setReceiver(household.getOwner());
    membershipRequest.setType(RequestType.JOIN_REQUEST);
    membershipRequest.setStatus(RequestStatus.PENDING);
    membershipRequest.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    logger.debug("Created join request: sender={}, receiver={}, status={}",
        sender.getFullName(), household.getOwner().getFullName(), membershipRequest.getStatus());

    membershipRequestRepository.save(membershipRequest);
    logger.debug("Saved join request with ID: {}", membershipRequest.getId());

    // Send a notification to the receiver
    NotificationDto notificationDto = new NotificationDto(
        NotificationType.MEMBERSHIP_REQUEST,
        household.getOwner().getId(),
        LocalDateTime.now(),
        false,
        sender.getFullName() + " has requested to join the household: " + household.getName()
    );
    logger.debug("Created notification for household owner: {}", household.getOwner().getId());

    notificationService.saveNotification(notificationDto);
    notificationService.broadcastNotification(notificationDto);
    logger.info("Join request sent successfully from user: {} to household: {}",
        sender.getFullName(), household.getName());
  }

  /**
   * Accept a membership join request.
   *
   * @param requestId the request id
   */
  public void acceptJoinRequest(Long requestId) {
    logger.info("Accepting join request with ID: {}", requestId);

    logger.debug("Looking up membership request with ID: {}", requestId);
    MembershipRequest request = membershipRequestRepository.findById(requestId)
        .orElseThrow(() -> {
          logger.warn("Membership request not found with ID: {}", requestId);
          return new IllegalArgumentException("Request not found");
        });
    logger.debug("Found request: type={}, status={}", request.getType(), request.getStatus());

    if (request.getStatus() != RequestStatus.PENDING) {
      logger.warn("Cannot accept request with ID: {} because status is: {}",
          requestId, request.getStatus());
      throw new IllegalArgumentException("Request is not pending");
    }

    request.setStatus(RequestStatus.ACCEPTED);
    membershipRequestRepository.save(request);
    logger.debug("Updated request status to ACCEPTED");

    UserHouseholdAssignmentRequestDto assignment = new UserHouseholdAssignmentRequestDto();
    assignment.setUserId(request.getSender().getId());
    logger.debug("Created assignment: userId={}, householdId={}",
        request.getSender().getId(), request.getHousehold().getId());

    householdService.addUserToHousehold(assignment);
    logger.info("Join request accepted: user {} added to household {}",
        request.getSender().getFullName(), request.getHousehold().getName());
  }

  /**
   * Accept an invitation request.
   *
   * @param requestId the request id
   */
  public void acceptInvitationRequest(Long requestId) {
    logger.info("Accepting invitation request with ID: {}", requestId);

    logger.debug("Looking up membership request with ID: {}", requestId);
    MembershipRequest request = membershipRequestRepository.findById(requestId)
        .orElseThrow(() -> {
          logger.warn("Membership request not found with ID: {}", requestId);
          return new IllegalArgumentException("Request not found");
        });
    logger.debug("Found request: type={}, status={}", request.getType(), request.getStatus());

    if (request.getStatus() != RequestStatus.PENDING) {
      logger.warn("Cannot accept invitation with ID: {} because status is: {}",
          requestId, request.getStatus());
      throw new IllegalArgumentException("Request is not pending");
    }

    request.setStatus(RequestStatus.ACCEPTED);
    membershipRequestRepository.save(request);
    logger.debug("Updated invitation status to ACCEPTED");

    UserHouseholdAssignmentRequestDto assignment = new UserHouseholdAssignmentRequestDto();
    assignment.setUserId(request.getReceiver().getId());
    assignment.setHouseholdId(request.getHousehold().getId());
    logger.debug("Created assignment: userId={}, householdId={}",
        request.getReceiver().getId(), request.getHousehold().getId());

    householdService.addUserToHousehold(assignment);
    logger.info("Invitation accepted: user {} added to household {}",
        request.getReceiver().getFullName(), request.getHousehold().getName());
  }

  /**
   * Cancel a membership request.
   *
   * @param requestId the request id
   */
  public void cancelRequest(Long requestId) {
    logger.info("Cancelling membership request with ID: {}", requestId);

    // Check if the request exists
    if (!membershipRequestRepository.existsById(requestId)) {
      logger.warn("Cannot cancel request - not found with ID: {}", requestId);
      throw new IllegalArgumentException("Request not found");
    }
    logger.debug("Request found with ID: {}", requestId);

    // Update the request status to "canceled"
    membershipRequestRepository.updateStatusById(requestId, RequestStatus.CANCELED);
    logger.info("Request with ID {} successfully cancelled", requestId);
  }

  /**
   * Reject a membership request.
   *
   * @param requestId the request id
   */
  public void declineRequest(Long requestId) {
    logger.info("Declining membership request with ID: {}", requestId);

    // Check if the request exists
    if (!membershipRequestRepository.existsById(requestId)) {
      logger.warn("Cannot decline request - not found with ID: {}", requestId);
      throw new IllegalArgumentException("Request not found");
    }
    logger.debug("Request found with ID: {}", requestId);

    // Update the request status to "rejected"
    membershipRequestRepository.updateStatusById(requestId, RequestStatus.REJECTED);
    logger.info("Request with ID {} successfully declined", requestId);
  }

  /**
   * Get received invitations by user.
   *
   * @return the active invitations by user
   */
  public List<MembershipRequestResponseDto> getReceivedInvitationsByUser() {
    logger.info("Getting received invitations for user with ID");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.warn("User not found with email: {}", email);
          return new IllegalArgumentException("User not found");
        });

    List<MembershipRequest> invitations =
        membershipRequestRepository.findAllByReceiverAndTypeAndStatus(
            user, RequestType.INVITATION, RequestStatus.PENDING);
    logger.debug("Found {} pending invitations received by user", invitations.size());

    List<MembershipRequestResponseDto> result = invitations.stream().map(invitation ->
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
            invitation.getCreatedAt()
        )
    ).toList();

    logger.info("Returning {} received invitations for user {}", result.size(), user.getFullName());
    return result;
  }

  /**
   * Get active join requests by householdID.
   *
   * @return the active join requests by user
   */
  public List<MembershipRequestResponseDto> getReceivedJoinRequestsByHousehold() {
    logger.info("Getting received join requests for household with ID");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.warn("User not found with email: {}", email);
          return new IllegalArgumentException("User not found");
        });
    Household household = user.getHousehold();

    List<MembershipRequest> requests =
        membershipRequestRepository.findAllByHouseholdIdAndTypeAndStatus(
            household.getId(), RequestType.JOIN_REQUEST, RequestStatus.PENDING);
    logger.debug("Found {} pending join requests for household {}", requests.size(),
        household.getId());

    List<MembershipRequestResponseDto> result = requests.stream().map(request ->
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
            request.getCreatedAt()
        )
    ).toList();

    logger.info("Returning {} received join requests for household {}", result.size(),
        household.getId());
    return result;
  }

  /**
   * Gets accepted received join requests by a household.
   *
   * @return the accepted received join requests by household
   */
  public List<MembershipRequestResponseDto> getAcceptedReceivedJoinRequestsByHousehold() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.warn("User not found with email: {}", email);
          return new IllegalArgumentException("User not found");
        });
    Household household = user.getHousehold();
    String householdId = household.getId();

    logger.info("Getting accepted join requests for household with ID: {}", householdId);

    List<MembershipRequest> requests =
        membershipRequestRepository.findAllByHouseholdIdAndTypeAndStatus(
            householdId, RequestType.JOIN_REQUEST, RequestStatus.ACCEPTED);
    logger.debug("Found {} accepted join requests for household {}", requests.size(), householdId);

    List<MembershipRequestResponseDto> result = requests.stream().map(request ->
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
            request.getCreatedAt()
        )
    ).toList();

    logger.info("Returning {} accepted join requests for household {}", result.size(), householdId);
    return result;
  }

  /**
   * Get invitations sent by a household, regardless of status (e.g., PENDING, ACCEPTED).
   *
   * @return list of membership invitations sent from the household
   */
  public List<MembershipRequestResponseDto> getInvitationsSentByHousehold() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.warn("User not found with email: {}", email);
          return new IllegalArgumentException("User not found");
        });
    Household household = user.getHousehold();
    String householdId = household.getId();

    logger.info("Getting invitations sent by household with ID: {}", householdId);

    List<RequestStatus> statuses = List.of(RequestStatus.PENDING, RequestStatus.ACCEPTED);
    logger.debug("Looking up invitations with statuses: {}", statuses);

    List<MembershipRequest> invitations =
        membershipRequestRepository.findAllByHouseholdIdAndTypeAndStatusIn(
            householdId, RequestType.INVITATION, statuses);
    logger.debug("Found {} invitations sent by household {}", invitations.size(), householdId);

    List<MembershipRequestResponseDto> result = invitations.stream().map(invitation ->
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
            invitation.getCreatedAt()
        )
    ).toList();

    logger.info("Returning {} invitations sent by household {}", result.size(), householdId);
    return result;
  }
}