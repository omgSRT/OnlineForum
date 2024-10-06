package com.FA24SE088.OnlineForum.service;

import com.FA24SE088.OnlineForum.dto.request.DailyPointRequest;
import com.FA24SE088.OnlineForum.dto.request.PointRequest;
import com.FA24SE088.OnlineForum.dto.response.DailyPointResponse;
import com.FA24SE088.OnlineForum.dto.response.PointResponse;
import com.FA24SE088.OnlineForum.dto.response.TagResponse;
import com.FA24SE088.OnlineForum.entity.Account;
import com.FA24SE088.OnlineForum.entity.DailyPoint;
import com.FA24SE088.OnlineForum.entity.Point;
import com.FA24SE088.OnlineForum.entity.Post;
import com.FA24SE088.OnlineForum.exception.AppException;
import com.FA24SE088.OnlineForum.exception.ErrorCode;
import com.FA24SE088.OnlineForum.mapper.DailyPointMapper;
import com.FA24SE088.OnlineForum.repository.UnitOfWork.UnitOfWork;
import com.FA24SE088.OnlineForum.utils.PaginationUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
public class DailyPointService {
    final UnitOfWork unitOfWork;
    final DailyPointMapper dailyPointMapper;
    final PaginationUtils paginationUtils;

    @Async("AsyncTaskExecutor")
    public CompletableFuture<DailyPointResponse> createDailyPoint(DailyPointRequest request){
        var accountFuture = findAccountById(request.getAccountId());
        var postFuture = findPostById(request.getPostId());
        var totalPointFuture = countUserTotalPointAtAGivenDate(request.getAccountId(), new Date());
        var pointFuture = getPoint();

        return CompletableFuture.allOf(accountFuture, postFuture, totalPointFuture, pointFuture)
                .thenCompose(all -> {
                    var account = accountFuture.join();
                    var post = postFuture.join();
                    var totalPoint = totalPointFuture.join();
                    var pointList = pointFuture.join();
                    Point point;

                    if(pointList.isEmpty()){
                        throw new AppException(ErrorCode.POINT_NOT_FOUND);
                    }
                    point = pointList.get(0);

                    DailyPoint newDailyPoint = new DailyPoint();
                    newDailyPoint.setCreatedDate(new Date());
                    newDailyPoint.setPoint(point);
                    newDailyPoint.setPost(post);
                    newDailyPoint.setAccount(account);
                    if(totalPoint + point.getPointPerPost() > point.getMaxPoint()){
                        newDailyPoint.setPointEarned(0);
                    }
                    else{
                        newDailyPoint.setPointEarned(point.getPointPerPost());
                    }

                    var dailyPointFuture = findDailyPointByAccountAndPost(account, post);

                    return dailyPointFuture.thenCompose(dailyPoint -> {
                        if(dailyPoint != null){
                            throw new AppException(ErrorCode.DAILY_POINT_ALREADY_EXIST);
                        }

                        return CompletableFuture.completedFuture(dailyPointMapper.toDailyPointResponse(
                                unitOfWork.getDailyPointRepository().save(newDailyPoint)
                        ));
                    });
                });
    }

    @Async("AsyncTaskExecutor")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('USER')")
    public CompletableFuture<DailyPointResponse> getDailyPointById(UUID dailyPointId) {
        var dailyPointFuture = findDailyPointById(dailyPointId);

        return dailyPointFuture.thenApply(dailyPointMapper::toDailyPointResponse);
    }

    @Async("AsyncTaskExecutor")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('USER')")
    public CompletableFuture<List<DailyPointResponse>> getAllDailyPoints(int page, int perPage, UUID accountId, String givenDate) {
        var accountFuture = accountId != null
                            ? findAccountById(accountId)
                            : CompletableFuture.completedFuture(null);

        return CompletableFuture.allOf(accountFuture).thenCompose(v -> {
            var account = accountFuture.join();

            Date date;
            if (givenDate != null && !givenDate.isEmpty()) {
                OffsetDateTime offsetDateTime = OffsetDateTime.parse(givenDate);
                date = Date.from(offsetDateTime.toInstant());
            } else {
                date = null;
            }

            var list = unitOfWork.getDailyPointRepository().findAll().stream()
                    .filter(dailyPoint -> !dailyPoint.getAccount().equals(account))
                    .filter(dailyPoint -> date == null || dailyPoint.getCreatedDate().compareTo(date) == 0)
                    .map(dailyPointMapper::toDailyPointResponse)
                    .toList();

            var paginatedList = paginationUtils.convertListToPage(page, perPage, list);

            return CompletableFuture.completedFuture(paginatedList);
        });
    }

    @Async("AsyncTaskExecutor")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<DailyPointResponse> deleteDailyPointPoint(UUID dailyPointId){
        var dailyPointFuture = findDailyPointById(dailyPointId);

        return CompletableFuture.allOf(dailyPointFuture)
                .thenCompose(all -> {
                    var dailyPoint = dailyPointFuture.join();

                    unitOfWork.getDailyPointRepository().delete(dailyPoint);

                    return CompletableFuture.completedFuture(dailyPoint);
                })
                .thenApply(dailyPointMapper::toDailyPointResponse);
    }

    @Async("AsyncTaskExecutor")
    private CompletableFuture<Double> countUserTotalPointAtAGivenDate(UUID accountId, Date givenDate){
        var accountFuture = findAccountById(accountId);
        return accountFuture.thenCompose(account ->
                unitOfWork.getDailyPointRepository().findByAccountAndCreatedDate(account, givenDate)
                        .thenCompose(dailyPoints -> {
                            double totalCount = 0;
                            if(dailyPoints == null || dailyPoints.isEmpty()){
                                totalCount = 0;
                            }
                            else{
                                for(DailyPoint dailyPoint : dailyPoints){
                                    totalCount += dailyPoint.getPointEarned();
                                }
                            }

                            return CompletableFuture.completedFuture(totalCount);
                        })
        );
    }
    @Async("AsyncTaskExecutor")
    private CompletableFuture<Account> findAccountById(UUID accountId) {
        return CompletableFuture.supplyAsync(() ->
                unitOfWork.getAccountRepository().findById(accountId)
                        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND))
        );
    }
    @Async("AsyncTaskExecutor")
    private CompletableFuture<Post> findPostById(UUID postId) {
        return CompletableFuture.supplyAsync(() ->
                unitOfWork.getPostRepository().findById(postId)
                        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND))
        );
    }
    @Async("AsyncTaskExecutor")
    private CompletableFuture<DailyPoint> findDailyPointById(UUID dailyPointId) {
        return CompletableFuture.supplyAsync(() ->
                unitOfWork.getDailyPointRepository().findById(dailyPointId)
                        .orElseThrow(() -> new AppException(ErrorCode.DAILY_POINT_NOT_FOUND))
        );
    }
    @Async("AsyncTaskExecutor")
    private CompletableFuture<List<Point>> getPoint() {
        return CompletableFuture.supplyAsync(() ->
                unitOfWork.getPointRepository().findAll().stream()
                        .toList());
    }
    @Async("AsyncTaskExecutor")
    private CompletableFuture<DailyPoint> findDailyPointByAccountAndPost(Account account, Post post){
        return unitOfWork.getDailyPointRepository().findByAccountAndPost(account, post);
    }
}
