package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Product;

public class ProductDAO {

    private HashMap<String, Product> products = new HashMap<>();

    public ProductDAO() {
    }

    public ProductDAO(String contextPath) {
        loadProducts(contextPath);
    }

    public Collection<Product> findAll() {
        return products.values();
    }

    public Product findProduct(String id) {
        return products.get(id);
    }

    public Product updateProduct(String id, Product product) {
        Product p = products.get(id);
        if (p == null) {
            return save(product);
        } else {
            p.setName(product.getName());
            p.setPrice(product.getPrice());
        }

        return p;
    }

    public Product save(Product product) {
        Integer maxId = products.keySet().stream()
                                .map(Integer::parseInt)
                                .max(Integer::compareTo)
                                .orElse(-1);
        maxId++;
        product.setId(maxId.toString());
        products.put(product.getId(), product);
        return product;
    }

    private void loadProducts(String contextPath) {
        File file = new File(contextPath + "/products.txt");
       

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                StringTokenizer st = new StringTokenizer(line, ";");
                String id = st.nextToken().trim();
                String name = st.nextToken().trim();
                String price = st.nextToken().trim();

                products.put(id, new Product(id, name, Double.parseDouble(price)));
            }
        } catch (IOException e) {
            e.printStackTrace(); // Ideally use logging
        }
    }

}
