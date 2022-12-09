# What is JPA
JPA ( Java Persistent API )는 JAVA ORM 기술에 대한 API 표준 명세를 의미한다.
즉, ORM을 사용하기 위한 인터페이스를 모아둔 것이다. 
JPA를 사용하기 위해서는 JPA를 구현한 Hibernate, EclipseLink, DataNucleus 같은 ORM Framework을 사용해야 한다.

# What is ORM
ORM ( Object Relational Mapping )은 객체와 DB의 테이블이 Mappingㅇ르 이루는 것을 말한다.
즉, 객체가 테이블이 되도록 Mapping 시켜주는 것을 말한다.

## Why to use ORM
SQL native Query가 아닌 직관적인 코드로서 데이터를 조작할 수 있다.

```` bash
SELECT * FROM User;
````
MySQL에서는 native query로 짜던 것을
`user.findAll();` 함수로 대체할 수 있다.

## effect to use ORM
* 메소드 호출로 query 수행을 하기 때문에, ORM을 사용하면 생산성이 매우 높다.
* query가 복잡해지면, ORM으로 표현하는 데 한계가 있다.
* 성능이 raw query에 비해 느리다.

## etc.
* JPQL, QueryDSL 등을 사용하거나, Mybatis나 JPA를 같이 사용하기도 한다.

> JDBC 직접 사용하기 보다는 MyBatis를 사용했을 때, 코드가 간결해지고 유지보수가 편하다.

# Mybatis vs Hibernate
JPA를 구현한 framework 중 하나이다.

## JPA 등장 배경
Mybatis는 테이블마다 비슷한 CRUD SQL을 계속 반복적으로 사용한다. 
분산 프로그래밍2 에서는 DAO 개발에서 반복된 코드가 매우 많았다.

테이블에 변화가 생긴다면, 이와 관련된 DAO의 SQL도 수정해야 한다. DAO와 테이블이 강한 의존성을 갖게 된다.
또한, 객체를 단순히 데이터 전달 목적으로 사용할 뿐, 객체 지향적이지 못하다는 문제가 있다.

## Hibernate
* 장점
1. 생산성
SQL를 직접 사용하지 않고, 메소드 호출로 쿼리가 수행된다.
반복적인 작업이 줄어, 생산성이 높아진다.
2. 유지보수
테이블 컬럼이 하나 변경되었을 경우, 
    Mybatis: 관련 DAO를 확인하고 수정해야 한다.
    JPA: 얘가 알아서 해준다.
3. 특정 벤더에 종속적이지 않다.
    MySQL, Oracle ..등 에 얽메이지 않는다.
    설정파일에서 JPA에게 어떤 DB를 사용하고 있는 지 알려주기만 하면 된다.

* 단점
1. 성능
    메소드 호출로 쿼리를 실행 -> 내부적으로 많은 것이 처리됨을 의미 -> raw query보다 성능이 떨어짐
2. 세밀함
    세밀함이 떨어진다. 
    객체간의 매핑이 잘못되거나 JPA를 잘못 사용하여 의도하지 않은 동작을 할 수도 있다.
    -> 이를 보완하기 위해 JPQL을 등장한다.
3. 러닝커브

출처: [spring JPA](https://victorydntmd.tistory.com/195)


<br/>

# ORM
ORM은 DB 테이블을 JAVA 객체로 매핑하면서 객체 간의 관계를 바탕으로 SQL을 자동으로 생성한다. ( *Mapper는 SQL을 명시해주어야 한다. )

## ORM vs. Mapper
ORM은 RDB의 관계를 Object에 반영하는 것이 목적이다.
Mapper는 필드를 매핑하는 게 목적이다.

## SQL Mapper
`sql` <- mapping -> object field
예: Mybatis, jdbcTemplate

> JDBC는 DB에 접근할 수 있도록 JAVA에서 제공하는 API이다.

## Sprin-Data-JPA
Application이 JPA를 통해 Hibernate와 JDBC로 RDB에 접근한다.
이때, JPA는 ORM을 위한 JAVA EE표준이며, Spring-Data-JPA는 JPA를 쉽게 사용하기 위해 Spring에서 제공하는 Framework이다.

## JPA 동작 과정
JPA는 Application과 JDBC 사이에서 동작한다.
개발자가 JPA를 사용하면, JPA 내부에서 JDBC API를 사용하여 SQL을 호출하여 DB와 통신한다.
> 개발자가 직접적으로 JDBC API를 사용하는 것이 아니다.!

* insert
UserService에서 새로운 User를 저장하고 싶을 때, JPA에 User 객체를 넘긴다.
JPA는 
    1. User Entity를 분석하고,
    2. INSERT query를 생성한다.
    3. JDBC API를 사용하여 query를 DB에 날린다.

##  JPA 특징
1. 데이터를 객체지향적으로 관리할 수 있다.
2. 자바 객체와 DB 테이블 사이의 매핑 설정을 통해 query를 생성한다.
3. 객체를 통해 query를 작성할 수 있는 JPQL를 지원한다.
4. JPA는 성능 향상을 위해 지연 로딩이나 즉시 로딩과 같은 몇 가지 기법을 제공한다.

# 영속성
데이터를 생성한 프로그램이 종료되어도 사라지지 않는 데이터의 특성을 말한다.
영속성을 갖지 않으면, 데이터는 메모리에서만 존재하게 되고 프로그램이 종료되면 해당 데이터는 모두 사라지게 된다.
그래서 데이터를 DB에 저장한다.

## Persistance Layer
아키텍처에서 데이터에 영속성을 부여해주는 계층이다.
JDBC를 이용해 직접 구현이 가능하나, 보통은 Persistance Framework를 사용한다.

----------------------
|presenatiation layer|      -> UI Layer
----------------------      -> Application layer ( service layer )
|business logic      |      -> Dmain layer
----------------------
|persistence layer   |      -> Data Access Layer 
----------------------
|DB                  |

Persistencec Framework
JDBC를사용한 프로그래밍에서의 복잡함이나 번거로움 없이 간단한 작업만으로도 DB와 연동되는 시스템을 빠르게 개발할 수 있고, 안정적인 구동을 보장한다.


출처 : [JPA](https://velog.io/@adam2/JPA%EB%8A%94-%EB%8F%84%EB%8D%B0%EC%B2%B4-%EB%AD%98%EA%B9%8C-orm-%EC%98%81%EC%86%8D%EC%84%B1-hibernate-spring-data-jpa)