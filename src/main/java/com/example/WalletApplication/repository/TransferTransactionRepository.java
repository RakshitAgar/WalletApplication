package com.example.WalletApplication.repository;

import com.example.WalletApplication.entity.Transaction;
import com.example.WalletApplication.entity.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferTransactionRepository extends JpaRepository<TransferTransaction, Long> {
    // Find transactions where the wallet is the sender
    List<TransferTransaction> findBySenderWalletId(Long senderWalletId);

    // Find transactions where the wallet is the receiver
    List<TransferTransaction> findByRecipientWalletId(Long receiverWalletId);
}
