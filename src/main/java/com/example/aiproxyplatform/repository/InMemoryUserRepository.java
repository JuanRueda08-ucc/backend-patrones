package com.example.aiproxyplatform.repository;

import com.example.aiproxyplatform.model.PlanType;
import com.example.aiproxyplatform.model.UserAccount;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, UserAccount> store = new ConcurrentHashMap<>();

    @PostConstruct
    void initializeDemoUsers() {
        registerUser("user-free-1",       PlanType.FREE);
        registerUser("user-pro-1",        PlanType.PRO);
        registerUser("user-enterprise-1", PlanType.ENTERPRISE);
    }

    @Override
    public Optional<UserAccount> findByUserId(String userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public UserAccount save(UserAccount userAccount) {
        store.put(userAccount.getUserId(), userAccount);
        return userAccount;
    }

    @Override
    public List<UserAccount> findAll() {
        return new ArrayList<>(store.values());
    }

    // Creates a fresh UserAccount and persists it in the store
    private void registerUser(String userId, PlanType planType) {
        UserAccount account = new UserAccount(userId, planType);
        store.put(userId, account);
    }

    // Returns the first day of the month following the given date
    private LocalDate firstDayOfNextMonth(LocalDate from) {
        return from.plusMonths(1).withDayOfMonth(1);
    }
}
