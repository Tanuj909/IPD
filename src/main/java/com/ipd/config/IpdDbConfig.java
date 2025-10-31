package com.ipd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.ipd.repository",
        entityManagerFactoryRef = "ipdEntityManagerFactory",
        transactionManagerRef = "ipdTransactionManager"
)
public class IpdDbConfig {
}
