package com.stevemd.orderservice.model;


import lombok.*;
import jakarta.persistence.*;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItems> orderItems;
}
