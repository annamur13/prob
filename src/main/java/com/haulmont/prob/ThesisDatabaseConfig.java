package com.haulmont.prob;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ThesisDatabaseConfig {
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
