package br.com.symon.orders.service;

import br.com.symon.orders.model.Order;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CacheService {

    private final CacheManager cacheManager;

    public boolean isOnCache(@Valid Order order) {
        String hashKey = String.valueOf(order.hashCode());
        Cache cache = cacheManager.getCache("orders");
        return cache != null && cache.get(hashKey) != null;
    }

    public void saveOrderOnCache(@Valid Order order) {
        String hashKey = String.valueOf(order.hashCode());
        Cache cache = cacheManager.getCache("orders");
        if (cache != null ) {
            cache.put(hashKey, true);
        }
    }
}
