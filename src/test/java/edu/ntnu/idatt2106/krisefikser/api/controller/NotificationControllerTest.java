package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import edu.ntnu.idatt2106.krisefikser.service.NotificationService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private NotificationController notificationController;

  private MockMvc mockMvc;
  private NotificationResponseDto testNotification;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();

    // Create a test notification to reuse in tests
    testNotification = new NotificationResponseDto();
    testNotification.setId(1L);
    testNotification.setMessage("Test notification content");
    testNotification.setType(NotificationType.MEMBERSHIP_REQUEST);
    testNotification.setRead(false);
    testNotification.setRecipientId("user-123");
    testNotification.setTimestamp(LocalDateTime.now());
  }

  @Test
  void getNotifications_shouldReturnOkWithNotifications() throws Exception {
    // Arrange
    List<NotificationResponseDto> notifications = List.of(testNotification);
    when(notificationService.getUserNotifications()).thenReturn(notifications);

    // Act & Assert
    mockMvc.perform(post("/api/notifications/get")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].message").value("Test notification content"))
        .andExpect(jsonPath("$[0].type").value(NotificationType.MEMBERSHIP_REQUEST.toString()))
        .andExpect(jsonPath("$[0].read").value(false))
        .andExpect(jsonPath("$[0].recipientId").value("user-123"));
  }

  @Test
  void getNotifications_shouldReturnBadRequest_whenServiceThrowsIllegalArgumentException()
      throws Exception {
    // Arrange
    when(notificationService.getUserNotifications())
        .thenThrow(new IllegalArgumentException("User not found"));

    // Act & Assert
    mockMvc.perform(post("/api/notifications/get")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("User not found"));
  }

  @Test
  void getNotifications_shouldReturnInternalServerError_whenServiceThrowsException()
      throws Exception {
    // Arrange
    when(notificationService.getUserNotifications())
        .thenThrow(new RuntimeException("Database error"));

    // Act & Assert
    mockMvc.perform(post("/api/notifications/get")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal server error"));
  }

  @Test
  void markNotificationAsRead_shouldReturnOk() throws Exception {
    // Arrange
    doNothing().when(notificationService).markNotificationAsRead(1L);

    // Act & Assert
    mockMvc.perform(put("/api/notifications/1/read")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Notification marked as read"));

    // Verify service method was called with correct ID
    verify(notificationService).markNotificationAsRead(1L);
  }

  @Test
  void markNotificationAsRead_shouldReturnBadRequest_whenServiceThrowsIllegalArgumentException()
      throws Exception {
    // Arrange
    doThrow(new IllegalArgumentException("Notification not found"))
        .when(notificationService).markNotificationAsRead(1L);

    // Act & Assert
    mockMvc.perform(put("/api/notifications/1/read")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Notification not found"));
  }

  @Test
  void markNotificationAsRead_shouldReturnInternalServerError_whenServiceThrowsException()
      throws Exception {
    // Arrange
    doThrow(new RuntimeException("Database error"))
        .when(notificationService).markNotificationAsRead(1L);

    // Act & Assert
    mockMvc.perform(put("/api/notifications/1/read")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal server error"));
  }
}