package com.akhund.orderservice.service;

import com.akhund.orderservice.dto.InventoryResponse;
import com.akhund.orderservice.dto.OrderRequest;
import com.akhund.orderservice.model.Order;
import com.akhund.orderservice.model.OrderLineItems;
import com.akhund.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList().stream()
                .map(orderLineItemsDto -> OrderLineItems.builder()
                        .skuCode(orderLineItemsDto.getSkuCode())
                        .price(orderLineItemsDto.getPrice())
                        .quantity(orderLineItemsDto.getQuantity())
                        .build())
                .toList();

        order.setOrderLineItemsList(orderLineItemsList);

        List<String> skuCodeList = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://localhost:8012/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodeList).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductIsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::getIsInStock);

        allProductIsInStock = allProductIsInStock && inventoryResponseArray.length == skuCodeList.size();

        if (allProductIsInStock) {
            orderRepository.save(order);
        }
        else {
            throw  new IllegalArgumentException("Not all product is in stock, please try again later");
        }
    }
}
