package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import beans.Chocolate;
import beans.ChocolateFactoryObject;
import beans.Location;
import beans.enums.ChocolateFactoryObjectStatus;

public class ChocolateFactoryObjectDAO {

    private Map<String, ChocolateFactoryObject> factoryObjects = new HashMap<>();
    private Map<String, Location> locationMap = new HashMap<>();
    private String filePath;
    private static final Logger LOGGER = Logger.getLogger(ChocolateFactoryObjectDAO.class.getName());

    public ChocolateFactoryObjectDAO() {
        // Default constructor
    }

    public ChocolateFactoryObjectDAO(String contextPath) {
        this.filePath = contextPath + "/chocolateFactoryObjects.txt";
        loadLocations(contextPath);
        loadFactoryObjects();  // Ovo učitava sve fabrike iz fajla u memoriju
    }

    public ArrayList<ChocolateFactoryObject> findAll() {
        LOGGER.log(Level.INFO, "Returning all factory objects, count: " + factoryObjects.size());
        return new ArrayList<>(factoryObjects.values());
    }

    public ChocolateFactoryObject save(ChocolateFactoryObject factoryObject) {
        // Prvo učitajte sve postojeće fabrike iz fajla kako biste ih osvežili
        loadFactoryObjects();

        // Uverite se da čokolade iz postojeće fabrike nisu izbrisane
        ChocolateFactoryObject existingFactory = factoryObjects.get(factoryObject.getFactoryId());
        if (existingFactory != null && (factoryObject.getChocolates() == null || factoryObject.getChocolates().isEmpty())) {
            factoryObject.setChocolates(existingFactory.getChocolates());
        }

        // Dodajte ili ažurirajte fabriku u mapi
        factoryObjects.put(factoryObject.getFactoryId(), factoryObject);

        // Sačuvajte sve fabrike nazad u fajl
        saveAllToTextFile();

        return factoryObject;
    }


    public void delete(String id) {
        LOGGER.log(Level.INFO, "Deleting factory object: " + id);
        factoryObjects.remove(id);
        boolean saved = saveAllToTextFile(); // Ažuriraj celu datoteku nakon brisanja
        if (saved) {
            LOGGER.log(Level.INFO, "Factory object deleted successfully: " + id);
        } else {
            LOGGER.log(Level.SEVERE, "Failed to delete factory object: " + id);
        }
    }

    public ChocolateFactoryObject findChocolateFactory(String id) {
        LOGGER.log(Level.INFO, "Finding factory object: " + id);
        return factoryObjects.get(id);
    }

