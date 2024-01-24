package org.abc.product.service.inventory.impl;

import org.abc.product.model.inventory.Inventory;
import org.abc.product.model.product.Clothes;
import org.abc.product.model.product.Laptop;
import org.abc.product.model.product.Mobile;
import org.abc.product.model.product.Product;
import org.abc.product.service.inventory.InventoryService;
import org.abc.product.ProductCategory;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Provides the service for the Inventory. Stores all the products.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class InventoryServiceImpl implements InventoryService {

    private static InventoryService inventoryService;
    private  final Inventory<Mobile> MOBILE_INVENTORY = new Inventory<>();
    private  final Inventory<Laptop> LAPTOP_INVENTORY = new Inventory<>();
    private  final Inventory<Clothes> CLOTHES_INVENTORY = new Inventory<>();

    /**
     * <p>
     * Default constructor of InventoryServiceImpl class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private InventoryServiceImpl() {}

    /**
     * <p>
     * Creates a single object of InventoryServiceImpl class and returns it.
     * </p>
     *
     * @return the single instance of InventoryController class.
     */
    public static InventoryService getInstance() {
        return Objects.isNull(inventoryService) ? inventoryService = new InventoryServiceImpl() : inventoryService;
    }

    /**
     * <p>
     * Adds the given products to the inventory.
     * </p>
     *
     * @param products Refers the {@link Product} to be added.
     */
    @Override
    public void addItem(final List<Product> products) {
        for (final Product product : products) {
            switch (product.getProductCategory()) {
                case MOBILE -> MOBILE_INVENTORY.add((Mobile) product);
                case LAPTOP -> LAPTOP_INVENTORY.add((Laptop) product);
                case CLOTHES -> CLOTHES_INVENTORY.add((Clothes) product);
            }
        }
    }

    /**
     * <p>
     * Removes the given item from the inventory.
     * </p>
     *
     * @param productId Refers the id of the {@link Product} to be removed.
     * @param productCategory Refers the {@link ProductCategory}
     */
    @Override
    public void removeItem(final int productId, final ProductCategory productCategory) {
        switch (productCategory) {
            case MOBILE -> MOBILE_INVENTORY.remove(productId);
            case LAPTOP -> LAPTOP_INVENTORY.remove(productId);
            case CLOTHES -> CLOTHES_INVENTORY.remove(productId);
        }
    }

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @return all the {@link Product} from the inventory.
     */
    @Override
    public List<? extends Product> getItemsByCategory(final ProductCategory productCategory) {
        return switch (productCategory) {
            case MOBILE -> MOBILE_INVENTORY.get();
            case LAPTOP -> LAPTOP_INVENTORY.get();
            case CLOTHES -> CLOTHES_INVENTORY.get();
        };
    }
}
