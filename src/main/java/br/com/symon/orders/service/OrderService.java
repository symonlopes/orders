package br.com.symon.orders.service;

import br.com.symon.orders.exception.DuplicatedOrderException;
import br.com.symon.orders.model.Order;
import br.com.symon.orders.model.OrderStatus;
import br.com.symon.orders.repository.OrderRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class OrderService {

    private final CacheService cacheService;
    private final OrderRepository repository;

    public Order insert(@Valid Order order) {

        if (cacheService.isOnCache(order)){
            throw new DuplicatedOrderException();
        }

        order.setOrderStatus(OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());
        var savedOrder = repository.save(order);

        cacheService.saveOrderOnCache(order);

        return savedOrder;
    }

    public Optional<Order> findById(String id) {
        return repository.findById(id);
    }
}
