# TCP/UDP

## 전송계층
OSI 7 Layer에서 전송계층에 해당한다.
전송계층은 application 프로세스들 간의 논리적 통신을 제공한다. 즉, 네트워크 양 끝단에서 통신을 수행하는 당사자 간의 단대단 연결을 제공한다.

<br/>
전송계층은 아래와 같은 기능을 제공한다.

* 흐름 제어
* 오류 제어
* 분할과 병합
* 서비스 프리미티브

## TCP (Transmission Control Protocol)
IP 프로토콜 위에서 연결형 서비스를 지원하는 전송계층 프로토콜이다. 데이터 단위는 세그먼트이다.

* 특징
1. 연결형 서비스를 지원한다.
2. 전이중 방식의 양방향 가상 회선을 제공한다.
3. 신뢰성 있는 데이터 전송을 보장한다.

* 3 way hand-shake
[연결 과정]
<br/>

1. CLIENT -SYN--> SERVER
2. CLINET <--ACK,SYN- SERVER
3. CLIENT -ACK--> SERVER

<br/>

[특징]
* 데이터를 전송하기 전에, 정확한 데이터 전송을 보장한다.
* 양쪽 모두 데이터를 전송할 준비를 보장한다.
* 초기에 순차 일련 번호를 주고 받는다.
* 총 3번의 신호를 주고 받으면 연결 성공이다.

<br/>

* 4 way hand-shake
[연결 해제 과정]
1. CLIENT -SYN, FIN--> SERVER
2. CLIENT <--ACK- SERVER
3. CLIENT <--SYN,FIN- SERVER
4. CLIENT -ACK--> SERVER

<br/>

[특징]
* 서버 측에서 전송해야 할 데이터가 남아있을 수 있기 때문에 서버가 ACK 던지고 데이터 던지고 그 후에 FIN을 던진다.
* 만약, 서버의 FIN 패킷 보다 나중에 데이터가 전달된다면? 클라이언트는 FIN 패킷을 수신하고 일정 시간 대기한다.
* 왜 초기 일련 번호를 0부터가 아닌 난수로 하는가? SYN을 보고 패킷을 구분하는데, 순차적으로 오면 이전 패킷으로 인식할 수도 있다.

[TCP/UDP](https://velog.io/@averycode/%EB%84%A4%ED%8A%B8%EC%9B%8C%ED%81%AC-TCPUDP%EC%99%80-3-Way-Handshake4-Way-Handshake#-3-way-handshake%EC%99%80-4-way-handshake)