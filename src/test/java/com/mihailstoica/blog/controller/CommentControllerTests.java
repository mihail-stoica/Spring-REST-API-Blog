package com.mihailstoica.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihailstoica.blog.entity.Comment;
import com.mihailstoica.blog.entity.Post;
import com.mihailstoica.blog.exception.ResourceNotFoundException;
import com.mihailstoica.blog.payload.CommentDto;
import com.mihailstoica.blog.payload.CommentResponse;
import com.mihailstoica.blog.payload.PostDto;
import com.mihailstoica.blog.security.CustomUserDetailsService;
import com.mihailstoica.blog.service.CommentService;
import com.mihailstoica.blog.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class CommentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;
    @MockBean
    private CommentService commentService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private PostController postController;

    @Autowired
    private ObjectMapper objectMapper;

    private Comment comment;

    private CommentDto commentDto;

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

        this.comment = new Comment();
        this.comment.setId(1L);
        this.comment.setName("Test-name");
        this.comment.setEmail("testemail@test");
        this.comment.setBody("test body comment");
        this.comment.setPost(post);

        this.commentDto = new CommentDto();
        this.commentDto.setId(comment.getId());
        this.commentDto.setName(comment.getName());
        this.commentDto.setEmail(comment.getEmail());
        this.commentDto.setBody(comment.getBody());
    }

    @DisplayName("JUnit test for createComment")
    @WithMockUser(username = "user", password = "P4ssword", roles = {"ADMIN"})
    @Test
    public void givenCommentDto_whenCreateComment_thenReturnCommentDto() throws Exception {

        // given - precondition or setup
        // stub method for commentService.CreateComment
        //given(commentService.createComment(post.getId(), commentDto)).willReturn(commentDto);
        given(commentService.createComment(any(Long.class), any(CommentDto.class)))
                .willAnswer(invocationOnMock -> invocationOnMock.getArgument(1));
        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)));
        // then - verify the output
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(commentDto.getName())))
                .andExpect(jsonPath("$.email", is(commentDto.getEmail())))
                .andExpect(jsonPath("$.body", is(commentDto.getBody())));
    }

    @DisplayName("JUnit test for getAllCommentsByPostId")
    @Test
    public void givenPostIdAndPageNoAndPageSizeAndSortByAndSortDir_whenGetAllPosts_thenReturnPostResponse()
            throws Exception {

        // given - precondition or setup
        Long postId = post.getId();
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        String sortDir = "asc";

        List<CommentDto> contentDto = List.of(commentDto);
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setContent(contentDto);
        commentResponse.setPageNo(pageNo);
        commentResponse.setPageSize(pageSize);
        commentResponse.setTotalElements(contentDto.size());
        commentResponse.setTotalPages(10);
        commentResponse.setLast(true);

        //stub method for postService.getAllPosts
        given(commentService.getAllCommentsByPostId(post.getId(), pageNo, pageSize, sortBy, sortDir))
                .willReturn(commentResponse);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get(
                "/api/posts/{postId}/comments?pageNo=0&pageSize=10&sortBy=id&sorDir=asc", postId));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content.size()", is(commentResponse.getContent().size())))
                .andExpect(jsonPath("$.['pageNo']", is(commentResponse.getPageNo())))
                .andExpect(jsonPath("$.['pageSize']", is((commentResponse.getPageSize()))))
                .andExpect(jsonPath("$.['totalElements']", is(((int)commentResponse.getTotalElements()))))
                .andExpect(jsonPath("$.['totalPages']", is((commentResponse.getTotalPages()))))
                .andExpect(jsonPath("$.['last']", is((commentResponse.isLast()))));
    }

    //negative scenario - invalid post id
    @DisplayName("JUnit test for getAllCommentsByPostId() - negative scenario")
    @Test
    public void givenPostIdAndPageNoAndPageSizeAndSortByAndSortDir_whenGetAllPosts_thenReturnEmpty() throws Exception {

        //given - precondition or setup
        Long postId = 7L; //invalid postId
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        String sortDir = "asc";

        List<CommentDto> contentDto = List.of(commentDto);
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setContent(contentDto);
        commentResponse.setPageNo(pageNo);
        commentResponse.setPageSize(pageSize);
        commentResponse.setTotalElements(contentDto.size());
        commentResponse.setTotalPages(10);
        commentResponse.setLast(true);
        // stub method for postService.getPostById
        given(commentService.getAllCommentsByPostId(postId, pageNo, pageSize, sortBy, sortDir))
                .willThrow(new ResourceNotFoundException("Post", "id", postId));

        //when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get(
                "/api/posts/{postId}/comments", postId));

        //then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("JUnit test for getCommentById")
    @Test
    public void givenPostIdAndCommentId_whenGetCommentById_thenReturnCommentDto() throws Exception {

        // given - precondition or setup
        Long postId = post.getId();
        Long commentId = comment.getId();
        // stub methods
        given(postService.getPostById(postId)).willReturn(postDto);
        given(commentService.getCommentById(postId, commentId)).willReturn(commentDto);
        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get(
                "/api/posts/{postId}/comments/{commentId}", postId, commentId));
        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(commentDto.getName())))
                .andExpect(jsonPath("$.email", is(commentDto.getEmail())))
                .andExpect(jsonPath("$.body", is(commentDto.getBody())));
    }

    //negative scenario - invalid post id
    @DisplayName("JUnit test for getCommentById() - negative scenario")
    @Test
    public void givenPostIdAndCommentId_whenGetCommentById_thenReturnEmpty() throws Exception {

        //given - precondition or setup
        Long postId = 7L; //invalid postId
        Long commentId = comment.getId();

        // stub method for postService.getPostById
        given(postService.getPostById(postId)).willThrow(new ResourceNotFoundException("Post", "id", postId));
        given(commentService.getCommentById(postId, commentId))
                .willThrow(new ResourceNotFoundException("Comment", "id", commentId));
        //when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get(
                "/api/posts/{postId}/comments/{commentId}", postId, commentId));

        //then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("JUnit test for updateComment")
    @WithMockUser(username = "user", password = "P4ssword", roles = {"ADMIN"})
    @Test
    public void givenPostIdAndCommentIdCommentRequest_whenUpdateComment_thenReturnUpdatedCommentDto() throws Exception {

        // given - precondition or setup
        Long postId = post.getId();
        Long commentId = comment.getId();
        CommentDto commentRequest = new CommentDto();
        commentRequest.setName("Test-name-request");
        commentRequest.setEmail("testemail@test-request");
        commentRequest.setBody("test body request");
        // stub methods
        given(commentService.updateComment(postId, commentId, commentRequest)).willAnswer(
                invocationOnMock -> invocationOnMock.getArgument(2));
        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}",
                postId, commentId, commentRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)));
        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(commentRequest.getName())))
                .andExpect(jsonPath("$.email", is(commentRequest.getEmail())))
                .andExpect(jsonPath("$.body", is(commentRequest.getBody())));
    }

    //negative scenario - invalid post id
    @DisplayName("JUnit test for updateComment - negative scenario")
    @WithMockUser(username = "user", password = "P4ssword", roles = {"ADMIN"})
    @Test
    public void givenPostIdAndCommentIdCommentRequest_whenUpdateComment_thenReturnEmpty() throws Exception {

        //given - precondition or setup
        Long postId = 7L; //invalid postId
        Long commentId = comment.getId();
        CommentDto commentRequest = new CommentDto();
        commentRequest.setName("Test-name-request");
        commentRequest.setEmail("testemail@test-request");
        commentRequest.setBody("test body request");

        // stub method for postService.getPostById
        given(commentService.updateComment(postId, commentId, commentRequest)).
                willThrow(new ResourceNotFoundException("Post", "id", postId));
        //when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}",
                postId, commentId, commentRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)));

        //then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("JUnit test for deleteComment")
    @WithMockUser(username = "user", password = "P4ssword", roles = {"ADMIN"})
    @Test
    public void givenPostIdAndCommentId_whenDeleteComment_thenReturn200() throws Exception {

        // given - precondition or setup
        Long postId = post.getId();
        Long commentId = comment.getId();
        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}",
                postId, commentId));
        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print());
    }


}
