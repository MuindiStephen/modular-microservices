package com.stevemd.orderservice.service;

import com.stevemd.orderservice.dto.OrderLineItemsDTO;
import com.stevemd.orderservice.dto.OrderRequest;
import com.stevemd.orderservice.model.Order;
import com.stevemd.orderservice.model.OrderItems;
import com.stevemd.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void placeOrder(@RequestBody OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderName(UUID.randomUUID().toString());

         List<OrderItems> orderItemsList = orderRequest.getOrderLineItemsDTOList()
                 .stream()
                 .map(this::mapToDTO)
                 .toList();

         order.setOrderItems(orderItemsList);

         orderRepository.save(order);
    }


    private OrderItems mapToDTO(OrderLineItemsDTO orderLineItemsDTO) {
        return OrderItems.builder()
                .id(orderLineItemsDTO.getId())
                .price(orderLineItemsDTO.getPrice())
                .skuCode(orderLineItemsDTO.getSkuCode())
                .quantity(orderLineItemsDTO.getQuantity())
                .build();
    }
}
