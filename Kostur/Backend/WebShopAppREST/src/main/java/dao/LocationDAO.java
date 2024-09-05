package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.Location;

public class LocationDAO {
    private Map<String, Location> locations = new HashMap<>();
    private String filePath;
    private static final Logger LOGGER = Logger.getLogger(LocationDAO.class.getName());

    public LocationDAO() {
    }

    public LocationDAO(String contextPath) {
        this.filePath = contextPath + "/locations.txt";
        loadLocations();
    }

    public Map<String, Location> getLocations() {
        return locations;
    }

    public Location findLocation(String id) {
        return locations.get(id);
    }

    public boolean createLocation(Location location) {
        try {
            // Generisanje ID-a za novu lokaciju
            String newId = generateNewLocationId();
            location.setLocationId(newId);

            locations.put(location.getLocationId(), location);
            boolean success = saveLocationsToFile();
            if (success) {
                LOGGER.log(Level.INFO, "Location created: {0}", location);
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating location: {0}", e);
            return false;
        }
    }


    private String generateNewLocationId() {
        int maxId = locations.keySet().stream()
                .mapToInt(id -> {
                    try {
                        return Integer.parseInt(id);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);

        return String.valueOf(maxId + 1);
    }


	public boolean updateLocation(Location location) {
        if (!locations.containsKey(location.getLocationId())) {
            LOGGER.log(Level.WARNING, "Location with ID {0} not found", location.getLocationId());
            return false; // Lokacija ne postoji
        }
        locations.put(location.getLocationId(), location);
        boolean success = saveLocationsToFile();
        if (success) {
            LOGGER.log(Level.INFO, "Location updated: {0}", location);
        }
        return success;
    }

    public boolean deleteLocation(String id) {
        Location removed = locations.remove(id);
        if (removed != null) {
            boolean success = saveLocationsToFile();
            if (success) {
                LOGGER.log(Level.INFO, "Location deleted: {0}", removed);
            }
            return success;
        }
        LOGGER.log(Level.WARNING, "Location with ID {0} not found for deletion", id);
        return false;
    }

    private void loadLocations() {
        File file = new File(filePath);
        LOGGER.log(Level.INFO, "Loading locations from: {0}", file.getAbsolutePath());

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                StringTokenizer st = new StringTokenizer(line, ";");
                String locationId = st.nextToken().trim();
                double latitude = Double.parseDouble(st.nextToken().trim());
                double longitude = Double.parseDouble(st.nextToken().trim());
                String address = st.nextToken().trim();

                // Validacija latitude i longitude
                if (latitude < -90 || latitude > 90) {
                    LOGGER.log(Level.WARNING, "Invalid latitude value: {0}", latitude);
                    continue;
                }
                if (longitude < -180 || longitude > 180) {
                    LOGGER.log(Level.WARNING, "Invalid longitude value: {0}", longitude);
                    continue;
                }

                Location location = new Location(locationId, latitude, longitude, address);
                locations.put(locationId, location);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading locations from file", e);
        }
    }

    private boolean saveLocationsToFile() {
        File file = new File(filePath);
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            for (Location location : locations.values()) {
                out.write(formatLocationForFile(location));
                out.newLine();
            }
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving locations to file", e);
            return false;
        }
    }

    private String formatLocationForFile(Location location) {
        return String.join(";",
                location.getLocationId(),
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()),
                location.getAddress());
    }
}
