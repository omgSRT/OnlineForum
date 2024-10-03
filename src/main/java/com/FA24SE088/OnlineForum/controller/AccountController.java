package com.FA24SE088.OnlineForum.controller;


import com.FA24SE088.OnlineForum.dto.request.AccountRequest;
import com.FA24SE088.OnlineForum.dto.response.AccountResponse;
import com.FA24SE088.OnlineForum.dto.response.ApiResponse;
import com.FA24SE088.OnlineForum.exception.AppException;
import com.FA24SE088.OnlineForum.exception.ErrorCode;
import com.FA24SE088.OnlineForum.service.AccountService;
import com.FA24SE088.OnlineForum.service.EmailService;
import com.FA24SE088.OnlineForum.service.OTPService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.FA24SE088.OnlineForum.entity.Account;
import com.FA24SE088.OnlineForum.enums.SuccessReturnMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountController {
    final AccountService accountService;
    @Autowired
    final OTPService otpService;
    @Autowired
    final EmailService emailService;

    @Operation(summary = "Find Account", description = "Find By Username")
    @GetMapping(path = "/find/by-username")
    public ApiResponse<Account> findByUsername(@NotNull String username) {
        return ApiResponse.<Account>builder()
                .message(SuccessReturnMessage.SEARCH_SUCCESS.getMessage())
                .entity(accountService.findByUsername(username))
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<AccountResponse> create(@RequestBody AccountRequest request) {
        AccountResponse response = accountService.create(request);
//        String otp = otpService.generateOTP(request.getEmail());
//        emailService.sendOtpEmail(request.getEmail(), "Mã OTP xác thực tài khoản", "Mã OTP của bạn là: " + otp);
        return ApiResponse.<AccountResponse>builder()
                .entity(response)
                .build();
    }

    @GetMapping("/getAll")
    public ApiResponse<List<AccountResponse>> getAll(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int perPage) {
        return ApiResponse.<List<AccountResponse>>builder()
                .entity(accountService.getAll(page, perPage))
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<Void> verifyAccount(@RequestParam String email, @RequestParam String otp) {
        if (!otpService.validateOTP(email, otp)) {
            throw new AppException(ErrorCode.WRONG_OTP);
        }
        Account account = accountService.findByEmail(email);
        accountService.activeAccount(account);
        otpService.clearOTP(email);
        return ApiResponse.<Void>builder()
                .build();
    }
}