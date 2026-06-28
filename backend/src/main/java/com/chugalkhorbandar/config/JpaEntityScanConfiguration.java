package com.chugalkhorbandar.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = "com.chugalkhorbandar.adapters.persistence.postgres.entity")
public class JpaEntityScanConfiguration {}
