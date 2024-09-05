package services;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Purchase;
import dao.PurchaseDAO;

@Path("/purchases")
public class PurchaseService {

    private PurchaseDAO purchaseDAO;
    private static final Logger LOGGER = Logger.getLogger(PurchaseService.class.getName());

    public PurchaseService() {
        String contextPath = getClass().getClassLoader().getResource("").getPath();
        this.purchaseDAO = new PurchaseDAO(contextPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Purchase> getPurchases() {
        return purchaseDAO.findAll().values();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPurchase(@PathParam("id") String id) {
        Purchase purchase = purchaseDAO.find(id);
        if (purchase != null) {
            return Response.ok(purchase).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Purchase not found").build();
        }
    }

    @GET
    @Path("/factory/{factoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPurchasesByFactory(@PathParam("factoryId") String factoryId) {
        try {
            List<Purchase> purchases = (List<Purchase>) purchaseDAO.findByFactoryId(factoryId);
            
            // Provera ako lista kupovina nije prazna
            if (purchases == null || purchases.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("No purchases found for factory with ID: " + factoryId).build();
            }

            return Response.ok(purchases).build();
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception to the server logs
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred while fetching purchases: " + e.getMessage()).build();
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPurchase(@Valid Purchase newPurchase) {
        // Validacija podataka
        if (newPurchase.getCustomerName() == null || newPurchase.getCustomerName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Customer name cannot be empty").build();
        }

        // Dodavanje kupovine
        Purchase addedPurchase = purchaseDAO.create(newPurchase);
        if (addedPurchase != null) {
            // Logovanje dodate kupovine
            LOGGER.log(Level.INFO, "Added new purchase with ID: {0}", addedPurchase.getId());
            return Response.status(Response.Status.CREATED).entity(addedPurchase).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add purchase").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePurchase(@PathParam("id") String id, @Valid Purchase updatedPurchase) {
        // Validacija podataka
        if (updatedPurchase.getCustomerName() == null || updatedPurchase.getCustomerName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Customer name cannot be empty").build();
        }

        // Ažuriranje kupovine
        Purchase purchase = purchaseDAO.update(id, updatedPurchase);
        if (purchase != null) {
            // Logovanje ažurirane kupovine
            LOGGER.log(Level.INFO, "Updated purchase with ID: {0}", purchase.getId());
            return Response.ok(purchase).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Purchase not found").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletePurchase(@PathParam("id") String id) {
        purchaseDAO.delete(id);
        // Provera da li je kupovina uspešno obrisana
        Purchase deletedPurchase = purchaseDAO.find(id);
        if (deletedPurchase == null) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete purchase").build();
        }
    }
}
