package br.com.symon.orders.error;

import lombok.Builder;

import java.util.Collection;

@Builder
public record ErrorResponse( Collection<ApiError> errors) {
}
