package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.exceptions.ItemNotAvailableException;
import br.com.vss.resell_platform.model.Item;
import br.com.vss.resell_platform.model.Transaction;
import br.com.vss.resell_platform.model.User;
import br.com.vss.resell_platform.repository.TransactionRepository;
import br.com.vss.resell_platform.util.ItemStatus;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final CommissionService commissionService;
    private final TransactionRepository transactionRepository;

    public TransactionService(CommissionService commissionService, TransactionRepository transactionRepository) {
        this.commissionService = commissionService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void purchaseItem(User buyer, User seller, Item item) {

        BigDecimal commission = commissionService.calculateCommission(item);

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new ItemNotAvailableException();
        }

        Transaction transaction = new Transaction(seller, buyer, item);
        transactionRepository.save(transaction);

        seller.setBalance(seller.getBalance().add(item.getPrice().subtract(commission)));

        item.setStatus(ItemStatus.SOLD);
    }

}
