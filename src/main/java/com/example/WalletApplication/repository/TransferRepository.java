package com.example.WalletApplication.repository;

import com.example.WalletApplication.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transaction, Long> {
}
