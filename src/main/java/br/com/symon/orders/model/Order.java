package br.com.symon.orders.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@Builder
@Document(collection = "orders")
public class Order {

    @Id
    private String id;
    private LocalDateTime createdAt;

    @NotEmpty(message = "Order must contain at least one item")
    @Builder.Default
    @Valid
    private Set<OrderItem> items = new HashSet<>();
    private OrderStatus orderStatus;
    private Integer totalInCents;

    @NotBlank(message = "Seller ID is required")
    private String sellerId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @Override
    public int hashCode() {
        return Objects.hash(items, totalInCents, sellerId, customerId);
    }
}
