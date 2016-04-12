package pl.com.bottega.ecommerce.sales.application.api.handler;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;

public class AddProductCommandBuilder {
    private static final int DEFAULT_QUANTITY = 10;

    private Id orderId = Id.generate();
    private Id productId = Id.generate();
    private int quantity = DEFAULT_QUANTITY;

    public AddProductCommandBuilder() {

    }

    public AddProductCommandBuilder withOrderId(Id orderId) {
        this.orderId = orderId;
        return this;
    }

    public AddProductCommandBuilder withRandomOrderId() {
        this.orderId = Id.generate();
        return this;
    }

    public AddProductCommandBuilder withProductId(Id productId) {
        this.productId = productId;
        return this;
    }

    public AddProductCommandBuilder withRandomProductId() {
        this.productId = Id.generate();
        return this;
    }

    public AddProductCommandBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public AddProductCommandBuilder withDefaultQuantity() {
        this.quantity = DEFAULT_QUANTITY;
        return this;
    }

    public AddProductCommand build() {
        return new AddProductCommand(orderId, productId, quantity);
    }
}

