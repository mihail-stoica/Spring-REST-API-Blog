package com.mihailstoica.blog.service;

import com.mihailstoica.blog.payload.CommentDto;

public interface CommentService {

    CommentDto createComment(Long postId, CommentDto commentDto);
}
