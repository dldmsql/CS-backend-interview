# What is Spring with WebFlux

> 목차
* 리액티브 프로그래밍
* 리액티브 스트림
* observer 패턴

## 리액티브 프로그래밍

개념
- 데이터의 흐름을 먼저 정의하고, 데이터의 변화 혹은 작업의 종료에 따라 반응하여 진행되는 프로그래밍이다.
- 실시간 변화에 반응하는 것에 중점을 둔다.

특징
- 비동기 이벤트 처리를 위해 observer 패턴을 기반으로 동작한다.
- 함수형 프로그래밍의 지원을 통해 thread 안정성 보장
    -> 데드락, 동기화 문제에서 해방

## 리액티브 스트림
정의
- non-blocking / backpressure (배압)을 이용하여 비동기 서비스를 할 때, 표준이 되는 스펙이다.

목적
- reactive programming 구현
- 비동기의 경계를 명확히 하여 stream 데이터의 교환을 효과적으로 관리
- backpressure 를 통해서 컴포넌트 간의 데이터를 비동기적으로 전달

API 구성 요소

|클래스명|역할|시그널|예|
|------|---|---|---|
|Publisher|생산자|subscribe()||
|Subscriber|onSubcribe() <br/> onNext() <br/> onError() <br/> onComplete() ||
|Subscription|publisher와 subscriber의 중계자. 데이터 요청량 조절 및 구독 취소 담당.|request() <br/> cancel()||
|Preocessor|publisher와 subscriber를 혼합||subscriber가 다른 subscriber에게 전달 시, 마치 새로운 publisher처럼 동작 ( 멀티 캐스팅 )|

동작 순서
1. subscriber가 `subscribe()` 호출 :: publisher에게 구독 요청
2. publisher가 `onSubscribe(Subscription) 호출 :: subscriber에게 subscription 전달
3. subscriber가 `Subscription.request()` 호출 :: publisher에게 '나한테 데이터 줄래?' 요청
4. publisher가 Subscription을 통해 `Subscriber.onNext()`로 데이터 전달
5. 전달 성공 -> `onComplete()` <br/> 전달 오류 -> `onError()`

## Observer 패턴
정의
객체의 상태 변화를 관찰하는 관찰자의 목록을 객체에 등록하여 **상태 변화가 있을 때마다** 메소드 등을 통해 객체가 직접 목록의 각 관찰자에게 통지하도옥 하는 디자인 패턴

구성
|Subject|관찰 대상|
|Observer|관찰자|
|Subscribe|관찰 대상에게 관찰자 등록|
|publish/ Notiy| 관찰대상의 변화를 관찰자에게 알림|


참고자료
[여기](https://sleepy-cartwheel-ace.notion.site/What-is-WebFlux-6742a86760454661afad2d29ab4178ac) 참고
> 오늘 정리한 내용은 2022.03에 공부하던 내용을 복습 겸 다시 정리하는 시간을 갖고자 일부 자료를 정리하였습니다.