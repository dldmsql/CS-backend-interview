# 페이지네이션 with jpa

``` bash
SELECT * FROM ORDER BY ID DESC LIMIT 0, 10;
```
0 ~ 10개의 order를 조회한다는 쿼리이다.
이런 식으로 쿼리로 조회할 경우, 쿼리 실행 직후에 order 테이블에 값이 insert되면 중복의 여지가 발생한다.

## PageRequest
JPA에서 제공하는 PageRequest를 이용해보자!

PostController.java
``` bash
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  @GetMapping("/search")
  public ResponseEntity<BasicResponse> search(
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "page", defaultValue = "1") Integer page,
      @RequestParam(name = "filter", required = false) String filter,
      @RequestParam(name = "region") String region) {

    List<PostDto.SearchResponse> posts = postService.search(keyword, filter,
        PageRequest.of(page - 1, 12), region);
    return ResponseEntity.ok(
        BasicResponse.builder().message("search post success").data(posts).build());
  }
```
위의 코드는 DB에서 검색어를 통해 포스트를 검색한 후, 페이지네이션으로 응답하는 컨트롤러 코드이다.

` PageRequest.of(page - 1, 12)` 여기서 PageREquest 객체가 페이지네이션을 위해 사용된다.
`of()`에 인자로 처음으로 보여질 페이지 index와 한 페이지에 보여질 요소의 개수를 넣는다.
예시의 경우는 클라이언트로부터 넘어오는 index에 따라, 12개의 post를 페이지네이션 처리해서 보낸다.

PostService.java
``` bash
 public List<PostDto.SearchResponse> search(String keyword, String filter,
      Pageable page, String region) {
    return keyword == null ? searchAllPosts(page, filter, region)
        : searchPosts(keyword, page, filter, region);
  }
```
위의 서비스 로직이다. 컨트롤러에서 PageRequset로 보낸 파라미터가 Pagable로 온 것을 확인할 수 있다. 이 page를 JpaRepository를 상속받는 postRepository에 인자로 넘겨주면, List<> 혹은 Page<> 형식으로 return 해준다.