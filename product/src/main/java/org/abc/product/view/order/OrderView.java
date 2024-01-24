package org.abc.product.view.order;

import org.abc.product.OrderStatus;
import org.abc.product.PaymentMode;
import org.abc.product.controller.order.OrderController;
import org.abc.product.model.order.Order;
import org.abc.product.model.product.Product;
import org.abc.product.view.common_view.View;
import org.abc.product.view.homepage.HomepageView;
import org.abc.singleton_scanner.SingletonScanner;
import org.abc.authentication.model.User;
import org.abc.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * <p>
 * Views the orders placed by the user.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class OrderView extends View {

    private static OrderView orderView;
    private static final OrderController ORDER_CONTROLLER = OrderController.getInstance();
    private static final Scanner SCANNER = SingletonScanner.getScanner();
    private static final HomepageView HOME_PAGE_VIEW = HomepageView.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(OrderView.class);

    /**
     * <p>
     * Default constructor of OrderView class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private OrderView() {}

    /**
     * <p>
     * Creates a single object of OrderView class and returns it.
     * </p>
     *
     * @return the single instance of OrderView class.
     */
    public static OrderView getInstance() {
        return Objects.isNull(orderView) ? orderView = new OrderView() : orderView;
    }

    /**
     * <p>
     * Shows the order details of the user.
     * </p>
     *
     * @param user Refers {@link User} whose orders will be shown.
     */
    public void viewAndCancelOrder(final User user) {
        final List<Order> orders = ORDER_CONTROLLER.getOrders(user.getId());

        if (Objects.isNull(orders) || orders.isEmpty()) {
            LOGGER.info(String.format("User id :%d - No orders found", user.getId()));
            HOME_PAGE_VIEW.showHomePage(user);
        } else {
            for (int i = 0; i < orders.size(); i++) {
                LOGGER.info(String.format("%d :[%s]\n", i + 1, orders.get(i)));
            }
            LOGGER.info("Enter the order no to cancel the order.[Press '$' to go back]");
            final int index = getChoice();

            toGoBack(index, user);

            if (orders.size() >= index) {
                final Order order = orders.get(index - 1) ;

                ORDER_CONTROLLER.cancelOrder(order);
                LOGGER.info(String.format("User id :%d Order id :%d - Your order has been cancelled",
                        user.getId(), order.getId()));

            } else {
                LOGGER.warn("Enter a valid number");
                viewAndCancelOrder(user);
            }
        }
    }

    /**
     * <p>
     * Places the order for the user.
     * </p>
     *
     * @param product Refers the product to be placed.
     * @param user Refers the current {@link User}
     */
    public void placeOrder(final Product product, final User user){
        final int productQuantity = getQuantity(user, product);
        final String address = getAddress(ORDER_CONTROLLER.getAllAddresses(user.getId()), user);
        final PaymentMode paymentMode = getPaymentMode(user);

        ORDER_CONTROLLER.addOrder(user.getId(), new Order.OrderBuilder(user.getId(), product.getId(), paymentMode)
                .setAddress(address).setQuantity(productQuantity).setTotalAmount(productQuantity * product.getPrice())
                .setProductName(product.toString()).setOrderStatus(OrderStatus.PLACED).build());
        product.setQuantity(product.getQuantity() - productQuantity);
        LOGGER.info(String.format("User Id :%d Product Id :%d - Order placed successfully", user.getId(), product.getId()));
    }

    /**
     * <p>
     * Gets the payment mode from the user.
     * </p>
     *
     * @param user Refers the current {@link User}
     */
    private PaymentMode getPaymentMode(final User user) {
        LOGGER.info("Choose your payment mode : \n1.Cash On Delivery\n2.Credit or Debit card\n3.Net Banking\n4.UPI");
        final int choice = getChoice();

        toGoBack(choice, user);

        switch (choice) {
            case 1 -> {
                return PaymentMode.CASH_ON_DELIVERY;
            }
            case 2 -> {
                return PaymentMode.CREDIT_OR_DEBIT_CARD;
            }
            case 3 -> {
                return PaymentMode.NET_BANKING;
            }
            case 4 -> {
                return PaymentMode.UPI;
            }
            default -> LOGGER.warn("Enter a valid choice");
        }

        return getPaymentMode(user);
    }

    /**
     * <p>
     * Gets the quantity of product needed to place the order.
     * </p>
     *
     * @param product Refers the product to be placed.
     * @param user Refers the current {@link User}
     * @return the quantity of product need to place the order.
     */
    private int getQuantity(final User user, final Product product) {
        LOGGER.info("Enter the quantity : [Press '$' to go back]");
        final String quantity = SCANNER.nextLine().trim();

        toGoBack(quantity, user);

        if (Validator.getInstance().isPositiveNumber(quantity) && Integer.parseInt(quantity) <= product.getQuantity()) {
            return Integer.parseInt(quantity);
        } else {
            LOGGER.warn(String.format("User id :%d Product id:%d - Enter a valid quantity. Available quantity :%d",
                    product.getId(), user.getId(), product.getQuantity()));

            return getQuantity(user, product);
        }
    }

    /**
     * <p>
     * Gets a address of the user.
     * </p>
     *
     * @param addresses Refers the addresses of the user.
     * @param user Refers the current {@link User}
     * @return the address of the user.
     */
    private String getAddress(final List<String> addresses, final User user) {
        if (Objects.isNull(addresses) || addresses.isEmpty()) {
            LOGGER.info("Enter a address : [Press '$' to go back]");
            final String newAddress = SCANNER.nextLine().trim();

            toGoBack(newAddress, user);
            ORDER_CONTROLLER.addAddress(user.getId(), newAddress);

            return newAddress;
        }
        showAllAddress(addresses);
        LOGGER.info("Press '1' to select the address or '2' to add new address");
        final int choice = getChoice();

        toGoBack(choice, user);

        switch (choice) {
            case 1 -> {
                LOGGER.info("Enter the index:");
                final String index = SCANNER.nextLine().trim();
                toGoBack(index, user);

                return ORDER_CONTROLLER.getAllAddresses(user.getId()).get(Integer.parseInt(index));
            }
            case 2 -> {
                LOGGER.info("Enter a new address:");
                final String newAddress = SCANNER.nextLine().trim();
                toGoBack(newAddress, user);
                ORDER_CONTROLLER.addAddress(user.getId(), newAddress);

                return newAddress;
            }
            default -> LOGGER.warn("Enter a valid choice");
        }

        return getAddress(addresses, user);
    }

    /**
     * <p>
     * Shows all the addresses of the user.
     * </p>
     *
     * @param addresses Refers the addresses of the user.
     */
    private void showAllAddress(final List<String> addresses) {
        for (int i = 0; i < addresses.size(); i++) {
            LOGGER.info(String.format("%d :[%s]\n", i + 1, addresses.get(i)));
        }
    }

    /**
     * <p>
     * Goes back to the previous page.
     * </p>
     *
     * @param user Refers the current {@link User}.
     * @param input Refers the input given by the user
     */
    private void toGoBack(final int input, final User user) {
        if (-1 == input) {
            HOME_PAGE_VIEW.showHomePage(user);
        }
    }

    /**
     * <p>
     * Goes back to the previous page.
     * </p>
     *
     * @param user Refers the current {@link User}.
     * @param input Refers the input given by the user
     */
    private void toGoBack(final String input, final User user) {
        if (Validator.getInstance().checkToGoBack(input)) {
            HOME_PAGE_VIEW.showHomePage(user);
        }
    }
}
