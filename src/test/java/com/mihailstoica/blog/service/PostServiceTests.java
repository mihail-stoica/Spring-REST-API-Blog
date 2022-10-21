package com.mihailstoica.blog.service;

import com.mihailstoica.blog.entity.Post;
import com.mihailstoica.blog.payload.PostDto;
import com.mihailstoica.blog.repository.PostRepository;
import com.mihailstoica.blog.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;

    private PostDto postDto;

    @BeforeEach
    public void setup() {
        this.post = new Post();
        this.post.setTitle("Title");
        this.post.setDescription("Description");
        this.post.setContent("Content");

        this.postDto = new PostDto();
        this.postDto.setId(post.getId());
        this.postDto.setTitle(post.getTitle());
        this.postDto.setDescription(post.getDescription());
        this.postDto.setContent(post.getContent());
    }


    @DisplayName("JUnit test for createPost method")
    @Test
    public void givenPostDtoObject_whenCreatePost_thenReturnPostDtoObject() {

        // given - precondition or setup

        // stub method postRepository.save()
        given(postRepository.save(post)).willReturn(post);

        // when - action or behaviour that we are going to test
        PostDto savedPostDto = postService.createPost(postDto);

        // then - verify the output
        assertThat(savedPostDto).isEqualTo(postDto);
    }

    @DisplayName("JUnit test for getAllPosts method")
    @Test
    public void givenPostRepository_whenGetAllPosts_thenReturnPostsDtoList() {

        // given - precondition or setup
        // stub method postRepository.findAll()
        given(postRepository.findAll()).willReturn(List.of(post));

        // when - action or behaviour that we are going to test
        List<PostDto> savedPostsDto = postService.getAllPosts();

        // then - verify the output
        assertThat(savedPostsDto.size()).isEqualTo(1);
        assertThat(savedPostsDto).isEqualTo(List.of(postDto));
    }

    @DisplayName("JUnit test for getPostById method")
    @Test
    public void givenPostId_whenGetPostById_thenReturnPostDtoObject() {

        // given - precondition or setup
        Long id = post.getId();
        // stub method for postRepository.findById
        given(postRepository.findById(id)).willReturn(Optional.of(post));

        // when - action or behaviour that we are going to test
        PostDto savedPostDto = postService.getPostById(id);

        // then - verify the output
        assertThat(savedPostDto).isEqualTo(postDto);
    }

    @DisplayName("JUnit test for updatePost")
    @Test
    public void givenPostIdAndUpdatedPostDto_whenUpdatePost_thenReturnUpdatePostDtoObject() {

        // given - precondition or setup
        Long postId = post.getId();
        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setId(postId);
        updatedPostDto.setTitle("Title updated");
        updatedPostDto.setDescription("Description updated");
        updatedPostDto.setContent("Content updated");

        // stub method for postRepository.findById
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        // stub method postRepository.save()
        given(postRepository.save(post)).willReturn(post);

        // when - action or behaviour that we are going to test
        PostDto savedPostDto = postService.updatePost(updatedPostDto, postId);

        // then - verify the output
        assertThat(savedPostDto).isEqualTo(updatedPostDto);

    }

    @DisplayName("JUnit test for deletePostById")
    @Test
    public void givenPostId_whenDeletePostById_thenNothing() {

        // given - precondition or setup
        Long postId = post.getId();

        // stub method for postRepository.findById
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // stub method for postRepository.deleteById
        willDoNothing().given(postRepository).delete(post);

        // when - action or behaviour that we are going to test
        postService.deletePostById(postId);

        // then - verify the output
        verify(postRepository, times(1)).delete(post);
    }

}
