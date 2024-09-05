package services;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.CustomerType;
import beans.User;
import beans.enums.UserRole;
import dao.UserDAO;

@Path("")
public class LoginAndRegistrationService {

    @Context
    ServletContext ctx;

    public LoginAndRegistrationService() {
    }

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("userDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("userDAO", new UserDAO(contextPath));
        }
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    public Response options() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User user, @Context HttpServletRequest request) {
        UserDAO userDao = (UserDAO) ctx.getAttribute("userDAO");
        User loggedUser = userDao.find(user.getUsername(), user.getPassword());

        if (loggedUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid username and/or password\"}")
                    .build();
        }

        request.getSession().setAttribute("user", loggedUser);
        System.out.println("User logged in: " + loggedUser.getUsername());

        return Response.ok(loggedUser).build();
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletRequest request) {
        request.getSession().invalidate();
        return Response.ok("{\"message\": \"Logout successfully completed.\"}")
                       .build();
    }

    @GET
    @Path("/currentUser")
    @Produces(MediaType.APPLICATION_JSON)
    public User getCurrentUser(@Context HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user, @Context HttpServletRequest request) {
        UserDAO userDao = (UserDAO) ctx.getAttribute("userDAO");

        User loggedUser = (User) request.getSession().getAttribute("user");
        if (loggedUser != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"User is already logged in as " + loggedUser.getUsername() + "\"}")
                    .build();
        }

        if (userDao.findUser(user.getUsername()) != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Username already exists\"}")
                    .build();
        }

        user.setId(generateUserId());
        user.setRole(UserRole.CUSTOMER);
        user.setCustomerType(CustomerType.NO_TYPE);
        user.setEarnedPoints(0);

        userDao.save(user);

        request.getSession().setAttribute("user", user);

        return Response.status(Response.Status.CREATED)
                .entity("{\"message\": \"User registered and logged in successfully\", \"username\": \"" + user.getUsername() + "\"}")
                .build();
    }

    private String generateUserId() {
        UserDAO userDao = (UserDAO) ctx.getAttribute("userDAO");
        int maxId = 0;
        for (String id : userDao.findAll().keySet()) {
            int numericId;
            try {
                numericId = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                continue;
            }
            if (numericId > maxId) {
                maxId = numericId;
            }
        }
        return String.valueOf(maxId + 1);
    }

    @GET
    @Path("/profile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@Context HttpServletRequest request) {
        User loggedUser = (User) request.getSession().getAttribute("user");

        if (loggedUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"User is not logged in\"}")
                    .build();
        }

        return Response.ok(loggedUser).build();
    }

    @PUT
    @Path("/profile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProfile(User updatedUser, @Context HttpServletRequest request) {
        UserDAO userDao = (UserDAO) ctx.getAttribute("userDAO");
        User loggedUser = (User) request.getSession().getAttribute("user");

        if (loggedUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"User is not logged in\"}")
                    .build();
        }

        // Provera jedinstvenosti korisničkog imena ako se menja
        if (!loggedUser.getUsername().equals(updatedUser.getUsername())) {
            if (userDao.find(updatedUser.getUsername(), updatedUser.getPassword()) != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"message\": \"Username already exists\"}")
                        .build();
            }
        }

        // Ažuriranje korisničkih podataka
        loggedUser.setFirstName(updatedUser.getFirstName());
        loggedUser.setLastName(updatedUser.getLastName());
        loggedUser.setGender(updatedUser.getGender());
        loggedUser.setBirthDate(updatedUser.getBirthDate());
        loggedUser.setUsername(updatedUser.getUsername());
        loggedUser.setPassword(updatedUser.getPassword());

        // Čuvanje ažuriranih podataka u fajl
        userDao.save(loggedUser);

        // Logovanje ažuriranja
        System.out.println("User updated: " + loggedUser.getUsername());

        // Vraćanje odgovora sa porukom o uspehu
        return Response.ok("{\"message\": \"Profile updated successfully\"}")
                .build();
    }


    // Nova metoda za brisanje korisnika
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") String id, @Context HttpServletRequest request) {
        UserDAO userDao = (UserDAO) ctx.getAttribute("userDAO");

        User loggedUser = (User) request.getSession().getAttribute("user");
        if (loggedUser == null || !loggedUser.getRole().equals(UserRole.ADMIN)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"Access denied: Only administrators can delete users.\"}")
                    .build();
        }

        User userToDelete = userDao.findUser(id);
        if (userToDelete == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found.\"}")
                    .build();
        }

        userDao.delete(id);
        return Response.status(Response.Status.OK)
                .entity("{\"message\": \"User deleted successfully.\"}")
                .build();
    }
}
