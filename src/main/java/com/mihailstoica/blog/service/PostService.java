package com.mihailstoica.blog.service;

import com.mihailstoica.blog.payload.PostDto;

import java.util.List;
import java.util.Optional;

public interface PostService {

    PostDto createPost(PostDto postDto);

    List<PostDto> getAllPosts();

    PostDto getPostById(Long id);
}
