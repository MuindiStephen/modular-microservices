package com.stevemd.orderservice.model;


import lombok.*;
import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String orderName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItems> orderItems;
}
