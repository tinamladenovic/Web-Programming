package services;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Chocolate;
import beans.ChocolateFactoryObject;
import beans.User;
import beans.enums.UserRole;
import dao.ChocolateDAO;
import dao.ChocolateFactoryObjectDAO;
import dao.UserDAO;

@Path("/chocolateFactoryObjects")
public class ChocolateFactoryObjectService {

    private ChocolateFactoryObjectDAO chocolateFactoryObjectDAO;
    private UserDAO userDAO;
    private ChocolateDAO chocolateDAO;
    private static final Logger LOGGER = Logger.getLogger(ChocolateFactoryObjectService.class.getName());

    public ChocolateFactoryObjectService() {
        String contextPath = getClass().getClassLoader().getResource("").getPath();
        this.chocolateFactoryObjectDAO = new ChocolateFactoryObjectDAO(contextPath);
        this.userDAO = new UserDAO(contextPath);
        this.chocolateDAO = new ChocolateDAO(contextPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ChocolateFactoryObject> getChocolateFactoryObjects() {
        Collection<ChocolateFactoryObject> factories = chocolateFactoryObjectDAO.findAll();
        LOGGER.log(Level.INFO, "Returning factory objects, count: " + factories.size());
        return factories;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChocolateFactoryObject(@PathParam("id") String id) {
        ChocolateFactoryObject factory = chocolateFactoryObjectDAO.findChocolateFactory(id);
        if (factory != null) {
            List<Chocolate> chocolates = chocolateDAO.getChocolatesByFactoryId(factory.getFactoryId());
            factory.setChocolates(chocolates);
            return Response.ok(factory).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Chocolate factory not found").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addChocolateFactoryObject(@Valid ChocolateFactoryObject chocolateFactoryObject, @Context HttpServletRequest request) {
        try {
            // Provera da li je korisnik ulogovan kao admin
            User loggedUser = (User) request.getSession().getAttribute("user");
            if (loggedUser == null || !loggedUser.getRole().equals(UserRole.ADMIN)) {
                return Response.status(Response.Status.FORBIDDEN).entity("Only administrators can add new chocolate factories.").build();
            }

            // Validacija podataka
            if (chocolateFactoryObject.getName() == null || chocolateFactoryObject.getName().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Chocolate factory name cannot be empty").build();
            }

            // Generisanje novog ID-a
            String newId = generateNewFactoryId();
            chocolateFactoryObject.setFactoryId(newId);
            chocolateFactoryObjectDAO.save(chocolateFactoryObject);

            // Ažuriranje fabrike dodeljene menadžeru
            User manager = userDAO.findUser(chocolateFactoryObject.getManagerId());
            if (manager != null) {
                manager.setFactoryId(newId);
                userDAO.updateUser(manager);
            }

            // Logovanje dodate fabrike čokolada
            LOGGER.log(Level.INFO, "Added new chocolate factory with ID: " + chocolateFactoryObject.getFactoryId());

            return Response.status(Response.Status.CREATED).entity(chocolateFactoryObject).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding chocolate factory: ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error adding chocolate factory").build();
        }
    }


    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateChocolateFactoryObject(@PathParam("id") String id, @Valid ChocolateFactoryObject updatedFactory) {
        try {
            ChocolateFactoryObject existingFactory = chocolateFactoryObjectDAO.findChocolateFactory(id);
            if (existingFactory != null) {
                // Validacija podataka
                if (updatedFactory.getName() == null || updatedFactory.getName().isEmpty()) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Chocolate factory name cannot be empty").build();
                }

                existingFactory.setName(updatedFactory.getName());
                existingFactory.setChocolates(updatedFactory.getChocolates());
                existingFactory.setOpeningTime(updatedFactory.getOpeningTime());
                existingFactory.setClosingTime(updatedFactory.getClosingTime());
                existingFactory.setLocation(updatedFactory.getLocation());
                existingFactory.setLogo(updatedFactory.getLogo());
                existingFactory.setRating(updatedFactory.getRating());
                existingFactory.setStatus(updatedFactory.getStatus());
                chocolateFactoryObjectDAO.save(existingFactory);  // Sačuvaj ažuriran objekat

                // Logovanje ažurirane fabrike čokolada
                LOGGER.log(Level.INFO, "Updated chocolate factory with ID: " + existingFactory.getFactoryId());

                return Response.ok(existingFactory).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Chocolate factory not found").build();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating chocolate factory: ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating chocolate factory").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteChocolateFactoryObject(@PathParam("id") String id) {
        try {
            ChocolateFactoryObject factory = chocolateFactoryObjectDAO.findChocolateFactory(id);
            if (factory != null) {
                chocolateFactoryObjectDAO.delete(id);
                LOGGER.log(Level.INFO, "Deleted chocolate factory with ID: " + id);
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Chocolate factory not found").build();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting chocolate factory: ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting chocolate factory").build();
        }
    }

    private String generateNewFactoryId() {
        // Generisanje jedinstvenog ID-a, možeš koristiti UUID ili drugi mehanizam
        int maxId = chocolateFactoryObjectDAO.findAll().stream()
                     .mapToInt(factory -> Integer.parseInt(factory.getFactoryId()))
                     .max()
                     .orElse(0);
        return String.valueOf(maxId + 1);
    }
}
