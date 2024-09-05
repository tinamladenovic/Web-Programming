package services;

import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import dao.ProductDAO;
import beans.Product;

@Path("/products")
public class ProductService {

    private ProductDAO productDAO;

    public ProductService() {
        String contextPath = getClass().getClassLoader().getResource("").getPath();
        this.productDAO = new ProductDAO(contextPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Product> getProducts() {
        return productDAO.findAll();
    }
}
