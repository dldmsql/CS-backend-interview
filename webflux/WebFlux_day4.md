# What is Spring with WebFlux

> 목차
* Non-blocking vs. blocking

## Non-blocking vs. blocking

‼️ **Point*
* 제어권
    자신의 코드를 실행할 권리
    제어권을 가진 함수는 자신의 코드를 끝까지 실행한 후, 자신을 호출한 함수에게 제어권을 돌려준다.
* 결과값을 기다리다
    When `fun A` calls `fun B`, A가 B의 결과를 기다리는 지 여부

1. Blocking
fun A -> fun B 호출
A (제어권) -> B (제어권) 이동

함수 A가 함수 B를 호출한다. 이때, 제어권이 A -> B 로 이동.
함수 B는 자신의 코드를 끝까지 실행한다. ( 함수 A는 제어권이 없으므로, 함수 B의 return이 있기 까지 기다린다. )
함수 B는 실행이 끝나면, 함수 A에게 제어권을 return 한다.

2. Non-Blocking
fun A -> fun B 호출
A(제어권) 

함수 A가 함수 B를 호출한다. 이때, 함수 B는 실행되지만, 제어권은 함수 A가 그대로 지니고 있는다.
함수 A는 자신의 코드를 계속 실행한다.

3. Synchronous vs. Asynchronous
동기와 비동기는 **결과값을 기다리는 지 여부*의 차이이다.

    1. Synchronous
    함수 A가 함수 B를 호출한다. 함수 A는 함수 B의 return을 계속 확인한다. 
    2. Asynchronous
    함수 A가 함수 B를 호출할 때, callback을 같이 전달한다. 함수 B가 자신의 코드를 끝까지 실행하면, callback을 실행한다.
    함수 A는 함수 B를 호출한 뒤로, 함수 B의 완료 여부는 신경쓰지 않는다.

참고자료 [여기](https://velog.io/@nittre/%EB%B8%94%EB%A1%9C%ED%82%B9-Vs.-%EB%85%BC%EB%B8%94%EB%A1%9C%ED%82%B9-%EB%8F%99%EA%B8%B0-Vs.-%EB%B9%84%EB%8F%99%EA%B8%B0)

