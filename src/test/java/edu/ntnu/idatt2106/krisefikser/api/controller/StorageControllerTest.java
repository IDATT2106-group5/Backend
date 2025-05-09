package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ntnu.idatt2106.krisefikser.api.controller.storage.StorageController;
import edu.ntnu.idatt2106.krisefikser.api.dto.item.ItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.storage.StorageItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.household.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.item.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.storage.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import edu.ntnu.idatt2106.krisefikser.service.storage.StorageService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
class StorageControllerTest {

  @Mock
  private StorageService storageService;

  @InjectMocks
  private StorageController storageController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  private StorageItem testStorageItem;
  private StorageItemResponseDto testStorageItemDto;
  private LocalDateTime now;
  private String householdId;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(storageController).build();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    now = LocalDateTime.now();
    householdId = "household-123";

    // Create test item
    Item item = new Item("Test Item", 100, ItemType.FOOD);
    item.setId(1L);

    // Create test household
    Household household = new Household();
    household.setId(householdId);

    // Create test storage item
    testStorageItem = new StorageItem();
    testStorageItem.setId(1L);
    testStorageItem.setItem(item);
    testStorageItem.setHousehold(household);
    testStorageItem.setUnit("kg");
    testStorageItem.setAmount(5);
    testStorageItem.setExpirationDate(now.plusDays(7));
    testStorageItem.setDateAdded(now);

    // Create test DTO
    ItemResponseDto itemDto = new ItemResponseDto(1L, "Test Item", 100, ItemType.FOOD);
    testStorageItemDto = new StorageItemResponseDto(1L, itemDto, householdId, "kg", 5,
        now.plusDays(7));
  }

  @Test
  void getStorageItemsByHousehold_shouldReturnOkWithItems() throws Exception {
    // Arrange
    when(storageService.getStorageItemsByHousehold())
        .thenReturn(List.of(testStorageItemDto));

    // Act & Assert
    mockMvc.perform(get("/api/storage/household"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].itemId").value(1))
        .andExpect(jsonPath("$[0].item.name").value("Test Item"))
        .andExpect(jsonPath("$[0].item.itemType").value("FOOD"))
        .andExpect(jsonPath("$[0].householdId").value(householdId))
        .andExpect(jsonPath("$[0].unit").value("kg"))
        .andExpect(jsonPath("$[0].amount").value(5));
  }

  @Test
  void getStorageItemsByHouseholdAndType_shouldReturnOkWithFilteredItems() throws Exception {
    // Arrange
    when(storageService.getStorageItemsByHouseholdAndType(ItemType.FOOD))
        .thenReturn(List.of(testStorageItem));

    // Act & Assert
    mockMvc.perform(get("/api/storage/household/type/FOOD"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].unit").value("kg"))
        .andExpect(jsonPath("$[0].amount").value(5));
  }

  @Test
  void getExpiringItems_shouldReturnOkWithExpiringItems() throws Exception {
    // Arrange
    LocalDateTime expirationDate = now.plusDays(3);
    when(storageService.getExpiringItems(expirationDate))
        .thenReturn(List.of(testStorageItem));

    // Act & Assert
    mockMvc.perform(get("/api/storage/household/expiring")
            .param("before", expirationDate.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].unit").value("kg"));
  }

  @Test
  void addItemToStorage_shouldReturnOkWithStorageItem() throws Exception {
    // Arrange
    Long itemId = 1L;
    Map<String, Object> requestBody = Map.of(
        "unit", "kg",
        "amount", 5,
        "expirationDate", now.plusDays(7).toString()
    );

    when(storageService.addItemToStorage(eq(itemId), eq("kg"), eq(5), any(LocalDateTime.class)))
        .thenReturn(testStorageItem);

    // Act & Assert
    mockMvc.perform(post("/api/storage/household/item/" + itemId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.unit").value("kg"))
        .andExpect(jsonPath("$.amount").value(5));
  }

  @Test
  void addItemToStorage_withoutExpirationDate_shouldReturnOkWithStorageItem() throws Exception {
    // Arrange
    Long itemId = 1L;
    Map<String, Object> requestBody = Map.of(
        "unit", "kg",
        "amount", 5
    );

    when(storageService.addItemToStorage(eq(itemId), eq("kg"), eq(5), isNull()))
        .thenReturn(testStorageItem);

    // Act & Assert
    mockMvc.perform(post("/api/storage/household/item/" + itemId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isOk());
  }

  @Test
  void removeItemFromStorage_shouldReturnOk() throws Exception {
    // Arrange
    Long storageItemId = 1L;
    doNothing().when(storageService).removeItemFromStorage(storageItemId);

    // Act & Assert
    mockMvc.perform(post("/api/storage/" + storageItemId))
        .andExpect(status().isOk());

    verify(storageService).removeItemFromStorage(storageItemId);
  }

  @Test
  void updateStorageItem_shouldReturnOkWithUpdatedItem() throws Exception {
    // Arrange
    Long storageItemId = 1L;
    Map<String, Object> requestBody = Map.of(
        "unit", "kg",
        "amount", 10,
        "expirationDate", now.plusDays(14).toString()
    );

    when(storageService.updateStorageItem(
        eq(storageItemId), eq("kg"), eq(10), any(LocalDateTime.class)))
        .thenReturn(testStorageItem);

    // Act & Assert
    mockMvc.perform(put("/api/storage/" + storageItemId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void updateStorageItem_withNullValues_shouldReturnOkWithUpdatedItem() throws Exception {
    // Arrange
    Long storageItemId = 1L;
    Map<String, Object> requestBody = Map.of(
        "unit", "kg"
    );

    when(storageService.updateStorageItem(
        eq(storageItemId), eq("kg"), isNull(), isNull()))
        .thenReturn(testStorageItem);

    // Act & Assert
    mockMvc.perform(put("/api/storage/" + storageItemId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isOk());
  }

  @Test
  void updateStorageItem_withIllegalArgumentException_shouldReturnBadRequest() throws Exception {
    // Arrange
    Long storageItemId = 1L;
    Map<String, Object> requestBody = Map.of(
        "unit", "kg",
        "amount", 10
    );

    when(storageService.updateStorageItem(
        eq(storageItemId), eq("kg"), eq(10), isNull()))
        .thenThrow(new IllegalArgumentException("Storage item not found"));

    // Act & Assert
    mockMvc.perform(put("/api/storage/" + storageItemId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Storage item not found"));
  }

  @Test
  void updateStorageItem_withGeneralException_shouldReturnInternalServerError() throws Exception {
    // Arrange
    Long storageItemId = 1L;
    Map<String, Object> requestBody = Map.of(
        "unit", "kg",
        "amount", 10
    );

    when(storageService.updateStorageItem(
        eq(storageItemId), eq("kg"), eq(10), isNull()))
        .thenThrow(new RuntimeException("Database error"));

    // Act & Assert
    mockMvc.perform(put("/api/storage/" + storageItemId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Failed to update storage item"));
  }
}