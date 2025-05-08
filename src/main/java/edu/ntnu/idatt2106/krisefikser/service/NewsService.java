package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.EditNewsDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.News;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.NewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * The type News service.
 */
@Service
public class NewsService {

  private final NewsRepository newsRepository;

  /**
   * The NewsService class provides methods for managing news items.
   *
   * @param newsRepository the news repository
   */
  public NewsService(NewsRepository newsRepository) {
    this.newsRepository = newsRepository;
  }

  /**
   * Creates a new news item.
   *
   * @param newsDto the news item to create
   */
  public void createNewsItem(EditNewsDto newsDto) {
    News news = new News();
    news.setName(newsDto.getName());
    news.setTitle(newsDto.getTitle());
    news.setUrl(newsDto.getUrl());
    news.setContent(newsDto.getContent());
    news.setSource(newsDto.getSource());

    newsRepository.save(news);
  }

  /**
   * Updates an existing news item.
   *
   * @param id      the ID of the news item to update
   * @param newsDto the updated news item data
   */
  public void updateNewsItem(Long id, EditNewsDto newsDto) {
    News news = newsRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("News with given id not found"));
    if (news != null) {
      if (newsDto.getTitle() != null) {
        news.setTitle(newsDto.getTitle());
      }
      if (newsDto.getUrl() != null) {
        news.setUrl(newsDto.getUrl());
      }
      if (newsDto.getContent() != null) {
        news.setContent(newsDto.getContent());
      }
      if (newsDto.getSource() != null) {
        news.setSource(newsDto.getSource());
      }

      newsRepository.save(news);
    } else {
      throw new IllegalArgumentException("News with id " + id + " not found");
    }
  }

  /**
   * Deletes a news item.
   *
   * @param id the ID of the news item to delete
   */
  public void deleteNewsItem(Long id) {
    News news = newsRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("News with given id not found"));
    if (news != null) {
      newsRepository.delete(news);
    } else {
      throw new IllegalArgumentException("News with id " + id + " not found");
    }
  }

  /**
   * Finds a news item by its ID.
   *
   * @param id the ID of the news item
   * @return the found news item, or null if not found
   */
  public News findNewsById(Long id) {
    return newsRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("News with given id not found"));
  }

  /**
   * Finds a paginated list of news items.
   *
   * @param page the current page number (0-indexed)
   * @param size the number of items per page
   * @return a paginated list of news items
   */
  public Page<News> findPaginatedList(int page, int size) {
    return newsRepository.findPaginatedList(PageRequest.of(page, size));
  }
}
