package com.FA24SE088.OnlineForum.dto.response;

import com.FA24SE088.OnlineForum.entity.Account;
import com.FA24SE088.OnlineForum.entity.Document;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RedeemDocumentResponse {
//    @JsonIgnoreProperties(value = {"redeemList","notificationList","followeeList","followerList","dailyPointList","postList","upvoteList","commentList","categoryList","eventList"}, allowSetters = true)
//    Account account;
    @JsonIgnoreProperties(value = {"redeemList"}, allowSetters = true)
    List<Document> document;
}
