package com.ruoyi;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class FlywayInitializerOverride implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LoggerFactory.getLogger(FlywayInitializerOverride.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();

        if (!"true".equals(env.getProperty("spring.flyway.enabled"))) {
            log.info("Flyway disabled, skipping migration");
            return;
        }

        String url      = env.getProperty("spring.datasource.druid.master.url");
        String username = env.getProperty("spring.datasource.druid.master.username");
        String password = env.getProperty("spring.datasource.druid.master.password");
        String locations = env.getProperty("spring.flyway.locations", "classpath:db/migration");

        log.info("Flyway migration starting, locations: {}", locations);
        long start = System.currentTimeMillis();

        Flyway flyway = Flyway.configure()
                .dataSource(url, username, password)
                .locations(locations)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .validateOnMigrate(true)
                .encoding("UTF-8")
                .load();

        flyway.migrate();
        log.info("Flyway migration completed in {} ms", System.currentTimeMillis() - start);
    }
}
