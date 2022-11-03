package com.mihailstoica.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihailstoica.blog.entity.Comment;
import com.mihailstoica.blog.entity.Post;
import com.mihailstoica.blog.payload.CommentDto;
import com.mihailstoica.blog.payload.CommentResponse;
import com.mihailstoica.blog.service.CommentService;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class CommentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private PostController postController;

    @Autowired
    private ObjectMapper objectMapper;

    private Comment comment;

    private CommentDto commentDto;

    private Post post;

    @BeforeEach
    public void setup() {

        this.post = new Post();
        this.post.setId(1L);
        this.post.setTitle("Title");
        this.post.setDescription("Description");
        this.post.setContent("Content");

        this.comment = new Comment();
        this.comment.setId(1L);
        this.comment.setName("Test-name");
        this.comment.setEmail("testemail@test");
        this.comment.setBody("test body");
        this.comment.setPost(post);

        this.commentDto = new CommentDto();
        this.commentDto.setId(comment.getId());
        this.commentDto.setName(comment.getName());
        this.commentDto.setEmail(comment.getEmail());
        this.commentDto.setBody(comment.getBody());
    }

    @DisplayName("JUnit test for")
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
    public void givenPageNoAndPageSizeAndSortByAndSortDir_whenGetAllPosts_thenReturnPostResponse() throws Exception {

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

}
