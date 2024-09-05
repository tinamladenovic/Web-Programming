package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import beans.CustomerType;
import beans.User;
import beans.enums.Gender;
import beans.enums.UserRole;

public class UserDAO {

    private Map<String, User> users = new HashMap<>();
    private String contextPath;

    public UserDAO() {
        // Default constructor
    }

    public UserDAO(String contextPath) {
        // Koristimo specifičnu putanju do datoteke u Eclipse radnom okruženju
        this.contextPath = "C:\\Users\\PC\\eclipse-workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp3\\wtpwebapps\\WebShopAppREST\\WEB-INF\\classes\\";
        loadUsers(this.contextPath);
    }

    public Map<String, User> findAll() {
        return users;
    }

    public User findUser(String id) {
        return users.get(id);
    }

    public User save(User user) {
        users.put(user.getId(), user);
        saveUsersToFile();
        System.out.println("User saved: " + user.getUsername());
        return user;
    }

    private void loadUsers(String contextPath) {
        File file = new File(contextPath + "users.txt");
        
        // Logovanje putanje fajla
        System.out.println("Loading users from file: " + file.getAbsolutePath());

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                StringTokenizer st = new StringTokenizer(line, ";");
                String id = st.nextToken().trim();
                String username = st.nextToken().trim();
                String password = st.nextToken().trim();
                String firstName = st.nextToken().trim();
                String lastName = st.nextToken().trim();
                Gender gender = Gender.valueOf(st.nextToken().trim());
                LocalDate birthDate = LocalDate.parse(st.nextToken().trim(), DateTimeFormatter.ISO_DATE);
                UserRole role = UserRole.valueOf(st.nextToken().trim());
                CustomerType customerType = CustomerType.valueOf(st.nextToken().trim());
                int earnedPoints = Integer.parseInt(st.nextToken().trim());
                boolean blocked = Boolean.parseBoolean(st.nextToken().trim());
                String factoryId = st.hasMoreTokens() ? st.nextToken().trim() : null;

                User user = new User(id, username, password, firstName, lastName, gender, birthDate, role,
                        customerType, earnedPoints, null, blocked, factoryId);

                users.put(id, user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public User find(String username, String password) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void delete(String id) {
        users.remove(id);
        saveUsersToFile();
    }

    private void saveUsersToFile() {
        File file = new File(contextPath + "users.txt");

        // Logovanje putanje fajla
        System.out.println("Saving users to file: " + file.getAbsolutePath());

        try (FileWriter writer = new FileWriter(file)) {
            for (User user : users.values()) {
                writer.write(formatUserForFile(user) + "\n");
            }
            System.out.println("Users successfully saved to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save users to file: " + e.getMessage());
        }
    }


    private String formatUserForFile(User user) {
        List<String> userFields = new ArrayList<>();
        userFields.add(user.getId());
        userFields.add(user.getUsername());
        userFields.add(user.getPassword());
        userFields.add(user.getFirstName());
        userFields.add(user.getLastName());
        userFields.add(user.getGender().name());
        userFields.add(user.getBirthDate().toString());
        userFields.add(user.getRole().name());
        userFields.add(user.getCustomerType().getName().name());
        userFields.add(String.valueOf(user.getEarnedPoints()));
        userFields.add(String.valueOf(user.isBlocked()));
        userFields.add(user.getFactoryId() != null ? user.getFactoryId() : ""); // Ovaj deo

        return String.join(";", userFields);
    }


    public List<User> getAvailableManagers() {
        return users.values().stream()
            .filter(user -> user.getRole() == UserRole.MANAGER && user.getFactoryId() == null)
            .collect(Collectors.toList());
    }

    public void updateUser(User user) {
        users.put(user.getId(), user);
        System.out.println("User updated: " + user.getUsername()); // Logovanje ažuriranja korisnika
        saveUsersToFile(); // Poziv metode za čuvanje korisnika u fajl
    }


    public void createUser(User newUser) {
        String id = generateUserId();
        newUser.setId(id);
        save(newUser);
    }

    private String generateUserId() {
        int maxId = 0;
        for (String id : users.keySet()) {
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

    public List<User> findCustomersByFactoryId(String factoryId) {
        return users.values().stream()
            .filter(user -> user.getFactoryId() != null && user.getFactoryId().equals(factoryId) && user.getRole() == UserRole.CUSTOMER)
            .collect(Collectors.toList());
    }

    public List<User> findWorkersByFactoryId(String factoryId) {
        return users.values().stream()
                .filter(user -> user.getFactoryId() != null && user.getFactoryId().equals(factoryId) && user.getRole() == UserRole.WORKER)
                .collect(Collectors.toList());
    }


}
