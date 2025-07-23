package com.rethink.api.repository;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Map;

public class RepositoryTestProfile implements QuarkusTestProfile {
    
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "quarkus.hibernate-orm.sql-load-script", "no-file",
            "quarkus.hibernate-orm.database.generation", "drop-and-create"
        );
    }
}