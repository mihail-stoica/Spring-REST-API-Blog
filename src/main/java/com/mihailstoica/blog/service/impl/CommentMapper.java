package com.mihailstoica.blog.service.impl.util;

import com.mihailstoica.blog.entity.Comment;
import com.mihailstoica.blog.payload.CommentDto;

public class CommentMapper {
    static CommentDto mapEntityToDto(Comment comment) {

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setName(comment.getName());
        commentDto.setBody(comment.getBody());
        commentDto.setEmail(comment.getEmail());
        return commentDto;
    }

    static Comment mapDtoToEntity(CommentDto commentDto) {

        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());
        return comment;
    }

    /*public Set<CommentDto> mapEntitiesToDtos(Set<Comment> comments) {
        return comments.stream().map(this::mapEntityToDto).collect(Collectors.toSet());
    }

    public Set<Comment> mapDtosToEntities(Set<CommentDto> commentDtos) {
        return commentDtos.stream().map(this::mapDtoToEntity).collect(Collectors.toSet());
    }*/
}
