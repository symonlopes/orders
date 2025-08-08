package br.com.symon.orders.service;

import br.com.symon.orders.exception.DuplicatedOrderException;
import br.com.symon.orders.model.Order;
import br.com.symon.orders.model.OrderStatus;
import br.com.symon.orders.repository.OrderRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Log4j2
public class OrderService {

    private final CacheService cacheService;
    private final OrderRepository orderRepository;

    private Integer calculateOrderTotal(@Valid Order order){
        var total = 0;
        for(var item : order.getItems()){
            total += item.getQuantity() * item.getUnitPriceInCents();
        }
        return total;
    }

    public Order insert(@Valid Order order) {

        if (cacheService.isOnCache(order)){
            log.warn("Duplicated order detected: {}",  order.hashCode() );
            throw new DuplicatedOrderException();
        }

        order.setTotalInCents(calculateOrderTotal(order));
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());
        var savedOrder = orderRepository.save(order);

        cacheService.saveOrderOnCache(order);

        return order;
    }

    public Optional<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByStatus(String status, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "createdAt"));
        return orderRepository.findByStatus(status, pageRequest);
    }
    public List<Order> importNewOrders(int limit) {
        return orderRepository.findAndMarkIndividually(OrderStatus.NEW.name(), OrderStatus.IMPORTED.name(), limit);
    }


}
