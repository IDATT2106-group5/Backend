package edu.ntnu.idatt2106.krisefikser.api.controller;


import edu.ntnu.idatt2106.krisefikser.api.dto.EditNewsDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.News;
import edu.ntnu.idatt2106.krisefikser.service.NewsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type News controller.
 */
@Tag(name = "News", description = "Endpoints for news related requests")
@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class NewsController {

  private final NewsService newsService;

  /**
   * Constructor for News controller.
   *
   * @param newsService the news service
   */
  public NewsController(NewsService newsService) {
    this.newsService = newsService;
  }

  /**
   * Gets news.
   *
   * @param page the page
   * @param size the size
   * @return the news
   */
  @GetMapping("/get/")
  public ResponseEntity<?> getNews(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Page<News> paginatedNews = newsService.findPaginatedList(page, size);
      Map<String, Object> response = new HashMap<>();
      response.put("news", paginatedNews.getContent());
      response.put("currentPage", paginatedNews.getNumber());
      response.put("totalItems", paginatedNews.getTotalElements());
      response.put("totalPages", paginatedNews.getTotalPages());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Create news response entity.
   *
   * @param request the request
   * @return the response entity
   */
  @PostMapping("/create")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createNews(@RequestBody EditNewsDto request) {
    try {
      newsService.createNewsItem(request);
      return ResponseEntity.ok(Map.of("message", "News created successfully"));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(400).body(Map.of("error", "Invalid request data"));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Edit news response entity.
   *
   * @param newsId  the news id
   * @param request the request
   * @return the response entity
   */
  @PostMapping("/edit/{newsId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> editNews(@PathVariable Long newsId,
      @RequestBody EditNewsDto request) {
    try {
      newsService.updateNewsItem(newsId, request);
      return ResponseEntity.ok(Map.of("message", "News updated successfully"));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(400).body(Map.of("error", "Invalid request data"));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }

  }

  /**
   * Delete news response entity.
   *
   * @param newsId the news id
   * @return the response entity
   */
  @PostMapping("/delete/{newsId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteNews(@PathVariable Long newsId) {
    try {
      newsService.deleteNewsItem(newsId);
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(400).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(null);
    }
  }
}
