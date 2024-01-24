package org.abc.product.controller.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.abc.product.ProductCategory;
import org.abc.product.exceptions.MethodNotFoundException;
import org.abc.product.model.cart.Cart;
import org.abc.product.service.cart.CartServiceREST;
import org.abc.product.service.cart.impl2.CartServiceImpl;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.util.Objects;

/**
 * <p>
 * Interacts between CartView and CartService for adding , removing from the cart of the user.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/")
public class CartControllerREST {

    private static CartControllerREST cartController;
    private static final CartServiceREST CART_SERVICE = CartServiceImpl.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.byProvider(HibernateValidator.class)
            .configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory().getValidator();
//    private final ExecutableValidator executableValidator = Validation.buildDefaultValidatorFactory()
//            .getValidator().forExecutables();

    /**
     * <p>
     * Default constructor of the CartController class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private CartControllerREST() {}

    /**
     * <p>
     * Creates a single object of CartController Class and returns it.
     * </p>
     *
     * @return returns the single instance of CartController Class.
     */
    public static CartControllerREST getInstance() {
        return Objects.isNull(cartController) ? cartController = new CartControllerREST() : cartController;
    }

    /**
     * <p>
     * Adds the product to the cart of the specified user.
     * </p>
     *
     * @param productId Refers the id of the product to be added
     * @param userId Refers the user id.
     * @param productCategory Refers the product category.
     * @return true if product added.
     */
    @Path("/add/{userId}/{category}/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public ObjectNode addItem(@PathParam("productId") @Positive final int productId,
                              @PathParam("userId") @Positive final int userId,
                              @PathParam("category") @NotNull final ProductCategory productCategory) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();
        final Object[] parameterValues = {productId, userId, productCategory};

//        try {
//            executableValidator.validateParameters(this, this.getClass()
//                            .getMethod("addItem", int.class, int.class, ProductCategory.class), parameterValues)
//                            .stream().forEach(violation -> violationsInJson
//                            .put(violation.getPropertyPath().toString(), violation.getMessage()));
//        } catch (NoSuchMethodException e) {
//            throw new MethodNotFoundException(e.getMessage());
//        }

        return violationsInJson.isEmpty()
                ? objectMapper.createObjectNode().put("status", CART_SERVICE.addItem(productId, userId, productCategory))
                : violationsInJson;
    }

    /**
     * <p>
     * Removes the product from the cart of the specified user.
     * </p>
     *
     * @param productId Refers the id of the product to be removed.
     * @param userId Refers the user id.
     */
    @Path("/remove/{userId}/{productId}")
    @DELETE
    public ObjectNode removeItem(@PathParam("productId") @Positive final int productId,
                                 @PathParam("userId")@Positive final int userId) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();
        final Object[] parameterValues = {productId, userId};

//        try {
//            executableValidator.validateParameters(this, this.getClass()
//                            .getMethod("removeItem", int.class, int.class), parameterValues)
//                            .stream().forEach(violation -> violationsInJson
//                            .put(violation.getPropertyPath().toString(), violation.getMessage()));
//        } catch (NoSuchMethodException e) {
//            throw new MethodNotFoundException(e.getMessage());
//        }

        if (violationsInJson.isEmpty()) {
            CART_SERVICE.removeItem(productId, userId);

            return objectMapper.createObjectNode().put("status", "Successfull");
        } else {
            return violationsInJson;
        }
    }

    /**
     * <p>
     * Gets the cart of the specified user id and returns it.
     * </p>
     *
     * @param userId Refers the user id.
     * @param page Refers the page number.
     * @param limit Refers the limit of data to show.
     * @return the {@link Cart} of the user.
     */
    @Path("/get/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public ObjectNode getCart(@PathParam("userId") @Positive final int userId,
                              @QueryParam("page") @Positive final int page,
                              @QueryParam("limit") @Positive final int limit) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();
        final Object[] parameterValues = {userId, page, limit};

//        try {
//            executableValidator.validateParameters(this, this.getClass()
//                            .getMethod("getCart", int.class, int.class, int.class), parameterValues)
//                            .stream().forEach(violation -> violationsInJson
//                            .put(violation.getPropertyPath().toString(), violation.getMessage()));
//        } catch (NoSuchMethodException e) {
//            throw new MethodNotFoundException(e.getMessage());
//        }

        return violationsInJson.isEmpty()
                ? objectMapper.valueToTree(CART_SERVICE.getCart(userId, page, limit))
                : violationsInJson;
    }
}



