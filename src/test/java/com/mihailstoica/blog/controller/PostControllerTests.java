package com.mihailstoica.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihailstoica.blog.entity.Post;
import com.mihailstoica.blog.exception.ResourceNotFoundException;
import com.mihailstoica.blog.payload.PostDto;
import com.mihailstoica.blog.payload.PostResponse;
import com.mihailstoica.blog.service.CommentService;
import com.mihailstoica.blog.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class PostControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Post post;

    private PostDto postDto;

    @BeforeEach
    public void setup() {
        this.post = new Post();
        this.post.setId(1L);
        this.post.setTitle("Title");
        this.post.setDescription("Description");
        this.post.setContent("Content");

        this.postDto = new PostDto();
        this.postDto.setId(post.getId());
        this.postDto.setTitle(post.getTitle());
        this.postDto.setDescription(post.getDescription());
        this.postDto.setContent(post.getContent());
    }
    @DisplayName("JUnit test for createPost")
    @Test
    public void givenPostDtoObject_whenCreatePost_thenReturnCreatedPostDto() throws Exception {

        // given - precondition or setup
        // stub method for postService.createPost
        given(postService.createPost(any(PostDto.class)))
                .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)));

        // then - verify the output
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(postDto.getTitle())))
                .andExpect(jsonPath("$.description", is(postDto.getDescription())))
                .andExpect(jsonPath("$.content", is(postDto.getContent())));
    }

    @DisplayName("JUnit test for getAllPosts")
    @Test
    public void givenPageNoAndPageSizeAndSortByAndSortDir_whenGetAllPosts_thenReturnPostResponse() throws Exception {

        // given - precondition or setup
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        String sortDir = "asc";

        List<PostDto> contentDto = List.of(postDto);
        PostResponse postResponse = new PostResponse();
        postResponse.setContent(contentDto);
        postResponse.setPageNo(pageNo);
        postResponse.setPageSize(pageSize);
        postResponse.setTotalElements(contentDto.size());
        postResponse.setTotalPages(10);
        postResponse.setLast(true);
        //stub method for postService.getAllPosts
        given(postService.getAllPosts(pageNo, pageSize, sortBy, sortDir)).willReturn(postResponse);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get("/api/posts?pageNo=0&pageSize=10&sortBy=id&sorDir=asc"));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content.size()", is(postResponse.getContent().size())))
                .andExpect(jsonPath("$.['pageNo']", is(postResponse.getPageNo())))
                .andExpect(jsonPath("$.['pageSize']", is((postResponse.getPageSize()))))
                .andExpect(jsonPath("$.['totalElements']", is(((int)postResponse.getTotalElements()))))
                .andExpect(jsonPath("$.['totalPages']", is((postResponse.getTotalPages()))))
                .andExpect(jsonPath("$.['last']", is((postResponse.isLast()))));
    }

    // positive scenario - valid post id
    @DisplayName("JUnit test for getPostById - positive scenario")
    @Test
    public void givenPostId_whenGetPostById_thenReturnPostDtoObject() throws Exception {

        // given - precondition or setup
        Long id = post.getId();
        // stub method for postService.getPostById
        given(postService.getPostById(id)).willReturn(postDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get("/api/posts/{id}", id));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title", is(postDto.getTitle())))
                .andExpect(jsonPath("$.description", is(postDto.getDescription())))
                .andExpect(jsonPath("$.content", is(postDto.getContent())));
    }

    //negative scenario - invalid post id
    @DisplayName("JUnit test for getPostById() - negative scenario")
    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmpty() throws Exception {

        //given - precondition or setup
        Long postId = 2L;

        // stub method for postService.getPostById
        given(postService.getPostById(postId)).willThrow(new ResourceNotFoundException("Post", "id", postId));

        //when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get("/api/posts/{id}", postId));

        //then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("JUnit test for updatePost positive scenario - valid post id")
    @Test
    public void givenPostIdAndUpdatedPostDto_whenUpdatePost_thenReturnUpdatePostDtoObject() throws Exception {

        // given - precondition or setup
        Long postId = post.getId();
        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setId(postId);
        updatedPostDto.setTitle("Title updated");
        updatedPostDto.setDescription("Description updated");
        updatedPostDto.setContent("Content updated");

        // stub method for postService.updatePost
        given(postService.updatePost(updatedPostDto, postId)).willAnswer(invocation -> invocation.getArgument(0));

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(put("/api/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPostDto)));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title", is(updatedPostDto.getTitle())))
                .andExpect(jsonPath("$.description", is(updatedPostDto.getDescription())))
                .andExpect(jsonPath("$.content", is(updatedPostDto.getContent())));
    }

    @DisplayName("JUnit test for updatePost negative scenario - invalid post id")
    @Test
    public void givenPostIdAndUpdatedPostDto_whenUpdatePost_thenReturn404() throws Exception {

        // given - precondition or setup
        Long postId = post.getId();
        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setId(postId);
        updatedPostDto.setTitle("Title updated");
        updatedPostDto.setDescription("Description updated");
        updatedPostDto.setContent("Content updated");

        // stub method for postService.updatePost
        given(postService.updatePost(updatedPostDto, postId))
                .willThrow(new ResourceNotFoundException("Post", "id", postId));

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(put("/api/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPostDto)));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("JUnit test for deletePostById positive scenario - valid post id")
    @Test
    public void givenPostId_whenDeletePostById_thenReturn200() throws Exception {

        // given - precondition or setup
        Long postId = post.getId();

        // stub method for postService.deletePostById
        willDoNothing().given(postService).deletePostById(postId);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(delete("/api/posts/{id}", postId));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("JUnit test for deletePostById negative scenario - invalid post id")
    @Test
    public void givenPostId_whenDeletePostById_thenReturn404() throws Exception {

        // given - precondition or setup
        Long postId = post.getId();

        // stub method for postService.updatePost
        willThrow(new ResourceNotFoundException("Post", "id", postId)).given(postService).deletePostById(postId);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(delete("/api/posts/{id}", postId));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

}
