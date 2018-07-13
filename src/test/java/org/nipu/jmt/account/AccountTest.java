package org.nipu.jmt.account;

import org.junit.Test;
import org.nipu.jmt.Result;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Test for {@link Account}.
 *
 * @author Nikita_Puzankov
 */
public class AccountTest {

    private final String testCustomerName = "test customer";
    private BigDecimal overdraft = BigDecimal.valueOf(100L);

    @Test
    public void withdrawMoney() {
        final Account account = new Account(testCustomerName, overdraft);
        final Result<Account, AccountError> result = account.withdrawMoney(BigDecimal.valueOf(100L));
        assertEquals(BigDecimal.valueOf(-100L), account.getBalance());
        assertTrue(result.getValue().isPresent());
        assertFalse(result.getError().isPresent());
    }

    @Test
    public void withdrawMoneyReachOverdraft() {
        final Account account = new Account(testCustomerName, overdraft);
        final Result<Account, AccountError> result = account.withdrawMoney(BigDecimal.valueOf(101L));
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertFalse(result.getValue().isPresent());
        assertTrue(result.getError().isPresent());
    }

    @Test
    public void withdrawNegativeAmountOfMoneyShouldReturnAnAccountError() {
        final Account account = new Account(testCustomerName, overdraft);
        final Result<Account, AccountError> result = account.withdrawMoney(BigDecimal.valueOf(-100L));
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertFalse(result.getValue().isPresent());
        assertTrue(result.getError().isPresent());
    }

    @Test
    public void putMoney() {
        final Account account = new Account(testCustomerName, overdraft);
        final Result<Account, AccountError> result = account.putMoney(BigDecimal.valueOf(100L));
        assertEquals(BigDecimal.valueOf(100L), account.getBalance());
        assertTrue(result.getValue().isPresent());
        assertFalse(result.getError().isPresent());
    }

    @Test
    public void putNegativeAmountOfMoneyShouldReturnAnAccountErro() {
        final Account account = new Account(testCustomerName, overdraft);
        final Result<Account, AccountError> result = account.putMoney(BigDecimal.valueOf(-100L));
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertFalse(result.getValue().isPresent());
        assertTrue(result.getError().isPresent());
    }

    @Test
    public void getBalance() {
        final Account account = new Account(testCustomerName, overdraft);
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }
}