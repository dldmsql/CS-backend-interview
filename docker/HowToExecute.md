# 도커는 무엇이며, 어떻게 동작할까

## 도커

도커는 컨테이너 기술을 사용하여 애플리케이션에 필요한 환경을 신속하게 구축하고 테스트 및 배포를 할 수 있게 해주는 플랫폼이다.

과거 하나의 서버 당 하나의 애플리케이션이 동작되었다. 그래서 새로운 애플리케이션이 필요할 때마다 서버를 구매해야했고, 이는 비용적 측면에서 비효율적이다.
그래서 등장한 것이 VM이다. 하나의 운영체제 위에 여러 개의 운영체제를 실행시켜 낭비를 크게 줄여주었다. 하지만 하나의 VM마다 고유한 운영체제가 필요하기 때문에 각각 CPU, RAM 및 리소스를 소비하며 보다 많은 시간과 자원을 낭비하게 되었다. 
이러한 단점을 보완하기 위해 등장한 것이 컨테이너이다. 컨테이너는 VM과 유사하다. 차이점은 컨테이너에 자체적인 하나의 완전한 운영체제가 필요하지 않다는 것이다.

실제로 컨테이너 모델에서는 단일 호스트의 모든 컨테이너들이 호스트의 운영체제를 공유한다. 이것은 CPI, RAM 및 저장소와 같은 막대한 양의 시스템 리소스가 낭비되지 않는다는 것을 의미한다.

## 초기 도커 엔진

초기 도커는 현재와는 달리 Docker Daemon, LXC라는 두 개의 주요 구성을 가지고 있었다. 

* Docker daemon

도커 데몬은 모듈화가 되어 있지 않은 채로 Docker client, Docker API, container runtime, image builds 등 많은 코드를 담고 있었다.

* LXC

LXC는 단일 컨트롤 호스트 상에서 여러 개의 고립된 컨테이너들을 실행하기 위한 운영 시스템 레벨 가상화 방법이다. Daemon에게 리눅스 커널에 존재하는 컨테이너의 기본 building block에 대한 namespaces나 cgroups와 같은 접근을 제공했다.
namespaces는 운영 시스템을 쪼개서 각각 고립된 상태로 운영이 되는 개념을 의미한다.
cgroups는 namespaces으로 고립된 환경에서 사용할 자원을 제한하는 역할 등을 한다. 

## LXC의 문제점

LXC는 리눅스에 특화되어 있는데, Docker가 다중 플랫폼을 목표로 하는데 큰 걸림돌이었다. 또한, 시스템을 구성하는 핵심적인 요소가 외부 시스템에 의존한다는 문제도 있었다. 때문에 Docker에서는 LXC를 대체하기 위해 libcontainer라는 툴을 개발했다.

## Libcontainer

현재의 도커 엔진에서 사용하고 있는 주요 컴포넌트이다. 컨테이너 생성 시, namespaces, cgroup, capabilities를 제공하고 파일 시스템의 접근을 제한할 수 있다. 또한, 컨테이너가 생성된 후 작업을 수행할 수 있도록 컨테이너의 수명 주기를 관리한다.

libcontainer는 도커 내부에서 실행된다는 점에서 LXC와 차이가 있다.

## 도커 엔진

도커는 클라이언트-서버 모델을 구현한 애플리케이션이다. 
도커 엔진은 도커 컴포넌트와 서비스를 제공하는 컨테이너를 구축하고 실행하는 기본 핵심 소프트웨어이다. 도커 데몬, REST API, API를 통해 도커 데몬과 통신하는 CLI로 구성되어 잇다.

컨테이너를 빌드, 실행, 배포하는 등의 무거운 작업은 도커 데몬이 하며, 
도커 클라이언트는 이러한 로컬 혹은 원격의 도커 데몬과 통신한다.
통신을 할 때에는 UNIX socket 또는 네트워크 인터페이스를 통한 REST API를 사용한다.

## 도커 실행 Flow

1. docker client

유저가 Docker CLI를 통해 `docker container run --name str1 -it alpine:latest sh` 를 입력한다.
Docker CLI에 입력하면 도커 클라이언트는 적절한 API payload로 변환해서 도커 데몬에게 REST API로 POST 요청을 한다.

2. docker daemon( dockerd )

API는 UNIX socket을 통해 도커 데몬에게 전달된다.
리눅스에서 socket은 `/var/run/docker.sock`이고, 윈도우에서는 `\pipe\docker_engine`이다.

