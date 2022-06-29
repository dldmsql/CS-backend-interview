# What is Webflux
> 목차
> * Practice

## Practice
Let's Write Rest API with webflux

1. Add dependency in `build.gradle` file
```` bash
...
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-mongodb-reactive'
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'io.projectreactor:reactor-test'
}
...
````

reactive programming을 하기 위해서는 reactive DB를 사용해야 한다. 본 예제에서는 MongoDB를 사용할 예정이며, Spring boot에서는 MongoDB에 맞는 Repository와 Template을 제공한다.

2. Change file extension and Add properites for MongoDB connection in `application.yml`

```` bash

spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: grtg
      
````

MongoDB 연결을 위해 `application.yml` 파일에 위와 같이 작성한다.

3. Pattern for REST API

![image](https://user-images.githubusercontent.com/61505572/175777964-708d5ada-0a03-4b25-90f8-7237b892bd82.png)

위의 사진과 같이 클래스를 만들 예정이다.

큰 구조는 Router-Handler-Service-Repository 이며, 이 구조는 Functional Endpoint 방식이다. 


4. Create domain ( called Post )

```` bash

import java.sql.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "post")
public class Post {

  @Transient
  public static final String SEQUENCE_NAME = "users_sequence";

  @Id
  private Long id;
  private String title;
  private String content;
  // @CreatedDate
  private Timestamp createdAt;

}
````
`Post` domain 코드이며,
`@Document` 어노테이션은 RDB에서 `@Entity`를 MongoDB에서 칭하는 표현이라고 보면 된다.
`@Id` 어노테이션은 MongoDB에서 **pk**를 나타내기 위해 사용한다.
`@Transient` 어노테이션은 MongoDB에서 **auto_increment**를 구현하기 위한 설정이다.
하드 코딩을 하지 않기 위해, `@Builder` 어노테이션을 사용했다.

5. How to make auto increment in MongoDB with spring

```` bash
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "database_sequences")
public class DatabaseSequence {

  @Id
  private String id;

  private long sequence;

}
````

이 도메인을 기반으로, sequence를 늘려나가며 auto_increment를 구현한다.

```` bash

import java.util.concurrent.ExecutionException;

public interface ISequenceGeneratorService {
  long generateSequence(final String sequenceName) throws InterruptedException, ExecutionException;
}
````

interface로 시퀀스를 증가시키는 서비스를 만든다.


```` bash

import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
@Service
public class SequenceGeneratorService implements ISequenceGeneratorService{
  private static final Logger logger = LoggerFactory.getLogger(SequenceGeneratorService.class);

  private ReactiveMongoOperations mongoOperations;

  @Autowired
  public SequenceGeneratorService(ReactiveMongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
  }

  @Override
  public long generateSequence(final String sequenceName) throws InterruptedException, ExecutionException {
    return mongoOperations.findAndModify(new Query(Criteria.where("_id").is(sequenceName)),
        new Update().inc("sequence", 1), options().returnNew(true).upsert(true), DatabaseSequence.class).doOnSuccess(object -> {
      logger.debug("databaseSequence is evaluated: {}", object);
    }).toFuture().get().getSequence();
  }
}
````

`generateSequence()`를 통해 **id**를 1씩 증가시켜준다.

```` bash

import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class PostModelListener extends AbstractMongoEventListener<Post> {
  private static final Logger logger = LoggerFactory.getLogger(PostModelListener.class);

  private ISequenceGeneratorService sequenceGenerator;

  @Autowired
  public PostModelListener(ISequenceGeneratorService sequenceGenerator) {
    this.sequenceGenerator = sequenceGenerator;
  }

  @Override
  public void onBeforeConvert(BeforeConvertEvent<Post> event) {
    try {
      event.getSource().setId(sequenceGenerator.generateSequence(Post.SEQUENCE_NAME));
    } catch (InterruptedException | ExecutionException e) {
      logger.error("Error:{}", e.getMessage());
    }
  }
}

````

각 도메인에 맞는 Listener를 생성하여, id를 자동으로 증가시켜준다.

