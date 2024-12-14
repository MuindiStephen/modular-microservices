package com.stevemd.orderservice.service;

import com.stevemd.orderservice.dto.OrderLineItemsDTO;
import com.stevemd.orderservice.dto.OrderRequest;
import com.stevemd.orderservice.model.InventoryResponse;
import com.stevemd.orderservice.model.Order;
import com.stevemd.orderservice.model.OrderItems;
import com.stevemd.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Transactional
    public void placeOrder(@RequestBody OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderName(UUID.randomUUID().toString());

        // Map DTOs to OrderItems
        List<OrderItems> orderItemsList = orderRequest.getOrderLineItemsDTOList()
                .stream()
                .map(this::mapToDTO)
                .toList();
        order.setOrderItems(orderItemsList);

        // Extract unique SKU codes
        String skuCodes = orderRequest.getOrderLineItemsDTOList()
                .stream()
                .map(OrderLineItemsDTO::getSkuCode)
                .distinct()
                .collect(Collectors.joining(","));

        // Check inventory
        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/v1/inventory", uriBuilder ->
                        uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        if (inventoryResponses == null || inventoryResponses.length == 0) {
            throw new IllegalStateException("Inventory service did not return a response.");
        }

        boolean allProductsInStock = Arrays.stream(inventoryResponses)
                .allMatch(InventoryResponse::isInStock);

        // Save order if all products are in stock
        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalStateException("Failed to place order. Some products are out of stock.");
        }
    }


//    /*
//    @Transactional
//    public void placeOrder(@RequestBody OrderRequest orderRequest) {
//        Order order = new Order();
//        order.setOrderName(UUID.randomUUID().toString());
//
//        List<OrderItems> orderItemsList = orderRequest.getOrderLineItemsDTOList()
//                .stream()
//                .map(this::mapToDTO)
//                .toList();
//        order.setOrderItems(orderItemsList);
//
//        /*
//         * use web client builder instead of rest template
//         */
//        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
//                .uri("http://inventory-service/api/v1/inventory?skuCode=iphone_14")
//                .retrieve()
//                .bodyToMono(InventoryResponse[].class)
//                .block();
//
//        assert inventoryResponses != null;
//        boolean allProductsInStock = Arrays.stream(inventoryResponses)
//                .allMatch(InventoryResponse::isInStock);
//
//        if (allProductsInStock) {
//            orderRepository.save(order);
//        }
//        else {
//            throw new IllegalStateException("failed to place order. product is not in store. ");
//        }





    private OrderItems mapToDTO(OrderLineItemsDTO orderLineItemsDTO) {
        return OrderItems.builder()
                .price(orderLineItemsDTO.getPrice())
                .skuCode(orderLineItemsDTO.getSkuCode())
                .quantity(orderLineItemsDTO.getQuantity())
                .build();
    }
}

