package br.com.vss.resell_platform.service;

import br.com.vss.resell_platform.model.Item;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CommissionService {

    private final BigDecimal commission = new BigDecimal("0.10");

    public BigDecimal calculateCommission(Item item) {
        return item.getPrice().multiply(commission);
    }
}
