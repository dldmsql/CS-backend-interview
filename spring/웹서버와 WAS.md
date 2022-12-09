# Web Server vs. WAS

## Web Server

사전적 정의는 "웹 브라우저 클라이언트로부터 HTTP 요청을 받아들이고, HTML문서와 같은 웹 페이지를 반환하는 컴퓨터 프로그램"이다.

웹 서버란 클라이언트가 웹 브라우저에서 어떠한 요청을 하면, 웹 서버에서 그 요청을 받아 정적 컨텐츠를 제공하는 서버이다. 정적 컨텐츠는 단순 HTML 문서, CSS, javascript, 이미지, 파일 등 즉시 응답 가능한 컨텐츠이다. 

정적 컨텐츠가 아닌 동적 컨텐츠의 경우, WAS에게 해당 요청을 넘겨주고 WAS에서 처리한 결과를 클라이언트에게 전달해주는 역할을 Web Server가 한다.

## WAS

사전적 정의는 "인터넷 상에서 HTTP 프로토콜을 통해 사용자 컴퓨터나 장치에 어플리케이션을 수행해주는 미들웨어로서, 주로 동적 서버 컨텐츠를 수행하는 것으로 웹 서버와 구별이 되며, 주로 데이터베이스 서버와 같이 수행"한다.

WAS는 Web Server와 Web Container가 합쳐진 형태로서, Web Server 단독으로는 처리할 수 없는 데이터베이스의 조회나 다양한 로직 처리가 필요한 동적 컨텐츠를 제공한다. WAS는 JSP, Servlet구동 환경을 제공하기 때문에 Web Container 혹은 Servlet Container라고도 불린다.

> Web Container

> WAS가 보낸 JSP, PHP 등의 파일을 수행한 결과를 다시 Web Server로 보내주는 역할이다.

대표적인 WAS로는 Tomcat이 있다.

## Web Service Architecture

웹 어플리케이션은 요청 처리 방식에 따라 다양한 구조를 가질 수 있다.

* [Client] -> [Web Server] -> [DB]

* [Client] -> [WAS] -> [DB]

* [Client] -> [Web Server] -> [WAS] -> [DB]

**[Client] -> [Web Server] -> [WAS] -> [DB] 구조의 동작 과정**

1. Web Server는 클라이언트로부터 HTTP 요청을 받는다. 
2. Web Server는 클라이언트의 요청을 WAS로 보낸다. 
3. WAS는 관련된 Servlet을 메모리에 올린다.
4. WAS는 web.xml을 참조하여 해당 Servlet에 대한 Thread를 생성한다. ( Thread Pool 이용 )
5. HttpServletRequest와 HttpServletResponse 객체를 생성하여 Servlet에 전달한다. 
    1. Thread는 Servlet의 service() 메소드를 호출한다.
    2. service() 메소드는 요청에 맞게 doGet() 또는 doPost() 메소드를 호출한다. 
6. protected doGet( HttpServletRequest reqeust, HttpServletResponse response )
7. doGet() 또는 doPost() 메소드는 인자에 맞게 생성된 적절한 동적 페이지를 Response 객체에 담아 WAS에 전달한다. 
8. WAS는 Response 객체를 HttpResponse 형태로 바꾸어 Web Server에 전달한다. 
9. 생성된 Thread를 종료하고, HttpServletRequest와 HttpServletResponse 객체를 제거한다.

## WAS만 사용하지 않는 이유

WAS는 DB 조회 및 다양한 로직을 처리하는 데 집중해야 한다. 그렇기에 단순한 정적 컨텐츠는 Web Server가 담당하게 하여 서버 부하를 방지한다. 

[그림도 있는 좋은 블로그](https://codechasseur.tistory.com/25)