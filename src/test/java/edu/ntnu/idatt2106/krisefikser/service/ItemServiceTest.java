package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.ItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ItemRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

/**
 * Unit tests for the ItemService class.
 */
class ItemServiceTest {

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private ItemService itemService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class GetAllItemsTests {

    @Test
    void shouldReturnAllItems() {
      Item item = new Item();
      item.setId(1L);
      item.setName("Tent");
      item.setCaloricAmount(100);
      item.setItemType(ItemType.LIQUIDS);

      when(itemRepository.findAll()).thenReturn(List.of(item));

      List<ItemResponseDto> result = itemService.getAllItems();

      assertEquals(1, result.size());
      assertEquals("Tent", result.get(0).getName());
    }
  }

  @Nested
  class GetItemByIdTests {

    @Test
    void shouldReturnItem_whenItemExists() {
      Item item = new Item();
      item.setId(1L);
      item.setName("Tent");
      item.setCaloricAmount(100);
      item.setItemType(ItemType.LIQUIDS);

      when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

      ItemResponseDto result = itemService.getItemById(1L);

      assertEquals("Tent", result.getName());
    }

    @Test
    void shouldThrowException_whenItemNotFound() {
      when(itemRepository.findById(99L)).thenReturn(Optional.empty());

      IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
        itemService.getItemById(99L);
      });

      assertEquals("Item not found with ID: 99", ex.getMessage());
    }
  }

  @Nested
  class GetItemsByTypeTests {

    @Test
    void shouldReturnItems_whenValidTypeProvided() {
      Item item = new Item();
      item.setId(1L);
      item.setName("Tent");
      item.setItemType(ItemType.LIQUIDS);

      when(itemRepository.findByItemType(ItemType.LIQUIDS)).thenReturn(List.of(item));

      List<ItemResponseDto> result = itemService.getItemsByType("LIQUIDS");

      assertEquals(1, result.size());
      assertEquals("Tent", result.get(0).getName());
    }

    @Test
    void shouldThrowException_whenInvalidTypeProvided() {
      IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
        itemService.getItemsByType("invalid_type");
      });

      assertEquals("Invalid item type: invalid_type", ex.getMessage());
    }
  }

  @Nested
  class GetPaginatedItemsTests {

    @Test
    void shouldReturnPaginatedItemsWithoutSearch() {
      Item item = new Item();
      item.setId(1L);
      item.setName("Tent");
      item.setItemType(ItemType.LIQUIDS);

      Page<Item> page = new PageImpl<>(List.of(item));
      when(itemRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

      Page<ItemResponseDto> result = itemService.getPaginatedItems(0, 10, null);

      assertEquals(1, result.getContent().size());
    }

    @Test
    void shouldReturnPaginatedItemsWithSearch() {
      Item item = new Item();
      item.setId(1L);
      item.setName("Tent");

      Page<Item> page = new PageImpl<>(List.of(item));
      when(itemRepository.findByNameContainingIgnoreCase("tent", PageRequest.of(0, 10)))
          .thenReturn(page);

      Page<ItemResponseDto> result = itemService.getPaginatedItems(0, 10, "tent");

      assertEquals(1, result.getContent().size());
      assertEquals("Tent", result.getContent().get(0).getName());
    }
  }
}
