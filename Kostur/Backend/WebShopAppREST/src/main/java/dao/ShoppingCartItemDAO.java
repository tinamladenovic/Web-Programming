package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.Chocolate;
import beans.ShoppingCart;
import beans.ShoppingCartItem;

public class ShoppingCartItemDAO {
    private HashMap<Integer, ShoppingCartItem> shoppingCartItems = new HashMap<>();
    private String contextPath;
    private static final Logger LOGGER = Logger.getLogger(ShoppingCartItemDAO.class.getName());

    public ShoppingCartItemDAO(String contextPath) {
        this.contextPath = contextPath.endsWith(File.separator) ? contextPath : contextPath + File.separator;
        loadShoppingCartItems();
    }

    public Collection<ShoppingCartItem> findAll() {
        return shoppingCartItems.values();
    }

    public ShoppingCartItem findShoppingCartItem(Integer id) {
        return shoppingCartItems.get(id);
    }

    public void save(ShoppingCartItem shoppingCartItem) {
        shoppingCartItems.put(shoppingCartItem.getId(), shoppingCartItem);
        saveShoppingCartItems();
    }

    public void delete(Integer id) {
        shoppingCartItems.remove(id);
        saveShoppingCartItems();
    }

    private void loadShoppingCartItems() {
        File file = new File(contextPath + "shoppingCartItems.txt");
        LOGGER.log(Level.INFO, "Path to shoppingCartItems.txt: {0}", file.getAbsolutePath());

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                StringTokenizer st = new StringTokenizer(line, ";");
                Integer id = Integer.parseInt(st.nextToken().trim());
                Integer shoppingCartId = Integer.parseInt(st.nextToken().trim());
                Integer chocolateId = Integer.parseInt(st.nextToken().trim());

                ShoppingCart shoppingCart = loadShoppingCartById(shoppingCartId); // Implement this method
                Chocolate chocolate = loadChocolateById(chocolateId); // Implement this method

                ShoppingCartItem shoppingCartItem = new ShoppingCartItem(id, shoppingCart, chocolate, false);

                shoppingCartItems.put(id, shoppingCartItem);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading shopping cart items from file", e);
        }
    }

    private void saveShoppingCartItems() {
        File file = new File(contextPath + "shoppingCartItems.txt");
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            for (ShoppingCartItem item : shoppingCartItems.values()) {
                String line = String.join(";",
                        item.getId().toString(),
                        item.getShoppingCart().getId().toString(),
                        item.getChocolate().getId().toString()
                );
                out.write(line);
                out.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving shopping cart items to file", e);
        }
    }

    private ShoppingCart loadShoppingCartById(Integer shoppingCartId) {
        // Implement this method to load a ShoppingCart by its ID
        // This might involve calling another DAO
        return new ShoppingCart(shoppingCartId, null, null, 0.0);
    }

    private Chocolate loadChocolateById(Integer chocolateId) {
        // Implement this method to load a Chocolate by its ID
        // This might involve calling another DAO
        return new Chocolate(chocolateId.toString(), null, 0.0, null, null, null, 0.0, null, null, null, 0);
    }
}
