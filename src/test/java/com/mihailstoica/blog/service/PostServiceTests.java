package com.mihailstoica.blog.service;

import com.mihailstoica.blog.entity.Post;
import com.mihailstoica.blog.payload.PostDto;
import com.mihailstoica.blog.payload.PostResponse;
import com.mihailstoica.blog.repository.PostRepository;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private Page<Post> posts;

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
    public void givenPostRepositoryAndPageNoAndPageSizeAndSortByAndSortDir_whenGetAllPosts_thenReturnPostsDtoList() {

        // given - precondition or setup
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        String sortDir = "asc";

        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Title 1");
        post1.setDescription("Description of post 1");
        post1.setContent("Content of post 1");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Title 2");
        post2.setDescription("Description of post 2");
        post2.setContent("Content of post 2");

        PostDto postDto1 = new PostDto();
        postDto1.setId(post1.getId());
        postDto1.setTitle(post1.getTitle());
        postDto1.setDescription(post1.getDescription());
        postDto1.setContent(post1.getContent());

        PostDto postDto2 = new PostDto();
        postDto2.setId(post2.getId());
        postDto2.setTitle(post2.getTitle());
        postDto2.setDescription(post2.getDescription());
        postDto2.setContent(post2.getContent());

        List<Post> postList = List.of(post1, post2);
        List<PostDto> content = List.of(postDto1, postDto2);

        Sort sort = Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        given(postRepository.findAll(pageable)).willReturn(posts);
        given(posts.getContent()).willReturn(postList);

        // when - action or behaviour that we are going to test
        PostResponse savedPostsDto = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);

        // then - verify the output
        assertThat(savedPostsDto.getContent()).isEqualTo(content);
        assertThat(savedPostsDto.getPageNo()).isEqualTo(this.posts.getNumber());
        assertThat(savedPostsDto.getPageSize()).isEqualTo(this.posts.getSize());
        assertThat(savedPostsDto.getTotalElements()).isEqualTo(this.posts.getTotalElements());
        assertThat(savedPostsDto.getTotalPages()).isEqualTo(this.posts.getTotalPages());
        assertThat(savedPostsDto.isLast()).isEqualTo(this.posts.isLast());
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
