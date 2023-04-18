# Spring Framework

## 스프링부트의 시작점

스프링부트 프로젝트를 시작하면, Application.java 코드가 함께 생성된다.

```` java

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
````

어플리케이션을 실행하면, 이 클래스의 main() 가 실행되는 데, 여기서 @SpringBootApplication 어노테이션에 먼저 주목해야 한다.

### @SpringBootApplication

스프링부트 프로젝트를 위한 자동 설정을 해주는 어노테이션이다.

```` java
/**
 * Indicates a {@link Configuration configuration} class that declares one or more
 * {@link Bean @Bean} methods and also triggers {@link EnableAutoConfiguration
 * auto-configuration} and {@link ComponentScan component scanning}. This is a convenience
 * annotation that is equivalent to declaring {@code @Configuration},
 * {@code @EnableAutoConfiguration} and {@code @ComponentScan}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @since 1.2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
}
````

해당 어노테이션의 클래스 정보를 확인하면 위와 같다.

설명을 보면, 해당 어노테이션은 @Bean 객체를 자동 구성하고, auto-configuration과 component 스캔을 트리거하도록 한다.

어노테이션을 통해 @Configuration, @EnableAutoConfiguration, @ComponentScan 을 선언하는 것과 같은 기능을 하는 편의성을 준다. ( 즉, 이 3가지 어노테이션을 한 번에 처리해준다. )

공식문서에 따르면, @EnableAutoConfiguration 어노테이션은 개발자가 추가한 jar 종속성을 기준으로 스프링을 구성하는 방법을 스프링부트가 추측하도록 지시하는 어노테이션이다. 

스프링 프로젝트를 생성할 때, spring-boot-starter-web 종속성을 추가하면, 이 jar 종속성이 Tomcat과 Spring MVC 를 함께 포함하기에 스프링부트도 웹 어플리케이션을 개발하고 있다고 가정하고 그에 따라 자동 구성 설정을 한다.

Auto-configuration에 대상이 되는 클래스들은 미리 정의되어 있기 때문에 @Configuration 어노테이션이 없어도 자동으로 빈으로 등록된다.

@ComponentScan 어노테이션은 @Component 어노테이션으로 정의한 클래스들을 실행과 동시에 탐자히여 빈으로 등록한다.

- 번외

위의 @SpringBootApplication 어노테이션을 정의한 클래스에 보면, 인터페이스에 정의된 어노테이션이 많음을 알 수 있다.

각각의 어노테이션이 의미하는 바를 짧막하게 정리하려 한다.

어노테이션은 @가 붙은 것으로, 기본적인 목적은 자바의 메타데이터로 사용하기 위함이다. 

스프링에서 어노테이션을 만들기 위해서는 @interface 를 추가해야 한다.

그 후, 어노테이션이 생성될 수 있는 위치를 지정해주어야 한다. 그것이 바로 @Target 이다.

위에서는 `TYPE`으로 정의하였는데, 이는 클래스, 인터페이스, enum 선언 시 사용할 수 있음을 의미한다.

또한, 어노테이션이 언제가지 유효할 수 있는지를 정의해야 하는데, 이는 @Retention 이 담당한다.

위에서는 `RUNTIME`으로 정의하였는데, 이는 컴파일 이후에도 참조가 가능함을 의미한다. 즉, 실행 동안에 계속 유효하다는 뜻이다.

@Documented 어노테이션은 javadoc으로 api 문서를 만들 때 어노테이션에 대한 설명도 포함하도록 지정해주는 것이다. 

@Inherited 어노테이션은 자식 클래스에서 부모 클래스에 선언된 어노테이션을 상속 받을 수 있음을 의미한다. 


### 실행 Flow

```` kotlin
@SpringBootApplication
class JhouseServerApplication

fun main(args: Array<String>) {
    runApplication<JhouseServerApplication>(*args)
}

````

> 코틀린 기반의 프로젝트 코드를 기준으로 설명하고 있다.

1. main 함수의 runApplication() 함수가 실행된다. 

코틀린에서 자바로 컨버팅되면서 자바의 run() 함수가 실행되도록 하는 helper 함수이다.

```` kotlin

/**
 * Top level function acting as a Kotlin shortcut allowing to write
 * `runApplication<FooApplication>(arg1, arg2)` instead of
 * `SpringApplication.run(FooApplication::class.java, arg1, arg2)`.
 *
 * @author Sebastien Deleuze
 * @since 2.0.0
 */
inline fun <reified T : Any> runApplication(vararg args: String): ConfigurableApplicationContext =
		SpringApplication.run(T::class.java, *args)
````

해당 함수를 타고 들어가면 위와 같이 정의되어 있는 것을 확인할 수 있다.

2. SpringApplication.run() 실행

```` java
	/**
	 * Run the Spring application, creating and refreshing a new
	 * {@link ApplicationContext}.
	 * @param args the application arguments (usually passed from a Java main method)
	 * @return a running {@link ApplicationContext}
	 */
	public ConfigurableApplicationContext run(String... args) {
		long startTime = System.nanoTime();
		DefaultBootstrapContext bootstrapContext = createBootstrapContext();
		ConfigurableApplicationContext context = null;
		configureHeadlessProperty();
		SpringApplicationRunListeners listeners = getRunListeners(args);
		listeners.starting(bootstrapContext, this.mainApplicationClass);
		try {
			ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
			ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);
			configureIgnoreBeanInfo(environment);
			Banner printedBanner = printBanner(environment);
			context = createApplicationContext();
			context.setApplicationStartup(this.applicationStartup);
			prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
			refreshContext(context);
			afterRefresh(context, applicationArguments);
			Duration timeTakenToStartup = Duration.ofNanos(System.nanoTime() - startTime);
			if (this.logStartupInfo) {
				new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), timeTakenToStartup);
			}
			listeners.started(context, timeTakenToStartup);
			callRunners(context, applicationArguments);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, listeners);
			throw new IllegalStateException(ex);
		}
		try {
			Duration timeTakenToReady = Duration.ofNanos(System.nanoTime() - startTime);
			listeners.ready(context, timeTakenToReady);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, null);
			throw new IllegalStateException(ex);
		}
		return context;
	}
````

스프링 어플리케이션을 실행하는 실질적인 함수이다. 함수에서는 ApplicationContext를 생성하고 리프레쉬하는 역할을 한다.

스프링부트 프로젝트를 실행하면 콘솔창에 여러 info가 출력되는 것을 알 수 있다. 콘솔창에 출력되는 값들이 모두 여기서 세팅되는 값들이다.

ApplicationContext를 생성학는 데 걸리는 시간을 노출하기 위해 함수 실행과 동시에 시작 시각을 측정한다. 

ApplicationContext 생성을 위해 BootstrapContext를 먼저 생성한다. 그리고 필요한 인자들을 찾아 프로퍼티로 등록한다. 여기까지 세팅이 되었으면, 배너를 출력한다.

그 후, 어플리케이션 실행을 위한 세팅을 한다. 

큼직하게 아래와 같은 플로우라고 이해하면 될 거 같다.

[시작 시각 측정] -> [어플리케이션 컨텍스트 세팅을 위한 부트스트랩 컨텍스트 생성 및 구성] -> [시스템 프로퍼티 등록 (configuration 같은 거)] -> [부트스트랩 컨텍스트에 세팅한 내용들을 실제 어플리케이션 컨텍스트에 세팅] -> [콘솔 창에 배너로 등록한 configuration 정보 출력] -> [종료 시각 측정]

> 자세한 건 코드를 보면 한 눈에 파악이 가능하다.

[Next 이야기](./%5BChap-1%5D%20Spring%20Bean.md)