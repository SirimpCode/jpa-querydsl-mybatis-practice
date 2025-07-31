package com.github.jpaquerydslmybatis.repository.db1.jpa.comment;

import com.github.jpaquerydslmybatis.common.converter.custom.StatusConverter;
import com.github.jpaquerydslmybatis.common.myenum.Status;
import com.github.jpaquerydslmybatis.repository.db1.jpa.board.Board;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.MyUser;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentRequest;
import com.github.jpaquerydslmybatis.web.dto.storage.FileInfoUpdateRequest;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@DynamicInsert
@Table(name = "my_comment")
public class MyComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private String content;
    @Convert(converter = StatusConverter.class)
    private Status status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MyUser myUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private MyComment parentMyComment;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentMyComment")
    private List<MyComment> childrenComments;

    private String fileName;
    private String filePath;
    private Long fileSize;


    private LocalDateTime createdAt;

    public static MyComment requestToEntity(CommentRequest request){
        MyComment myComment = new MyComment();
        myComment.content = request.getContent();
        myComment.myUser = MyUser.onlyId(request.getUserId());
        myComment.board = Board.onlyId(request.getBoardId());
        myComment.createdAt = LocalDateTime.now();
        myComment.fileName = request.getFileName();
        myComment.filePath = request.getFilePath();
        myComment.fileSize = request.getFileSize();
        if(request.getParentCommentId() != null)
            myComment.parentMyComment =  onlyId(request.getParentCommentId());
        return myComment;
    }
    public static MyComment onlyId(Long commentId) {
        MyComment myComment = new MyComment();
        myComment.commentId = commentId;
        return myComment;
    }

    public void modifyContent(String content) {
        this.content = content;
    }

    public void fileInfoUpdate(FileInfoUpdateRequest request) {
        this.fileName = request.getFileName();
        this.filePath = request.getFilePath();
        this.fileSize = request.getFileSize();
    }
}
