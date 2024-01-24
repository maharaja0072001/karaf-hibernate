package org.abc.product.controller.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.abc.product.ProductCategory;
import org.abc.product.model.product.Product;
import org.abc.product.service.inventory.InventoryServiceREST;
import org.abc.product.service.inventory.impl2.InventoryServiceImpl;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Interacts between InventoryView and InventoryService for adding, removing and retrieving products from inventory.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/")
public class InventoryControllerREST {

    private static InventoryControllerREST inventoryController;
    private static final InventoryServiceREST INVENTORY = InventoryServiceImpl.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.byProvider(HibernateValidator.class)
            .configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory().getValidator();
//    private final ExecutableValidator executableValidator = Validation.buildDefaultValidatorFactory()
//            .getValidator().forExecutables();

    /**
     * <p>
     * Default constructor of InventoryController class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private InventoryControllerREST() {}

    /**
     * <p>
     * Creates a single object of InventoryController class and returns it.
     * </p>
     *
     * @return the single instance of InventoryController class.
     */
    public static InventoryControllerREST getInstance() {
        return Objects.isNull(inventoryController) ? inventoryController = new InventoryControllerREST() : inventoryController;
    }

    /**
     * <p>
     * Adds the given products to the inventory.
     * </p>
     *
     * @param products the products to be added.
     */
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public ObjectNode addItemToInventory(@Valid final List<Product> products) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();

        validator.validate(products).forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        if (violationsInJson.isEmpty()) {
            INVENTORY.addItem(products);

            return objectMapper.createObjectNode().put("status","Successfull");
        } else {
            return  violationsInJson;
        }
    }

    /**
     * <p>
     * Removes the given item from the inventory.
     * </p>
     *
     * @param productId Refers the id of the product to be removed.
     */
    @Path("/remove/{category}/{id}")
    @DELETE
    public ObjectNode removeItemFromInventory(@PathParam("id") @Positive final int productId,
                                              @PathParam("category")@NotNull final ProductCategory productCategory) {
        final Object[] parameterValues = {productId, productCategory};
        final ObjectNode violationsInJson = objectMapper.createObjectNode();

//        try {
//            executableValidator.validateParameters(this, this.getClass()
//                            .getMethod("removeItemFromInventory", int.class, ProductCategory.class), parameterValues).forEach(violation -> violationsInJson
//                            .put(violation.getPropertyPath().toString(), violation.getMessage()));
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException();
//        }

        if (violationsInJson.isEmpty()) {
            INVENTORY.removeItem(productId, productCategory);

            return objectMapper.createObjectNode().put("status", "Successfull");
        } else {
            return violationsInJson;
        }
    }

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @return all the {@link Product} from the inventory.
     */
    @Path("/getByCategory")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public ObjectNode getItemsByCategory(@QueryParam("category")final ProductCategory productCategory,
                                         @QueryParam("page")final int page,
                                         @QueryParam("limit")final int limit) {
        final Object[] parameterValues = {page, limit, productCategory};
        final ObjectNode violationsInJson = objectMapper.createObjectNode();
//
//        try {
//            executableValidator.validateParameters(this, this.getClass()
//                            .getMethod("getItemsByCategory", ProductCategory.class, int.class, int.class), parameterValues).forEach(violation -> violationsInJson
//                            .put(violation.getPropertyPath().toString(), violation.getMessage()));
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException();
//        }

        if (violationsInJson.isEmpty()) {
            return objectMapper.valueToTree(INVENTORY.getItemsByCategory(productCategory, page, limit));
        } else {
            return violationsInJson;
        }
    }
}
