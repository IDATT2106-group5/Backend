package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;

@Table
@Entity(name = "storage")
public class Storage {

  /**
   * The storage id
   */
  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The item in the storage
   */

  @JoinColumn(nullable = false)
  @ManyToOne
  private Item item;

  /**
   * The unit of the item in the storage
   */
  @Column(nullable = false)
  private String unit;

  /**
   * The amount of the item in the storage
   */

  @Column(nullable = false)
  private int amount;

  /**
   * The expiration date of the item in the storage
   */

  @Column(nullable = false)
  private Date expirationDate;
}
