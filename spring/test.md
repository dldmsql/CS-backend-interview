### Basic Setting for controller test with mockMVC
``` bash
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(PostController.class)
public class PostDocumentationTests {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private PostService postService;
  @MockBean
  private UserInterceptor userInterceptor;
  @MockBean
  private JwtProvider jwtProvider;
  @MockBean
  private PasswordEncoder passwordEncoder;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private Session session;
  @MockBean
  private Authentication authentication;
  @MockBean
  private SecurityContext securityContext;

  @InjectMocks
  private PostMapper postMapper = PostMapper.INSTANCE;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(
        documentationConfiguration(restDocumentation).operationPreprocessors()
            .withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint())).build();
  }
```
- Service에서 사용하는 utils 혹은 final로 선언한 클래스들은 모두 `@MockBean`을 붙여야 한다. 하지 않을 시, NPE 오류 발생
- MockMVC만 `@Autowired`로 실제 메모리에 올라갈 수 있게 선언한다.
  `@MockBean`으로 선언 시, 가짜 객체이기 때문에 실제 메모리에 올라가지 않는다.
- `@BeforeEach` setUp()에서는 전체 테스트 코드 실행을 위한 mockMVC 초기화와 RestDocs 세팅을 한다.

### How to write test code
``` bash
@Test
  @DisplayName("포스트 생성")
  public void createPost() throws Exception {
    // given
    PostDto.Request request = postReq();
    PostDto.CreateResponse createResponse = postRes();

    given(postService.createPost(eq(request))).willReturn(createResponse);

    String json = objectMapper.writeValueAsString(request);

    // when
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.post("/post").characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
    );
    // then
    resultActions.andExpect(status().isOk())
        .andDo(print());

  }
```
Controller에서 정의된 함수를 테스트하기 위해서는 `@Test`을 붙여야 한다.
`@Display()`는 테스트코드의 함수명 혹은 테스트하고자 하는 내용을 나타낼 때 사용한다.
테스트 코드 작성은 위의 코드와 같이 
``` bash
// given
       Request request = getRequest();
       Response response = getResponse();
       테스트 코드를 위한 request와 response를 정의한다.

       given( 실행하고자 하는 함수( request ) ).willReturn( response );
// when

     ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.post("/post").characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
    );
      ResultActions 은 결과를 받는 객체이다. 
      RestDocumentationRequestBuilders 는 문서화에 쓰이는 객체이다.
      `post("/post")` Http Method를 지정해주고,
      `characterEncoding("utf-8")` 인코딩을 지정해주고,
      `content(json)` request body에 담긴 내용을 지정해준다.

// then
       resultActions.andExpect(status().isOk()).andDo(print());
     
     결과에 대한 행위를 정의한다.
     위의 코드는 200 status 코드가 돌아오고, 그 결과를 출력하라는 의미이다.
```
으로 작성한다.
GET/POST/PUT/PATCH/DELETE 모두 request와 response만 달라지고, 위의 구조와 테스트는 동일하게 진행된다.