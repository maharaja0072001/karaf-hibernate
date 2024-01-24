package org.abc.authentication.dao.impl;

import org.abc.authentication.dao.UserDAO;
import org.abc.authentication.exceptions.UserNotFoundException;
import org.abc.authentication.exceptions.UpdateActionFailedException;
import org.abc.authentication.model.User;
import org.abc.dbconnection.connection.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * <p>
 * Stores and gets the user details from the database.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class UserDAOImpl implements UserDAO {

    private static UserDAOImpl userDAO;

    /**
     * <p>
     * Default constructor of the UserDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private UserDAOImpl() {}

    /**
     * <p>
     * Creates a single object of UserDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of UserDAOImpl class.
     */
    public static UserDAOImpl getInstance() {
        return Objects.isNull(userDAO) ? userDAO = new UserDAOImpl() : userDAO;
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
    public boolean createNewUser(final User user) {
        try (PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(String.join(" ", "insert into",
                "users(name, mobile_number, email, password) values(?, ?, ?, crypt(?,gen_salt('bf'))) returning id"))) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getMobileNumber());
            preparedStatement.setString(3, user.getEmailId());
            preparedStatement.setString(4, user.getPassword());
            final ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            user.setId(resultSet.getInt(1));

            return true;
        } catch (final SQLException exception) {
            return false;
        }
    }

    /**
     * <p>
     * Gets the existing user by the given credentials.
     * </p>
     *
     * @param emailIdOrMobileNumber Refers the mobile number or email id of the user
     * @param password Refers the password of the user.
     * @param query Refers the query to be executed.
     * @return {@link User} if the credentials are correct or null otherwise.
     */
    @Override
    public User getUser(final String emailIdOrMobileNumber, final String password, final String query) {
        try (PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, emailIdOrMobileNumber);
            preparedStatement.setString(2, password);

            return getUserObjectFromResultSet(preparedStatement.executeQuery());
        } catch (final SQLException exception) {
            throw new UserNotFoundException(exception.getMessage());
        }
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
        try (PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(String.join(" ","update",
                "users set name=? ,email=?, password=crypt(?,gen_salt('bf')), mobile_number=? where id =?"))) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmailId());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getMobileNumber());
            preparedStatement.setInt(5, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UpdateActionFailedException(exception.getMessage());
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
    @Override
    public User getUserById(int userId) {
        try (PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(String.join(" ","select",
                "id, name, mobile_number, email, password from users where id =?"))) {
            preparedStatement.setInt(1, userId);

            return getUserObjectFromResultSet(preparedStatement.executeQuery());
        } catch (final SQLException exception) {
            throw new UserNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the user object from the provided resultset.
     * </p>
     *
     * @param resultSet Refers the Resultset.
     * @return {@link User}.
     */
    private User getUserObjectFromResultSet(final ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }
        final User user = new User();

        user.setId(resultSet.getInt(1));
        user.setName(resultSet.getString(2));
        user.setMobileNumber(resultSet.getString(3));
        user.setEmailId(resultSet.getString(4));
        user.setPassword(resultSet.getString(5));

        return user;
    }
}
