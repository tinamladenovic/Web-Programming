package dao;

import beans.ShoppingCart;
import beans.ShoppingCartItem;
import beans.User;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShoppingCartDAO {
    private Map<Integer, ShoppingCart> shoppingCarts = new HashMap<>();
    private String contextPath;
    private static final Logger LOGGER = Logger.getLogger(ShoppingCartDAO.class.getName());

    public ShoppingCartDAO(String contextPath) {
        this.contextPath = contextPath.endsWith(File.separator) ? contextPath : contextPath + File.separator;
        loadShoppingCarts();
    }

    public ShoppingCart find(Integer id) {
        return shoppingCarts.get(id);
    }

    public Map<Integer, ShoppingCart> findAll() {
        return shoppingCarts;
    }

    public ShoppingCart create(ShoppingCart newShoppingCart) {
        Integer id = generateUniqueId();
        newShoppingCart.setId(id);
        shoppingCarts.put(id, newShoppingCart);
        saveShoppingCarts();
        return newShoppingCart;
    }

    public ShoppingCart update(Integer id, ShoppingCart updatedShoppingCart) {
        if (shoppingCarts.containsKey(id)) {
            shoppingCarts.put(id, updatedShoppingCart);
            saveShoppingCarts();
            return updatedShoppingCart;
        }
        return null;
    }

    public void delete(Integer id) {
        shoppingCarts.remove(id);
        saveShoppingCarts();
    }

    private void loadShoppingCarts() {
        File file = new File(contextPath + "shoppingCarts.txt");
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                StringTokenizer st = new StringTokenizer(line, ";");
                Integer id = Integer.parseInt(st.nextToken().trim());
                String userId = st.nextToken().trim(); // Assuming userId is used to retrieve the User object
                Double price = Double.parseDouble(st.nextToken().trim());

                List<ShoppingCartItem> items = new ArrayList<>();
                while (st.hasMoreTokens()) {
                    String itemId = st.nextToken().trim();
                    // Assuming ShoppingCartItem has a method to load by id
                    ShoppingCartItem item = loadShoppingCartItemById(itemId);
                    if (item != null) {
                        items.add(item);
                    }
                }

                User user = loadUserById(userId); // Assuming a method to load a User by id
                ShoppingCart shoppingCart = new ShoppingCart(id, user, items, price);
                shoppingCarts.put(id, shoppingCart);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading shopping carts from file", e);
        }
    }

    private void saveShoppingCarts() {
        File file = new File(contextPath + "shoppingCarts.txt");
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            for (ShoppingCart cart : shoppingCarts.values()) {
                StringBuilder sb = new StringBuilder();
                sb.append(cart.getId()).append(";");
                sb.append(cart.getUser().getId()).append(";"); // Assuming User has a getId method
                sb.append(cart.getPrice());
                for (ShoppingCartItem item : cart.getItems()) {
                    sb.append(";").append(item.getId()); // Assuming ShoppingCartItem has a getId method
                }
                out.write(sb.toString());
                out.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving shopping carts to file", e);
        }
    }

    private Integer generateUniqueId() {
        return shoppingCarts.size() + 1;
    }

    private ShoppingCartItem loadShoppingCartItemById(String itemId) {
        // Implement this method to load a ShoppingCartItem by its ID
        // This might involve calling another DAO
        return null; // Placeholder
    }

    private User loadUserById(String userId) {
        // Implement this method to load a User by its ID
        // This might involve calling another DAO
        return null; // Placeholder
    }
}
