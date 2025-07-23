package com.rethink.api.repository;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@QuarkusTest
@TestProfile(RepositoryTestProfile.class)
public abstract class BaseRepositoryTest {
    
    @Inject
    protected EntityManager entityManager;
    
    protected void cleanDatabase() {
        entityManager.createQuery("DELETE FROM OrderItem").executeUpdate();
        entityManager.createQuery("DELETE FROM Order").executeUpdate();
        entityManager.createQuery("DELETE FROM Customer").executeUpdate();
        entityManager.createQuery("DELETE FROM Product").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }
}