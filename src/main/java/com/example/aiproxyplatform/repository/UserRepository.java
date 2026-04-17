package com.example.aiproxyplatform.repository;

import com.example.aiproxyplatform.model.UserAccount;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<UserAccount> findByUserId(String userId);

    UserAccount save(UserAccount userAccount);

    List<UserAccount> findAll();
}
