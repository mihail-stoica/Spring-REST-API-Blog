package com.mihailstoica.blog.repository;

import com.mihailstoica.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
