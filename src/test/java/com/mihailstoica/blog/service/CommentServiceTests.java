package com.mihailstoica.blog.service;

import com.mihailstoica.blog.entity.Comment;
import com.mihailstoica.blog.entity.Post;
import com.mihailstoica.blog.payload.CommentDto;
import com.mihailstoica.blog.payload.CommentResponse;
import com.mihailstoica.blog.repository.CommentRepository;
import com.mihailstoica.blog.repository.PostRepository;
import com.mihailstoica.blog.service.impl.CommentServiceImpl;
import com.mihailstoica.blog.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
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

    @InjectMocks
    PostServiceImpl postService;

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

    @DisplayName("JUnit test for getAllPosts method")
    @Test
    public void givenCommentRepositoryAndPageNoAndPageSizeAndSortByAndSortDir_whenGetAllComments_thenReturnCommentsDtoList() {

        // given - precondition or setup
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        String sortDir = "asc";

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setName("Test-name 1");
        comment1.setEmail("testemail@test.1");
        comment1.setBody("Body of post 1");
        comment1.setPost(post);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setName("Test-name 2");;
        comment2.setEmail("testemail@test.2");
        comment2.setBody("Body of post 2");
        comment2.setPost(post);

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setId(comment1.getId());
        commentDto1.setName(comment1.getName());
        commentDto1.setEmail(comment1.getEmail());
        commentDto1.setBody(comment1.getBody());

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setId(comment2.getId());
        commentDto2.setName(comment2.getName());
        commentDto2.setEmail(comment2.getEmail());
        commentDto2.setBody(comment2.getBody());

        List<Comment> commentList = List.of(comment1, comment2);
        List<CommentDto> content = List.of(commentDto1, commentDto2);

        Sort sort = Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // stub methods
        given(commentRepository.findAll(pageable)).willReturn(comments);
        given(comments.getContent()).willReturn(commentList);

        // when - action or behaviour that we are going to test
        CommentResponse savedCommentsDto = commentService.getAllCommentsByPostId(
                post.getId(), pageNo, pageSize, sortBy, sortDir);

        // then - verify the output
        assertThat(savedCommentsDto.getContent()).isEqualTo(content);
        assertThat(savedCommentsDto.getPageNo()).isEqualTo(this.comments.getNumber());
        assertThat(savedCommentsDto.getPageSize()).isEqualTo(this.comments.getSize());
        assertThat(savedCommentsDto.getTotalElements()).isEqualTo(this.comments.getTotalElements());
        assertThat(savedCommentsDto.getTotalPages()).isEqualTo(this.comments.getTotalPages());
        assertThat(savedCommentsDto.isLast()).isEqualTo(this.comments.isLast());
    }

    @DisplayName("JUnit test for getCommentsById")
    @Test
    public void givenPostIdAndCommentId_whenGetCommentById_thenReturnCommentDto() {

        // given - precondition or setup
        Long postId = post.getId();
        Long commentId = comment.getId();

        // stub methods
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when - action or behaviour that we are going to test
        CommentDto commentDtoReturned = commentService.getCommentById(postId, commentId);

        // then - verify the output
        assertThat(commentDtoReturned).isEqualTo(commentDto);
    }

//    @DisplayName("JUnit test for updateComment")
//    @Test
//    public void given_when_then() {
//
//        // given - precondition or setup
//        Long postId = post.getId();
//        Long commentId = comment.getId();
//        CommentDto commentRequest = new CommentDto();
//        commentRequest.setId(2L);
//        commentRequest.setName("Test-name new");;
//        commentRequest.setEmail("testemail@test.new");
//        commentRequest.setBody("Body of post new");
//        // stub
//        //given(postRepository.findById(postId)).willReturn(Optional.of(post));
//        //given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
//        when(commentService.getCommentById(postId, commentId)).thenReturn(commentDto);
//
//        //given(postRepository.findById(postId)).willReturn(Optional.of(post));
//        //given(commentRepository.save(mapToEntity(commentRequest)));
//        // when - action or behaviour that we are going to test
//        CommentDto updatedComment = commentService.updateComment(postId, commentId, commentRequest);
//        // then - verify the output
//        assertThat(updatedComment).isEqualTo(commentDto);
//
//    }

    private Comment mapToEntity(CommentDto commentDto) {

        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());
        return comment;
    }

}
