package edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest;

import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestStatus;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestType;
import java.sql.Timestamp;

public class MembershipRequestResponseDto {

  Long id;
  String householdId;
  String householdName;
  UserResponseDto sender;
  UserResponseDto recipient;
  RequestType requestType;
  RequestStatus status;
  Timestamp sentAt;

  public MembershipRequestResponseDto(Long id, String householdId, String householdName,
      UserResponseDto sender,
      UserResponseDto recipient,
      RequestType requestType, RequestStatus status, Timestamp sentAt) {
    this.id = id;
    this.householdId = householdId;
    this.householdName = householdName;
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

  public String getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(String householdId) {
    this.householdId = householdId;
  }

  public String getHouseholdName() {
    return householdName;
  }

  public void setHouseholdName(String householdName) {
    this.householdName = householdName;
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