새 컨테이너를 시작할 때, 도커 데몬은 로컬 이미지가 있는지 확인하고 없다면 registry repository에서 해당하는 이름의 이미지를 가져온다. 

또한 로깅 드라이버와 볼륨, 볼륨 드라이버를 설정하는 등 컨테이너에 필요한 대부분의 설정을 지정한다.

도커 데몬이 새로운 컨테이너를 생성하라는 명령을 받으면, containerd를 호출한다.

이때, 도커 데몬은 CRUD 스타일의 API를 통해 gRPC로 containerd와 통신한다.

3. containerd

containerd는 실제로 컨테이너를 생성하지 못하고,`runc` 를 통해 생성한다.
도커 이미지를 가져와서 컨테이너 구성을 적용하여 runc가 실행할 수 있는 OCI 번들로 변환한다. 

4. runc

runc는 운영체제 커널에 접속해서 컨테이너를 만드는 데 필요한 모든 구성 요소 ( namespaces, cgroup ,..)를 하나로 묶는다. 그리고 새로운 컨테이너를 생성한다.

5. shim

그 다음 컨테이너를 시작하기 위해 docker-containerd-shim과 같은 shim을 실행한다. 
컨테이너 프로세스는 runc의 하위 프로세스로 시작되는 데, 컨테이너 프로세스가 실행하자마자 runc가 종료된다. 그리고 docker-containerd-shim이 새로운 상위 프로세스가 되어 컨테이너의 생명주기를 관리한다.

## High-level runtime

containerd는 high-level runtime이다. 
high-level runtime은 보통 이미지 관리, gRPC/Web API와 같이 컨테이너를 관리하는 것 이상의 높은 수준의 기능을 지원하는 런타임을 의미한다. 다른 표현으로는 컨테이너 런타임이라고도 한다.
low-level runtime으로는 runc가 있다.

도커 클라이언트로부터 컨테이너 관련 요청은 도커 데몬을 거쳐 gRPC 통신을 통해 containerd로 전달된다. 그리고 나서 containerd는 컨테이너의 관리를 위해 runc를 사용한다.

## runc

libcontainer용 CLI Wrapper로, 독립된 컨테이너 런타임이다. 
도커가 컨테이너 관련된 기능들을 쉽게 사용할 수 있도록 해주는 가볍고 이식가능한 툴이다.
runcs의 목적은 **컨테이너 생성** 단 하나이다.

## OCI

runc는 OCI container-runtime-spec의 구현체이다. OCI는 커널의 컨테이너 관련 기술을 다루는 인터페이스를 표준화시킨 기준이다. 그래서 runc 가 동작하는 계층을 OCI layer라고도 부른다.

* low level runtimes

보통 컨테이너를 운영하는 것에 초점을 맞춘 실제 컨테이너 런타임을 의미한다.

runc는 독립된 컨테이너 런타임이기 때문에 바이너리로 다운받고 빌드할 수 있다. 즉, runc container를 빌드하고 실행시키는 데 모든 것을 갖출 수 있다는 의미이다.
하지만 이것은 뼈대일 뿐, 완전한 도커 엔진으로 볼 수는 없다.

## shim

containerd가 새로운 컨테이너를 만들기 위해 runc를 사용한다. 생성되는 모든 컨테이너당 runc의 새로운 인스턴스를 fork한다. 그러나 각 컨테이너가 생성되면, 상위 runc 프로세스가 종료된다.
컨테이너에 할당된 부모 runc 프로세스가 종료되면, 연결된 containerd-shim 프로세스가 컨테이너의 부모 프로세스가 된다. 이는 containerd에게 컨테이너의 file descriptor와 종료 상태를 관리하는 데 필요한 최소한의 코드를 메모리에 남긴다.

> file descriptor <br/>
프로세스에서 열린 파일의 목록을 관리하는 파일 테이블의 인덱스이다. 프로그램이 프로세스로 메모리에서 실행될 때, 기본적으로 할당되는 file discriptor는 표준 입력, 표준 출력, 표준 에러이며 이들에게 각각 정수가 할당된다.

shim은 도커 데몬이 재시작될 때, 컨테이너가 종료되지 않도록 표준 입력, 표준 출력 스트림을 열린 상태로 유지한다.
shim은 도커 데몬에게 컨테이너의 종료 상태를 보고한다.

[도커 엔진 정리](https://gngsn.tistory.com/128)

