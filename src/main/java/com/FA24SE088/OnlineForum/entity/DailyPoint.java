package com.FA24SE088.OnlineForum.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID dailyPointId;
    double pointEarned;
    Date createdDate;

    @ManyToOne
    @JoinColumn(name = "accountId")
    Account account;

    @ManyToOne
    @JoinColumn(name = "pointId")
    Point point;

    @JsonIgnoreProperties(value = {"dailyPoint"}, allowSetters = true)
    @OneToOne
    @JoinColumn(name = "postId")
    Post post;
}
