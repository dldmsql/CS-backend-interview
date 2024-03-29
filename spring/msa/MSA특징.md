# MSA 특징 정리

> 앞선 자료의 이어서 특징을 정리해보겠다.

[Spring Cloud 개요](./SpringCloud%EA%B0%9C%EC%9A%94.md) 링크를 먼저 가볍게 읽고 읽으면 더 와닿는다.

## CI/CD

지속적인 통합 ( CI )

통합 서버, 소스 관리, 빌드 도구, 테스트 도구

예) 젠킨스, Team CI, Travis CI

깃과 연동해서 사용한다.

지속적인 배포 ( CD )

- continous Delivery
- Continous Deployment

둘 사이는 깃과 같은 소스 저장소에서 코드를 가져와서 패키지화 된 것을 어떻게 배포하는가.

수작업으로 배포해야 한다면 delivery. 자동화되어 있다면 deployment.

변경된 시스템을 무조건 반영하기 보다는 사용자에게 발생할 수 있는 문제를 최소화하는 것이 중요하다. 

- pipe line

카나리 배포와 블루그린 배포

95% 사용자는 이전 버전 서비스 5% 새 버전 서비스 -> 이게 카나리

100% 사용자는 새 버전 서비스 -> 이게 블루-그린

## DevOps

개발조직과 운영조직의 통합을 의미.

기존의 엔터프라이즈에서는 고객의 니즈를 분석하고 구현-배포-테스트를 한다. 이 과정은 매우 길게 진행된다.

개발 기간이 길어진다는 것은 변경사항에 바로 대처하기 힘들다. 고객의 요구사항은 언제든지 변경될 수 있다. 오류사항과 변경사항은 시스템 종반에 발생하기 보다는 필요할 때마다 바로 수정될 수 있는 것이 좋다. 

자주 테스트하고 자주 피드백하는 과정을 거쳐 전체 개발 완료까지 지속적으로 배포하는 것이 데봅스다.

클라우드 환경에서는 이런 것을 위해 더 작은 어플리케이션 개발-배포가 가능하다.

## 컨테이너 가상화

가상화는 클라우드 네이티브 아키텍처의 핵심이다.

적은 비용으로 탄력성 있는 시스템 구축의 중심에는 컨테이너 가상화가 있다.

전통적인 방식의 시스템에는 하드웨어 위에 os를 설치하고 app을 올린다.

가상화된 방식에는 하드웨어 위에 os 위에 하이퍼바이저 위에 vm을 올린다. host 시스템의 물리적 자원을 쪼개서 사용한다. 각각의 vm에 app을 올린다. vm에서 작동하는 app은 host에 부하를 많이 준다.

컨테이너 방식은 하드웨어 위에 os 위에 컨테이너 런타임 위에 컨테이너를 올린다. 독립적인 영역으로 실행된다. 따라서 기존의 하드웨어 가상화보다 더 적은 리소스 사용하고, 컨테이너 가상화 위에서 동작하는 app은 더 가볍고 빠르게 동작한다.

## 12 Factors

클라우드 네이티브 어플리케이션을 개발하거나 운영할 때, 고려해야 하는 요소를 정리했다. - 헤로쿠

### 1. 코드 베이스

자체 레포지토리에 저장된 코드를 의미한다. 버전 제어 목적이다. 배포하기 위해서 개발환경 스테이징, 운영 환경 스테이징에서 코드의 통일성이 필요하다.

### 2. 종속성 격리

전체 시스템에 영향을 주지 않은 채 변경사항을 반영할 수 있어야 한다.

### 3. 설정

시스템 코드 외적으로 환경 설정이 가능해야 한다.

### 4. 서비스 지원

외부 서비스 사용 시, 마이크로서비스가 가져야 하는 기능을 추가적으로 지원할 수 있어야 한다. 빽킹 시스템을 사용해서 코드 의존성을 갖지 않은 상태에서 작업할 수 있다.

### 5. 환경 분리

개발-테스트-운영 환경을 분리해야 한다. 각각은 고유한 아이디로 태그를 가져야 하고 롤백도 가능해야 한다. 자동화된 시스템 구축이 중요하다.

### 6. 실행단계

독립성과 일치하는 항목이다. 하나의 마이크로서비스는 다른 마이크로서비스와 분리되어 독립적으로 운영될 수 있어야 한다. 

### 7. 포트 바인딩

각각의 마이크로서비스는 포트를 통해 분리되어야 한다.

### 8. 동시성

수많은 서비스를 복사해서 확장해나간다. 부하분산으로 이용될 수 있다. 

### 9. 서비스 인스턴스 자체가 삭제 가능해야 한다.

정상적으로 종료가 될 수 있는 상태여야 한다.

### 10. 개발/운영 단계 구분

수명 주기 전반에 걸쳐 최대한으로 많은 서비스 직전에 접근하는 것을 방지해서 영역을 분리한 채로 서비스를 유지해야 한다.

### 11. 로깅 시스템

마이크로 서비스에서 발생하는 로그를 수집해서 모니터링해야 한다. 

### 12. 프로세스

현재 운영되고 있는 모든 마이크로서비스를 어떤 상태로 활용되고 리소스 상태를 확인하고 관리하는 도구가 필요하다. 리포팅, 데이터 정리, 분석하는 도구가 포함되어야 한다.

최근에 3개 항목을 더해졌다. - 피보탈

### 13. API 먼저

API형태로 제공해야 한다. 사용자 측에서 어떤 형태로 쓸 것인지 고민하고 개발해야 한다.

### 14. telemetry

모든 지표는 시각화되어야 한다.

### 15. Authentication and Authorization

인증을 갖는 시스템이어야 한다.