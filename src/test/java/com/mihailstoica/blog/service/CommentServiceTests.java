package com.mihailstoica.blog.service;

import com.mihailstoica.blog.entity.Comment;
import com.mihailstoica.blog.entity.Post;
import com.mihailstoica.blog.payload.CommentDto;
import com.mihailstoica.blog.repository.CommentRepository;
import com.mihailstoica.blog.repository.PostRepository;
import com.mihailstoica.blog.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private Page<Comment> comments;

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

    @DisplayName("JUnit test for createPost method")
    @Test
    public void givenCommentDto_whenCreateComment_thenReturnCommentDtoObject() {

        // given - precondition or setup

        // stub methods
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        given(commentRepository.save(comment)).willReturn(comment);
        // when - action or behaviour that we are going to test
        CommentDto savedCommentDto = commentService.createComment(post.getId(), commentDto);
        // then - verify the output
        assertThat(savedCommentDto).isEqualTo(commentDto);
    }


}
