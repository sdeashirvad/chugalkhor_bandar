package com.chugalkhorbandar.config;



import org.springframework.beans.factory.InitializingBean;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Profile;

import org.springframework.util.StringUtils;



@Configuration

@Profile("postgres-dev")

public class PostgresDevConfigurationValidator implements InitializingBean {



    @Value("${spring.datasource.url:}")

    private String url;



    @Value("${spring.datasource.username:}")

    private String username;



    @Value("${spring.datasource.password:}")

    private String password;



    @Value("${POSTGRES_HOST:}")

    private String host;



    @Value("${POSTGRES_DB:}")

    private String database;



    @Override

    public void afterPropertiesSet() {

        if (containsUnresolvedPlaceholder(url)) {
            throw new PostgresConfigurationException(
                    "spring.datasource.url contains unresolved placeholders (e.g. ${POSTGRES_HOST}). "
                            + "Set POSTGRES_HOST, POSTGRES_DB, and related vars in backend/.env, "
                            + "or set POSTGRES_URL for a full JDBC URL override.");
        }

        if (!StringUtils.hasText(url)) {
            if (!StringUtils.hasText(host)) {
                throw new PostgresConfigurationException(
                        "postgres-dev profile requires POSTGRES_HOST or spring.datasource.url. "
                                + "Set POSTGRES_HOST in .env or configure spring.datasource.url.");
            }
            if (!StringUtils.hasText(database)) {
                throw new PostgresConfigurationException(
                        "postgres-dev profile requires POSTGRES_DB or spring.datasource.url. "
                                + "Set POSTGRES_DB in .env or configure spring.datasource.url.");
            }
        }

        if (!StringUtils.hasText(username)) {

            throw new PostgresConfigurationException(

                    "postgres-dev profile requires spring.datasource.username. "

                            + "Set POSTGRES_USER environment variable or configure it in application-postgres-dev.yml.");

        }

        if (!StringUtils.hasText(password)) {

            throw new PostgresConfigurationException(

                    "postgres-dev profile requires spring.datasource.password. "

                            + "Set POSTGRES_PASSWORD environment variable or configure it in application-postgres-dev.yml.");

        }

    }



    private static boolean containsUnresolvedPlaceholder(String value) {

        return value.contains("${");

    }

}

