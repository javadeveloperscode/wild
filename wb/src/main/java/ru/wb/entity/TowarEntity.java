package ru.wb.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "towar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TowarEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private Long nmId;

  @Column(nullable = false)
  private String name;

  private String brand;

  private Double priceRub;

  private String url;
}
