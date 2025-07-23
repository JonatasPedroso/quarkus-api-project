package com.rethink.api.config;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Startup
public class TestConfig {
    
    static {
        System.setProperty("quarkus.test.continuous-testing", "enabled");
        System.setProperty("quarkus.test.include-pattern", "**/*Test.class");
        System.setProperty("quarkus.test.exclude-pattern", "");
        System.setProperty("quarkus.test.include-tags", "");
        System.setProperty("quarkus.test.exclude-tags", "");
        System.setProperty("quarkus.test.include-module-pattern", ".*");
        System.setProperty("quarkus.test.exclude-module-pattern", "");
        System.setProperty("quarkus.test.profile", "test");
        System.setProperty("quarkus.test.type", "unit");
        System.setProperty("quarkus.test.flat-class-path", "true");
    }
}