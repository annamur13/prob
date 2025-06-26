package com.haulmont.prob;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
@EnableTransactionManagement
@Configuration
public class ThesisDatabaseConfig {

    // Первая БД (для записи)
    @Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "thesisDataSource")
    @ConfigurationProperties("app.datasource.thesis")
    public DataSource thesisDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "thesisJdbcTemplate")
    public JdbcTemplate thesisJdbcTemplate(@Qualifier("thesisDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
