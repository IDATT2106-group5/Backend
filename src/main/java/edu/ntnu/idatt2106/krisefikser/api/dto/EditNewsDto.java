package edu.ntnu.idatt2106.krisefikser.api.dto;

/**
 * The type Edit news dto.
 */
public class EditNewsDto {
  private String name;
  private String title;
  private String url;
  private String content;
  private String source;

  /**
   * Instantiates a new Edit news dto.
   */
  public EditNewsDto() {
  }

  /**
   * Instantiates a new Edit news dto.
   *
   * @param name    the name
   * @param title   the title
   * @param url     the url
   * @param content the content
   * @param source  the source
   */
  public EditNewsDto(String name, String title, String url, String content, String source) {
    this.name = name;
    this.title = title;
    this.url = url;
    this.content = content;
    this.source = source;
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

}
