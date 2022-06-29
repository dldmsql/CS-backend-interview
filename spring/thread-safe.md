# Spring is Thread-safe ?

> 목차
> * What is singleton pattern
> * What is thread
> * REST API 적용

## 1. What is singleton pattern
spring Bean의 기본 scope은 singleton이고, spring 환경은 multi-thread이다. 여기서 singleton은 무엇일까?

### singleton 
어플리케이션이 실행될 때, 어떤 클래스가 **최초 한 번만 메모리를 할당하고** 그 메모리에 인스턴스를 만들어 사용하는 디자인 패턴을 말한다.
> 쉽게 생각해보자. 생성자의 호출이 반복적으로 이루어질 때, 실제로 생성되는 객체는 최초에 생성된 객체를 반환 해주는 거다.

```` bash
public clas Singleton {
    private static Singleton instance = new Singleton();

    private Singleton() {}

    public static Singleton getInstance() {
        return instance;
    }

}
````
위의 코드를 보면, static으로 instance 변수를 선언하여 인스턴스화 하지 않고 사용할 수 있게 하였다. 
> 인스턴스화 ? [여기](https://victor8481.tistory.com/280)참고 <br/>
> OOP에서 어떤 클래스에 속하는 각 객체를 인스턴스라고 한다. <br/>
> 예를 들면, 붕어빵을 만들기 위해서는 붕어빵을 찍기 위한 틀이 필요하고, 틀이 있다면 그 안에 밀가루 반죽과 팥을 넣고 구워준다. 적당히 구워지면, 붕어빵이 완성된다.!! 이 과정을 OOP의 관점에서 다시 보자. <br/>
> 붕어빵을 만들기 위한 틀은 Class에 해당한다. <br/>
> 붕어빵 자체는 Object에 해당한다. <br/>
> 붕어빵 틀에서 만들어진 붕어빵들은 Instance에 해당한다. <br/>
> 붕어빵을 굽는 행위는 인스턴스화 하는 행위이다.

인스턴스가 1개만 생성되기에, 하나의 인스턴스를 메모리에 등록해서 여러 thread가 동시에 해당 인스턴스를 공유하여 사용할 수 있게끔 할 수 있다. 따라서, 요청이 많은 곳에서 사용하면 효율을 높일 수 있다.

단, 주의할 점은 singleton을 만들 때, 동시성(concurrency) 문제를 고려해서 설계해야 한다.

### feature of singleton
* Adventage
1. 한 번의 객체 생성으로 재상용이 가능하기 때문에, 메모리 낭비를 방지할 수 있다.
2. singleton으로 생성된 객체는 무조건 한 번 생성하기 때문에, 전역성을 띄운다. 따라서, 다른 객체와 공유가 용이하다.

> 언제 많이 쓸까? 공통된 객체를 여러 개 생성해서 사용해야 하는 상황!! DBCP (Database Connection Pool)

* Disadventage
1. singleton으로 만든 객체(인스턴스)가 너무 많은 일을 하거나, 많은 데이터를 공유시킬 경우에 다른 클래스의 인스턴스들 간에 결합도가 높아진다. 이는 "개방-폐쇄 원칙"을 위배하게 된다.
2. Multi-thread 환경에서 동기화 처리를 안하면, 인스턴스가 2개 생성될 수 있는 가능성이 생기게 된다.

[여기](https://elfinlas.github.io/2019/09/23/java-singleton/) 랑 [여기](https://devmoony.tistory.com/43) 참고

<hr/>
위에서 singleton에 대해 알아봤다. <br/>
spring은 Bean 객체를 singleton으로 관리하는데, 어떻게 다중 요청을 처리할까?  <br/>답을 찾기 위해서는 Thread에 대해 알아야 한다.

## 2. What is thread ? 
Thread는 process내에서 실제로 작업을 수행하는 주체를 말한다. thread는 순서와 상관없이 실행되며, thread를 생성하는 비용은 많이 든다. 
spring에서는 threadPool을 사용하여 thread를 미리 만들어 놓고, 요청 시 사용하고 반납하는 방식이다. 
> spring에서는 왜 ThreadPool을 사용하지? <br/>
> Spring boot에 Spring-Web dependency를 추가하면, 내장 서블릿 컨테이너로 Tomcat을 사용한다.  <br/>
> Tomcat은 다중 요청을 처리하기 위해 Thread Pool 방식을 사용하는데, 이는 Thread를 미리 만들어 놓는 것을 말한다. 작업을 할 때, Thread Pool에서 thread를 가져가 사용하고, thread pool에 다시 돌려준다.  <br/>
> 이렇게 하는 이유는 thread를 생성하고 할당을 해제하는 데 많은 비용이 들기 때문이다. 

[여기](https://dingdingmin-back-end-developer.tistory.com/entry/SpringBoot%EB%8A%94-%EC%8B%B1%EA%B8%80%ED%86%A4%EC%9D%B8%EB%8D%B0-%EC%96%B4%EB%96%BB%EA%B2%8C-%EB%8B%A4%EC%A4%91-%EC%9A%94%EC%B2%AD%EC%9D%84-%EC%B2%98%EB%A6%AC%ED%95%A0%EA%B9%8C?category=910739) 참고

### thread 생성 비용이 얼마나 될까?

Thread는 어떻게 생겼을까?
Thread는 고유의 Register와 Stack 영역을 갖고 있다. 따라서, Thread의 생성을 위해서는 메모리의 할당이라는 비용이 발생하게 된다.

Thread는 Process가 할당 받는 메모리를 사용한다. 즉, JVM이 할당받은 메모리 내에서 메모리를 재할당하기 때문에 thread의 생성비용은 JVM 메모리의 소비로 이어진다.
> 64 bit JAVA 8과 JAVA 11에서는 thread에 기본적으로 11MB의 메모리를 예약할당한다.

이러한 비용문제로 등장한 게, 위에서 설명한 Thread Pool이다.

[여기](https://velog.io/@agugu95/%EC%9E%90%EB%B0%94%EC%99%80-%EC%93%B0%EB%A0%88%EB%93%9C%ED%92%80-%EC%93%B0%EB%A0%88%EB%93%9C%EC%9D%98-%EC%83%9D%EC%84%B1%EB%B9%84%EC%9A%A9) 참고

## 3. REST API 적용
우리가 지금까지 개발해왔던 REST API를 기반으로 위의 개념을 좀 더 학장시켜보자.

```` bash
@RestController
public class HelloController {
    private final MemberService memberService;
 
    @Autowired
    public HelloController(MemberService memberService) {
        this.memberService = memberService;
    }
 
    @RequestMapping("/")
    public String hello(@RequestParam(name = "n") Integer n, @RequestParam(name = "string")String string) throws InterruptedException {
        memberService.print(n, string);
        return "ok";
    }
}
````

```` bash
public class MemberService {
 
    private final MemberRepository memberRepository;
    private String serviceString;
    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
 
    public void print(int n, String string) throws InterruptedException {
        serviceString=string;
        log.info("number: {} string: {} service start", n, serviceString);
        Thread.sleep(3000);
        log.info("number:{} string: {} service end", n, serviceString);
    }
}
````

GET 요청이 들어오면, memberService에 print()함수가 실행된다. 

```` bash

localhost:8080/? n=1&string=a
localhost:8080/? n=2&string=b
localhost:8080/? n=3&string=c
localhost:8080/? n=4&string=d
localhost:8080/? n=5&string=e
```` 
위와 같이 요청 url을 보내보자.

```` bash
number: 1, string: a
number: 2, string: b
number: 3, string: c
number: 4, string: d
number: 5, string: e 
````
위와 같이 결과가 출력되리라 짐작했지만, 숫자와 알파벳이 뒤죽박죽 섞인 결과가 나왔다. 왜 이런 결과가 나온 것일까? (싱글톤 객체 안에 변환이 가능한 멤버 변수를 사용했기 때문)

하나의 공유자원을 놓고 여러 개의 thread가 읽기/쓰기를 하면서 데이터 조작 중에 문제가 발생하게 된다. 이를 Race Condition이라 한다. 즉, singleton 객체 안에 변환이 가능한 멤버 변수를 사용했기 때문이다. (MemberService의 serviceString 변수) <br/>
이러한 경우를 Thread-safe 하지 못하다고 한다.

정리하면, JVM에서 각각의 thread는 고유의 stack 영역을 가지고 있지만, heap 영역은 thread들 간에 공유하고 있다.  <br/> (stack -> 지역변수, heap -> 전역변수) 그래서 상태를 가지는 가변 객체 (serviceString)의 경우 문제가 발생하는 것이다.

spring Bean도 singleton 방식으로 동작하기 때문에, multi-thread 환경에서의 가변 객체일 경우 thread-safe하지 못하다.

그래서!! spring bean (@Controller, @Service, @Component가 붙은 객체)의 전역변수에는 주로 불변 객체(Service, Repository)가 있다. VO, DTO와 같이 가변 객체는 존재하지 않는다. 만약 있다면 synchronized 키워드를 붙여 동시성 문제를 해결했을 것이다. 이러한 개발 방식은 thread-safe한 방식이다.
[여기](https://alwayspr.tistory.com/11)참고


## 최종 정리
spring은 singleton 방식으로 Bean 객체를 관리한다. <br/>
@Controller, @Service, @Component 등 이런 어노테이션이 붙은 애들은 Singleton 방식으로 관리하기 때문에, 주로 불변객체이다. <br/>
spring은 multi-thread 환경이다. <br/>
여러 clinet로부터 request가 들어올 때, 그 요청들이 원활히 처리될 수 있었던 이유는 multi-thread 때문이다.
만약 single thread 환경이라면, request가 처리되기 전까지 다른 request에 대한 처리가 이루어지지 않는다.
> single-thread 방식을 이용하는 웹 서버는 node.js

단, multi-thread 방식에서 요청하는 client의 숫자가 많아지면 그만큼 thread를 생성하고 수거하는 비용과 오버헤드가 발생하게 된다.
> 이런 문제점 때문에 등장한 게, threadpool이다.

왜 spring에서 모든 bean 들을 singleton 객체로 생성할까? <br/>
spring의 정의를 보면, java 엔터플이즈 개발을 편하게 해주는 오픈소스 경량급 애플리케이션 프레임워크이다. 엔터프라이즈 개발은 기업을 대상으로 하는 개발이며, 이는 동시에 서비스를 이용하고자 하는 사용자가 많을 것이라는 점을 내포한다.
그렇기에, 수많은 사용자들로 인해 발생하는 많은 요청을 multi-thread로 생성된 수많은 thread가 처리하는 과정마다 필요한 객체를 생성해야 한다. 이는 성능 저하와 메모리 낭비의 결과를 초래한다. <br/>
따라서 spring에서 singleton 패턴을 사용하는 이유는 위와 같은 위험성을 관리하기 위함이다.

[이 블로그가 찐이다.](https://fbtmdwhd33.tistory.com/256)