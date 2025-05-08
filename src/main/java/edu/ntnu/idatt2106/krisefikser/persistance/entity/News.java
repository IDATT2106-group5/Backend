package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The type News.
 */
@Entity
@Table(name = "item")
public class News {

  /**
   * The item id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The name of the item.
   */
  private String name;

  /**
   * The title of the news story
   */
  private String title;

  /**
   * The url of the news story
   */
  private String url;

  /**
   * The content of the news story
   */

  private String content;

  /**
   * The source of the news story
   */
  private String source;

  /**
   *  The time the news story was created
   */
    private String createdAt;

  /**
   * Instantiates a new News.
   */
  public News() {

  }

  /**
   * Instantiates a new Item with all fields.
   *
   * @param name    the name
   * @param title   the title
   * @param url     the url
   * @param content the content
   * @param source  the source
   */
  public News(String name, String title, String url, String content, String source) {
    this.name = name;
    this.title = title;
    this.url = url;
    this.content = content;
    this.source = source;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public Long getId() {
        return id;
    }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(Long id) {
        this.id = id;
    }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
        return name;
    }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
        this.name = name;
    }

  /**
   * Gets title.
   *
   * @return the title
   */
  public String getTitle() {
        return title;
    }

  /**
   * Sets title.
   *
   * @param title the title
   */
  public void setTitle(String title) {
        this.title = title;
    }

  /**
   * Gets url.
   *
   * @return the url
   */
  public String getUrl() {
        return url;
    }

  /**
   * Sets url.
   *
   * @param url the url
   */
  public void setUrl(String url) {
        this.url = url;
    }

  /**
   * Gets content.
   *
   * @return the content
   */
  public String getContent() {
        return content;
    }

  /**
   * Sets content.
   *
   * @param content the content
   */
  public void setContent(String content) {
        this.content = content;
    }

  /**
   * Gets source.
   *
   * @return the source
   */
  public String getSource() {
        return source;
    }

  /**
   * Sets source.
   *
   * @param source the source
   */
  public void setSource(String source) {
        this.source = source;
    }

  /**
   * Gets created at.
   *
   * @return the created at
   */
  public String getCreatedAt() {
        return createdAt;
    }

  /**
   * Sets created at.
   *
   * @param createdAt the created at
   */
  public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
