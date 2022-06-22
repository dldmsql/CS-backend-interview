# What is Spring with WebFlux

> 목차
* 스프링부트란 무엇인가
* WebFlux


## 스프링 부트란 ?
spring 기반 프로젝트를 신속하게, 미리 정의된 방식으로, 이식성 있게, 실제 서비스 환경에 사용할 수 있도록 조립한 것이다.

* 신속성 : 의존 관계를 신속히 적용할 수 있게 해준다.
* 미리 정의된 방식 : 구성을 미리 정의하면, 정의에 따라 기본 설정이 자동으로 된다.
* 이식성 : JDK가 있는 환경이라면 스프링 부트 애플리케이션을 어디에서나 실행할 수 있다.
* 실제 서비스 환경에 사용 가능 : 스프링 부트로 만들어진 애플리케이션이라면, 어디든 배포해서 실행할 수 있기에 아주 작은 부분에서만 사용해야 한다는 제약이 없다.

## 리액티브 프로그래밍
> spring framework 5.0에 포함된 새로운 패러다임이다.
대규모 사용자가 지속적으로 증가하는 시스템 ( high-end )은 비동기적으로 인입되는 거의 무제한의 데이터 스트림을 non-blocking 방식으로 처리할 수 있어야 한다.

리액티브 프로그래밍은 오래 전부터 존재했으나, 이것을 사용할 만큼 대규모의 서비스가 많지 않았기에 주류 기술로 적용되지 않았다. 
현재 IT 시장은 수백만 명의 사용자에게 콘텐츠를 제공하고 24시간 동안 끊임없이 운영되어야 하며, 클라우드 환경에서 운영하는 것이 보편화되고 있다.

기존 자원을 효율적이고 일관성 있게 사용하기 위해 떠오르는 방법이 `리액티브 스트림`이다.

리액티브 스트림은 `발행자`와 `구독자` 사이의 간단한 계약을 정의하는 명세다. 트래픽을 가능한 한 빨리 발행하는 대신에 구독자가 발행자에게 수요를 알리는 방식으로 트래픽을 제어할 수 있다. 
기업 간 시스템을 발행자와 구독자 관계로 표현하면, 시스템 범위의 `배압(backpressure)`을 적용할 수 있다. 이는 트래픽을 잘 조절해서 관리할 수 있는 장점이 있다.

<br>

프로젝트 리액터는 VM 웨어에서 만든 리액티브 스트림 구현체다. 리액터를 사용하면 아래의 특징이 있다.
* Non-blocking, 비동기 프로그래밍
* 함수형 프로그래밍 스타일
* Thread를 신경 쓸 필요 없는 동시성

## 리액터 타입
리액티브 스트림은 수요 조절에 기반하고 있다. 프로젝트 리액터는 Flux<T>를 사용해서 수요 조절을 구현한다. Flux<T>는 일련의 T 객체를 담고 있는 컨테이너다. 쉽게 말해, 물건을 전달해주는 역할을 하는 plcaeholer로 레스토랑에서 일하는 서빙 점원과 비슷하다고 할 수 있다.