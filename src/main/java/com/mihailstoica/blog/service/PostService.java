package com.mihailstoica.blog.service;

import com.mihailstoica.blog.payload.PostDto;

public interface PostService {

    PostDto createPost(PostDto postDto);
}
