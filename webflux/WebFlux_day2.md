# What is Spring with WebFlux

> 목차
* 리액티브 단위 테스트
* 테스트 코드 작성
* 좋은 테스트 전략
* 스프링 부트 슬라이스 테스트

## 리액티브 단위 테스트

**단위 테스트** 란?
단위(unit)는 JAVA에서 하나의 클래스라고 볼 수 있다. 테스트 대상 클래스가 의존하는 다른 협력 클래스의 실제 인스턴스 대신 가짜 인스턴스인 스텁 (stub)을 사용해서 협력 클래스는 테스트 대상에서 제외하고, 오직 테스트 대상 클래스만의 기능을 테스트하고 검증하는 것을 말한다.
> '그루터기' 프로젝트에서 controller 단위 테스트를 했던 것을 떠올리자. controller가 의존하는 service 클래스를 new하지 않고, stub을 이용해서 테스트를 진행했었다.

spring boot에서는 테스트에 필요한 여러 도구를 쉽게 사용할 수 있도록 starter를 제공한다. 빌드 파일에 `spring-boot-starter-test`를 추가하면, 아래와 같은 라이브러리가 자동으로 추가된다.
* spring boot test
* json path
* JUnit 5
* AssertJ
* Mockito
* JSONassert
* Spring Test

## 테스트 코드 작성
`@ExtendWith()` 어노테이션은 테스트 핸들러를 지정할 수 있는 JUnit 5의 API다. `SpringExtension`은 스프링에 특화된 테스트 기능을 사용할 수 있게 해준다.
테스트의 대상이 되는 클래스를 `CUT(class under test)`라고 한다. 만약, 서비스 클래스의 단위 테스트라면 이 클래스를 제외하고 연관되는 클래스들은 모두 **협력자**라는 이름을 붙여 Mock 객체를 만들거나 Stub을 만들어서 테스트 대상에서 제외한다.
> Mock vs. Stub
    Mock은 메소드 호출 여부, 순서, 횟수 등 행위 검증을 위해 만들어지는 '가짜' 객체이다.
    Stub은 값 기반의 상태 검증을 위해 미리 정해진 값을 반환하도록 만들어진 '가짜' 객체이다.

`@MockBean` 어노테이션은 협력자의 가짜 객체를 만들고, 스프링 빈으로 등록하기 위해 사용된다. 스프링 부트 테스트는 이 어노테이션을 보면, mockito를 사용해서 가짜 객체를 만들고 이를 애플리케이션 컨텍스트에 빈으로 추가한다.
이 어노테이션이 갖는 특징은 2가지이다.
1. 코드 작성 시간 단축
2. 테스트 대상과 협력자의 명확한 구분

**리엑티브 테스트** 시, 주의할 점
기능만을 검사하는 것이 아닌, 리액티브 스트림 시그널도 함께 검사해야 한다. 
리액티브 스트림은 `onSubscribe`, `onNext`, `onError`, `onComplete`를 말한다. 
리액티브는 **누군가가 구독을 하기 전까지는 아무 일도 일어나지 않는다**. 그렇다면, 테스트에서는 누가 구독자의 역할을 하는가? 바로 `StepVerifier`이다. 결괏값을 얻기 위해 블로킹 방식으로 기다리는 대신에 테스트 도구가 대신 구독을 하고 확인할 수 있게 해준다.

```` bash
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
````
위의 예제 코드는 Top-level 방식으로 작성된 것이다. 이 방식 외에도 다른 방식이 있다. 

```` bash
@Test
  void addItemToEmptyCartShouldProduceOneCartItem() {
    StepVerifier.create( // 리액터 테스트 모듈의 정적 메소드인 StepVerifier.create()에 메소드 레퍼런스로 연결해서, 테스트 기능을 전담하는 리액터 타입 핸들러 생성
        cartService.addToCart("My Cart", "item1")) // Mono<T> 를 리턴한다.
        .expectNextMatches(cart -> { // 결과 검증
          assertThat(cart.getCartItems()).extracting(CartItem::getQuantity) // 각 장바구니에 담긴 상품의 개수를 추출하고, 장바구니에 한 가지 종류의 상품 한 개만 들어 있음을 단언
              .containsExactlyInAnyOrder(1);

          assertThat(cart.getCartItems()).extracting(CartItem::getItem) // 각 장바구니에 담긴 상품을 추출해서 그 상품이 setUp()에서 정의한 바와 맞는지 검증
              .containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99));

          return true; // expectNextMatches()는 boolean을 반환한다.
        })
        .verifyComplete(); // 리액티브 스트림의 complete 시그널 발생, 테스트 성공을 검증한다.
  }
````
이 방식은 단순히 바깥에 명시적으로 드러난 행이 아니라 메소드의 인자까지 살펴봐야 무엇이 테스트되는지를 알 수 있으므로 별로 좋지 않아 보인다.

첫번째 테스트 코드 처럼 테스트 대상 메소드 호출부를 맨 위에 배치하고, 리액터의 `as()` 연산자를 사용해서 테스트 대상 메소드 결괏값을 StepVerifier로 흘려보내는 top-level 방식으로 작성하면 테스트 코드의 의도가 더 분명해진다.

> 리액터의 StepVerifier를 사용하는 모든 테스트 케이스에서 `onSubscribe` 시그널이 발생했다. 하지만, `doOnSubscribe(...)`에 구독 시 실행되어야 하는 기능을 작성했다면, `expectSubscription(...)`을 사용해서 구독에 대한 테스트도 반드시 추가해야 한다.

## 좋은 테스트 전략
1. null 값 처리를 포함한 도메인 객체 테스트
2. 가짜 협력자를 활용해서 모든 비즈니스 로직을 검사하는 서비스 계층 테스트
3. 내장 웹 컨테이너를 사용하는 약간의 종단 간 테스트

**약간의 종단 간 테스트** ?? 
테스트 범위가 넓어질수록 테스트는 깨지기 쉽다. '그루터기' 테스트 코드 작성하면서, 느꼈던 바와 같이 도메인의 변경 혹은 서비스 로직의 변경 등이 발생할 때마다 테스트 코드를 변경해야 하는 일이 발생한다. 


## 스프링 부트 슬라이스 테스트
단위 테스트와 종단 간 통합 테스트 중간 정도에 해당하는 게 **슬라이스 테스트**이다. 스프링 부트는 슬라이스 테스를 위해 다양한 기능을 제공한다.