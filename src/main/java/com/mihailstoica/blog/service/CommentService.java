package com.mihailstoica.blog.service;

import com.mihailstoica.blog.payload.CommentDto;
import com.mihailstoica.blog.payload.CommentResponse;

public interface CommentService {

    CommentDto createComment(Long postId, CommentDto commentDto);

    CommentResponse getAllCommentsByPostId(Long postId, int pageNo, int pageSize, String sortBy, String sortDir);

    CommentDto getCommentById(Long postId, Long commentId);
}
