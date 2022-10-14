package com.adidas.tsar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncTransferExecutorConfiguration {

    @Bean(name = "asyncSingleThreadTaskExecutor")
    public AsyncListenableTaskExecutor asyncTransferSingleThreadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("AsyncTaskExecutor-");
        return executor;
    }

}
