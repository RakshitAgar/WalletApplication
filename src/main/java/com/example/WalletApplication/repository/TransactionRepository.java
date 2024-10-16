package com.example.WalletApplication.repository;

import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long walletId);
    List<Transaction> findByWalletIdAndType(Long walletId, TransactionType type);
}
