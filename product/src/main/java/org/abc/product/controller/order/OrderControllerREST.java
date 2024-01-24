package org.abc.product.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.abc.product.model.order.Order;
import org.abc.product.service.order.OrderServiceREST;
import org.abc.product.service.order.impl2.OrderServiceImpl;
import org.abc.product.exceptions.MethodNotFoundException;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.PATCH;

import java.util.Objects;

/**
 * <p>
 * Interacts between OrderView and OrderService for adding , viewing and cancelling orders.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/")
public class OrderControllerREST {

    private static OrderControllerREST orderController;
    private static final OrderServiceREST ORDER_SERVICE = OrderServiceImpl.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.byProvider(HibernateValidator.class)
            .configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory().getValidator();
//    private final ExecutableValidator executableValidator = Validation.buildDefaultValidatorFactory()
//            .getValidator().forExecutables();
    /**
     * <p>
     * Default constructor of OrderController class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private OrderControllerREST() {}

    /**
     * <p>
     * Creates a single object of OrderController class and returns it.
     * </p>
     *
     * @return the single instance of OrderController class.
     */
    public static OrderControllerREST getInstance() {
        return Objects.isNull(orderController) ? orderController = new OrderControllerREST() : orderController;
    }

    /**
     * <p>
     * Gets all the orders placed by the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @return  all the {@link Order} of the user.
     */
    @Path("/getOrders/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public ObjectNode getOrders(@PathParam("userId") @Positive final int userId,
                                 @QueryParam("page") @Positive final int page,
                                 @QueryParam("limit") @Positive final int limit) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();
        final Object[] parameterValues = {userId, page, limit};
//
//        try {
//            executableValidator.validateParameters(this, this.getClass()
//                            .getMethod("getOrders", int.class, int.class, int.class), parameterValues)
//                    .stream().forEach(violation -> violationsInJson
//                            .put(violation.getPropertyPath().toString(), violation.getMessage()));
//        } catch (NoSuchMethodException e) {
//            throw new MethodNotFoundException(e.getMessage());
//        }

        return violationsInJson.isEmpty()
                ? objectMapper.valueToTree(ORDER_SERVICE.getOrders(userId, page, limit))
                : violationsInJson;
    }

    /**
     * <p>
     * Adds the order of the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @param order Refers the {@link Order} to be added.
     */
    @Path("/add/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public ObjectNode addOrder(@PathParam("userId")@NotNull final int userId, final Order order) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();
        final Object[] parameterValues = {userId, order};
//
//        try {
//            executableValidator.validateParameters(this, this.getClass()
//                            .getMethod("addOrder", int.class, Order.class), parameterValues)
//                    .stream().forEach(violation -> violationsInJson
//                            .put(violation.getPropertyPath().toString(), violation.getMessage()));
//        } catch (NoSuchMethodException e) {
//            throw new MethodNotFoundException(e.getMessage());
//        }

        if (violationsInJson.isEmpty()) {
            ORDER_SERVICE.addOrder(userId, order);

            return objectMapper.createObjectNode().put("status", "Successfull");
        } else {
            return violationsInJson;
        }
    }

    /**
     * <p>
     * Cancels the order placed by the user.
     * </p>
     *
     * @param order Refers the {@link Order} to be cancelled.
     */
    @Path("/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    @PATCH
    public ObjectNode cancelOrder(final Order order) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();

        validator.validate(order).stream().forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        if (violationsInJson.isEmpty()) {
            ORDER_SERVICE.cancelOrder(order);

            return objectMapper.createObjectNode().put("status", "Successfull");
        } else {
            return violationsInJson;
        }
    }

    /**
     * <p>
     * Adds the address of the user.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @param address Refers the address to be added.
     */
    @Path("/addAddress/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public ObjectNode addAddress(@PathParam("userId") @Positive final int userId,
                                 @FormParam("address") @NotNull final String address) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();
        final Object[] parameterValues = {userId, address};

//        try {
//            executableValidator.validateParameters(this, this.getClass().getMethod("addAddress", int.class, String.class), parameterValues)
//                    .stream().forEach(violation -> violationsInJson.put(violation.getPropertyPath().toString(), violation.getMessage()));
//        } catch (NoSuchMethodException e) {
//            throw new MethodNotFoundException(e.getMessage());
//        }

        if (violationsInJson.isEmpty()) {
            ORDER_SERVICE.addAddress(userId, address);

            return objectMapper.createObjectNode().put("status","Successfull");
        } else {
            return  violationsInJson;
        }
    }

    /**
     * <p>
     * Gets all the addresses of the user.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return the list of all the address.
     */
    @Path("/getAddresses/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public ObjectNode getAllAddresses(@PathParam("userId") @Positive final int userId) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();

        validator.validate(userId).stream().forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        return violationsInJson.isEmpty()
                ? objectMapper.valueToTree(ORDER_SERVICE.getAllAddresses(userId))
                : violationsInJson;
    }
}




