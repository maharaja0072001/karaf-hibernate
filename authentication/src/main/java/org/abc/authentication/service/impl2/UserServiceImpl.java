package org.abc.authentication.service.impl2;

import org.abc.authentication.dao.impl.UserDAOImpl;
import org.abc.authentication.dao.UserDAO;
import org.abc.authentication.service.UserService;
import org.abc.authentication.model.User;
import org.abc.validation.Validator;

import java.util.Objects;

/**
 * <p>
 * Provides the implementation for the USerService.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class UserServiceImpl implements UserService {

    private static UserServiceImpl userService;
    private static final UserDAO USER_DAO = UserDAOImpl.getInstance();

    /**
     * <p>
     * Default constructor of the UserServiceImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private UserServiceImpl() {}

    /**
     * <p>
     * Creates a single object of UserServiceImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of UserServiceImpl Class.
     */
    public static UserService getInstance() {
        return Objects.isNull(userService) ? userService = new UserServiceImpl() : userService;
    }

    /**
     * <p>
     * Checks if the user already exists, if not then creates a new user.
     * </p>
     *
     * @return true if the user created or false if user already exists.
     * @param user Refers the {@link User}to be created.
     */
    @Override
    public boolean createUser(final User user) {
        return USER_DAO.createNewUser(user);
    }

    /**
     * <p>
     * Gets the existing user by the given credentials.
     * </p>
     *
     * @param emailIdOrMobileNumber Refers the mobile number or email id of the user.
     * @param password Refers the password of the user.
     * @return {@link User} if the credentials are correct and the user exists or null otherwise.
     */
    @Override
    public User getUser(final String emailIdOrMobileNumber, final String password) {
        return (Validator.getInstance().isValidEmail(emailIdOrMobileNumber)
                ? USER_DAO.getUser(emailIdOrMobileNumber, password, "select id, name, mobile_number, email, password from users where email=? and password=crypt(?,password)")
                : USER_DAO.getUser(emailIdOrMobileNumber, password, "select id, name, mobile_number, email, password from users where mobile_number=? and password=crypt(?,password)"));
    }

    /**
     * <p>
     * Updates the details of the user.
     * </p>
     *
     * @param user Refers the current {@link User}.
     */
    @Override
    public void updateDetails(final User user) {
        USER_DAO.updateDetails(user);
    }

    /**
     * <p>
     * Gets the user by id.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return {@link User}.
     */
    @Override
    public User getUserById(int userId) {
        return USER_DAO.getUserById(userId);
    }
}
