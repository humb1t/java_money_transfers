package org.nipu.jmt.account;

import java.math.BigDecimal;

/**
 * Provides an ability to deserialize Json String into account.
 *
 * @author Nikita_Puzankov
 */
public class JsonAsAccountDetails {
    private String customerName;
    private BigDecimal overdraft;

    public JsonAsAccountDetails() {
    }

    public JsonAsAccountDetails(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getOverdraft() {
        return overdraft;
    }

    public void setOverdraft(BigDecimal overdraft) {
        this.overdraft = overdraft;
    }
}
