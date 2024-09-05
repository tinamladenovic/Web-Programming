package services;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.ShoppingCartItem;
import dao.ShoppingCartItemDAO;

@Path("/shoppingCartItems")
public class ShoppingCartItemService {

    private ShoppingCartItemDAO shoppingCartItemDAO;
    private static final Logger LOGGER = Logger.getLogger(ShoppingCartItemService.class.getName());

    public ShoppingCartItemService() {
        String contextPath = getClass().getClassLoader().getResource("").getPath();
        this.shoppingCartItemDAO = new ShoppingCartItemDAO(contextPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ShoppingCartItem> getShoppingCartItems() {
        return shoppingCartItemDAO.findAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShoppingCartItem(@PathParam("id") Integer id) {
        ShoppingCartItem shoppingCartItem = shoppingCartItemDAO.findShoppingCartItem(id);
        if (shoppingCartItem != null) {
            return Response.ok(shoppingCartItem).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Shopping cart item not found").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addShoppingCartItem(@Valid ShoppingCartItem newShoppingCartItem) {
        // Validacija podataka
        if (newShoppingCartItem.getShoppingCart() == null || newShoppingCartItem.getChocolate() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Shopping cart and chocolate cannot be null").build();
        }

        // Dodavanje stavke korpe
        shoppingCartItemDAO.save(newShoppingCartItem);
        // Logovanje dodate stavke korpe
        LOGGER.log(Level.INFO, "Added new shopping cart item with ID: {0}", newShoppingCartItem.getId());
        return Response.status(Response.Status.CREATED).entity(newShoppingCartItem).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateShoppingCartItem(@PathParam("id") Integer id, @Valid ShoppingCartItem updatedShoppingCartItem) {
        // Validacija podataka
        if (updatedShoppingCartItem.getShoppingCart() == null || updatedShoppingCartItem.getChocolate() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Shopping cart and chocolate cannot be null").build();
        }

        // Ažuriranje stavke korpe
        ShoppingCartItem shoppingCartItem = shoppingCartItemDAO.findShoppingCartItem(id);
        if (shoppingCartItem != null) {
            shoppingCartItemDAO.save(updatedShoppingCartItem);
            // Logovanje ažurirane stavke korpe
            LOGGER.log(Level.INFO, "Updated shopping cart item with ID: {0}", updatedShoppingCartItem.getId());
            return Response.ok(updatedShoppingCartItem).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Shopping cart item not found").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteShoppingCartItem(@PathParam("id") Integer id) {
        ShoppingCartItem shoppingCartItem = shoppingCartItemDAO.findShoppingCartItem(id);
        if (shoppingCartItem != null) {
            shoppingCartItemDAO.delete(id);
            // Logovanje obrisane stavke korpe
            LOGGER.log(Level.INFO, "Deleted shopping cart item with ID: {0}", id);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Shopping cart item not found").build();
        }
    }
}
