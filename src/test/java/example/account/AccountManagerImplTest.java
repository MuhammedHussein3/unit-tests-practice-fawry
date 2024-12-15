package example.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AccountManagerImplTest {

    private static final int MAX_CREDIT = 5000;
    private static final double INITIAL_BALANCE = 1000d;
    private static final int WITHDRAW_AMOUNT = 2000;
    private static final int SMALL_AMOUNT = 50;
    private static final double EXPECTED_BALANCE_AFTER_SMALL_WITHDRAW = 950.0;    private AccountManager accountManager;

    @BeforeEach
    void setUp() {
        accountManager = new AccountManagerImpl();
    }




    @Test
    void shouldWithdrawMoneySuccessfullyWhenBalanceIsSufficient(){
        Customer customer = mockCustomerWithBalance(INITIAL_BALANCE);


        String result = accountManager.withdraw(customer, SMALL_AMOUNT);

        assertEquals("success", result);
        verify(customer, times(1)).getBalance();
        verify(customer, times(1)).setBalance(EXPECTED_BALANCE_AFTER_SMALL_WITHDRAW);

    }

    @Test
    void shouldFailWithdrawMoneyWithInsufficientBalanceAndCreditNotAllowed() {
        Customer customer = mockCustomerWithBalanceAndCreditAllowed(
                INITIAL_BALANCE,
                false
        );

        String result = accountManager.withdraw(customer, WITHDRAW_AMOUNT);

        assertEquals("insufficient account balance", result);
        verify(customer, times(1)).getBalance();
        verify(customer, times(1)).isCreditAllowed();
    }

    @Test
    void shouldFailWithdrawMoneyWhenExceedsMaxCreditAndNonVip(){
        Customer customer = mockCustomerWithBalanceAndCreditAllowedAndVip(
                INITIAL_BALANCE + 100,
                true,
                false
        );

        String result = accountManager.withdraw(customer, 2000);

        assertEquals("maximum credit exceeded", result);

    }

    @Test
    void shouldSuccessWithdrawWithVipCustomerWhenExceedingMaxCredit(){
        Customer customer = mockCustomerWithBalanceAndCreditAllowedAndVip(
                INITIAL_BALANCE + 100,
                true,
                true
        );

        String result = accountManager.withdraw(customer, 2000);

        assertEquals("success", result);
        verify(customer, times(1)).getBalance();
        verify(customer, times(1)).isCreditAllowed();
        verify(customer, times(1)).isVip();
    }

    @Test
    void successDepositAmount(){
        Customer customer = mockCustomerWithBalance(INITIAL_BALANCE);

        accountManager.deposit(customer, 500);
        verify(customer, times(1)).getBalance();
        verify(customer, times(1)).setBalance(1500d);
    }

    private Customer mockCustomerWithBalance(double blance){
        Customer customer = mock(Customer.class);
        when(customer.getBalance()).thenReturn(blance);
        return customer;
    }
    private Customer mockCustomerWithBalanceAndCreditAllowed(double balance, boolean creditAllowed){
        Customer customer = mockCustomerWithBalance(balance);
        when(customer.isCreditAllowed()).thenReturn(creditAllowed);
        return customer;
    }

    private Customer mockCustomerWithBalanceAndCreditAllowedAndVip(double balance, boolean creditAllowed, boolean Vip){
        Customer customer = mockCustomerWithBalanceAndCreditAllowed(
                balance,
                creditAllowed
        );
        when(customer.isVip()).thenReturn(Vip);
        return customer;
    }
}
