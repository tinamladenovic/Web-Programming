package services;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Chocolate;
import beans.User;
import beans.enums.UserRole;
import dao.ChocolateDAO;

@Path("/chocolates")
public class ChocolateService {

    private ChocolateDAO chocolateDAO;
    private static final Logger LOGGER = Logger.getLogger(ChocolateService.class.getName());

    public ChocolateService() {
        String contextPath = getClass().getClassLoader().getResource("").getPath();
        this.chocolateDAO = new ChocolateDAO(contextPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Chocolate> getChocolates() {
        return chocolateDAO.findAll().values();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChocolate(@PathParam("id") String id) {
        Chocolate chocolate = chocolateDAO.find(id);
        if (chocolate != null) {
            return Response.ok(chocolate).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Chocolate not found").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addChocolate(@Valid Chocolate newChocolate, @Context HttpServletRequest request) {
        // Provera da li je korisnik menadžer
        if (!isManager(request)) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied: Only managers can add chocolates.").build();
        }

        // Validacija podataka
        if (newChocolate.getName() == null || newChocolate.getName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Chocolate name cannot be empty").build();
        }
        if (newChocolate.getPrice() < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Chocolate price cannot be negative").build();
        }
        if (newChocolate.getWeight() < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Chocolate weight cannot be negative").build();
        }
        if (newChocolate.getQuantity() < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Chocolate quantity cannot be negative").build();
        }

        // Postavljanje ID-a kao rednog broja
        String newId = generateNextId();
        newChocolate.setId(newId);

        // Dodavanje čokolade
        Chocolate addedChocolate = chocolateDAO.create(newChocolate);
        if (addedChocolate != null) {
            // Logovanje dodate čokolade
            LOGGER.log(Level.INFO, "Added new chocolate with ID: {0}", addedChocolate.getId());
            return Response.status(Response.Status.CREATED).entity(addedChocolate).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add chocolate").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteChocolate(@PathParam("id") String id, @Context HttpServletRequest request) {
        // Provera da li je korisnik menadžer
        if (!isManager(request)) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied: Only managers can delete chocolates.").build();
        }

        chocolateDAO.delete(id);
        // Provera da li je čokolada uspešno obrisana
        Chocolate deletedChocolate = chocolateDAO.find(id);
        if (deletedChocolate == null) {
            return Response.ok("{\"message\": \"Chocolate deleted.\"}").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"Failed to delete chocolate\"}").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateChocolate(@PathParam("id") String id, @Valid Chocolate updatedChocolate, @Context HttpServletRequest request) {
        // Provera da li je korisnik menadžer
        if (!isManager(request)) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied: Only managers can update chocolates.").build();
        }

        // Validacija podataka
        if (updatedChocolate.getName() == null || updatedChocolate.getName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Chocolate name cannot be empty").build();
        }
        if (updatedChocolate.getPrice() < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Chocolate price cannot be negative").build();
        }
        if (updatedChocolate.getWeight() < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Chocolate weight cannot be negative").build();
        }
        if (updatedChocolate.getQuantity() < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Chocolate quantity cannot be negative").build();
        }

        // Pronalaženje postojeće čokolade
        Chocolate existingChocolate = chocolateDAO.find(id);
        if (existingChocolate == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Chocolate not found").build();
        }

        // Zadržavanje originalnog ID-a
        updatedChocolate.setId(existingChocolate.getId());

        // Ažuriranje čokolade
        Chocolate chocolate = chocolateDAO.update(id, updatedChocolate);
        if (chocolate != null) {
            // Logovanje ažurirane čokolade
            LOGGER.log(Level.INFO, "Updated chocolate with ID: {0}", chocolate.getId());
            return Response.ok(chocolate).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to update chocolate").build();
        }
    }

    @PUT
    @Path("/{id}/quantity")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateChocolateQuantity(@PathParam("id") String id, Chocolate updatedChocolate, @Context HttpServletRequest request) {
        // Provera da li je korisnik radnik
        if (!isWorker(request)) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied: Only workers can update chocolate quantities.").build();
        }

        // Pronalaženje postojeće čokolade
        Chocolate existingChocolate = chocolateDAO.find(id);
        if (existingChocolate == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Chocolate not found").build();
        }

        // Provera da li radnik radi u fabrici kojoj pripada ova čokolada
        User loggedInUser = (User) request.getSession().getAttribute("user");
        if (!existingChocolate.getFactoryId().equals(loggedInUser.getFactoryId())) {
            LOGGER.log(Level.WARNING, "Worker tried to update chocolate from another factory. Chocolate factory ID: " + existingChocolate.getFactoryId() + ", Worker factory ID: " + loggedInUser.getFactoryId());
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied: You can only update quantities for chocolates in your factory.").build();
        }

        // Ažuriranje količine
        existingChocolate.setQuantity(updatedChocolate.getQuantity());
        chocolateDAO.update(id, existingChocolate);

        return Response.ok("{\"message\": \"Chocolate quantity updated successfully.\"}").build();
    }

    private boolean isManager(HttpServletRequest request) {
        User loggedInUser = (User) request.getSession().getAttribute("user");
        if (loggedInUser != null) {
            LOGGER.log(Level.INFO, "Logged in user role: " + loggedInUser.getRole());
        }
        return loggedInUser != null && loggedInUser.getRole().equals(UserRole.MANAGER);
    }

    private boolean isWorker(HttpServletRequest request) {
        User loggedInUser = (User) request.getSession().getAttribute("user");
        if (loggedInUser != null) {
            LOGGER.log(Level.INFO, "Logged in user role: " + loggedInUser.getRole());
        }
        return loggedInUser != null && loggedInUser.getRole().equals(UserRole.WORKER);
    }

    private String generateNextId() {
        int size = chocolateDAO.findAll().size();
        return String.valueOf(size + 1);
    }
    
    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context HttpServletRequest request) {
        User loggedInUser = (User) request.getSession().getAttribute("user");
        if (loggedInUser != null) {
            LOGGER.log(Level.INFO, "Current logged in user ID: " + loggedInUser.getId());
            return Response.ok(loggedInUser).build();
        } else {
            LOGGER.log(Level.WARNING, "No user logged in");
            return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
        }
    }
}
