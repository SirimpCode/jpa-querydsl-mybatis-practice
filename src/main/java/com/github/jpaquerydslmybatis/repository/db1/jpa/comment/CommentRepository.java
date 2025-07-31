package com.github.jpaquerydslmybatis.repository.db1.jpa.comment;

import com.github.jpaquerydslmybatis.repository.db1.jpa.user.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<MyComment, Long>, CommentRepositoryCustom {
    long deleteMyCommentByCommentIdAndMyUser(Long commentId, MyUser myUser);
    Optional<MyComment> findMyCommentByCommentIdAndMyUser(Long commentId, MyUser myUser);
}
