package com.example.aiproxyplatform.scheduler;

import com.example.aiproxyplatform.model.UserAccount;
import com.example.aiproxyplatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ResetScheduler {

    private static final Logger log = LoggerFactory.getLogger(ResetScheduler.class);

    private final UserRepository userRepository;

    public ResetScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 60_000)
    public void resetRateLimitWindows() {
        List<UserAccount> users = userRepository.findAll();
        users.forEach(user -> {
            user.resetRateLimitWindow();
            userRepository.save(user);
        });
        log.debug("Rate-limit windows reset for {} users.", users.size());
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetMonthlyQuotas() {
        LocalDate today = LocalDate.now();
        List<UserAccount> users = userRepository.findAll();

        long resetCount = users.stream()
                .filter(user -> !today.isBefore(user.getQuotaResetDate()))
                .peek(user -> {
                    log.info("Resetting monthly quota for user '{}' (plan: {}).",
                            user.getUserId(), user.getPlanType().name());
                    user.resetMonthlyQuota();
                    userRepository.save(user);
                })
                .count();

        if (resetCount > 0) {
            log.info("Monthly quota reset completed for {} user(s).", resetCount);
        }
    }
}
