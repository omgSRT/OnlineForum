package com.FA24SE088.OnlineForum.service;

import com.FA24SE088.OnlineForum.dto.request.ImageCreateRequest;
import com.FA24SE088.OnlineForum.dto.response.ImageResponse;
import com.FA24SE088.OnlineForum.dto.response.TopicNoCategoryResponse;
import com.FA24SE088.OnlineForum.entity.Image;
import com.FA24SE088.OnlineForum.entity.Post;
import com.FA24SE088.OnlineForum.exception.AppException;
import com.FA24SE088.OnlineForum.exception.ErrorCode;
import com.FA24SE088.OnlineForum.mapper.ImageMapper;
import com.FA24SE088.OnlineForum.repository.UnitOfWork.UnitOfWork;
import com.FA24SE088.OnlineForum.utils.PaginationUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
public class ImageService {
    final UnitOfWork unitOfWork;
    final ImageMapper imageMapper;
    final PaginationUtils paginationUtils;

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Async("AsyncTaskExecutor")
    public CompletableFuture<List<ImageResponse>> createImage(ImageCreateRequest request){
        var postFuture = findPostById(request.getPostId());

        return postFuture.thenCompose(post -> {
            List<Image> imageList = new ArrayList<>();
            for(String url : request.getUrls()){
                Image newImage = new Image();
                newImage.setUrl(url);
                newImage.setPost(post);
                unitOfWork.getImageRepository().save(newImage);
                imageList.add(newImage);
            }

            List<ImageResponse> imageResponseList = imageList.stream()
                    .map(imageMapper::toImageResponse)
                    .toList();

            return CompletableFuture.completedFuture(imageResponseList);
        });
    }
    @Async("AsyncTaskExecutor")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('USER')")
    public CompletableFuture<List<ImageResponse>> getAllImages(int page, int perPage, UUID postId) {
        var postFuture = postId != null
                ? findPostById(postId)
                : CompletableFuture.completedFuture(null);

        return postFuture.thenCompose(post -> {
            var list = unitOfWork.getImageRepository().findAll().stream()
                    .filter(image -> post == null || image.getPost().equals(post))
                    .map(imageMapper::toImageResponse)
                    .toList();

            var paginatedList = paginationUtils.convertListToPage(page, perPage, list);

            return CompletableFuture.completedFuture(paginatedList);
        });
    }
    @Async("AsyncTaskExecutor")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('USER')")
    public CompletableFuture<ImageResponse> getImageById(UUID imageId){
        var imageFuture = findImageById(imageId);

        return imageFuture.thenApply(imageMapper::toImageResponse);
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Async("AsyncTaskExecutor")
    public CompletableFuture<ImageResponse> deleteImageById(UUID imageId){
        var imageFuture = findImageById(imageId);

        return imageFuture.thenCompose(image -> {
                    unitOfWork.getImageRepository().delete(image);

                    return CompletableFuture.completedFuture(image);
                })
                .thenApply(imageMapper::toImageResponse);
    }

    @Async("AsyncTaskExecutor")
    private CompletableFuture<Post> findPostById(UUID postId) {
        return CompletableFuture.supplyAsync(() ->
                unitOfWork.getPostRepository().findById(postId)
                        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND))
        );
    }
    @Async("AsyncTaskExecutor")
    private CompletableFuture<Image> findImageById(UUID imageId) {
        return CompletableFuture.supplyAsync(() ->
                unitOfWork.getImageRepository().findById(imageId)
                        .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND))
        );
    }
}