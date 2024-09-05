package services;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Comment;
import dao.CommentDAO;

@Path("/comments")
public class CommentService {

    private CommentDAO commentDAO;
    private static final Logger LOGGER = Logger.getLogger(CommentService.class.getName());

    public CommentService() {
        String contextPath = getClass().getClassLoader().getResource("").getPath();
        this.commentDAO = new CommentDAO(contextPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Comment> getComments() {
        return commentDAO.findAll().values();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComment(@PathParam("id") String id) {
        Comment comment = commentDAO.find(id);
        if (comment != null) {
            return Response.ok(comment).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Comment not found").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addComment(@Valid Comment newComment) {
        // Validacija podataka
        if (newComment.getCustomer() == null || newComment.getFactory() == null || newComment.getText() == null || newComment.getText().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Customer, factory, and text cannot be null or empty").build();
        }
        if (newComment.getRating() < 1 || newComment.getRating() > 5) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Rating must be between 1 and 5").build();
        }

        // Dodavanje komentara
        Comment addedComment = commentDAO.create(newComment);
        if (addedComment != null) {
            // Logovanje dodatog komentara
            LOGGER.log(Level.INFO, "Added new comment with ID: {0}", addedComment.getId());
            return Response.status(Response.Status.CREATED).entity(addedComment).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add comment").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateComment(@PathParam("id") String id, @Valid Comment updatedComment) {
        // Validacija podataka
        if (updatedComment.getCustomer() == null || updatedComment.getFactory() == null || updatedComment.getText() == null || updatedComment.getText().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Customer, factory, and text cannot be null or empty").build();
        }
        if (updatedComment.getRating() < 1 || updatedComment.getRating() > 5) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Rating must be between 1 and 5").build();
        }

        // Ažuriranje komentara
        Comment comment = commentDAO.update(id, updatedComment);
        if (comment != null) {
            // Logovanje ažuriranog komentara
            LOGGER.log(Level.INFO, "Updated comment with ID: {0}", comment.getId());
            return Response.ok(comment).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Comment not found").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteComment(@PathParam("id") String id) {
        Comment comment = commentDAO.find(id);
        if (comment != null) {
            commentDAO.delete(id);
            // Logovanje obrisanog komentara
            LOGGER.log(Level.INFO, "Deleted comment with ID: {0}", id);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Comment not found").build();
        }
    }
}
