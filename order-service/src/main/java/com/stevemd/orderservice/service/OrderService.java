package com.stevemd.orderservice.service;

import com.stevemd.orderservice.dto.OrderLineItemsDTO;
import com.stevemd.orderservice.dto.OrderRequest;
import com.stevemd.orderservice.model.Inventory;
import com.stevemd.orderservice.model.InventoryResponse;
import com.stevemd.orderservice.model.Order;
import com.stevemd.orderservice.model.OrderItems;
import com.stevemd.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final RestTemplate restTemplate;
    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public void placeOrder(@RequestBody OrderRequest orderRequest) {

      InventoryResponse[] inventoryResponses = restTemplate.getForObject("http://localhost:8082/api/v1/inventory?skuCode=iphone_14", InventoryResponse[].class);

       // assert inventoryResponses != null;

        // Check if inventory data is present
        if (inventoryResponses == null || inventoryResponses.length == 0) {
            throw new IllegalStateException("Inventory data not found for SKU: iphone_14");
        }

        InventoryResponse inventoryResponse = inventoryResponses[0];

        if (inventoryResponse.isInStock() == true) {
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

    /*

    public void placeOrder(@RequestBody OrderRequest orderRequest) {
        // Iterate over each item in the order request to check inventory for each SKU
        for (OrderLineItemsDTO orderLineItem : orderRequest.getOrderLineItemsDTOList()) {
            String skuCode = orderLineItem.getSkuCode();
            int quantity = orderLineItem.getQuantity();

            // Fetch the inventory for this particular SKU from the Inventory service
            Inventory[] inventories = restTemplate.getForObject(
                    "http://localhost:8082/api/v1/inventory?skuCode=" + skuCode, Inventory[].class);

            if (inventories == null || inventories.length == 0 || inventories[0].getQuantity() == null) {
                // If no inventory data exists or the stock is less than required, throw an exception
                throw new IllegalStateException("Insufficient stock for SKU: " + skuCode);
            }

            Integer inventoryQuantity = inventories[0].getQuantity();
            if (inventoryQuantity == null || inventoryQuantity < quantity) {
                // If the quantity is null or the stock is less than required, throw an exception
                throw new IllegalStateException("Insufficient stock for SKU: " + skuCode);
            }
        }

        // If all inventory checks pass, create the order and save it
        Order order = new Order();
        order.setOrderName(UUID.randomUUID().toString());

        List<OrderItems> orderItemsList = orderRequest.getOrderLineItemsDTOList()
                .stream()
                .map(this::mapToDTO)
                .toList();

        order.setOrderItems(orderItemsList);
        orderRepository.save(order);
    }
    */


    private OrderItems mapToDTO(OrderLineItemsDTO orderLineItemsDTO) {
        return OrderItems.builder()
                .id(orderLineItemsDTO.getId())
                .price(orderLineItemsDTO.getPrice())
                .skuCode(orderLineItemsDTO.getSkuCode())
                .quantity(orderLineItemsDTO.getQuantity())
                .build();
    }
}
