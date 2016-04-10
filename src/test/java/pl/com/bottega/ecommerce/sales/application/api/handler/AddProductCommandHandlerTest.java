package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Before;
import org.junit.Test;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;
import pl.com.bottega.ecommerce.system.application.SystemUser;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class AddProductCommandHandlerTest {
    private AddProductCommandHandler addProductCommandHandler;

    private ReservationRepositoryStub reservationRepositoryStub;
    private ReservationRepository reservationRepositoryMock;

    private ProductRepository productRepository;

    private SuggestionService suggestionService;

    private Reservation reservation;

    private Product availableProduct, notAvailableProduct, suggestedProduct;

    public Id availableProductId = Id.generate();
    public Id notAvailableProductId = Id.generate();
    public Id suggestedProductId = Id.generate();


    @Before
    public void setUp() {
        addProductCommandHandler = new AddProductCommandHandler();

        reservationRepositoryStub = new ReservationRepositoryStub();
        reservationRepositoryMock = mock(ReservationRepository.class);

        // Reservation repository
        reservation = mock(Reservation.class);
        when(reservationRepositoryMock.load(any(Id.class))).thenReturn(reservation);

        // Product repository
        productRepository = mock(ProductRepository.class);

        // loadClient() fake
        SystemContext systemContext = mock(SystemContext.class);
        when(systemContext.getSystemUser()).thenReturn(new SystemUser(Id.generate()));

        ClientRepository clientRepository = mock(ClientRepository.class);
        when(clientRepository.load(any(Id.class))).thenReturn(new Client());

        // Suggested product
        suggestionService = mock(SuggestionService.class);

        addProductCommandHandler.setProductRepository(productRepository);
        addProductCommandHandler.setClientRepository(clientRepository);
        addProductCommandHandler.setSuggestionService(suggestionService);
        addProductCommandHandler.setSystemContext(systemContext);
    }


    @Test
    public void handleMethodShouldSaveReservationToReservationRepository() {
        setForStateTest();

        AddProductCommand addProductCommand = new AddProductCommand(Id.generate(), availableProductId, 10);
        addProductCommandHandler.handle(addProductCommand);

        assertThat(reservationRepositoryStub.size(), is(1));
    }

    @Test
    public void handleMethodShouldSaveReservationWithAvailableProduct() {
        setForStateTest();

        AddProductCommand addProductCommand = new AddProductCommand(Id.generate(), availableProductId, 10);
        addProductCommandHandler.handle(addProductCommand);

        Id addedProductId = getProductIdFromReservationRepository(0, 0);;

        assertThat(addedProductId, is(availableProductId));
    }

    @Test
    public void handleMethodShouldSaveReservationWithSuggestedProduct() {
        setForStateTest();

        AddProductCommand addProductCommand = new AddProductCommand(Id.generate(), notAvailableProductId, 10);
        addProductCommandHandler.handle(addProductCommand);

        Id addedProductId = getProductIdFromReservationRepository(0, 0);

        assertThat(addedProductId, is(suggestedProductId));
    }

    @Test
    public void handleMethodShouldAppendProductToExistingReservation() {
        setForStateTest();

        Id orderId = Id.generate();

        AddProductCommand addProductCommand = new AddProductCommand(orderId, availableProductId, 10);
        addProductCommandHandler.handle(addProductCommand);
        addProductCommand = new AddProductCommand(orderId, notAvailableProductId, 20);
        addProductCommandHandler.handle(addProductCommand);

        int reservedProductsAmount = -1;
        if (reservationRepositoryStub.reservations.size() > 0) {
            Reservation addedReservation = reservationRepositoryStub.reservations.get(0);
            reservedProductsAmount = addedReservation.getReservedProducts().size();
        }

        assertThat(reservedProductsAmount, is(2));
    }

    @Test
    public void handleMethodBehaviourTestWithAvailableProduct() {
        setForBehaviourTest();

        Id orderId = Id.generate();

        AddProductCommand addProductCommand = new AddProductCommand(orderId, availableProductId, 10);
        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepositoryMock).load(orderId);

        verify(productRepository).load(availableProductId);

        verify(availableProduct).isAvailable();

        verify(reservation).add(availableProduct, 10);

        verify(reservationRepositoryMock).save(reservation);
    }


    private Id getProductIdFromReservationRepository(int resIndex, int prodIndex) {
        if (reservationRepositoryStub.reservations.size() > resIndex) {
            Reservation addedReservation = reservationRepositoryStub.reservations.get(resIndex);

            if (addedReservation.getReservedProducts().size() > prodIndex) {
                return addedReservation.getReservedProducts().get(prodIndex).getProductId();
            }
        }

        return null;
    }

    private void setForStateTest() {
        reservationRepositoryStub.clear();
        addProductCommandHandler.setReservationRepository(reservationRepositoryStub);

        availableProduct = new Product(availableProductId, new Money(50), "Swordfish", ProductType.FOOD);
        when(productRepository.load(availableProductId)).thenReturn(availableProduct);

        notAvailableProduct = new Product(notAvailableProductId, new Money(70), "Trout", ProductType.FOOD);
        notAvailableProduct.markAsRemoved();
        when(productRepository.load(notAvailableProductId)).thenReturn(notAvailableProduct);

        suggestedProduct = new Product(suggestedProductId, new Money(100), "Shark", ProductType.FOOD);
        when(suggestionService.suggestEquivalent(any(Product.class), any(Client.class))).thenReturn(suggestedProduct);
    }

    private void setForBehaviourTest() {
        addProductCommandHandler.setReservationRepository(reservationRepositoryMock);

        availableProduct = mock(Product.class);
        when(availableProduct.isAvailable()).thenReturn(true);
        when(productRepository.load(availableProductId)).thenReturn(availableProduct);

        notAvailableProduct = mock(Product.class);
        when(notAvailableProduct.isAvailable()).thenReturn(false);
        when(productRepository.load(notAvailableProductId)).thenReturn(notAvailableProduct);

        suggestedProduct = mock(Product.class);
        when(suggestionService.suggestEquivalent(
                any(Product.class), any(Client.class))).thenReturn(suggestedProduct);
    }
}
