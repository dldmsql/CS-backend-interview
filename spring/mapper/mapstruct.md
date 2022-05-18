# Mapstruct 사용하기 ( POST 버전 )
### 사용 DTO class
- PostDto.Response
- PostDto.ReviewResponse
- ScheduleDto.Resonse
- HashtagDto.Response

### 관련 Service 로직
``` bash
public PostDto.Response getPostResponse(Integer postId) {

    if (this.postRepository.findById(postId).isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    Optional<Post> post = postRepository.findById(postId);
    post.get().setViews(post.get().getViews() + 1);
    Post updatePost = postRepository.save(post.get());

    PostDto.Response result = PostMapper.INSTANCE.toDetailResponse(updatePost);

    User user = post.get().getUser();
    result.setMentor(PostMapper.INSTANCE.toUserResponse(user, user.getUserInfo()));
    return result;
  }

  public List<ScheduleDto.Response> getSchedulesResponse(Integer postId) {
    List<Schedule> schedules = scheduleRepository.findByPostId(postId);
    return PostMapper.INSTANCE.toScheduleResponses(schedules);
  }

  public List<PostDto.ReviewResponse> getReviewsResponse(Integer postId) {
    List<PostDto.ReviewResponse> responses = new ArrayList<>();
    reviewRepository.findByPostId(postId).forEach(review -> {
      User user = review.getUser();
      UserInfo userInfo = user.getUserInfo();
      PostDto.ReviewResponse response =
          PostMapper.INSTANCE.toReviewResponse(review, user, userInfo);
      responses.add(response);
    });
    return responses;
  }

  public List<HashtagDto.Response> getHashtagsResponse(Integer postId) {
    List<HashtagDto.Response> responses = new ArrayList<>();
    postHashtagRepository.findByPostId(postId).forEach(postHashtag -> {
      Hashtag hashTag = postHashtag.getHashTag();
      HashtagDto.Response response = HashtagMapper.INSTANCE.toPostResponseDto(postHashtag, hashTag);
      responses.add(response);
    });
    return responses;
  }
```
위의 로직들 모두 DTO만 다르고, 흐름은 동일하다.
