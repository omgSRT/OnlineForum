package com.FA24SE088.OnlineForum.controller;


import com.FA24SE088.OnlineForum.dto.request.AccountUpdateCategoryRequest;
import com.FA24SE088.OnlineForum.dto.request.AccountUpdateInfoRequest;
import com.FA24SE088.OnlineForum.dto.response.AccountResponse;
import com.FA24SE088.OnlineForum.dto.response.ApiResponse;
import com.FA24SE088.OnlineForum.dto.response.FollowResponse;
import com.FA24SE088.OnlineForum.entity.Account;
import com.FA24SE088.OnlineForum.entity.Follow;
import com.FA24SE088.OnlineForum.enums.AccountStatus;
import com.FA24SE088.OnlineForum.enums.SuccessReturnMessage;
import com.FA24SE088.OnlineForum.service.AccountService;
import com.FA24SE088.OnlineForum.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/follow")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FollowController {
    final FollowService followService;

    @Operation(summary = "Create follow", description = "Create a new follow for the current user")
    @PostMapping("/create")
    public ApiResponse<FollowResponse> createFollow(@RequestBody UUID id) {
        return ApiResponse.<FollowResponse>builder()
                .entity(followService.create(id))
                .build();
    }

    @Operation(summary = "Block user", description = "Block a user for the current user")
    @PostMapping("/block")
    public ApiResponse<Void> blockUser(@RequestBody UUID accountId) {
        followService.blockUser(accountId);
        return ApiResponse.<Void>builder().build();
    }

    @Operation(summary = "Get block list", description = "")
    @GetMapping("/get-list-user-block")
    public ApiResponse<List<AccountResponse>> getFollow() {
        return ApiResponse.<List<AccountResponse>>builder()
                .entity(followService.listBlock())
                .build();
    }

    @Operation(summary = "Get all follows", description = "")
    @GetMapping
    public ApiResponse<List<FollowResponse>> getAllFollows() {
        return ApiResponse.<List<FollowResponse>>builder()
                .entity(followService.getFollowedAccounts())
                .build();
    }

}