package com.mihailstoica.blog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihailstoica.blog.entity.Post;
import com.mihailstoica.blog.exception.ResourceNotFoundException;
import com.mihailstoica.blog.payload.PostDto;
import com.mihailstoica.blog.repository.PostRepository;
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
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class PostControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

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
    public void givenListOfPostDto_whenGetAllPosts_thenReturnPostDtoList() throws Exception {

        // given - precondition or setup
        List<PostDto> listOfPostDto = List.of(postDto);
        //stub method for postService.getAllPosts
        given(postService.getAllPosts()).willReturn(listOfPostDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get("/api/posts"));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(listOfPostDto.size())));
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

}
