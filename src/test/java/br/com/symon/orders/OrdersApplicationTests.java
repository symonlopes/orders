package br.com.symon.orders;

import br.com.symon.orders.error.ErrorResponse;
import br.com.symon.orders.model.Order;
import br.com.symon.orders.model.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
class OrdersApplicationTests {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void shouldNotCreateOrderWithoutItems() throws Exception {

		var order = Order.builder()
				.sellerId("SYMON1110900")
				.customerId("CUSTOMER998528411")
				.items(new HashSet<>())
				.createdAt(LocalDateTime.now())
				.build();

		var result = mockMvc.perform(post("/api/order")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(order)))
				.andExpect(status().isBadRequest())
				.andReturn();


		var errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
		assertEquals(1, errorResponse.errors().size(), "An error must be returned.");
	}

	@Test
	public void shouldNotCreateOrderWithItemWithoutQuantity() throws Exception {

		var order = Order.builder()
				.sellerId("SYMON1110900")
				.customerId("CUSTOMER998528411")
				.items(new HashSet<>())
				.createdAt(LocalDateTime.now())
				.build();

		order.getItems().add(
				OrderItem.builder()
						.code("EAN1231222")
						.priceInCents(7800)
						.description("CX MANTEIGA 12 UND")
						.build()
		);

		var result = mockMvc.perform(post("/api/order")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(order)))
				.andExpect(status().isBadRequest())
				.andReturn();


		var errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
		assertEquals(1, errorResponse.errors().size(), "Item must have quantity");
	}

	@Test
	public void shouldSaveAnOrderSuccessfully() throws Exception {

		var order = Order.builder()
				.sellerId("SYMON1110900")
				.customerId("CUSTOMER998528411")
				.items(new HashSet<>())
				.createdAt(LocalDateTime.now())
				.build();

		order.getItems().add(
				OrderItem.builder()
						.code("EAN1231222")
						.quantity(10)
						.priceInCents(7800)
						.description("CX MANTEIGA 12 UND")
						.build()
		);

		var result = mockMvc.perform(post("/api/order")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(order)))
				.andExpect(status().isCreated())
				.andReturn();

		var returnedItem =  objectMapper.readValue(result.getResponse().getContentAsString(), Order.class);

		assertNotNull(returnedItem.getId(), "Order id must not be null");
	}

}
