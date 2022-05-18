# DTO

## 정의
Data Transfer Object의 약어이다.
계층 간 데이터 교환을 위해 사용하는 객체이다.
로직을 갖지 않는 순수한 데이터 객체이다.
getter/setter가 있다.

## 왜 쓰지?
Return 타입을 entity로 할 경우, controller와 service에서 해당 로직들이 entity의 속성값과 의존관계를 맺게 된다.
이는 유지보수나 관리 측면에서 부적합하다.
entity는 한 번 변경되면 그 파장이 매우 크다.
DTO는 view를 위한 용도이기에 변경이 잦다.

[참고 블로그]
https://ws-pace.tistory.com/72