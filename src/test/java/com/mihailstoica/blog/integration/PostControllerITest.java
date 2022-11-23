package com.mihailstoica.blog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihailstoica.blog.entity.Post;
import com.mihailstoica.blog.payload.PostDto;
import com.mihailstoica.blog.payload.PostResponse;
import com.mihailstoica.blog.repository.CommentRepository;
import com.mihailstoica.blog.repository.PostRepository;
import com.mihailstoica.blog.security.CustomUserDetailsService;
import com.mihailstoica.blog.service.CommentService;
import com.mihailstoica.blog.service.PostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PostControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService;

    @Autowired
    CommentService commentService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Post post;

    private PostDto postDto;


    @BeforeEach
    public void setup() {
        this.post = new Post();
        this.post.setTitle("Title");
        this.post.setDescription("Description");
        this.post.setContent("Content");

        postRepository.save(this.post);

        this.postDto = new PostDto();
        this.postDto.setId(post.getId());
        this.postDto.setTitle(post.getTitle());
        this.postDto.setDescription(post.getDescription());
        this.postDto.setContent(post.getContent());
    }

    @AfterEach
    public void setupDeleteAll() {

        postRepository.deleteAll();
    }

    @DisplayName("JUnit test for createPost")
    @WithMockUser(username = "user",password = "P4ssword",roles = {"ADMIN"})
    @Test
    public void givenPostDtoObject_whenCreatePost_thenReturnCreatedPostDto() throws Exception {

        postRepository.deleteAll();
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
        int totalPages = 1;
        String sortBy = "id";
        String sortDir = "asc";

        List<PostDto> contentDto = List.of(postDto);

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(contentDto);
        postResponse.setPageNo(pageNo);
        postResponse.setPageSize(pageSize);
        postResponse.setTotalElements(contentDto.size());
        postResponse.setTotalPages(totalPages);
        postResponse.setLast(true);

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

        //given - precondition or setup - invalid id
        Long postId = 2L;

        //when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get("/api/posts/{id}", postId));

        //then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("JUnit test for updatePost positive scenario - valid post id")
    @WithMockUser(username = "user",password = "P4ssword",roles = {"ADMIN"})
    @Test
    public void givenPostIdAndUpdatedPostDto_whenUpdatePost_thenReturnUpdatePostDtoObject() throws Exception {

        // given - precondition or setup
        Long postId = post.getId();

        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setId(postId);
        updatedPostDto.setTitle("Title updated");
        updatedPostDto.setDescription("Description updated");
        updatedPostDto.setContent("Content updated");

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
    @WithMockUser(username = "user",password = "P4ssword",roles = {"ADMIN"})
    @Test
    public void givenPostIdAndUpdatedPostDto_whenUpdatePost_thenReturn404() throws Exception {

        // given - precondition or setup - invalid id
        Long postId = 2L;

        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setId(postId);
        updatedPostDto.setTitle("Title updated");
        updatedPostDto.setDescription("Description updated");
        updatedPostDto.setContent("Content updated");

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(put("/api/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPostDto)));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("JUnit test for deletePostById positive scenario - valid post id")
    @WithMockUser(username = "user",password = "P4ssword",roles = {"ADMIN"})
    @Test
    public void givenPostId_whenDeletePostById_thenReturn200() throws Exception {

        // given - precondition or setup
        Long postId = post.getId();

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(delete("/api/posts/{id}", postId));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("JUnit test for deletePostById negative scenario - invalid post id")
    @WithMockUser(username = "user",password = "P4ssword",roles = {"ADMIN"})
    @Test
    public void givenPostId_whenDeletePostById_thenReturn404() throws Exception {

        // given - precondition or setup - invalid id
        Long postId = 2L;

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(delete("/api/posts/{id}", postId));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
}
