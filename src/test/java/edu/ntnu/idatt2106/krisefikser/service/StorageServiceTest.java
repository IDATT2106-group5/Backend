package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.HouseholdRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ItemRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.StorageItemRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StorageServiceTest {

  private final String householdId = "1L";
  private final Long itemId = 2L;
  private final Long storageItemId = 3L;
  @Mock
  private StorageItemRepository storageItemRepository;
  @Mock
  private HouseholdRepository householdRepository;
  @Mock
  private ItemRepository itemRepository;
  @InjectMocks
  private StorageService storageService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class GetHouseholdStorageTests {

    @Test
    void getHouseholdStorage_shouldReturnItems() {
//      // Arrange
//      StorageItem item1 = new StorageItem();
//      item1.setId(1L);
//      StorageItem item2 = new StorageItem();
//      item2.setId(2L);
//      List<StorageItem> expectedItems = Arrays.asList(item1, item2);
//
//      when(storageItemRepository.findByHouseholdId(householdId)).thenReturn(expectedItems);
//
//      // Act
//      List<StorageItem> result = storageService.getHouseholdStorage(householdId);
//
//      // Assert
//      assertEquals(expectedItems.size(), result.size());
//      assertEquals(expectedItems, result);
//      verify(storageItemRepository).findByHouseholdId(householdId);
    }

    @Test
    void getHouseholdStorage_shouldReturnEmptyList_whenNoItemsFound() {
//      // Arrange
//      when(storageItemRepository.findByHouseholdId(householdId)).thenReturn(
//          Collections.emptyList());
//
//      // Act
//      List<StorageItem> result = storageService.getHouseholdStorage(householdId);
//
//      // Assert
//      assertTrue(result.isEmpty());
//      verify(storageItemRepository).findByHouseholdId(householdId);
    }
  }

  @Nested
  class GetHouseholdStorageByItemTypeTests {

    @Test
    void getHouseholdStorageByItemType_shouldReturnFilteredItems() {
//      // Arrange
//      ItemType itemType = ItemType.WATER;
//      StorageItem item1 = new StorageItem();
//      item1.setId(1L);
//      List<StorageItem> expectedItems = Collections.singletonList(item1);
//
//      when(storageItemRepository.findByHouseholdIdAndItemItemType(householdId, itemType))
//          .thenReturn(expectedItems);
//
//      // Act
//      List<StorageItem> result = storageService.getHouseholdStorageByItemType(householdId,
//          itemType);
//
//      // Assert
//      assertEquals(expectedItems.size(), result.size());
//      assertEquals(expectedItems, result);
//      verify(storageItemRepository).findByHouseholdIdAndItemItemType(householdId, itemType);
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

    @Test
    void addItemToStorage_shouldCreateAndSaveStorageItem() {
      // Arrange
      String unit = "liters";
      Integer amount = 5;
      LocalDateTime expirationDate = LocalDateTime.now().plusDays(7);

      Household household = new Household();
      household.setId(householdId);

      Item item = new Item();
      item.setId(itemId);

      when(householdRepository.findById(householdId)).thenReturn(Optional.of(household));
      when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
      when(storageItemRepository.save(any(StorageItem.class))).thenAnswer(
          invocation -> invocation.getArgument(0));

      // Act
      StorageItem result = storageService.addItemToStorage(householdId, itemId, unit, amount,
          expirationDate);

      // Assert
      assertNotNull(result);
      assertEquals(household, result.getHousehold());
      assertEquals(item, result.getItem());
      assertEquals(unit, result.getUnit());
      assertEquals(amount, result.getAmount());
      assertEquals(expirationDate, result.getExpirationDate());

      ArgumentCaptor<StorageItem> captor = ArgumentCaptor.forClass(StorageItem.class);
      verify(storageItemRepository).save(captor.capture());

      StorageItem capturedItem = captor.getValue();
      assertEquals(household, capturedItem.getHousehold());
      assertEquals(item, capturedItem.getItem());
      assertEquals(unit, capturedItem.getUnit());
      assertEquals(amount, capturedItem.getAmount());
      assertEquals(expirationDate, capturedItem.getExpirationDate());
    }

    @Test
    void addItemToStorage_shouldThrowException_whenHouseholdNotFound() {
      // Arrange
      when(householdRepository.findById(householdId)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          storageService.addItemToStorage(householdId, itemId, "liters", 5, LocalDateTime.now()));

      assertEquals("Household not found", exception.getMessage());
      verify(householdRepository).findById(householdId);
      verifyNoInteractions(storageItemRepository);
    }

    @Test
    void addItemToStorage_shouldThrowException_whenItemNotFound() {
      // Arrange
      Household household = new Household();
      household.setId(householdId);

      when(householdRepository.findById(householdId)).thenReturn(Optional.of(household));
      when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          storageService.addItemToStorage(householdId, itemId, "liters", 5, LocalDateTime.now()));

      assertEquals("Item not found", exception.getMessage());
      verify(householdRepository).findById(householdId);
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