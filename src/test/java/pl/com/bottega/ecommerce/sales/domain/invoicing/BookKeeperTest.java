package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class BookKeeperTest {
    private BookKeeper bookKeeper;

    // Mocks
    private RequestItem requestItem;
    private InvoiceRequest invoiceRequest;
    private TaxPolicy taxPolicy;

    @Before
    public void setUp() {
        InvoiceFactory invoiceFactory = new InvoiceFactory();
        bookKeeper = new BookKeeper(invoiceFactory);

        invoiceRequest = Mockito.mock(InvoiceRequest.class);

        ClientData clientData = mock(ClientData.class);
        when(invoiceRequest.getClientData()).thenReturn(clientData);

        Money money = new Money(0);
        Tax tax = new Tax(money, "test");

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.DRUG);

        requestItem = mock(RequestItem.class);
        when(requestItem.getTotalCost()).thenReturn(money);
        when(requestItem.getProductData()).thenReturn(productData);
        when(requestItem.getQuantity()).thenReturn(0);

        taxPolicy = Mockito.mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).
                thenReturn(tax);
    }

    @Test
    public void requestInvoiceMethodWithOneElementShouldReturnInvoiceWithOneElement() {
        List<RequestItem> requestItemList = getRequestItemList(1, requestItem);
        when(invoiceRequest.getItems()).thenReturn(requestItemList);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getItems().size(), is(1));
    }

    @Test
    public void requestInvoiceMethodWithoutAnyElementsShouldReturnInvoiceWithoutAnyElements() {
        List<RequestItem> requestItemList = getRequestItemList(0, requestItem);
        when(invoiceRequest.getItems()).thenReturn(requestItemList);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getItems().size(), is(0));
    }

    @Test
    public void requestInvoiceMethodWithTwoElementsShouldCallCalculateTaxTwoTimes() {
        List<RequestItem> requestItemList = getRequestItemList(2, requestItem);
        when(invoiceRequest.getItems()).thenReturn(requestItemList);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(2)).calculateTax(any(ProductType.class), any(Money.class));
    }

    private static List<RequestItem> getRequestItemList(int size, RequestItem requestItem) {
        List<RequestItem> requestItemList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            requestItemList.add(requestItem);
        }

        return requestItemList;
    }
}