6. Create repository ( called dao )

```` bash
@Repository
public interface PostRepository extends ReactiveMongoRepository<Post, Long> {

//  Flux<Post> findAll(Pageable page);
}
````

`ReactiveMongoRepository<Post, Long>`를 상속받아 MongoDB와의 데이터 조작을 가능하게 한다.
주석으로 표시한 부분은 페이지네이션을 구현하기 위함이었다. 

7. Create Router ( like Controller )

```` bash

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class PostRouter {

  @Bean
  public RouterFunction<ServerResponse> routePost(PostHandler postHandler) {
    return RouterFunctions
        .route(GET("/api/post")
        , postHandler::getAllPosts)
        .andRoute(GET("/api/post/{id}")
        , postHandler::getPost)
        .andRoute(POST("/api/post")
        , postHandler::createPost)
        .andRoute(PUT("/api/post/{id}")
        , postHandler::modifyPost)
        .andRoute(DELETE("/api/post/{id}")
        , postHandler::deletePost);
  }
}
````

Router는 controller라고 보면 된다. 

8. Create Handler 

```` bash

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PostHandler {

  private final PostService postService;

  public PostHandler(PostService postService) {
    this.postService = postService;
  }

  public Mono<ServerResponse> getAllPosts(ServerRequest request) {
//    Integer page = request.queryParam("page").isPresent() ? Integer.parseInt(request.queryParam("page").get())-1 : 0;
//    Integer size = request.queryParam("size").isPresent() ? Integer.parseInt(request.queryParam("size").get()) : 20;
//
//    return Mono.just(postService.getAllPosts(PageRequest.of(page,size,Sort.by("id").descending())))
//        .flatMap(post -> ok().body(fromObject(post)))
//        .switchIfEmpty(notFound().build());
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(postService.findAll(), Post.class);
  }

  public Mono<ServerResponse> getPost(ServerRequest request) {
    Long id = Long.parseLong(request.pathVariable("id"));

    return postService.getPost(id)
        .flatMap(post -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromValue(post))
        ).switchIfEmpty(ServerResponse.notFound().build());
  }

  public Mono<ServerResponse> createPost(ServerRequest request) {
    Mono<Post> unsavedPost = request.bodyToMono(Post.class);
    return unsavedPost
        .flatMap(post -> postService.createPost(post)
            .flatMap(savedPost -> ServerResponse.accepted()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(savedPost)))
        ).switchIfEmpty(ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).build());
  }

  public Mono<ServerResponse> modifyPost(ServerRequest request) {
    Mono<Post> unsavedPost = request.bodyToMono(Post.class);
    Long id = Long.parseLong(request.pathVariable("id"));

    Mono<Post> updatedPost = unsavedPost.flatMap(post ->
        postService.modifyPost(post, id));

    return updatedPost.flatMap(post ->
        ServerResponse.accepted()
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromValue(post))
    ).switchIfEmpty(ServerResponse.notFound().build());
  }

  public Mono<ServerResponse> deletePost(ServerRequest request) {
    Long id = Long.parseLong(request.pathVariable("id"));

    Mono<Void> deleted = postService.deleteById(id);

    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(deleted, Void.class);
  }
}
````

Router에서 정의한 함수들을 각 서비스와 연결한다. 
reactive programming으로 구현하기 위해 return type을 Mono wrapper로 하였으며, 그 외에는 기존의 방식과 큰 차이가 없다.

아래 사진은 **PUT** 요청을 테스트하는 모습이다. PUT/PATCH 요청에 대해 api가 원활하게 동작하지 않아 이 부분은 수정해 나갈 예정이다.

![image](https://user-images.githubusercontent.com/61505572/175776280-681c9a5d-ff25-4c4b-ae27-7efab90c99ff.png)


참고 자료 [여기](https://jstobigdata.com/spring/spring-webflux-rest-api-with-mongodb-and-spring-data/) | [여기](https://www.knowledgefactory.net/2021/09/building-reactive-crud-apis-using-webflux-r2dbc-example.html)
