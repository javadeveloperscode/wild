package ru.wb.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class ItemRequest {

  private String brand;
  private String name;
  private double price;

}
