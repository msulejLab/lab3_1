package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
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
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class AddProductCommandHandlerTest {
    private AddProductCommandHandler addProductCommandHandler;

    private ReservationRepositoryStub reservationRepository;

    public Id availableProductId = Id.generate();
    public Id notAvailableProductId = Id.generate();
    public Id suggestedProductId = Id.generate();


    @Before
    public void setUp() {
        addProductCommandHandler = new AddProductCommandHandler();

        reservationRepository = new ReservationRepositoryStub();

        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.load(availableProductId))
                .thenReturn(new Product(availableProductId, new Money(50), "Swordfish", ProductType.FOOD));
        when(productRepository.load(notAvailableProductId))
                .thenReturn(new Product(availableProductId, new Money(70), "Trout", ProductType.FOOD));


        // loadClient() fake
        SystemContext systemContext = mock(SystemContext.class);
        when(systemContext.getSystemUser()).thenReturn(new SystemUser(Id.generate()));

        ClientRepository clientRepository = mock(ClientRepository.class);
        when(clientRepository.load(any(Id.class))).thenReturn(new Client());

        // Suggested product
        Product suggestedProduct = new Product(suggestedProductId, new Money(100), "Shark", ProductType.FOOD);
        SuggestionService suggestionService = mock(SuggestionService.class);
        when(suggestionService.suggestEquivalent(any(Product.class), any(Client.class))).thenReturn(suggestedProduct);

        addProductCommandHandler.setReservationRepository(reservationRepository);
        addProductCommandHandler.setProductRepository(productRepository);
        addProductCommandHandler.setClientRepository(clientRepository);
        addProductCommandHandler.setSuggestionService(suggestionService);
        addProductCommandHandler.setSystemContext(systemContext);
    }


    @Test
    public void handleMethodShouldSaveReservationToReservationRepository() {
        reservationRepository.clear();

        AddProductCommand addProductCommand = new AddProductCommand(Id.generate(), availableProductId, 10);
        addProductCommandHandler.handle(addProductCommand);

        assertThat(reservationRepository.size(), is(1));
    }
}
