package com.mihailstoica.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihailstoica.blog.entity.Comment;
import com.mihailstoica.blog.payload.CommentDto;
import com.mihailstoica.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
public class CommentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Comment comment;

    private CommentDto commentDto;


}
