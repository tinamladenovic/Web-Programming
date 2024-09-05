package services;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.ChocolateFactoryObject;
import beans.CustomerType;
import beans.Purchase;
import beans.User;
import beans.enums.UserRole;
import dao.ChocolateFactoryObjectDAO;
import dao.PurchaseDAO;
import dao.UserDAO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/users")
public class UserService {

    private UserDAO userDAO;
    private ChocolateFactoryObjectDAO chocolateFactoryObjectDAO;
    private PurchaseDAO purchaseDAO;
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    public UserService() {
        String contextPath = getClass().getClassLoader().getResource("").getPath();
        this.userDAO = new UserDAO(contextPath);
        this.chocolateFactoryObjectDAO = new ChocolateFactoryObjectDAO(contextPath);
        this.purchaseDAO = new PurchaseDAO(contextPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        Map<String, User> allUsers = userDAO.findAll();
        System.out.println("Returning users: " + allUsers);
        return Response.ok(allUsers)
                       .header("Access-Control-Allow-Origin", "http://localhost:3000")
                       .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                       .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                       .header("Access-Control-Allow-Credentials", "true")
                       .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") String id) {
        User user = userDAO.findUser(id);
        if (user != null) {
            return Response.ok(user)
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found")
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        }
    }

    @GET
    @Path("/managers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableManagers() {
        List<User> managers = userDAO.getAvailableManagers();
        return Response.ok(managers)
                       .header("Access-Control-Allow-Origin", "http://localhost:3000")
                       .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                       .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                       .header("Access-Control-Allow-Credentials", "true")
                       .build();
    }

    @POST
    @Path("/createuser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(User newUser) {
        return createUserWithRole(newUser, UserRole.CUSTOMER);
    }

    private Response createUserWithRole(User newUser, UserRole role) {
        // Validacija unosa
        if (newUser.getUsername() == null || newUser.getUsername().isEmpty() || 
            newUser.getPassword() == null || newUser.getPassword().isEmpty() ||
            newUser.getFirstName() == null || newUser.getFirstName().isEmpty() ||
            newUser.getLastName() == null || newUser.getLastName().isEmpty() ||
            newUser.getGender() == null || newUser.getBirthDate() == null ||
            newUser.getCustomerType() == null || newUser.getEarnedPoints() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("All fields are required.")
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        }

        // Postavljanje uloge
        newUser.setRole(role);

        // Kreiranje korisnika
        try {
            userDAO.createUser(newUser);
            return Response.status(Response.Status.CREATED).entity(newUser)
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error creating user.")
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        }
    }

    @POST
    @Path("/createmanager")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createManager(User newUser, @Context HttpServletRequest request) {
        // Provera da li je trenutni korisnik admin
        User loggedInUser = (User) request.getSession().getAttribute("user");
        if (loggedInUser == null || !loggedInUser.getRole().equals(UserRole.ADMIN)) {
            return Response.status(Response.Status.FORBIDDEN).entity("Only administrators can create managers.")
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        }

        // Validacija ulaznih podataka
        if (newUser.getUsername() == null || newUser.getUsername().isEmpty() || 
            newUser.getPassword() == null || newUser.getPassword().isEmpty() ||
            newUser.getFirstName() == null || newUser.getFirstName().isEmpty() ||
            newUser.getLastName() == null || newUser.getLastName().isEmpty() ||
            newUser.getGender() == null || newUser.getBirthDate() == null ||
            newUser.getRole() == null || !newUser.getRole().equals(UserRole.MANAGER)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("All fields are required and role must be MANAGER.")
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        }
        
        // Inicijalizacija CustomerType i drugih polja specifičnih za menadžera
        newUser.setCustomerType(CustomerType.NO_TYPE);
        newUser.setEarnedPoints(0);

        try {
            userDAO.createUser(newUser);
            return Response.status(Response.Status.CREATED).entity(newUser)
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating manager", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error creating manager.")
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        }
    }

    @POST
    @Path("/createWorker")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createWorker(User newUser, @Context HttpServletRequest request) {
        // Provera da li je trenutni korisnik menadžer
        User loggedInUser = (User) request.getSession().getAttribute("user");
        if (loggedInUser == null || !loggedInUser.getRole().equals(UserRole.MANAGER)) {
            return Response.status(Response.Status.FORBIDDEN).entity("Only managers can create workers.")
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        }

        // Validacija ulaznih podataka
        if (newUser.getUsername() == null || newUser.getUsername().isEmpty() || 
            newUser.getPassword() == null || newUser.getPassword().isEmpty() ||
            newUser.getFirstName() == null || newUser.getFirstName().isEmpty() ||
            newUser.getLastName() == null || newUser.getLastName().isEmpty() ||
            newUser.getGender() == null || newUser.getBirthDate() == null ||
            newUser.getRole() == null || !newUser.getRole().equals(UserRole.WORKER) ||
            newUser.getFactoryId() == null || newUser.getFactoryId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("All fields are required, including a valid factoryId, and role must be WORKER.")
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        }

        // Provera da li fabrika sa datim ID-em postoji
        ChocolateFactoryObject factory = chocolateFactoryObjectDAO.findChocolateFactory(newUser.getFactoryId());
        if (factory == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid factoryId. The factory does not exist.")
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        }

        // Inicijalizacija CustomerType i drugih polja specifičnih za radnika
        newUser.setCustomerType(CustomerType.NO_TYPE);
        newUser.setEarnedPoints(0);

        try {
            userDAO.createUser(newUser);
            return Response.status(Response.Status.CREATED).entity(newUser)
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating worker", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error creating worker.")
                           .header("Access-Control-Allow-Origin", "http://localhost:3000")
                           .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                           .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                           .header("Access-Control-Allow-Credentials", "true")
                           .build();
        }
    }
    
    @GET
    @Path("/customers/factory/{factoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomersByFactory(@PathParam("factoryId") String factoryId) {
        List<User> customers = userDAO.findCustomersByFactoryId(factoryId);
        return Response.ok(customers)
                       .header("Access-Control-Allow-Origin", "http://localhost:3000")
                       .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                       .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                       .header("Access-Control-Allow-Credentials", "true")
                       .build();
    }

    @GET
    @Path("/workers/factory/{factoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWorkersByFactory(@PathParam("factoryId") String factoryId) {
        List<User> workers = userDAO.findWorkersByFactoryId(factoryId);
        return Response.ok(workers)
                       .header("Access-Control-Allow-Origin", "http://localhost:3000")
                       .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                       .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                       .header("Access-Control-Allow-Credentials", "true")
                       .build();
    }

    @GET
    @Path("/{customerName}/purchases")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserPurchases(@PathParam("customerName") String customerName) {
        try {
            // Dohvatanje svih kupovina za korisnika prema imenu
            Collection<Purchase> purchases = purchaseDAO.getPurchasesByCustomerName(customerName);
            if (purchases.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(purchases).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching purchases for user with name: " + customerName, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error fetching purchases").build();
        }
    }

}
