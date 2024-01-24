package org.abc.authentication.view;

import org.abc.authentication.controller.UserController;
import org.abc.authentication.model.User;
import org.abc.pageview.PageViewer;
import org.abc.singleton_scanner.SingletonScanner;
import org.abc.validation.Validator;

import java.util.Objects;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * <p>
 * Handles user creation and user authentication.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class AuthenticationView {

    private static AuthenticationView authenticationView;
    private static final Scanner SCANNER = SingletonScanner.getScanner();
    private static final Logger LOGGER = LogManager.getLogger(AuthenticationView.class);
    private static final UserController USER_CONTROLLER = UserController.getInstance();
    private static final Validator VALIDATOR = Validator.getInstance();
    private PageViewer homePageView;

    /**
     * <p>
     * Default constructor of AuthenticationView class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private AuthenticationView() {}

    /**
     * <p>
     * Creates a single object of AuthenticationView class and returns it.
     * </p>
     *
     * @return the single instance of AuthenticationView class.
     */
    public static AuthenticationView getInstance() {
        return Objects.isNull(authenticationView) ? authenticationView = new AuthenticationView() : authenticationView;
    }

    /**
     * <p>
     * Shows the authentication page for user login and signup and responsible for creating new user and authenticating the existing user.
     * </p>
     */
    public void showAuthenticationPage() {
        LOGGER.info("1.Signup\n2.Login\n3.Exit\nEnter your choice:");

        switch (getChoice()) {
            case 1:
                signUp();
                break;
            case 2:
                login();
                break;
            case 3:
                exit();
            default:
               LOGGER.warn("Enter a valid choice");
               showAuthenticationPage();
        }
    }

    /**
     * <p>
     * Exits the application.
     * </p>
     */
    private void exit() {
        LOGGER.info("Exiting application");
        SCANNER.close();
        System.exit(0);
    }

    /**
     * <p>
     * Sets the HomePageView instance.
     * </p>
     *
     * @param homePageView Refers the HomePageView instance.
     */
    public void setHomePageView(final PageViewer homePageView) {
        this.homePageView = homePageView;
    }

    /**
     * <p>
     * Creates a new user by getting details from the user.
     * </p>
     *
     */
    private void signUp() {
        final User user = new User();

        LOGGER.info("Enter the details to create new user or press '$' to go back");
        user.setName(getName());
        user.setMobileNumber(getMobileNumber());
        user.setEmailId(getEmailId());
        user.setPassword(getPassword());

        if (USER_CONTROLLER.createUser(user)) {
            LOGGER.info("User created successfully");
            homePageView.viewPage(user);
        } else {
            LOGGER.info("User already exists. Please login");
            login();
        }
    }

    /**
     * <p>
     * Authenticates the existing user by the credentials entered by the user.
     * </p>
     */
    private void login() {
        LOGGER.info("Login : [Press '$' to go back] \n1.Mobile number\n2.Email Id \nEnter your choice:");
        final int choice = getChoice();

        toGoBack(choice);

        final String emailIdOrMobileNumber = switch (choice) {
            case 1 -> getMobileNumber();
            case 2 -> getEmailId();
            default -> {
                LOGGER.warn("Enter a valid choice");
                login();
                yield null;
            }
        };

        LOGGER.info("Enter the Password :");
        final String password = SCANNER.nextLine();

        toGoBack(password);
        final User user = USER_CONTROLLER.getUser(emailIdOrMobileNumber, password);

        if (Objects.nonNull(user)) {
            LOGGER.info("Login successful");
            homePageView.viewPage(user);
        } else {
            LOGGER.warn("Wrong credentials or user doesn't exist.");
            login();
        }
    }

    /**
     * <p>
     * Shows the profile of the user and updates the details chosen by the user.
     * </p>
     *
     * @param user Refers the current {@link User}
     */
    public void viewAndEditProfile(final User user) {
        LOGGER.info(String.format("User profile:\nName : %s\nEmail Id : %s\nMobile : +%s\nEnter 'yes' to edit details or press $ to go back",
                user.getName(), user.getEmailId(), user.getMobileNumber()));
        final String choice = SCANNER.nextLine().trim();

        if (VALIDATOR.checkToGoBack(choice)) {
            homePageView.viewPage(user);
        }

        if (VALIDATOR.hasAccessToProceed(choice)) {
            USER_CONTROLLER.updateDetails(editUserDetails(user));
            LOGGER.info(String.format("User Id : %d - User details updated", user.getId()));
        } else {
            viewAndEditProfile(user);
        }
    }

    /**
     * <p>
     * Updates the details of the current user.
     * </p>
     *
     * @param user Refers the current {@link User}
     */
    private User editUserDetails(final User user) {
        LOGGER.info("Do you want to edit name ?. Enter 'yes' to proceed or any key to no.");
        user.setName(VALIDATOR.hasAccessToProceed(SCANNER.nextLine().trim()) ? getName() : user.getName());
        LOGGER.info("Do you want to edit email id ?. Enter 'yes' to proceed or any key to no.");
        user.setEmailId(VALIDATOR.hasAccessToProceed(SCANNER.nextLine().trim()) ? getName() : user.getName());
        LOGGER.info("Do you want to edit mobile number ?. Enter 'yes' to proceed or any key to no.");
        user.setMobileNumber(VALIDATOR.hasAccessToProceed(SCANNER.nextLine().trim()) ? getName() : user.getName());
        LOGGER.info("Do you want to edit password ?. Enter 'yes' to proceed or any key to no.");
        user.setPassword(VALIDATOR.hasAccessToProceed(SCANNER.nextLine().trim()) ? getName() : user.getName());

        return user;
    }

    /**
     * <p>
     * Gets a valid name from the user and returns it.
     * </p>
     *
     * @return the name of the user.
     */
    private String getName() {
        LOGGER.info("Enter your name :");
        final String name = SCANNER.nextLine().trim();

        toGoBack(name);

        if (VALIDATOR.isValidName(name)) {
            return name;
        } else {
            LOGGER.warn("Entered Name is invalid");
        }

        return getName();
    }

    /**
     * <p>
     * Gets a valid password from the user and returns it.
     * </p>
     *
     * @return the password of the user.
     */
    private String getPassword() {
        LOGGER.info(String.join(" ", "Create a password:[Password should contain",
                "an uppercase, a lowercase, a special character and a digit. Minimum length is 8]"));
        final String password = SCANNER.nextLine().trim();

        toGoBack(password);

        if (VALIDATOR.isValidPassword(password)) {
            return password;
        } else {
            LOGGER.warn("Entered password is invalid");
        }

        return getPassword();
    }

    /**
     * <p>
     * Gets a valid email id from the user and returns it.
     * </p>
     *
     * @return the email id of the user.
     */
    private String getEmailId() {
        LOGGER.info("Enter your email id:");
        final String emailId = SCANNER.nextLine().trim();

        toGoBack(emailId);

        if (VALIDATOR.isValidEmail(emailId)) {
            return emailId;
        } else {
            LOGGER.warn("Entered email id is invalid.");
        }

        return getEmailId();
    }

    /**
     * <p>
     * Gets a valid mobile number from the user and returns it.
     * </p>
     *
     * @return the mobile number of the user.
     */
    private String getMobileNumber() {
        LOGGER.info("Choose your country code :\n1.America\n2.Australia\n3.China\n4.Germany\n5.India");
        final int availableCountryCode = 5;
        final int choice = getChoice();

        toGoBack(choice);

        if (availableCountryCode < choice) {
            LOGGER.warn("Invalid choice");
            return getMobileNumber();
        }

        LOGGER.info("Enter your mobile number with country code:");
        final String mobileNumber = SCANNER.nextLine().trim();

        toGoBack(mobileNumber);

        if (VALIDATOR.isValidMobileNumber(mobileNumber, choice)) {
            return mobileNumber;
        } else {
            LOGGER.warn("Entered mobile number is invalid.");
        }

        return getMobileNumber();
    }

    /**
     * <p>
     * Gets the choice from the user and returns it.
     * </p>
     * @return the choice of the user.
     */
    private int getChoice() {
        try {
            final String choice = SCANNER.nextLine().trim();

            if (VALIDATOR.checkToGoBack(choice)) {
                return -1;
            }

            if (Integer.parseInt(choice) <= 0) {
                LOGGER.warn("Invalid choice");

                return getChoice();
            }

            return Integer.parseInt(choice);
        } catch (final NumberFormatException exception) {
            LOGGER.warn("Enter a valid choice");
        }

        return getChoice();
    }

    /**
     * <p>
     * Goes back to the previous page.
     * </p>
     *
     * @param input Refers the input given by the user
     */
    private void toGoBack(final String input) {
        if (VALIDATOR.checkToGoBack(input)) {
            showAuthenticationPage();
        }
    }

    /**
     * <p>
     * Goes back to the previous page.
     * </p>
     *
     * @param input Refers the input given by the user
     */
    private void toGoBack(final int input) {
        if (-1 == input) {
            showAuthenticationPage();
        }
    }
}