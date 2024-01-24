package org.abc.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.executable.ExecutableValidator;

import org.abc.authentication.exceptions.MethodNotFoundException;
import org.abc.authentication.model.User;
import org.abc.authentication.service.UserService;
import org.abc.authentication.service.impl2.UserServiceImpl;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

import java.util.Objects;

/**
 * <p>
 * Interacts between UserView and UserService for creating new user and getting existing user for login.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/")
public class UserControllerREST {

    private static UserControllerREST userController;
    private static final UserService USER_SERVICE = UserServiceImpl.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.byProvider(HibernateValidator.class)
            .configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory().getValidator();
//    private final ExecutableValidator executableValidator = Validation.buildDefaultValidatorFactory()
//            .getValidator().forExecutables();

    /**
     * <p>
     * Default constructor of UserController class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private UserControllerREST() {}

    /**
     * <p>
     * Creates a single object of UserController class and returns it.
     * </p>
     *
     * @return the single instance of UserController class.
     */
    public static UserControllerREST getInstance() {
        return Objects.isNull(userController) ? userController = new UserControllerREST() : userController;
    }

    /**
     * <p>
     * Checks if the user already exists, if not then creates a new user.
     * </p>
     *
     * @return true if the user created or false if user already exists.
     * @param user Refers the {@link User}to be created.
     */
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public ObjectNode createUser(final User user) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();

        validator.validate(user).stream().forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        return violationsInJson.isEmpty()
                ? objectMapper.createObjectNode().put("status", USER_SERVICE.createUser(user))
                : violationsInJson;
    }

    /**
     * <p>
     * Gets the existing user by the given credentials.
     * </p>
     *
     * @param emailIdOrMobileNumber Refers the mobile number or email id of the user
     * @param password Refers the password of the user.
     * @return {@link User} if the credentials are correct and the user exists or null otherwise.
     */
    @Path("/getUser")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public ObjectNode getUser(@FormParam("username")@NotNull final String emailIdOrMobileNumber,
                              @FormParam("password")@NotNull final String password) {

        final Object[] parameterValues = {emailIdOrMobileNumber, password};
        final ObjectNode violationsInJson = objectMapper.createObjectNode();

//        try {
//            executableValidator.validateParameters(this, this.getClass()
//                            .getMethod("getUser", String.class, String.class), parameterValues)
//                    .stream().forEach(violation -> violationsInJson
//                            .put(violation.getPropertyPath().toString(), violation.getMessage()));
//        } catch (NoSuchMethodException e) {
//            throw new MethodNotFoundException(e.getMessage());
//        }

        return  violationsInJson.isEmpty()
                ? objectMapper.valueToTree(USER_SERVICE.getUser(emailIdOrMobileNumber, password))
                : violationsInJson;
    }

    /**
     * <p>
     * Updates the details of the user.
     * </p>
     *
     * @param user Refers the current {@link User}.
     */
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    public ObjectNode updateDetails(final User user) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();

        validator.validate(user).stream().forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        if (violationsInJson.isEmpty()) {
            USER_SERVICE.updateDetails(user);

            return objectMapper.createObjectNode().put("status","Successfull");
        } else {
            return  violationsInJson;
        }
    }

    /**
     * <p>
     * Gets the user by id.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return {@link User}.
     */
    @Path("/getById/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public ObjectNode getUserById(@PathParam("userId")@Positive final int userId) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();

        validator.validate(userId).stream().forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        return violationsInJson.isEmpty()
                ? objectMapper.valueToTree(USER_SERVICE.getUserById(userId))
                : violationsInJson;
    }
}
