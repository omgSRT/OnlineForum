package com.FA24SE088.OnlineForum.mapper;

import com.FA24SE088.OnlineForum.dto.request.CommentCreateRequest;
import com.FA24SE088.OnlineForum.dto.request.CommentUpdateRequest;
import com.FA24SE088.OnlineForum.dto.request.ReplyCreateRequest;
import com.FA24SE088.OnlineForum.dto.response.CommentNoPostResponse;
import com.FA24SE088.OnlineForum.dto.response.CommentResponse;
import com.FA24SE088.OnlineForum.dto.response.ReplyCreateResponse;
import com.FA24SE088.OnlineForum.entity.Comment;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-10T14:30:30+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment toComment(CommentCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Comment.CommentBuilder comment = Comment.builder();

        comment.content( request.getContent() );

        return comment.build();
    }

    @Override
    public Comment toCommentFromReplyRequest(ReplyCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Comment.CommentBuilder comment = Comment.builder();

        comment.content( request.getContent() );

        return comment.build();
    }

    @Override
    public CommentResponse toCommentResponse(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentResponse.CommentResponseBuilder commentResponse = CommentResponse.builder();

        commentResponse.commentId( comment.getCommentId() );
        commentResponse.content( comment.getContent() );
        commentResponse.account( comment.getAccount() );
        commentResponse.post( comment.getPost() );

        return commentResponse.build();
    }

    @Override
    public ReplyCreateResponse toReplyCreateResponse(Comment reply) {
        if ( reply == null ) {
            return null;
        }

        ReplyCreateResponse.ReplyCreateResponseBuilder replyCreateResponse = ReplyCreateResponse.builder();

        replyCreateResponse.commentId( reply.getCommentId() );
        replyCreateResponse.content( reply.getContent() );
        replyCreateResponse.account( reply.getAccount() );
        replyCreateResponse.post( reply.getPost() );
        replyCreateResponse.parentComment( reply.getParentComment() );

        return replyCreateResponse.build();
    }

    @Override
    public CommentNoPostResponse toCommentNoPostResponse(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentNoPostResponse.CommentNoPostResponseBuilder commentNoPostResponse = CommentNoPostResponse.builder();

        commentNoPostResponse.commentId( comment.getCommentId() );
        commentNoPostResponse.content( comment.getContent() );
        commentNoPostResponse.account( comment.getAccount() );
        List<Comment> list = comment.getReplies();
        if ( list != null ) {
            commentNoPostResponse.replies( new ArrayList<Comment>( list ) );
        }

        return commentNoPostResponse.build();
    }

    @Override
    public void updateComment(Comment comment, CommentUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        comment.setContent( request.getContent() );
    }
}
