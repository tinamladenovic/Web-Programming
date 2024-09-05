package services;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Location;
import dao.LocationDAO;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/locations")
public class LocationService {

    private LocationDAO locationDAO;
    private static final Logger LOGGER = Logger.getLogger(LocationService.class.getName());

    public LocationService() {
        String contextPath = getClass().getClassLoader().getResource("").getPath();
        this.locationDAO = new LocationDAO(contextPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Location> getLocations() {
        LOGGER.log(Level.INFO, "Fetching all locations");
        return locationDAO.getLocations();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocation(@PathParam("id") String id) {
        LOGGER.log(Level.INFO, "Fetching location with ID: {0}", id);
        Location location = locationDAO.findLocation(id);
        if (location != null) {
            return Response.ok(location).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Location not found").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createLocation(Location location) {
        LOGGER.log(Level.INFO, "Creating location with ID: {0}", location.getLocationId());
        boolean created = locationDAO.createLocation(location);
        if (created) {
            return Response.status(Response.Status.CREATED).entity(location).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity("Location ID already exists").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateLocation(@PathParam("id") String id, Location updatedLocation) {
        LOGGER.log(Level.INFO, "Updating location with ID: {0}", id);
        updatedLocation.setLocationId(id);
        boolean updated = locationDAO.updateLocation(updatedLocation); // Metoda za ažuriranje
        if (updated) {
            return Response.status(Response.Status.OK).entity(updatedLocation).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Location not found").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteLocation(@PathParam("id") String id) {
        LOGGER.log(Level.INFO, "Deleting location with ID: {0}", id);
        boolean deleted = locationDAO.deleteLocation(id);
        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Location not found").build();
        }
    }
}
