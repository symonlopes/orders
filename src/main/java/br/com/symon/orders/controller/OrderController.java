package br.com.symon.orders.controller;

import br.com.symon.orders.model.Order;
import br.com.symon.orders.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> save(@RequestBody @Valid Order order){
        log.debug("Inserting new ORDER [{}] ", order);
        var saved = orderService.insert(order);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable String id) {
        log.debug("Fetching ORDER with id [{}]", id);
        return orderService.findById(id)
                .map(order -> new ResponseEntity<>(order, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> findByStatus(
            @PathVariable String status,
            @RequestParam(name = "limit", defaultValue = "1000") @Min(1) @Max(100000) int limit) {

        log.debug("Fetching up to [{}] ORDERS with status [{}]", limit, status);
        List<Order> orders = orderService.findByStatus(status, limit);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/import")
    public ResponseEntity<List<Order>> importNewOrders(@RequestParam(name = "limit", defaultValue = "1000") @Min(10) @Max(1000) int limit) {
        log.debug("Importing new orders.");
        List<Order> orders = orderService.importNewOrders(limit);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

}
