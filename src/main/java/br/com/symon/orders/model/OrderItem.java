package br.com.symon.orders.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
public class OrderItem {

    @NotBlank(message = "Product code cannot be ampty.")
    @Size(min = 15, max = 15, message = "Product code must have 10 characters.")
    private String code ;

    @NotBlank(message = "Product description cannot be empty.")
    @Size(max = 100, message = "Product description must be at most 100 characters.")
    private String description;

    @NotNull(message = "Price must be provided.")
    @Min(value = 1, message = "Price must be greater than zero.")
    private Integer unitPriceInCents;

    @NotNull(message = "Quantity must be provided.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity;

    @Override
    public int hashCode() {
        return Objects.hash(code, quantity);
    }
}
