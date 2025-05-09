package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.controller.item.ItemController;
import edu.ntnu.idatt2106.krisefikser.api.dto.item.ItemResponseDto;
import edu.ntnu.idatt2106.krisefikser.service.item.ItemService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for the ItemController class.
 */
class ItemControllerTest {

  @Mock
  private ItemService itemService;

  @InjectMocks
  private ItemController itemController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class GetAllItemsTests {

    @Test
    void shouldReturnAllItemsSuccessfully() {
      ItemResponseDto item1 = new ItemResponseDto();
      item1.setId(1L);
      item1.setName("Tent");

      ItemResponseDto item2 = new ItemResponseDto();
      item2.setId(2L);
      item2.setName("First Aid Kit");

      when(itemService.getAllItems()).thenReturn(List.of(item1, item2));

      ResponseEntity<List<ItemResponseDto>> response = itemController.getAllItems();

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(2, response.getBody().size());
      assertEquals("Tent", response.getBody().get(0).getName());
      assertEquals("First Aid Kit", response.getBody().get(1).getName());
    }

    @Test
    void shouldReturnInternalServerError_whenExceptionThrown() {
      when(itemService.getAllItems()).thenThrow(new RuntimeException("Unexpected error"));

      ResponseEntity<List<ItemResponseDto>> response = itemController.getAllItems();

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
  }

  @Nested
  class GetItemByIdTests {

    @Test
    void shouldReturnItemByIdSuccessfully() {
      Long id = 1L;
      ItemResponseDto item = new ItemResponseDto();
      item.setId(id);
      item.setName("Tent");

      when(itemService.getItemById(id)).thenReturn(item);

      ResponseEntity<ItemResponseDto> response = itemController.getItemById(id);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("Tent", response.getBody().getName());
    }

    @Test
    void shouldReturnNotFound_whenItemDoesNotExist() {
      Long id = 99L;
      when(itemService.getItemById(id)).thenThrow(new IllegalArgumentException("Not found"));

      ResponseEntity<ItemResponseDto> response = itemController.getItemById(id);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedErrorOccurs() {
      Long id = 1L;
      when(itemService.getItemById(id)).thenThrow(new RuntimeException("Unexpected error"));

      ResponseEntity<ItemResponseDto> response = itemController.getItemById(id);

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
  }

  @Nested
  class GetItemsByTypeTests {

    @Test
    void shouldReturnItemsByTypeSuccessfully() {
      String type = "shelter";
      ItemResponseDto item = new ItemResponseDto();
      item.setId(1L);
      item.setName("Shelter");

      when(itemService.getItemsByType(type)).thenReturn(List.of(item));

      ResponseEntity<List<ItemResponseDto>> response = itemController.getItemsByType(type);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentExceptionThrown() {
      String type = "invalid";
      when(itemService.getItemsByType(type)).thenThrow(
          new IllegalArgumentException("Invalid type"));

      ResponseEntity<List<ItemResponseDto>> response = itemController.getItemsByType(type);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedExceptionThrown() {
      String type = "food";
      when(itemService.getItemsByType(type)).thenThrow(new RuntimeException("Unexpected error"));

      ResponseEntity<List<ItemResponseDto>> response = itemController.getItemsByType(type);

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
  }

  @Nested
  class GetPaginatedItemsTests {

    @Test
    void shouldReturnPaginatedItemsSuccessfully() {
      ItemResponseDto item = new ItemResponseDto();
      item.setId(1L);
      item.setName("Radio");

      Page<ItemResponseDto> page = new PageImpl<>(List.of(item));

      when(itemService.getPaginatedItems(0, 20, null)).thenReturn(page);

      ResponseEntity<?> response = itemController.getPaginatedItems(0, 20, null);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      Map<String, Object> body = (Map<String, Object>) response.getBody();
      assertEquals(1, ((List<?>) body.get("items")).size());
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentExceptionThrown() {
      when(itemService.getPaginatedItems(0, 20, null))
          .thenThrow(new IllegalArgumentException("Invalid params"));

      ResponseEntity<?> response = itemController.getPaginatedItems(0, 20, null);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Invalid params", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedExceptionThrown() {
      when(itemService.getPaginatedItems(0, 20, null))
          .thenThrow(new RuntimeException("Unexpected error"));

      ResponseEntity<?> response = itemController.getPaginatedItems(0, 20, null);

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", ((Map<?, ?>) response.getBody()).get("error"));
    }
  }
}
