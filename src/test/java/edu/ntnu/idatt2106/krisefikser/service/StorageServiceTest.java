package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.api.dto.StorageItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
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

    private Authentication authentication;
    private SecurityContext securityContext;
    private User user;

    @BeforeEach
    void setUp() {
      authentication = mock(Authentication.class);
      securityContext = mock(SecurityContext.class);
      SecurityContextHolder.setContext(securityContext);

      user = new User();
      Household household = new Household();
      household.setId(householdId);
      user.setHousehold(household);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("user@example.com");
      when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
      SecurityContextHolder.clearContext();
    }

    @Test
    void getStorageItemsByHousehold_shouldReturnMappedDtos() {
      // Arrange
      Item item1 = new Item();
      item1.setId(1L);
      item1.setName("Rice");
      item1.setCaloricAmount(100);
      item1.setItemType(ItemType.FOOD);

      StorageItem storageItem1 = new StorageItem();
      storageItem1.setId(1L);
      storageItem1.setItem(item1);
      storageItem1.setHousehold(user.getHousehold());
      storageItem1.setUnit("kg");
      storageItem1.setAmount(2);
      storageItem1.setExpirationDate(LocalDateTime.now().plusDays(30));

      List<StorageItem> storageItems = Collections.singletonList(storageItem1);

      when(storageItemRepository.findByHouseholdId(householdId)).thenReturn(storageItems);

      // Act
      List<StorageItemResponseDto> result = storageService.getStorageItemsByHousehold();

      // Assert
      assertEquals(1, result.size());
      StorageItemResponseDto dto = result.get(0);
      assertEquals(storageItem1.getId(), dto.getItemId());
      // Use getter methods instead of direct access to private fields
      assertEquals(storageItem1.getItem().getId(), dto.getItem().getId());
      assertEquals(storageItem1.getItem().getName(), dto.getItem().getName());
      assertEquals(storageItem1.getHousehold().getId(), dto.getHouseholdId());
      assertEquals(storageItem1.getUnit(), dto.getUnit());
      assertEquals(storageItem1.getAmount(), dto.getAmount());
      assertEquals(storageItem1.getExpirationDate(), dto.getExpiration());

      verify(userRepository).getUserByEmail("user@example.com");
      verify(storageItemRepository).findByHouseholdId(householdId);
    }

    @Test
    void getStorageItemsByHousehold_shouldThrowException_whenNoUserLoggedIn() {
      // Arrange
      when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> storageService.getStorageItemsByHousehold());

      assertEquals("No user logged in", exception.getMessage());
      verify(userRepository).getUserByEmail("user@example.com");
      verifyNoInteractions(storageItemRepository);
    }
  }

  @Nested
  class GetStorageItemsByHouseholdAndTypeTests {

    private Authentication authentication;
    private SecurityContext securityContext;
    private User user;

    @BeforeEach
    void setUp() {
      authentication = mock(Authentication.class);
      securityContext = mock(SecurityContext.class);
      SecurityContextHolder.setContext(securityContext);

      user = new User();
      Household household = new Household();
      household.setId(householdId);
      user.setHousehold(household);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("user@example.com");
      when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
      SecurityContextHolder.clearContext();
    }

    @Test
    void getStorageItemsByHouseholdAndType_shouldReturnFilteredItems() {
      // Arrange
      ItemType itemType = ItemType.FOOD;

      Item item = new Item();
      item.setId(1L);
      item.setItemType(itemType);

      StorageItem storageItem = new StorageItem();
      storageItem.setId(1L);
      storageItem.setItem(item);

      List<StorageItem> expectedItems = Collections.singletonList(storageItem);

      when(storageItemRepository.findByHouseholdIdAndItemItemType(householdId, itemType))
          .thenReturn(expectedItems);

      // Act
      List<StorageItem> result = storageService.getStorageItemsByHouseholdAndType(itemType);

      // Assert
      assertEquals(expectedItems.size(), result.size());
      assertEquals(expectedItems.get(0), result.get(0));
      verify(storageItemRepository).findByHouseholdIdAndItemItemType(householdId, itemType);
    }

    @Test
    void getStorageItemsByHouseholdAndType_shouldThrowException_whenNoUserLoggedIn() {
      // Arrange
      when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> storageService.getStorageItemsByHouseholdAndType(ItemType.FOOD));

      assertEquals("No user logged in", exception.getMessage());
      verify(userRepository).getUserByEmail("user@example.com");
      verifyNoInteractions(storageItemRepository);
    }
  }

  @Nested
  class GetExpiringItemsTests {

    private Authentication authentication;
    private SecurityContext securityContext;
    private User user;
    private LocalDateTime before;

    @BeforeEach
    void setUp() {
      authentication = mock(Authentication.class);
      securityContext = mock(SecurityContext.class);
      SecurityContextHolder.setContext(securityContext);

      user = new User();
      Household household = new Household();
      household.setId(householdId);
      user.setHousehold(household);

      before = LocalDateTime.now().plusDays(7);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("user@example.com");
      when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
      SecurityContextHolder.clearContext();
    }

    @Test
    void getExpiringItems_shouldReturnItemsExpiringBeforeDate() {
      // Arrange
      StorageItem item1 = new StorageItem();
      item1.setId(1L);
      item1.setExpirationDate(LocalDateTime.now().plusDays(3));

      List<StorageItem> expectedItems = Collections.singletonList(item1);

      when(storageItemRepository.findByHouseholdIdAndExpirationDateBefore(householdId, before))
          .thenReturn(expectedItems);

      // Act
      List<StorageItem> result = storageService.getExpiringItems(before);

      // Assert
      assertEquals(expectedItems.size(), result.size());
      assertEquals(expectedItems.get(0), result.get(0));
      verify(storageItemRepository).findByHouseholdIdAndExpirationDateBefore(householdId, before);
    }

    @Test
    void getExpiringItems_shouldThrowException_whenNoUserLoggedIn() {
      // Arrange
      when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> storageService.getExpiringItems(before));

      assertEquals("No user logged in", exception.getMessage());
      verify(userRepository).getUserByEmail("user@example.com");
      verifyNoInteractions(storageItemRepository);
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

    private Authentication authentication;
    private SecurityContext securityContext;
    private User user;

    @BeforeEach
    void setUp() {
      // Create and set up security context mock
      authentication = mock(Authentication.class);
      securityContext = mock(SecurityContext.class);
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
      when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
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
      when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          storageService.addItemToStorage(itemId, "liters", 5, LocalDateTime.now()));

      assertEquals("No user logged in", exception.getMessage());
      verify(userRepository).getUserByEmail("user@example.com");
      verifyNoInteractions(storageItemRepository);
    }

    @Test
    void addItemToStorage_shouldThrowException_whenItemNotFound() {
      // Arrange
      when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
      when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          storageService.addItemToStorage(itemId, "liters", 5, LocalDateTime.now()));

      assertEquals("Item not found", exception.getMessage());
      verify(userRepository).getUserByEmail("user@example.com");
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
      Integer newAmount = null;
      LocalDateTime newExpirationDate = null;

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
      StorageItem result = storageService.updateStorageItem(storageItemId, newUnit, newAmount,
          newExpirationDate);

      // Assert
      assertNotNull(result);
      assertEquals(newUnit, result.getUnit());
      assertEquals(existingItem.getAmount(), result.getAmount());
      assertEquals(newExpirationDate, result.getExpirationDate());

      ArgumentCaptor<StorageItem> captor = ArgumentCaptor.forClass(StorageItem.class);
      verify(storageItemRepository).save(captor.capture());

      StorageItem capturedItem = captor.getValue();
      assertEquals(newUnit, capturedItem.getUnit());
      assertEquals(existingItem.getAmount(), capturedItem.getAmount());
      assertEquals(newExpirationDate, capturedItem.getExpirationDate());
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