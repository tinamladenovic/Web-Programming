package dao;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.Chocolate;
import beans.enums.ChocolateKind;
import beans.enums.ChocolateStatus;
import beans.enums.ChocolateType;

public class ChocolateDAO {
    private Map<String, Chocolate> chocolates = new HashMap<>();
    private String contextPath;
    private static final Logger LOGGER = Logger.getLogger(ChocolateDAO.class.getName());

    public ChocolateDAO(String contextPath) {
        this.contextPath = contextPath.endsWith(File.separator) ? contextPath : contextPath + File.separator;
        loadChocolates();
    }

    public Chocolate find(String id) {
        return chocolates.get(id);
    }

    public Map<String, Chocolate> findAll() {
        return chocolates;
    }

    public void delete(String id) {
        chocolates.remove(id);
        saveChocolates();
    }

    private void loadChocolates() {
        File file = new File(contextPath + "chocolates.txt");
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                StringTokenizer st = new StringTokenizer(line, ";");
                String id = st.nextToken().trim();
                String name = st.nextToken().trim();
                double price = Double.parseDouble(st.nextToken().trim());
                ChocolateType type = ChocolateType.valueOf(st.nextToken().trim());
                String factoryId = st.nextToken().trim();
                ChocolateKind kind = ChocolateKind.valueOf(st.nextToken().trim());
                double weight = Double.parseDouble(st.nextToken().trim());
                String description = st.nextToken().trim();
                String picture = st.nextToken().trim();
                ChocolateStatus status = ChocolateStatus.valueOf(st.nextToken().trim());
                int quantity = Integer.parseInt(st.nextToken().trim());

                Chocolate chocolate = new Chocolate(id, name, price, type, factoryId, kind, weight, description, picture, status, quantity);
                chocolates.put(id, chocolate);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading chocolates from file", e);
        }
    }

    private void saveChocolates() {
        File file = new File(contextPath + "chocolates.txt");
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            for (Chocolate chocolate : chocolates.values()) {
                out.write(String.join(";",
                    chocolate.getId(),
                    chocolate.getName(),
                    String.valueOf(chocolate.getPrice()),
                    chocolate.getType().toString(),
                    chocolate.getFactoryId(), 
                    chocolate.getKind().toString(),
                    String.valueOf(chocolate.getWeight()),
                    chocolate.getDescription(),
                    chocolate.getPicture(),
                    chocolate.getStatus().toString(),
                    String.valueOf(chocolate.getQuantity())
                ));
                out.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving chocolates to file", e);
        }
    }

    public Chocolate update(String id, Chocolate updatedChocolate) {
        if (chocolates.containsKey(id)) {
            chocolates.put(id, updatedChocolate);
            saveChocolates();
            return updatedChocolate;
        }
        return null;
    }

    public Chocolate create(Chocolate newChocolate) {
        // Generiše novi ID ako ne postoji ili ako je već zauzet
        if (newChocolate.getId() == null || newChocolate.getId().isEmpty() || chocolates.containsKey(newChocolate.getId())) {
            newChocolate.setId(generateNextId());
        }
        chocolates.put(newChocolate.getId(), newChocolate);
        saveChocolates();
        return newChocolate;
    }

    private String generateNextId() {
        int maxId = 0;
        for (String id : chocolates.keySet()) {
            try {
                int numericId = Integer.parseInt(id);
                if (numericId > maxId) {
                    maxId = numericId;
                }
            } catch (NumberFormatException e) {
                // Ako ID nije numerički, preskače se
                continue;
            }
        }
        return String.valueOf(maxId + 1);
    }
	

    public Chocolate findChocolate(String chocolateId) {
        return chocolates.get(chocolateId);
    }

    public List<Chocolate> getChocolatesByFactoryId(String factoryId) {
        List<Chocolate> chocolatesByFactory = new ArrayList<>();
        for (Chocolate chocolate : chocolates.values()) {  
            if (chocolate.getFactoryId().equals(factoryId)) {
                chocolatesByFactory.add(chocolate);
            }
        }
        return chocolatesByFactory;
    }

    public Chocolate findChocolateById(String chocolateId) {
        // Pretpostavljamo da je 'chocolates' mapa koja sadrži sve čokolade sa njihovim ID-jem kao ključem
        if (chocolates.containsKey(chocolateId)) {
            return chocolates.get(chocolateId);
        } else {
            System.out.println("Chocolate with ID " + chocolateId + " not found.");
            return null;
        }
    }

}
