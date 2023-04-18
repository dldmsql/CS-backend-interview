# Spring Framework

스프링에서 가장 자주 사용되는 4가지 디자인 패턴에 대해 정리해보자.

## Singleton Pattern

어플리케이션당 오직 하나의 인스턴스만 존재하도록 보장해주는 패턴이다. 공유 자원을 관리하거나 로깅과 같이 cross-cutting service를 제공할 때 유용하다.

스프링에서는 하나의 IoC Container당 하나의 싱글톤 객체를 갖도록 제한한다. 즉, 스프링 프레임워크가 하나의 어플리케이션 컨텍스트당 하나의 빈을 생성함을 의미한다.

예를 들어, 하나의 어플리케이션 컨텍스트 내에서 두 개의 Controller를 생성하고, 같은 타입의 빈을 각각에 주입할 수 있다.

UserController에서 UserRepository를 통해 userID가 1L인 값을 조회하는 것과 BookController에서 UserRepository를 통해 userID가 1L인 값을 조회하는 것은 동일한 결과를 반환한다. 이는 2개의 Controller가 같은 UserRepository 빈을 주입했다는 것을 보여준다.

## Factory Pattern

팩토리 패턴은 원하는 객체를 생성하기 위한 추상 메서드가 있는 팩토리 클래스를 생성한다. 

스프링에서는 이 패턴을 Dependency Injection에서 사용한다. 빈 컨테이너를 빈을 생성하는 팩토리로 취급한다. 빈 팩토리 인터페이스를 빈 컨테이너의 추상화로 정의한다.

인터페이스에는 빈 객체를 반환하는 메소드가 정의되어 있다. 이 메소드들은 각각 인자가 다르게 정의되어 있다. 메소드에 제공된 기준과 일치하는 빈을 찾아 반환한다.

스프링에서는 빈 팩토리를 상속하여 추가적인 어플리케이션 설정을 할 수 있는 ApplicationContext 인터페이스를 구현한다. 

이러한 팩토리 패턴은 어플리케이션의 동작을 외부 설정에 맞게 변경할 수 있다. ( 예: DB 벤더 설정할 때 )

## Proxy Pattern

프록시 패턴은 한 객체가 다른 객체로의 접근을 제어하도록 하는 기술이다.

일반적으로 스프링에서는 두 가지 타입의 프록시를 사용한다.

CGLib Proxy - Class들을 프록싱할 때 사용
JDK Dynamic Proxy - 인터페이스들을 프록싱할 때 사용

프록시를 생성하기 위해 subject와 동일한 인터페이스를 구현하고 subject에 대한 참조를 포함하는 객체를 생성한다. 이로써 subject 대신 프록시를 사용할 수 있게 된다.

스프링에서 빈들은 underlying 빈에 대한 접근을 제어하기 위해 프록싱된다. 대표적인 예시가 트랜잭션이다.

```` kotlin

@Transactional(readOnly = true)
@Service
class LoveServiceImpl(
    val boardRepository: BoardRepository,
    val loveRepository: LoveRepository
) : LoveService {

    @Transactional
    override fun loveBoard(postId: Long, user: User): Long {
        val board = boardRepository.findByIdOrThrow(postId)
        if (loveRepository.existsByBoardAndUser(board, user)) {
            throw ApplicationException(ErrorCode.ALREADY_LOVE)
        }
        val love = Love(
            user, board
        )
        loveRepository.save(love)
        return board.addLove(love).id
    }
}
````

위의 코드에서 loveBoard 메소드에 @Transactional 어노테이션을 붙여줌으로써 해당 메소드를 원자적으로 실행하도록 한다. 프록시 없이는 스프링이 repository 빈에 접근해서 트랜잭션 일관성을 보장할 수 없다.

CGLib Proxy는 스프링이 Repository를 감싸는 프록시에게 loveBoard 메소드를 원자적으로 실행하도록 지시한다. 

loveBoard 메소드 내에 아래의 코드를 통해 출력을 하면 repository의 오브젝트 ID가 아닌 EnhancerBySpringCGLib의 오브젝트 ID가 출력된다.

```` kotlin
print(repository.getClass().getName())
````

이는 뒷단에서 스프링이 repository 객체를 EnhancerBySpringCGLib 객체로 wrapping하기 때문이다. 

## Template Pattern

템플릿 패턴은 일부 작업에 필요한 단계들을 정의하고 반복적인(진부한) 단계들을 구현하고 사용자 정의 단계를 추상적으로 남겨두는 기술이다. 

이렇게 표현하면 와닿지 않을 것 같으니 예시를 들어보자.

DB에서 쿼리를 실행하기 위해서는 다음 단계들이 필요하다.

1. Connection 생성
2. 쿼리 실행
3. cleanUp 실행
4. Connection 종료

이를 표현한 코드가 아래이다.

```` bash java
public abstract DBQuery {
    public void execute() {
        Connection connection = createConnection();
        executeQuery(connection);
        closeConnection(connection);
    }

    protected Connection createConnection(){
        // set for db
    }

    protected void closeConnection(Connection connection) {
        // close connection
    }

    protected abstract void executeQuery(Connection connection);
}
````

또는 Callback 함수를 통해 위의 과정 중 원하는 작업이 완료되었음을 클라이언트에게 알릴 수 있다.

[참고자료-baeldung](https://www.baeldung.com/spring-framework-design-patterns)

[참고자료-madplay](https://madplay.github.io/post/spring-framework-basic-design-pattern)