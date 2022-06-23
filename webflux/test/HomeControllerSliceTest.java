// import static org.assertj.core.api.Assertions.assertThat;
// import static org.mockito.Mockito.when;


// import com.example.webflux_study.chap2.Cart;
// import com.example.webflux_study.chap2.CartService;
// import com.example.webflux_study.chap2.HomeController;
// import com.example.webflux_study.chap2.InventoryService;
// import com.example.webflux_study.chap2.Item;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.web.reactive.server.WebTestClient;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;

// @WebFluxTest(HomeController.class)
// public class HomeControllerSliceTest {

//   @Autowired
//   private WebTestClient client;

//   @MockBean
//   InventoryService inventoryService;

//   @Test
//   void homePage() {
//     when(inventoryService.getInventory()).thenReturn(Flux.just(
//         new Item("id1", "name1", 1.99),
//         new Item("id2", "name2",  1.99)
//     ));

//     when(inventoryService.getCart("My Cart"))
//         .thenReturn(Mono.just(new Cart("My Cart")));

//     client.get().uri("/").exchange()
//         .expectStatus().isOk()
//         .expectBody(String.class)
//         .consumeWith(exchangeResult -> {
//           assertThat(
//               exchangeResult.getResponseBody()).contains("action=\"/add/id1\"");
//           assertThat(
//               exchangeResult.getResponseBody()).contains("action=\"/add/id2\"");
//         });
//   }
// }
