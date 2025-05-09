package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.News;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface News repository.
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

  /**
   * Saves a news item.
   *
   * @param news the news item to save
   * @return the saved news item
   */
  News save(News news);

  /**
   * Deletes a news item.
   *
   * @param news the news item to delete
   */
  void delete(News news);

  /**
   * Finds a news item by its ID.
   *
   * @param id the ID of the news item
   * @return the found news item, or null if not found
   */
  Optional<News> findById(Long id);

  /**
   * Finds a paginated list of news stories.
   *
   * @param pageable the pageable
   * @return the page
   */
  Page<News> findByOrderByCreatedAtDesc(Pageable pageable);

}
