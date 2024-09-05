package services;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.ShoppingCart;
import dao.ShoppingCartDAO;

@Path("/shoppingCarts")
public class ShoppingCartService {

    private ShoppingCartDAO shoppingCartDAO;
    private static final Logger LOGGER = Logger.getLogger(ShoppingCartService.class.getName());

    public ShoppingCartService() {
        String contextPath = getClass().getClassLoader().getResource("").getPath();
        this.shoppingCartDAO = new ShoppingCartDAO(contextPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ShoppingCart> getShoppingCarts() {
        return shoppingCartDAO.findAll().values();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShoppingCart(@PathParam("id") Integer id) {
        ShoppingCart shoppingCart = shoppingCartDAO.find(id);
        if (shoppingCart != null) {
            return Response.ok(shoppingCart).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Shopping cart not found").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addShoppingCart(@Valid ShoppingCart newShoppingCart) {
        // Validacija podataka
        if (newShoppingCart.getUser() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User cannot be null").build();
        }

        // Dodavanje korpe
        ShoppingCart addedShoppingCart = shoppingCartDAO.create(newShoppingCart);
        if (addedShoppingCart != null) {
            // Logovanje dodate korpe
            LOGGER.log(Level.INFO, "Added new shopping cart with ID: {0}", addedShoppingCart.getId());
            return Response.status(Response.Status.CREATED).entity(addedShoppingCart).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add shopping cart").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateShoppingCart(@PathParam("id") Integer id, @Valid ShoppingCart updatedShoppingCart) {
        // Validacija podataka
        if (updatedShoppingCart.getUser() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User cannot be null").build();
        }

        // Ažuriranje korpe
        ShoppingCart shoppingCart = shoppingCartDAO.update(id, updatedShoppingCart);
        if (shoppingCart != null) {
            // Logovanje ažurirane korpe
            LOGGER.log(Level.INFO, "Updated shopping cart with ID: {0}", shoppingCart.getId());
            return Response.ok(shoppingCart).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Shopping cart not found").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteShoppingCart(@PathParam("id") Integer id) {
        shoppingCartDAO.delete(id);
        // Provera da li je korpa uspešno obrisana
        ShoppingCart deletedShoppingCart = shoppingCartDAO.find(id);
        if (deletedShoppingCart == null) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete shopping cart").build();
        }
    }
}
