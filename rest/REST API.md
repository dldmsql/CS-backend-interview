# REST API

## 정의
REST ( Representational State Transfer )의 약자로 아키텍처이다.
자원을 이름으로 구분하여 자원의 상태를 주고 받는 모든 것을 지칭한다.
WWW와 같은 분산 시스템을 위한 **아키텍처**이다.

## 구체화
HTTP URI를 통해 자원을 명시,
HTTP Method ( POST, GET, PUT, DELETE )를 통해 자원에 대한 CRUD 행동

## 결론
자원 기반의 구조 설계가 가능하다.
설계의 중심에는 자원이 있고, 이걸 HTTP Method를 통해 처리하도록 설계된 아키텍처이다.

## 왜 사용하지?
Client에 제약을 두지 않기 위해서이다. Client는 모바일, PC 등 다양한 방식으로 접근 가능하다.
    과거에는 SSR ( Server Side Rendering )방식으로 Client는 PC 브라우저로 명확했기에, JSP, ASP, PHP를 이용한 웹 페이지를 구성했다.
    현재에는 다양한 Client를 일일이 고려하지 않고 메세지 기반, XML, JSON과 같은 Client에서 바로 객체로 바꿀 수 있는 데이터 통신을 지향하게 되면서 Server와 Client의 역할이 분리되었다.
    이 과정에서 필요해진 것이 규칙이다.

## 구성
- 자원 : URL
- 행위 : Http Method
- 표현

1. 자원
모든 자원에는 고유한 ID가 존재하고, 이 자원은 SERVER에 존재한다.
자원을 구별하는 ID는 HTTP URI가 있다. ( 예: `/post/post_id`)
2. 행위
GET, POST, PUT, DELETE, PATCH 메서드를 사용한다.
3. 표현
CLIENT가 자원의 상태에 대한 조작을 요청하면 SERVER는 이에 적절한 응답을 보낸다.
이때 응답을 표현이라 하는데, 이유는 하나의 자원을 여러 형태로 표현할 수 있기 때문이다.
현재는 JSON으로 주고 받는 것이 대부분이다.

## 특징
1. CLIENT-SERVER 구조
CLIENT : USER와 가장 가까운 SIDE
SERVER : REST API를 제공
2. Stateless
HTTP를 이용하기 때문에, stateless하다.
### STATELESS
서버에서 어떤 작업을 하기 위해 상태 정보를 기억할 필요가 없고, 들어온 요청에 대해서만 처리해주기 때문에 구현이 쉽다.
3. 캐시 처리 가능
대량의 요청을 효율적으로 처리하기 위해 캐시가 요구된다. 캐시 사용을 통해 응답시간이 빨리지고, SERVER는 트랜잭션이 발생하지 않기 때문에 전체 응답시간, 성능, 자원 이용률을 향상 시킬 수 있다. 
**캐시, 트랜잭션 조사하기**
4. 자체 표현 구조
JSON을 이용해 직관적으로 이해할 수 있고, REST API 메세지만으로 그 요청이 어떤 행위를 하는 지 알 수 있다.
5. 계층화
CLIENT-SERVER로 분리되기 때문에, 중간에 프록시 서버, 암호화를 위한 별도의 계층 등 중간매체를 사용할 수 있어 자유도가 높다.
**프록시 서버 조사하기**
6. UNIFORM
UNIFORM INTERFACE는 http 표준에만 따르면 모든 플랫폼에서 사용이 가능하다. ( URI로 지정한 자원에 대한 조작을 가능하게 한다. )
**즉, 특정 언어나 기술에 종속되지 않는다.**

## !! 핵심 규칙 !!
- URI는 정보의 자원을 표현해야 한다.
- 자원에 대한 행위는 HTTP Method로 표현한다.