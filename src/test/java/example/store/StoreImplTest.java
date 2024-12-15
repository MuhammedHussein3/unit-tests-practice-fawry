package example.store;

import example.account.AccountManager;
import example.account.AccountManagerImpl;
import example.account.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StoreImplTest {

    private AccountManager accountManager;
    private Store store;
    private Product product;
    private Customer customer;

    @BeforeEach
    void setUP(){
        accountManager = mock(AccountManager.class);
        store = new StoreImpl(accountManager);
        product = mock(Product.class);
        customer = mock(Customer.class);
    }

    @Test
    void shouldThrowExceptionWhenQuantityForProductOutOfStock(){
        when(product.getQuantity()).thenReturn(0);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () ->  store.buy(product, customer)
        );

        assertEquals("Product out of stock", exception.getMessage());
        verify(product, times(1)).getQuantity();
        verify(accountManager, never()).withdraw(customer, 1000);
    }

    @Test
    void ShouldBuyThrowExceptionWhenWithdrawMoneyFails(){
        when(product.getQuantity()).thenReturn(10);
        when(accountManager.withdraw(customer, product.getPrice())).thenReturn("insufficient account balance");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> store.buy(product, customer)
        );

        assertEquals("Payment failure: insufficient account balance", exception.getMessage());
        verify(product, times(1)).getQuantity();
        verify(accountManager, times(1)).withdraw(customer, product.getPrice());
        verify(product, never()).setQuantity(anyInt());
    }

    @Test
    void shouldBuySuccessfullyWhenProductInStockAndPaymentSucceeds(){
        when(product.getQuantity()).thenReturn(10);
        when(accountManager.withdraw(customer, product.getPrice())).thenReturn("success");

        store.buy(product, customer);

        verify(product, times(2)).getQuantity();
        verify(accountManager, times(1)).withdraw(customer, product.getPrice());
        verify(product, times(1)).setQuantity(9);
    }
}