    private void loadFactoryObjects() {
        File file = new File(filePath);
        LOGGER.log(Level.INFO, "Loading Chocolate Factory Objects from: " + file.getAbsolutePath());

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Podeli liniju po znaku ";"
                String[] tokens = line.split(";");
                
                if (tokens.length != 10) {
                    LOGGER.log(Level.WARNING, "Invalid line format: " + line);
                    continue;
                }

                try {
                    String id = tokens[0].trim();
                    String name = tokens[1].trim();
                    String chocolatesString = tokens[2].trim(); // String of chocolate IDs
                    LocalTime openingTime = LocalTime.parse(tokens[3].trim());
                    LocalTime closingTime = LocalTime.parse(tokens[4].trim());
                    String locationId = tokens[5].trim();
                    String logo = tokens[6].trim();
                    Double rating = Double.parseDouble(tokens[7].trim());
                    ChocolateFactoryObjectStatus status = ChocolateFactoryObjectStatus.valueOf(tokens[8].trim());
                    String managerId = tokens[9].trim();

                    Location location = locationMap.get(locationId);
                    if (location == null) {
                        LOGGER.log(Level.WARNING, "Location with ID " + locationId + " not found for factory " + id);
                        continue; // Skip this factory object if location is not found
                    }

                    ArrayList<Chocolate> chocolates = parseChocolates(chocolatesString); // Parse chocolates from string

                    ChocolateFactoryObject factoryObject = new ChocolateFactoryObject(id, name, chocolates, openingTime,
                            closingTime, location, logo, rating, status, managerId);

                    factoryObjects.put(id, factoryObject);
                } catch (IllegalArgumentException | NullPointerException e) {
                    LOGGER.log(Level.SEVERE, "Error parsing line: " + line, e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading file: " + file.getAbsolutePath(), e);
        }
    }

    private ArrayList<Chocolate> parseChocolates(String chocolatesString) {
        ArrayList<Chocolate> chocolates = new ArrayList<>();

        if (chocolatesString == null || chocolatesString.isEmpty()) {
            return chocolates;
        }

        String[] chocolateIds = chocolatesString.split(",");

        for (String chocolateId : chocolateIds) {
            chocolateId = chocolateId.trim();

            if (!chocolateId.isEmpty()) {
                try {
                    Chocolate chocolate = loadChocolateById(chocolateId);
                    if (chocolate != null) {
                        chocolates.add(chocolate);
                    } else {
                        LOGGER.log(Level.WARNING, "Unable to load chocolate with ID: " + chocolateId);
                    }
                } catch (IllegalArgumentException | NullPointerException e) {
                    LOGGER.log(Level.SEVERE, "Error loading chocolate with ID: " + chocolateId, e);
                }
            }
        }

        return chocolates;
    }

    private Chocolate loadChocolateById(String chocolateId) {
        // Here you would use your ChocolateDAO to load chocolate by ID
        ChocolateDAO chocolateDAO = new ChocolateDAO(chocolateId); // Adjust this to your implementation
        return chocolateDAO.findChocolate(chocolateId); // Assuming a method like findChocolate exists
    }

    private void loadLocations(String contextPath) {
        File file = new File(contextPath + "/locations.txt");
        LOGGER.log(Level.INFO, "Loading Locations from: " + file.getAbsolutePath());

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length == 4) {
                    try {
                        String locationId = parts[0].trim();
                        double latitude = Double.parseDouble(parts[1].trim());
                        double longitude = Double.parseDouble(parts[2].trim());
                        String address = parts[3].trim();

                        // Validacija latitude i longitude
                        if (latitude < -90 || latitude > 90) {
                            LOGGER.log(Level.WARNING, "Invalid latitude value: " + latitude);
                            continue;
                        }
                        if (longitude < -180 || longitude > 180) {
                            LOGGER.log(Level.WARNING, "Invalid longitude value: " + longitude);
                            continue;
                        }

                        Location location = new Location(locationId, latitude, longitude, address);
                        locationMap.put(locationId, location);
                    } catch (NumberFormatException | NullPointerException e) {
                        LOGGER.log(Level.SEVERE, "Error parsing location: " + line, e);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading file: " + file.getAbsolutePath(), e);
        }
    }

    private boolean saveAllToTextFile() {
        File file = new File(filePath);

        LOGGER.log(Level.INFO, "Saving all factory objects to file: " + file.getAbsolutePath());

        try (FileWriter writer = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(writer);
             PrintWriter out = new PrintWriter(bw)) {

            for (ChocolateFactoryObject factoryObject : factoryObjects.values()) {
                String chocolatesString = factoryObject.getChocolates().stream()
                        .map(chocolate -> String.valueOf(chocolate.getId()))
                        .collect(Collectors.joining(","));

                LOGGER.log(Level.INFO, "Saving factory ID: " + factoryObject.getFactoryId() + ", Chocolates: " + chocolatesString);

                String line = factoryObject.getFactoryId() + ";" +
                              factoryObject.getName() + ";" +
                              chocolatesString + ";" +  // Ovo će uključiti sve čokolade za svaku fabriku
                              factoryObject.getOpeningTime() + ";" +
                              factoryObject.getClosingTime() + ";" +
                              factoryObject.getLocation().getLocationId() + ";" +
                              factoryObject.getLogo() + ";" +
                              factoryObject.getRating() + ";" +
                              factoryObject.getStatus() + ";" +
                              factoryObject.getManagerId();

                out.println(line);
            }

            LOGGER.log(Level.INFO, "Successfully saved all factory objects to file.");
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing to file: " + file.getAbsolutePath(), e);
            return false;
        }
    }


}
