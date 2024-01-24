package org.abc.product.model.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import org.abc.product.ProductCategory;

import java.util.Objects;

/**
 * <p>
 * Represents the model for Mobile.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class Mobile extends Product {

    @NotNull(message = "Product model name can't be null")
    private final String model;

    @JsonCreator
    public Mobile(@JsonProperty("brandName") final String brandName,
                  @JsonProperty("model") final String model,
                  @JsonProperty("float") final float price,
                  @JsonProperty("quantity") final int quantity) {
        super(ProductCategory.MOBILE, price, brandName, quantity);
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return String.format("%s : %s - Rs : %.2f", super.getBrandName(), model, super.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBrandName(), model);
    }
}