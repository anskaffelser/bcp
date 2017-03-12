package no.difi.virksert.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author erlend
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {

    @Value("${virksert.scheduler.threads:10}")
    private int threads;

    @Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(threads);
    }
}
