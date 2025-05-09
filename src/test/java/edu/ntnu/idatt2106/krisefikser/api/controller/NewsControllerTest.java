package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.EditNewsDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.News;
import edu.ntnu.idatt2106.krisefikser.service.NewsService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class NewsControllerTest {

  @Mock
  private NewsService newsService;

  @InjectMocks
  private NewsController newsController;

  private News newsItem;
  private EditNewsDto editNewsDto;

  @BeforeEach
  void setUp() {
    newsItem = new News("Test Title", "http://example.com", "Test content", "Test Source");
    newsItem.setId(1L);
    newsItem.setCreatedAt(LocalDateTime.now());

    editNewsDto = new EditNewsDto("Test Title", "http://example.com", "Test content",
        "Test Source");
  }

  @Nested
  class GetNewsTests {

    @Test
    void getNews_Success() {
      // Arrange
      int page = 0;
      int size = 10;
      List<News> newsList = List.of(newsItem);
      Page<News> newsPage = new PageImpl<>(newsList);
      when(newsService.findPaginatedList(page, size)).thenReturn(newsPage);

      // Act
      ResponseEntity<?> response = newsController.getNews(page, size);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertInstanceOf(Map.class, response.getBody());

      @SuppressWarnings("unchecked")
      Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
      assertEquals(newsList, responseBody.get("news"));
      assertEquals(0, responseBody.get("currentPage"));
      assertEquals(1L, responseBody.get("totalItems"));
      assertEquals(1, responseBody.get("totalPages"));

      verify(newsService, times(1)).findPaginatedList(page, size);
    }

    @Test
    void getNews_Exception() {
      // Arrange
      int page = 0;
      int size = 10;
      when(newsService.findPaginatedList(page, size)).thenThrow(
          new RuntimeException("Database error"));

      // Act
      ResponseEntity<?> response = newsController.getNews(page, size);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals(Map.of("error", "Internal server error"), response.getBody());
      verify(newsService, times(1)).findPaginatedList(page, size);
    }
  }

  @Nested
  class GetNewsByIdTests {

    @Test
    void getNewsById_Success() {
      // Arrange
      Long newsId = 1L;
      when(newsService.findNewsById(newsId)).thenReturn(newsItem);

      // Act
      ResponseEntity<?> response = newsController.getNewsById(newsId);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(newsItem, response.getBody());
      verify(newsService, times(1)).findNewsById(newsId);
    }

    @Test
    void getNewsById_NotFound() {
      // Arrange
      Long newsId = 99L;
      when(newsService.findNewsById(newsId)).thenThrow(
          new IllegalArgumentException("News with given id not found"));

      // Act
      ResponseEntity<?> response = newsController.getNewsById(newsId);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals(Map.of("error", "News with given id not found"), response.getBody());
      verify(newsService, times(1)).findNewsById(newsId);
    }

    @Test
    void getNewsById_Exception() {
      // Arrange
      Long newsId = 1L;
      when(newsService.findNewsById(newsId)).thenThrow(new RuntimeException("Database error"));

      // Act
      ResponseEntity<?> response = newsController.getNewsById(newsId);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals(Map.of("error", "Internal server error"), response.getBody());
      verify(newsService, times(1)).findNewsById(newsId);
    }
  }

  @Nested
  class CreateNewsTests {

    @Test
    void createNews_Success() {
      // Arrange
      doNothing().when(newsService).createNewsItem(editNewsDto);

      // Act
      ResponseEntity<?> response = newsController.createNews(editNewsDto);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(Map.of("message", "News created successfully"), response.getBody());
      verify(newsService, times(1)).createNewsItem(editNewsDto);
    }

    @Test
    void createNews_ValidationError() {
      // Arrange
      doThrow(new IllegalArgumentException("Invalid request data"))
          .when(newsService).createNewsItem(editNewsDto);

      // Act
      ResponseEntity<?> response = newsController.createNews(editNewsDto);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals(Map.of("error", "Invalid request data"), response.getBody());
      verify(newsService, times(1)).createNewsItem(editNewsDto);
    }

    @Test
    void createNews_Exception() {
      // Arrange
      doThrow(new RuntimeException("Database error"))
          .when(newsService).createNewsItem(editNewsDto);

      // Act
      ResponseEntity<?> response = newsController.createNews(editNewsDto);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals(Map.of("error", "Internal server error"), response.getBody());
      verify(newsService, times(1)).createNewsItem(editNewsDto);
    }
  }

  @Nested
  class EditNewsTests {

    @Test
    void editNews_Success() {
      // Arrange
      Long newsId = 1L;
      doNothing().when(newsService).updateNewsItem(newsId, editNewsDto);

      // Act
      ResponseEntity<?> response = newsController.editNews(newsId, editNewsDto);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(Map.of("message", "News updated successfully"), response.getBody());
      verify(newsService, times(1)).updateNewsItem(newsId, editNewsDto);
    }

    @Test
    void editNews_NotFound() {
      // Arrange
      Long newsId = 99L;
      doThrow(new IllegalArgumentException("News with given id not found"))
          .when(newsService).updateNewsItem(newsId, editNewsDto);

      // Act
      ResponseEntity<?> response = newsController.editNews(newsId, editNewsDto);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals(Map.of("error", "Invalid request data"), response.getBody());
      verify(newsService, times(1)).updateNewsItem(newsId, editNewsDto);
    }

    @Test
    void editNews_Exception() {
      // Arrange
      Long newsId = 1L;
      doThrow(new RuntimeException("Database error"))
          .when(newsService).updateNewsItem(newsId, editNewsDto);

      // Act
      ResponseEntity<?> response = newsController.editNews(newsId, editNewsDto);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals(Map.of("error", "Internal server error"), response.getBody());
      verify(newsService, times(1)).updateNewsItem(newsId, editNewsDto);
    }
  }

  @Nested
  class DeleteNewsTests {

    @Test
    void deleteNews_Success() {
      // Arrange
      Long newsId = 1L;
      doNothing().when(newsService).deleteNewsItem(newsId);

      // Act
      ResponseEntity<Void> response = newsController.deleteNews(newsId);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNull(response.getBody());
      verify(newsService, times(1)).deleteNewsItem(newsId);
    }

    @Test
    void deleteNews_NotFound() {
      // Arrange
      Long newsId = 99L;
      doThrow(new IllegalArgumentException("News with given id not found"))
          .when(newsService).deleteNewsItem(newsId);

      // Act
      ResponseEntity<Void> response = newsController.deleteNews(newsId);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNull(response.getBody());
      verify(newsService, times(1)).deleteNewsItem(newsId);
    }

    @Test
    void deleteNews_Exception() {
      // Arrange
      Long newsId = 1L;
      doThrow(new RuntimeException("Database error"))
          .when(newsService).deleteNewsItem(newsId);

      // Act
      ResponseEntity<Void> response = newsController.deleteNews(newsId);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertNull(response.getBody());
      verify(newsService, times(1)).deleteNewsItem(newsId);
    }
  }
}