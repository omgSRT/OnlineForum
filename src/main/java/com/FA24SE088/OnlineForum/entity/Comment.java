package com.FA24SE088.OnlineForum.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID commentId;
    String content;

    @ManyToOne
    @JoinColumn(name = "accountId")
    Account account;

    @ManyToOne
    @JoinColumn(name = "postId")
    Post post;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="parentCommentId")
    Comment parentComment;

    @OneToMany(mappedBy="parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @JsonIgnoreProperties({ "parentComment", "post" })
    List<Comment> replies;
}
