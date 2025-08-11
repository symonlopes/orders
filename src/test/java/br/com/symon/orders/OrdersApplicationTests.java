package br.com.symon.orders;

import br.com.symon.orders.error.ErrorResponse;
import br.com.symon.orders.model.Order;
import br.com.symon.orders.model.OrderItem;
import br.com.symon.orders.model.OrderStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
		var result = saveOrder(order)
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

		var result = saveOrder(order)
				.andExpect(status().isCreated())
				.andReturn();

		var returned =  objectMapper.readValue(result.getResponse().getContentAsString(), Order.class);
		assertEquals(428000, returned.getTotalInCents(), "Order total does not match");
	}

	@Test
	public void shouldNotCreateOrderWithItemWithoutQuantity() throws Exception {

		var order = mockValidOrder();
		order.getItems().stream().findFirst().ifPresent(item -> {item.setQuantity(0);});
		saveOrder(order)
				.andExpect(status().isBadRequest())
				.andReturn();
	}

	@Test
	public void shouldNotCreateOrderWithItemWithoutPrice() throws Exception {

		var order = mockValidOrder();
		order.getItems().stream().findFirst().ifPresent(item -> {item.setUnitPriceInCents(0);});
		saveOrder(order)
				.andExpect(status().isBadRequest())
				.andReturn();
	}

	@Test
	public void shouldNotCreateOrderWithoutSellerId() throws Exception {

		var order = mockValidOrder();
		order.setSellerId(null);
		saveOrder(order)
				.andExpect(status().isBadRequest())
				.andReturn();
	}

	@Test
	public void shouldNotCreateOrderWithoutCustomerId() throws Exception {

		var order = mockValidOrder();
		order.setCustomerId(null);
		saveOrder(order)
				.andExpect(status().isBadRequest())
				.andReturn();

	}

	@Test
	public void shouldSaveAnOrderSuccessfully() throws Exception {

		var order = mockValidOrder();
		var result = saveOrder(order)
				.andExpect(status().isCreated())
				.andReturn();

		var returnedItem =  objectMapper.readValue(result.getResponse().getContentAsString(), Order.class);
		assertNotNull(returnedItem.getId(), "Order id must not be null");
	}

	@Test
	public void shouldGetOrderById() throws Exception {

		var order = mockValidOrder();

		var postResult = saveOrder(order)
				.andExpect(status().isCreated())
				.andReturn();

		var returnedOrder =  objectMapper.readValue(postResult.getResponse().getContentAsString(), Order.class);

		var getResult = findOrderById(returnedOrder.getId())
				.andExpect(status().isOk())
				.andReturn();

		var foundedOrder =  objectMapper.readValue(getResult.getResponse().getContentAsString(), Order.class);

		assertEquals(returnedOrder.getId(),foundedOrder.getId(), "Order id must  be equals");
	}

	@Test
	public void shouldGetOrderByStatus() throws Exception {

		var order = mockValidOrder();

		var postResult = saveOrder(order)
				.andExpect(status().isCreated())
				.andReturn();

		objectMapper.readValue(postResult.getResponse().getContentAsString(), Order.class);

		var orders = findOrderByStatus(OrderStatus.NEW)
				.andExpect(status().isOk())
				.andReturn();

		var foundedOrders = objectMapper.readValue(
				orders.getResponse().getContentAsString(),
				new TypeReference<List<Order>>() {}
		);

        assertFalse(foundedOrders.isEmpty(), "Order id must  be equals");
	}

	private ResultActions saveOrder(Order order) throws Exception {
		return mockMvc.perform(post("/api/order")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(order)));
	}

	private ResultActions findOrderById(String id) throws Exception {
		return mockMvc.perform(get("/api/order/" + id)
				.contentType(MediaType.APPLICATION_JSON));
	}

	private ResultActions findOrderByStatus(OrderStatus status) throws Exception {
		return mockMvc.perform(get("/api/order/status/" + status.name() + "?limit=" + 1)
				.contentType(MediaType.APPLICATION_JSON));
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
