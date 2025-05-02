package edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest;

import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestStatus;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestType;
import java.sql.Timestamp;

/**
 * The type Membership request dto.
 */

public class MembershipRequestResponseDto {

  Long id;
  Long householdId;
  UserResponseDto sender;
  UserResponseDto recipient;
  RequestType requestType;
  RequestStatus status;
  Timestamp sentAt;

  /**
   * Constructor for MembershipRequestResponseDto.
   *
   * @param id          The ID of the membership request.
   * @param householdId The ID of the household.
   * @param sender      The sender of the request.
   * @param recipient   The recipient of the request.
   * @param requestType The type of the request.
   * @param status      The status of the request.
   * @param sentAt      The timestamp when the request was sent.
   */
  public MembershipRequestResponseDto(Long id, Long householdId, UserResponseDto sender,
      UserResponseDto recipient,
      RequestType requestType, RequestStatus status, Timestamp sentAt) {
    this.id = id;
    this.householdId = householdId;
    this.sender = sender;
    this.recipient = recipient;
    this.requestType = requestType;
    this.sentAt = sentAt;
    this.status = status;
  }

  public Timestamp getSentAt() {
    return sentAt;
  }

  public void setSentAt(Timestamp sentAt) {
    this.sentAt = sentAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(Long householdId) {
    this.householdId = householdId;
  }

  public UserResponseDto getSender() {
    return sender;
  }

  public void setSender(UserResponseDto sender) {
    this.sender = sender;
  }

  public UserResponseDto getRecipient() {
    return recipient;
  }

  public void setRecipient(UserResponseDto recipient) {
    this.recipient = recipient;
  }

  public RequestType getRequestType() {
    return requestType;
  }

  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }

  public RequestStatus getStatus() {
    return status;
  }

  public void setStatus(RequestStatus status) {
    this.status = status;
  }
}
