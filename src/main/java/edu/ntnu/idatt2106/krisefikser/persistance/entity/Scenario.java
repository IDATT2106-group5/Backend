package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "scenario")
public class Scenario {

  @Id
  private Long id;
  private String name;
  private String description;
  private String toDo;
  private String packing_list;


  public Scenario() {
  }

  public Scenario(Long id, String name, String description, String toDo, String packing_list) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.toDo = toDo;
    this.packing_list = packing_list;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPacking_list() {
    return packing_list;
  }

  public void setPacking_list(String packing_list) {
    this.packing_list = packing_list;
  }

  public String getToDo() {
    return toDo;
  }

  public void setToDo(String toDo) {
    this.toDo = toDo;
  }

  public String getPackingList() {
    return packing_list;
  }

  public void setPackingList(String packing_list) {
    this.packing_list = packing_list;
  }
}
