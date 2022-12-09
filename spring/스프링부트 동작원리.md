# 스프링 부트 동작 원리

## 기본 용어 정리

* Html

마크업 언어

* Http

데이터 전송을 위한 프로토콜

* Http Status

|xx번대|내용|
|------|---|
|100번대|대기|
|200번대|통신 성공|
|300번대|리다이렉션|
|400번대|클라이언트 오류|
|500번대|서버 오류|

* 웹 브라우저

Html 문서를 읽기 위한 도구

* URL

Html 문서를 불러오기 위한 주소

* Apache

Web Server로, 정적 컨텐츠를 제공한다. 서버는 기본적으로 퐅트르르 열고 대기 상태에 있다. 

클라이언트가 웹 브라우저에 Apache에게 데이터를 요청하는데, 이때 Apache는 java가 포함된 JSP 파일을 만나면 브라우저가 이해하지 못한다고 인식하여 JSP 파일을 Tomcat에게 데이터 읽기를 위임한다.

* Tomcat

WAS로, 동적 컨텐츠를 제공한다. 앞서 설명했듯이 Apache가 읽지 못하는 JSP 파일을 Tomcat이 읽어 Html 파일로 바꿔준다.

> 정리하면, Tomcat은 JSP 파일이 존재할 경우에만 Servlet을 만들고, 컴파일하여 HTML 로 바꿔주는 일을 한다.

* MVC 모델에서의 서버 동작

Client - URl Request

        ⬇️

Servlet - Router ( 분기점 )
> 여기에 Controller가 있다고 보면 된다.

        ⬇️

Model에서 데이터 가져오기

        ⬇️

View에서 JSP 찿아 응답하기


* Stateless vs. Stateful

Stateless 서버

연결이 지속되지 않는 서버로, 가장 흔히 볼 수 있는 WEB을 떠올리면 된다. 

Stateful 서버

연결이 지속되는 서버로, 가장 흔히 볼 수 있는 전화를 떠올리면 된다. 지속적으로 서버에 연결되어 있어 서버에 부하가 걸린다는 단점이 있다. 
> 예) 소켓 통신. 소켓 통신은 클라이언트와 서버 양쪽에서 서로에게 데이터를 전달하는 방식의 양방향 통신을 말한다.

* Session and Cookie

> Stateless의 단점을 보완하기 위해 사용하는 것이 session과 cookie이다.

Session

전달받은 데이터를 서버에 저장하는 방식이다. 

Stateless 서버를 Stateful 서버인 것처럼 동작하게 한다.

클라이언트와 서버 모두에 Session ID를 저장한다. 

Cookie

전달받은 데이터를 브라우저에 저장하는 방식이다.

[용어 정리](https://www.saichoiblog.com/webprogramingword/)

## 서블릿 컨테이너

서블릿 컨테이너는 HTTP 요청을 받아 웹 페이지를 동적으로 생성하는 역할을 한다. 

대표적인 서블릿 컨테이너가 바로 Tomcat이다. 스프링에는 Tomcat이 내장되어 있다.

## 동작 과정

1. 웹 어플리케이션이 실행되면 Tomcat (WAS)에 의해 web.xml이 로딩된다.

2. web.xml에 등록되어 있는 ContextLoaderListener ( java 클래스 )가 생성된다. ContextLoaderListener 클래스는 ServletContextListener 인터페이스를 구현하고 있으며, ApplicationContext를 생성하는 역할을 한다.

3. 생성된 ContextLoaderListener는 root-context.xml( applicationContext.xml )을 로딩한다. 

4. root-context.xml에 등록되어 있는 spring container가 구동한다. 이때 개발자가 작성한 비즈니스 로직에 대한 부분과 DAO, VO 객체들이 생성된다.

5. 클라이언트로부터 웹 어플리케이션에 요청이 온다.

6. DispatcherServlet이 생성된다. DispatcherServlet은 FrontController로, 클라이언트로부터 온 요청의 메세지를 분석하여 알맞은 PageController에게 전달하고 응답을 받아 요청에 따른 응답을 어떻게 할지 결정만 한다. 
> 실질적인 작업은 PageController에서 이루어진다. 여기서 말하는 PageController가 우리가 개발하는 Controller class이다. 

7. DispatcherServlet은 servlet-context.xml을 로딩한다. 

8. 두 번째 Spring Container가 구동되며, 응답에 맞는 PageController들이 동작한다. 이때 첫번째 spring container 가 구동되면서 DAO, VO, ServiceImpl 클래스들과 협업하여 알맞은 작업을 처리한다.

[동작 과정 정리가 미쳤다.](https://asfirstalways.tistory.com/334)