# Mapstruct 사용 시, 마주할 수 있는 이슈 (insert가 아닌 update 쿼리)

## Issue about create post logic
![image](https://user-images.githubusercontent.com/61505572/168951971-24f0345d-9280-4a79-b637-8395218aa587.png)

### Situation
`createPost` 함수 호출 시, JPA가 insert가 아닌 update 쿼리를 날린다. 결과적으로 DB에 새로운 post 데이터가 insert 되지 않는다.

### Cause
JPA는 `PK` 값이 없을 경우, insert 쿼리를 날린다. 

``` bash
public PostDto.CreateResponse createPost(PostDto.Request request, Integer userId) {

    List<Schedule> requests = createSchedule(request.getSchedules());

    List<PostHashtag> postHashtags = createPostHashtag(request.getHashtags());

    Optional<User> user = userRepository.findById(userId);

    Post createdPost = PostMapper.INSTANCE.toEntity(request, user.get());
    System.out.println(createdPost.getId() + " >>> set null 하기 전");
    createdPost.setId(null);
    createdPost.setSchedules(requests);
    createdPost.setPostHashtags(postHashtags);

    Post savedPost = postRepository.save(createdPost);

    requests.forEach(schedule -> {
      schedule.setPost(savedPost);
      scheduleRepository.save(schedule);
    });

    postHashtags.forEach(postHashtag -> {
      postHashtag.setPost(savedPost);
      postHashtagRepository.save(postHashtag);
    });

    return PostMapper.INSTANCE.toCreateResponseDto(savedPost);
  }
```
위의 코드는 `createPost` 함수이다. 
`Post createdPost = PostMapper.INSTANCE.toEntity(request, user.get());` mapper를 통해 dto를 entity화 해서 리턴 받았을 때, createdPost의 PK인 id가 null 일 거라 예상했다. 하지만, `System.out.println(createdPost.getId() + " >>> set null 하기 전");` 로 확인해 보니, 이미 DB에 들어간 post의 id 값으로 들어가 있었다.
따라서, JPA는 기존의 데이터가 수정된 것으로 간주하고, insert 쿼리가 아닌 update 쿼리를 날린다.

### Solution
1. PostMapper.java `toEntity` 수정
``` bash
  @Mapping(source = "dto.title", target = "title")
  @Mapping(source = "dto.content", target = "content")
  @Mapping(source = "dto.credit", target = "credit")
  @Mapping(source = "dto.imageUrl", target = "imageUrl")
  @Mapping(source = "user", target = "user")
  @Mapping(target = "id", ignore = true)
  Post toEntity(Request dto, User user);
```
위의 코드와 같이 `@Mapping(target = "id", ignore = true)` mapping 조건을 추가해서 id에 null이 들어가도록 한다.
2. PostService.java `createPost` 수정
``` bash
 Post createdPost = PostMapper.INSTANCE.toEntity(request, user.get());
    System.out.println(createdPost.getId() + " >>> set null 하기 전");
    createdPost.setId(null);
    createdPost.setSchedules(requests);
    createdPost.setPostHashtags(postHashtags);
```
로직에서 mapper로 변환 된 entity에 `createdPost.setId(null)`을 해준다.

**1번 방식이 맞는 거 같다. 가급적 entity에 setter를 이용한 값을 넣는 행위는 좋지 않은 거 같기 때문이다.**