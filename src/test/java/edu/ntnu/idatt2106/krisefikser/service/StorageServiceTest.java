package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ItemRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.StorageItemRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class StorageServiceTest {

  private final String householdId = "1L";
  private final Long itemId = 2L;
  private final Long storageItemId = 3L;

  @Mock
  private StorageItemRepository storageItemRepository;

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private StorageService storageService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class GetStorageItemsByHouseholdTests {

    private User user;

    @BeforeEach
    void setUp() {
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      SecurityContextHolder.setContext(securityContext);

      user = new User();
      Household household = new Household();
      household.setId(householdId);
      user.setHousehold(household);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("user@example.com");
    }

    @AfterEach
    void tearDown() {
      SecurityContextHolder.clearContext();
    }
  }

  @Nested
  class GetStorageItemsByHouseholdAndTypeTests {

    @BeforeEach
    void setUp() {
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      SecurityContextHolder.setContext(securityContext);

      User user = new User();
      Household household = new Household();
      household.setId(householdId);
      user.setHousehold(household);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("user@example.com");
    }

    @AfterEach
    void tearDown() {
      SecurityContextHolder.clearContext();
    }
  }

  @Nested
  class GetExpiredItemsTests {

    @Test
    void getExpiredItems_shouldReturnExpiredItems() {
      // Arrange
      StorageItem item1 = new StorageItem();
      item1.setId(1L);
      item1.setExpirationDate(LocalDateTime.now().minusDays(1));
      List<StorageItem> expectedItems = Collections.singletonList(item1);

      when(storageItemRepository.findByHouseholdIdAndExpirationDateBefore(
          eq(householdId), any(LocalDateTime.class)))
          .thenReturn(expectedItems);

      // Act
      List<StorageItem> result = storageService.getExpiredItems(householdId);

      // Assert
      assertEquals(expectedItems.size(), result.size());
      assertEquals(expectedItems, result);
      verify(storageItemRepository).findByHouseholdIdAndExpirationDateBefore(
          eq(householdId), any(LocalDateTime.class));
    }
  }

  @Nested
  class AddItemToStorageTests {

    private User user;

    @BeforeEach
    void setUp() {
      // Create and set up security context mock
      Authentication authentication = mock(Authentication.class);
      SecurityContext securityContext = mock(SecurityContext.class);
      SecurityContextHolder.setContext(securityContext);

      // Create user with household
      user = new User();
      Household household = new Household();
      household.setId(householdId);
      user.setHousehold(household);

      // Set up mock behaviors
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("user@example.com");
    }

    @AfterEach
    void tearDown() {
      // Clear security context after each test
      SecurityContextHolder.clearContext();
    }

    @Test
    void addItemToStorage_shouldCreateAndSaveStorageItem() {
      // Arrange
      String unit = "liters";
      Integer amount = 5;
      LocalDateTime expirationDate = LocalDateTime.now().plusDays(7);

      Item item = new Item();
      item.setId(itemId);

      // Mock user repository to return our user with household
      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
      when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
      when(storageItemRepository.save(any(StorageItem.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      // Act
      StorageItem result = storageService.addItemToStorage(itemId, unit, amount, expirationDate);

      // Assert
      assertNotNull(result);
      assertEquals(user.getHousehold(), result.getHousehold());
      assertEquals(item, result.getItem());
      assertEquals(unit, result.getUnit());
      assertEquals(amount, result.getAmount());
      assertEquals(expirationDate, result.getExpirationDate());

      ArgumentCaptor<StorageItem> captor = ArgumentCaptor.forClass(StorageItem.class);
      verify(storageItemRepository).save(captor.capture());

      StorageItem capturedItem = captor.getValue();
      assertEquals(user.getHousehold(), capturedItem.getHousehold());
      assertEquals(item, capturedItem.getItem());
      assertEquals(unit, capturedItem.getUnit());
      assertEquals(amount, capturedItem.getAmount());
      assertEquals(expirationDate, capturedItem.getExpirationDate());
    }

    @Test
    void addItemToStorage_shouldThrowException_whenNoUserLoggedIn() {
      // Arrange
      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          storageService.addItemToStorage(itemId, "liters", 5, LocalDateTime.now()));

      assertEquals("No user logged in", exception.getMessage());
      verify(userRepository).findByEmail("user@example.com");
      verifyNoInteractions(storageItemRepository);
    }

    @Test
    void addItemToStorage_shouldThrowException_whenItemNotFound() {
      // Arrange
      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
      when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          storageService.addItemToStorage(itemId, "liters", 5, LocalDateTime.now()));

      assertEquals("Item not found", exception.getMessage());
      verify(userRepository).findByEmail("user@example.com");
      verify(itemRepository).findById(itemId);
      verifyNoInteractions(storageItemRepository);
    }
  }

  @Nested
  class RemoveItemFromStorageTests {

    @Test
    void removeItemFromStorage_shouldDeleteItemById() {
      // Act
      storageService.removeItemFromStorage(storageItemId);

      // Assert
      verify(storageItemRepository).deleteById(storageItemId);
    }
  }

  @Nested
  class UpdateStorageItemTests {

    @Test
    void updateStorageItem_shouldUpdateAndReturnItem() {
      // Arrange
      String newUnit = "kilograms";
      Integer newAmount = 10;
      LocalDateTime newExpirationDate = LocalDateTime.now().plusDays(14);

      StorageItem existingItem = new StorageItem();
      existingItem.setId(storageItemId);
      existingItem.setUnit("grams");
      existingItem.setAmount(5);
      existingItem.setExpirationDate(LocalDateTime.now().plusDays(7));

      when(storageItemRepository.findById(storageItemId)).thenReturn(Optional.of(existingItem));
      when(storageItemRepository.save(any(StorageItem.class))).thenAnswer(
          invocation -> invocation.getArgument(0));

      // Act
      StorageItem result = storageService.updateStorageItem(storageItemId, newUnit, newAmount,
          newExpirationDate);

      // Assert
      assertNotNull(result);
      assertEquals(newUnit, result.getUnit());
      assertEquals(newAmount, result.getAmount());
      assertEquals(newExpirationDate, result.getExpirationDate());

      ArgumentCaptor<StorageItem> captor = ArgumentCaptor.forClass(StorageItem.class);
      verify(storageItemRepository).save(captor.capture());

      StorageItem capturedItem = captor.getValue();
      assertEquals(newUnit, capturedItem.getUnit());
      assertEquals(newAmount, capturedItem.getAmount());
      assertEquals(newExpirationDate, capturedItem.getExpirationDate());
    }

    @Test
    void updateStorageItem_shouldUpdateOnlyProvidedFields() {
      // Arrange
      String newUnit = "kilograms";

      StorageItem existingItem = new StorageItem();
      existingItem.setId(storageItemId);
      existingItem.setUnit("grams");
      existingItem.setAmount(5);
      LocalDateTime originalExpirationDate = LocalDateTime.now().plusDays(7);
      existingItem.setExpirationDate(originalExpirationDate);

      when(storageItemRepository.findById(storageItemId)).thenReturn(Optional.of(existingItem));
      when(storageItemRepository.save(any(StorageItem.class))).thenAnswer(
          invocation -> invocation.getArgument(0));

      // Act
      StorageItem result = storageService.updateStorageItem(storageItemId, newUnit, null,
          null);

      // Assert
      assertNotNull(result);
      assertEquals(newUnit, result.getUnit());
      assertEquals(existingItem.getAmount(), result.getAmount());
      assertNull(result.getExpirationDate());

      ArgumentCaptor<StorageItem> captor = ArgumentCaptor.forClass(StorageItem.class);
      verify(storageItemRepository).save(captor.capture());

      StorageItem capturedItem = captor.getValue();
      assertEquals(newUnit, capturedItem.getUnit());
      assertEquals(existingItem.getAmount(), capturedItem.getAmount());
      assertNull(capturedItem.getExpirationDate());
    }

    @Test
    void updateStorageItem_shouldThrowException_whenItemNotFound() {
      // Arrange
      when(storageItemRepository.findById(storageItemId)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          storageService.updateStorageItem(storageItemId, "kg", 10, LocalDateTime.now()));

      assertEquals("Storage item not found", exception.getMessage());
      verify(storageItemRepository).findById(storageItemId);
      verify(storageItemRepository, never()).save(any());
    }
  }

  @Nested
  class UpdateItemAmountTests {

    @Test
    void updateItemAmount_shouldUpdateAndReturnItem() {
      // Arrange
      Integer newAmount = 10;
      StorageItem existingItem = new StorageItem();
      existingItem.setId(storageItemId);
      existingItem.setAmount(5);

      when(storageItemRepository.findById(storageItemId)).thenReturn(Optional.of(existingItem));
      when(storageItemRepository.save(any(StorageItem.class))).thenAnswer(
          invocation -> invocation.getArgument(0));

      // Act
      StorageItem result = storageService.updateItemAmount(storageItemId, newAmount);

      // Assert
      assertNotNull(result);
      assertEquals(newAmount, result.getAmount());

      ArgumentCaptor<StorageItem> captor = ArgumentCaptor.forClass(StorageItem.class);
      verify(storageItemRepository).save(captor.capture());
      assertEquals(newAmount, captor.getValue().getAmount());
    }

    @Test
    void updateItemAmount_shouldThrowException_whenItemNotFound() {
      // Arrange
      when(storageItemRepository.findById(storageItemId)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          storageService.updateItemAmount(storageItemId, 10));

      assertEquals("Storage item not found", exception.getMessage());
      verify(storageItemRepository).findById(storageItemId);
      verify(storageItemRepository, never()).save(any());
    }
  }
}