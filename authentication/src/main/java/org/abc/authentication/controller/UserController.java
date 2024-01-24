package org.abc.authentication.controller;

import org.abc.authentication.model.User;
import org.abc.authentication.service.UserService;
import org.abc.authentication.service.impl2.UserServiceImpl;

import java.util.Objects;

/**
 * <p>
 * Interacts between UserView and UserService for creating new user and getting existing user for login.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class UserController {

    private static UserController userController;
    private static final UserService USER_SERVICE = UserServiceImpl.getInstance();

    /**
     * <p>
     * Default constructor of UserController class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private UserController() {}

    /**
     * <p>
     * Creates a single object of UserController class and returns it.
     * </p>
     *
     * @return the single instance of UserController class.
     */
    public static UserController getInstance() {
        return Objects.isNull(userController) ? userController = new UserController() : userController;
    }

    /**
     * <p>
     * Checks if the user already exists, if not then creates a new user.
     * </p>
     *
     * @return true if the user created or false if user already exists.
     * @param user Refers the {@link User}to be created.
     */
    public boolean createUser(final User user) {
        return USER_SERVICE.createUser(user);
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
    public User getUser(final String emailIdOrMobileNumber, final String password) {
        return USER_SERVICE.getUser(emailIdOrMobileNumber, password);
    }

    /**
     * <p>
     * Updates the details of the user.
     * </p>
     *
     * @param user Refers the current {@link User}.
     */
    public void updateDetails(final User user) {
        USER_SERVICE.updateDetails(user);
    }

    /**
     * <p>
     * Gets the user by id.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return {@link User}.
     */
    public User getUserById(final int userId) {
        return USER_SERVICE.getUserById(userId);
    }
}
