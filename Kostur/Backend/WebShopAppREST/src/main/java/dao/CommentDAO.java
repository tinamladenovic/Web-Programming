package dao;

import beans.Comment;
import beans.User;
import beans.ChocolateFactoryObject;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommentDAO {
    private Map<String, Comment> comments = new HashMap<>();
    private String contextPath;
    private static final Logger LOGGER = Logger.getLogger(CommentDAO.class.getName());

    public CommentDAO(String contextPath) {
        this.contextPath = contextPath.endsWith(File.separator) ? contextPath : contextPath + File.separator;
        loadComments();
    }

    public Comment find(String id) {
        return comments.get(id);
    }

    public Map<String, Comment> findAll() {
        return comments;
    }

    public Comment create(Comment newComment) {
        String id = generateUniqueId();
        newComment.setId(id);
        comments.put(id, newComment);
        saveComments();
        return newComment;
    }

    public Comment update(String id, Comment updatedComment) {
        if (comments.containsKey(id)) {
            comments.put(id, updatedComment);
            saveComments();
            return updatedComment;
        }
        return null;
    }

    public void delete(String id) {
        comments.remove(id);
        saveComments();
    }

    private void loadComments() {
        File file = new File(contextPath + "comments.txt");
        LOGGER.log(Level.INFO, "Path to comments.txt: {0}", file.getAbsolutePath());

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                StringTokenizer st = new StringTokenizer(line, ";");
                String id = st.nextToken().trim();
                String userId = st.nextToken().trim();
                String factoryId = st.nextToken().trim();
                String text = st.nextToken().trim();
                int rating = Integer.parseInt(st.nextToken().trim());

                User customer = loadUserById(userId); // Implement this method
                ChocolateFactoryObject factory = loadFactoryById(factoryId); // Implement this method

                Comment comment = new Comment(id, customer, factory, text, rating);
                comments.put(id, comment);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading comments from file", e);
        }
    }

    private void saveComments() {
        File file = new File(contextPath + "comments.txt");
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            for (Comment comment : comments.values()) {
                String line = String.join(";",
                        comment.getId(),
                        comment.getCustomer().getId(),
                        comment.getFactory().getFactoryId(),
                        comment.getText(),
                        String.valueOf(comment.getRating())
                );
                out.write(line);
                out.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving comments to file", e);
        }
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    private User loadUserById(String userId) {
        // Implement this method to load a User by its ID
        // This might involve calling another DAO
        return null; // Placeholder
    }

    private ChocolateFactoryObject loadFactoryById(String factoryId) {
        // Implement this method to load a ChocolateFactoryObject by its ID
        // This might involve calling another DAO
        return null; // Placeholder
    }
}
