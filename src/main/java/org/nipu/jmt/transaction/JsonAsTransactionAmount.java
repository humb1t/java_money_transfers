package org.nipu.jmt.transaction;

import java.math.BigDecimal;

/**
 * Json representation of transaction amount.
 *
 * @author Nikita_Puzankov
 */
public class JsonAsTransactionAmount {
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
