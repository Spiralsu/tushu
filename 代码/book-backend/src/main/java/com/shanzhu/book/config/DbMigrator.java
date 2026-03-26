package com.shanzhu.book.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DbMigrator {

    @Bean
    public CommandLineRunner migrate(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                log.info("Checking database schema for book_info.auditStatus...");
                // DEFAULT 1 means existing records will act as already approved.
                jdbcTemplate.execute("ALTER TABLE book_info ADD COLUMN auditStatus INT DEFAULT 1 COMMENT '审核状态 0:待审 1:通过 2:驳回'");
                log.info("Successfully added auditStatus column to book_info table.");
            } catch (Exception e) {
                // If the column already exists, this exception will be caught. We can safely ignore it.
                if (e.getMessage() != null && e.getMessage().contains("Duplicate column name")) {
                    log.info("Column auditStatus already exists in book_info, skip migration.");
                } else {
                    log.error("Database migration executed with warnings: {}", e.getMessage());
                }
            }
        };
    }
}
