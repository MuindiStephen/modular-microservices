package com.stevemd.orderservice.service;

import com.stevemd.orderservice.dto.OrderLineItemsDTO;
import com.stevemd.orderservice.dto.OrderRequest;
import com.stevemd.orderservice.model.Inventory;
import com.stevemd.orderservice.model.Order;
import com.stevemd.orderservice.model.OrderItems;
import com.stevemd.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private RestTemplate restTemplate;
    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public void placeOrder(@RequestBody OrderRequest orderRequest) {

         // check if there exist products in the inventory service
        // inter service communication
      Inventory[] inventories = restTemplate.getForObject("http://localhost:8082/api/v1/inventory?skuCode=iphone_14", Inventory[].class);

      if (inventories!=null && inventories.length > 0) {
          Order order = new Order();
          order.setOrderName(UUID.randomUUID().toString());

          List<OrderItems> orderItemsList = orderRequest.getOrderLineItemsDTOList()
                  .stream()
                  .map(this::mapToDTO)
                  .toList();

          order.setOrderItems(orderItemsList);

          orderRepository.save(order);
      } else {
       throw new IllegalStateException("failed");
      }

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
