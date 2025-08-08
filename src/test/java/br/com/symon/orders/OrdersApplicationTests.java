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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

		var order = mockValidOrder();

		order.setItems(null);

		var result = mockMvc.perform(post("/api/order")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(order)))
				.andExpect(status().isBadRequest())
				.andReturn();


		var errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
		assertEquals(1, errorResponse.errors().size(), "An error must be returned.");
	}

	@Test
	public void shouldCalculateOrderTotal() throws Exception {

		var order = mockValidOrder();

		order.setItems(new HashSet<>());

		order.getItems().add(
				OrderItem.builder()
						.code("EAN12312221123A")
						.quantity(10)
						.unitPriceInCents(7800)
						.description("PRODUTO PPP")
						.build()
		);

		order.getItems().add(
				OrderItem.builder()
						.code("EAN12312AALKJHA")
						.quantity(10)
						.unitPriceInCents(35000)
						.description("PRODUTO XYZ")
						.build()
		);

		var result = mockMvc.perform(post("/api/order")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(order)))
				.andExpect(status().isCreated())
				.andReturn();


		var returned =  objectMapper.readValue(result.getResponse().getContentAsString(), Order.class);
		assertEquals(428000, returned.getTotalInCents(), "Order total does not match");
	}

	@Test
	public void shouldNotCreateOrderWithItemWithoutQuantity() throws Exception {

		var order = mockValidOrder();

		order.getItems().stream().findFirst().ifPresent(item -> {item.setQuantity(0);});

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

		var order = mockValidOrder();

		var result = mockMvc.perform(post("/api/order")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(order)))
				.andExpect(status().isCreated())
				.andReturn();

		var returnedItem =  objectMapper.readValue(result.getResponse().getContentAsString(), Order.class);

		assertNotNull(returnedItem.getId(), "Order id must not be null");
	}


	private static Order mockValidOrder() {
		var order = Order.builder()
				.sellerId("SYMON1110900")
				.customerId("CUSTOMER998528411")
				.items(new HashSet<>())
				.createdAt(LocalDateTime.now())
				.build();

		order.getItems().add(
				OrderItem.builder()
						.code("EAN12312221123A")
						.quantity(10)
						.unitPriceInCents(7800)
						.description("PRODUTO PPP")
						.build()
		);

		order.getItems().add(
				OrderItem.builder()
						.code("EAN12312AALKJHA")
						.quantity(30)
						.unitPriceInCents(35000)
						.description("PRODUTO XYZ")
						.build()
		);

		order.getItems().add(
				OrderItem.builder()
						.code("EAN12312QQADSWS")
						.quantity(2)
						.unitPriceInCents(5500)
						.description("PRODUTO AJU")
						.build()
		);

		return order;
	}

}
