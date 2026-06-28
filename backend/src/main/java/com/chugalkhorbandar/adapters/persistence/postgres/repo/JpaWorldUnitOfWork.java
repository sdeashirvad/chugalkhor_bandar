package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.domain.world.ports.WorldUnitOfWork;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public final class JpaWorldUnitOfWork implements WorldUnitOfWork {

    private final PlatformTransactionManager transactionManager;
    private TransactionStatus transactionStatus;

    public JpaWorldUnitOfWork(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void begin() {
        transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @Override
    public void commit() {
        if (transactionStatus != null && !transactionStatus.isCompleted()) {
            transactionManager.commit(transactionStatus);
        }
        transactionStatus = null;
    }

    @Override
    public void rollback() {
        if (transactionStatus != null && !transactionStatus.isCompleted()) {
            transactionManager.rollback(transactionStatus);
        }
        transactionStatus = null;
    }
}
