package org.abc.product.controller.wishlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.abc.product.ProductCategory;
import org.abc.product.model.wishlist.Wishlist;
import org.abc.product.service.wishlist.WishlistServiceREST;
import org.abc.product.service.wishlist.impl2.WishlistServiceImpl;
import org.abc.product.exceptions.MethodNotFoundException;

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
 * Handles requests and responses from WishlistView clas and WishlistService class.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/")
public class WishlistControllerREST {

    private static WishlistControllerREST wishlistController;
    private static final WishlistServiceREST WISHLIST_SERVICE = WishlistServiceImpl.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.byProvider(HibernateValidator.class)
            .configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory().getValidator();
//    private final ExecutableValidator executableValidator = Validation.buildDefaultValidatorFactory()
//            .getValidator().forExecutables();

    /**
     * <p>
     * Default constructor of the WishlistController class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private WishlistControllerREST() {}

    /**
     * <p>
     * Creates a single object of WishlistController Class and returns it.
     * </p>
     *
     * @return returns the single instance of WishlistController Class.
     */
    public static WishlistControllerREST getInstance() {
        return Objects.isNull(wishlistController) ? wishlistController = new WishlistControllerREST() : wishlistController;
    }

    /**
     * <p>
     * Adds the product to the wishlist of the specified user.
     * </p>
     * @param productId Refers the id of the product to be added
     * @param userId Refers the user id.
     * @param productCategory Refers the product category.
     * @return the if product added to the wishlist.
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
//                    .stream().forEach(violation -> violationsInJson
//                            .put(violation.getPropertyPath().toString(), violation.getMessage()));
//        } catch (NoSuchMethodException e) {
//            throw new MethodNotFoundException(e.getMessage());
//        }

        return violationsInJson.isEmpty()
                ? objectMapper.createObjectNode().put("status", WISHLIST_SERVICE.addItem(productId, userId, productCategory))
                : violationsInJson;
    }

    /**
     * <p>
     * Removes the product from the wishlist of the specified user.
     * </p>
     *
     * @param productId Refers the id of the product to be removed.
     * @param userId Refers the user id.
     */
    @Path("/remove/{userId}/{productId}")
    @DELETE
    public ObjectNode removeItem(@PathParam("productId") @Positive final int productId,
                                 @PathParam("userId") @Positive final int userId) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();
        final Object[] parameterValues = {productId, userId};

//        try {
//            executableValidator.validateParameters(this, this.getClass()
//                            .getMethod("removeItem", int.class, int.class), parameterValues)
//                    .stream().forEach(violation -> violationsInJson
//                            .put(violation.getPropertyPath().toString(), violation.getMessage()));
//        } catch (NoSuchMethodException e) {
//            throw new MethodNotFoundException(e.getMessage());
//        }

        if (violationsInJson.isEmpty()) {
            WISHLIST_SERVICE.removeItem(productId, userId);

            return objectMapper.createObjectNode().put("status", "Successfull");
        } else {
            return violationsInJson;
        }
    }

    /**
     * <p>
     * Gets the wishlist of the specified user id and returns it.
     * </p>
     *
     * @param userId Refers the user id who owns the cart.
     * @return the {@link Wishlist} of the user.
     */
    @Path("/get/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public ObjectNode getWishlist(@PathParam("userId") @Positive final int userId,
                                  @QueryParam("page") @Positive final int page,
                                  @QueryParam("limit") @Positive final int limit) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();
        final Object[] parameterValues = {userId, page, limit};
//
//        try {
//            executableValidator.validateParameters(this, this.getClass()
//                            .getMethod("getWishlist", int.class, int.class, int.class), parameterValues)
//                    .stream().forEach(violation -> violationsInJson
//                            .put(violation.getPropertyPath().toString(), violation.getMessage()));
//        } catch (NoSuchMethodException e) {
//            throw new MethodNotFoundException(e.getMessage());
//        }

        return violationsInJson.isEmpty()
                ? objectMapper.valueToTree(WISHLIST_SERVICE.getWishlist(userId, page, limit))
                : violationsInJson;
    }
}
