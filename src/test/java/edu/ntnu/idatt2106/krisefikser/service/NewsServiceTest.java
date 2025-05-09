package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.news.EditNewsDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.news.News;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.news.NewsRepository;
import edu.ntnu.idatt2106.krisefikser.service.news.NewsService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

  @Mock
  private NewsRepository newsRepository;

  @InjectMocks
  private NewsService newsService;

  private News testNews;
  private EditNewsDto testDto;

  @BeforeEach
  void setUp() {
    testNews = new News("Test Title", "http://test.com", "Test Content", "Test Source");
    testNews.setId(1L);
    testNews.setCreatedAt(LocalDateTime.now());

    testDto = new EditNewsDto("Test Title", "http://test.com", "Test Content", "Test Source");
  }

  @Test
  void createNewsItem_shouldCreateAndSaveNewsItem() {
    // Act
    newsService.createNewsItem(testDto);

    // Assert
    ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
    verify(newsRepository).save(newsCaptor.capture());

    News savedNews = newsCaptor.getValue();
    assertEquals(testDto.getTitle(), savedNews.getTitle());
    assertEquals(testDto.getUrl(), savedNews.getUrl());
    assertEquals(testDto.getContent(), savedNews.getContent());
    assertEquals(testDto.getSource(), savedNews.getSource());
    assertNotNull(savedNews.getCreatedAt());
  }

  @Test
  void updateNewsItem_shouldUpdateExistingNews() {
    // Arrange
    Long id = 1L;
    EditNewsDto updateDto = new EditNewsDto("Updated Title", "http://updated.com",
        "Updated Content", "Updated Source");
    when(newsRepository.findById(id)).thenReturn(Optional.of(testNews));

    // Act
    newsService.updateNewsItem(id, updateDto);

    // Assert
    ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
    verify(newsRepository).save(newsCaptor.capture());

    News updatedNews = newsCaptor.getValue();
    assertEquals(updateDto.getTitle(), updatedNews.getTitle());
    assertEquals(updateDto.getUrl(), updatedNews.getUrl());
    assertEquals(updateDto.getContent(), updatedNews.getContent());
    assertEquals(updateDto.getSource(), updatedNews.getSource());
  }

  @Test
  void updateNewsItem_withPartialData_shouldOnlyUpdateProvidedFields() {
    // Arrange
    Long id = 1L;

    EditNewsDto partialUpdateDto = new EditNewsDto();
    partialUpdateDto.setUrl("http://updated.com");
    partialUpdateDto.setSource("Updated Source");

    when(newsRepository.findById(id)).thenReturn(Optional.of(testNews));

    // Act
    newsService.updateNewsItem(id, partialUpdateDto);

    // Assert
    ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
    verify(newsRepository).save(newsCaptor.capture());

    News updatedNews = newsCaptor.getValue();
    String originalTitle = testNews.getTitle();
    assertEquals(originalTitle, updatedNews.getTitle());
    assertEquals(partialUpdateDto.getUrl(), updatedNews.getUrl());
    String originalContent = testNews.getContent();
    assertEquals(originalContent, updatedNews.getContent());
    assertEquals(partialUpdateDto.getSource(), updatedNews.getSource());
  }

  @Test
  void updateNewsItem_withNonExistentId_shouldThrowException() {
    // Arrange
    Long id = 999L;
    when(newsRepository.findById(id)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      newsService.updateNewsItem(id, testDto);
    });

    assertEquals("News with given id not found", exception.getMessage());
  }

  @Test
  void deleteNewsItem_shouldDeleteExistingNews() {
    // Arrange
    Long id = 1L;
    when(newsRepository.findById(id)).thenReturn(Optional.of(testNews));

    // Act
    newsService.deleteNewsItem(id);

    // Assert
    verify(newsRepository).delete(testNews);
  }

  @Test
  void deleteNewsItem_withNonExistentId_shouldThrowException() {
    // Arrange
    Long id = 999L;
    when(newsRepository.findById(id)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      newsService.deleteNewsItem(id);
    });

    assertEquals("News with given id not found", exception.getMessage());
  }

  @Test
  void findNewsById_shouldReturnNewsIfExists() {
    // Arrange
    Long id = 1L;
    when(newsRepository.findById(id)).thenReturn(Optional.of(testNews));

    // Act
    News result = newsService.findNewsById(id);

    // Assert
    assertEquals(testNews, result);
    verify(newsRepository).findById(id);
  }

  @Test
  void findNewsById_withNonExistentId_shouldThrowException() {
    // Arrange
    Long id = 999L;
    when(newsRepository.findById(id)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      newsService.findNewsById(id);
    });

    assertEquals("News with given id not found", exception.getMessage());
  }

  @Test
  void findPaginatedList_shouldReturnPageOfNews() {
    // Arrange
    int page = 0;
    int size = 10;
    PageRequest pageRequest = PageRequest.of(page, size);
    Page<News> expectedPage = new PageImpl<>(List.of(testNews));

    when(newsRepository.findByOrderByCreatedAtDesc(pageRequest)).thenReturn(expectedPage);

    // Act
    Page<News> result = newsService.findPaginatedList(page, size);

    // Assert
    assertEquals(expectedPage, result);
    verify(newsRepository).findByOrderByCreatedAtDesc(pageRequest);
  }
}