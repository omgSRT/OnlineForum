package com.FA24SE088.OnlineForum.repository.Repository;

import com.FA24SE088.OnlineForum.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {
}