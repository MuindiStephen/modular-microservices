package com.stevemd.orderservice.service;

import com.stevemd.orderservice.dto.OrderLineItemsDTO;
import com.stevemd.orderservice.dto.OrderRequest;
import com.stevemd.orderservice.model.InventoryResponse;
import com.stevemd.orderservice.model.Order;
import com.stevemd.orderservice.model.OrderItems;
import com.stevemd.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public void placeOrder(@RequestBody OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderName(UUID.randomUUID().toString());

        List<OrderItems> orderItemsList = orderRequest.getOrderLineItemsDTOList()
                .stream()
                .map(this::mapToDTO)
                .toList();
        order.setOrderItems(orderItemsList);

        /*
         * use web client builder instead of rest template
         */
        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/v1/inventory?skuCode=iphone_14")
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponses)
                .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
        }
        else {
            throw new IllegalStateException("failed to place order. product is not in store. ");
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

