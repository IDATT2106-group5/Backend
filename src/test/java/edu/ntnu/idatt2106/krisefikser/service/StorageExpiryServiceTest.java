package edu.ntnu.idatt2106.krisefikser.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.StorageItemRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StorageExpiryServiceTest {

  @Mock
  private StorageItemRepository storageRepository;

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private StorageExpiryService storageExpiryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class CheckForExpiringItemsTests {

    @Test
    void sendsNotificationsForExpiringItems() {
      StorageItem item = mock(StorageItem.class);
      edu.ntnu.idatt2106.krisefikser.persistance.entity.Item mockItem =
          mock(edu.ntnu.idatt2106.krisefikser.persistance.entity.Item.class);
      when(item.getItem()).thenReturn(mockItem);
      when(mockItem.getName()).thenReturn("Test Item");

      when(storageRepository.findExpiringItems(any(LocalDateTime.class), any(LocalDateTime.class)))
          .thenReturn(List.of(item));

      storageExpiryService.checkForExpiringItems();

      verify(notificationService, times(1)).sendExpiryNotification(item);
    }

    @Test
    void doesNotSendNotificationsWhenNoItemsAreExpiring() {
      when(storageRepository.findExpiringItems(any(LocalDateTime.class), any(LocalDateTime.class)))
          .thenReturn(Collections.emptyList());

      storageExpiryService.checkForExpiringItems();

      verify(notificationService, never()).sendExpiryNotification(any());
    }

    @Test
    void logsErrorWhenNotificationFails() {
      // Create and configure mocks
      StorageItem item = mock(StorageItem.class);
      edu.ntnu.idatt2106.krisefikser.persistance.entity.Item mockItem =
          mock(edu.ntnu.idatt2106.krisefikser.persistance.entity.Item.class);
      when(item.getItem()).thenReturn(mockItem);
      when(mockItem.getName()).thenReturn("Test Item");

      when(storageRepository.findExpiringItems(any(LocalDateTime.class), any(LocalDateTime.class)))
          .thenReturn(List.of(item));
      doThrow(new RuntimeException("Notification failed")).when(notificationService)
          .sendExpiryNotification(item);

      storageExpiryService.checkForExpiringItems();

      verify(notificationService, times(1)).sendExpiryNotification(item);
    }
  }
}