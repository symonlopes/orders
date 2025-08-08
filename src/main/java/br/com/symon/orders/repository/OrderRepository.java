package br.com.symon.orders.repository;

import br.com.symon.orders.model.Order;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderRepository {
    private final MongoTemplate mongoTemplate;

    public List<Order> findByStatus(String status, Pageable pageable) {
        Query query = new Query(Criteria.where("status").is(status))
                .with(pageable);
        return mongoTemplate.find(query, Order.class);
    }

    public Order save(Order order) {
        return mongoTemplate.save(order);
    }

    public Optional<Order> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, Order.class));
    }

    public List<Order> findAndMarkIndividually(String status, String newStatus, int limit) {
        var findQuery = new Query(Criteria.where("status").is(status))
                .with(org.springframework.data.domain.Sort.by("createdAt").ascending())
                .limit(limit);

        var ordersToUpdate = mongoTemplate.find(findQuery, Order.class);
        if (ordersToUpdate.isEmpty()) {
            return ordersToUpdate;
        }

        var orderIds = ordersToUpdate.stream()
                .map(Order::getId)
                .toList();

        var updateQuery = new Query(Criteria.where("id").in(orderIds));
        var update = new Update().set("status", newStatus);

        mongoTemplate.updateMulti(updateQuery, update, Order.class);

        return ordersToUpdate;
    }
}