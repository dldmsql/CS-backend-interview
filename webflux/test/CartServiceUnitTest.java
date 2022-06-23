import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static reactor.core.publisher.Mono.when;

import com.example.webflux_study.chap2.Cart;
import com.example.webflux_study.chap2.CartItem;
import com.example.webflux_study.chap2.CartRepository;
import com.example.webflux_study.chap2.CartService;
import com.example.webflux_study.chap2.Item;
import com.example.webflux_study.chap2.ItemRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class CartServiceUnitTest {

  CartService cartService;

  @MockBean private ItemRepository itemRepository;
  @MockBean private CartRepository cartRepository;

  @BeforeEach
  void setUp() {
    // 테스트 데이터 정의
    Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
    CartItem sampleCartItem = new CartItem(sampleItem);
    Cart sampleCart =  new Cart("My Cart", Collections.singletonList(sampleCartItem));

    // 협력자와의 상호작용 정의
    when(cartRepository.findById(anyString())).thenReturn(Mono.empty());
    when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
    when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

    cartService = new CartService(itemRepository, cartRepository); // 생성자 주입
  }

  @Test
  void addItemToEmptyCartShouldProduceOneCartItem() {
    cartService.addToCart("My Cart", "item1") // Mono<T> 를 리턴한다.
        .as(StepVerifier::create) // 리액터 테스트 모듈의 정적 메소드인 StepVerifier.create()에 메소드 레퍼런스로 연결해서, 테스트 기능을 전담하는 리액터 타입 핸들러 생성
        .expectNextMatches(cart -> { // 결과 검증
          assertThat(cart.getCartItems()).extracting(CartItem::getQuantity) // 각 장바구니에 담긴 상품의 개수를 추출하고, 장바구니에 한 가지 종류의 상품 한 개만 들어 있음을 단언
              .containsExactlyInAnyOrder(1);

          assertThat(cart.getCartItems()).extracting(CartItem::getItem) // 각 장바구니에 담긴 상품을 추출해서 그 상품이 setUp()에서 정의한 바와 맞는지 검증
              .containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99));

          return true; // expectNextMatches()는 boolean을 반환한다.
        })
        .verifyComplete(); // 리액티브 스트림의 complete 시그널 발생, 테스트 성공을 검증한다.
  }
}
