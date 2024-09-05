package dao;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import beans.Chocolate;
import beans.Purchase;
import beans.enums.PurchaseStatus;

public class PurchaseDAO {
    private Map<String, Purchase> purchases = new HashMap<>();
    private String contextPath;
    private static final Logger LOGGER = Logger.getLogger(PurchaseDAO.class.getName());

    public PurchaseDAO(String contextPath) {
        this.contextPath = contextPath.endsWith(File.separator) ? contextPath : contextPath + File.separator;
        initializeStaticPurchases();
        loadPurchases();
    }
    
    public Purchase find(String id) {
        return purchases.get(id);
    }

    public Map<String, Purchase> findAll() {
        return purchases;
    }

    public Purchase create(Purchase newPurchase) {
        String id = generateUniqueId();
        newPurchase.setId(id);
        purchases.put(id, newPurchase);
        savePurchases();
        return newPurchase;
    }

    public Purchase update(String id, Purchase updatedPurchase) {
        if (purchases.containsKey(id)) {
            purchases.put(id, updatedPurchase);
            savePurchases();
            return updatedPurchase;
        }
        return null;
    }

    public void delete(String id) {
        purchases.remove(id);
        savePurchases();
    }

    private void loadPurchases() {
        File file = new File(contextPath + "purchases.txt");
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                StringTokenizer st = new StringTokenizer(line, ";");
                String id = st.nextToken().trim();
                String chocolatesString = st.nextToken().trim(); // String of chocolate IDs
                String factoryId = st.nextToken().trim();
                LocalDateTime purchaseDateTime = LocalDateTime.parse(st.nextToken().trim(), DateTimeFormatter.ISO_DATE_TIME);
                double price = Double.parseDouble(st.nextToken().trim());
                String customerName = st.nextToken().trim();
                PurchaseStatus status = PurchaseStatus.valueOf(st.nextToken().trim());

                List<Chocolate> chocolates = parseChocolates(chocolatesString);

                Purchase purchase = new Purchase(id, chocolates, factoryId, purchaseDateTime, price, customerName, status);
                purchases.put(id, purchase);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading purchases from file", e);
        }
    }

    private void savePurchases() {
        File file = new File(contextPath + "purchases.txt");
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            for (Purchase purchase : purchases.values()) {
                String chocolatesString = purchase.getChocolates().stream()
                        .map(chocolate -> chocolate.getId())
                        .collect(Collectors.joining(","));

                String line = String.join(";",
                        purchase.getId(),
                        chocolatesString,
                        purchase.getFactoryId(),
                        purchase.getPurchaseDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                        String.valueOf(purchase.getPrice()),
                        purchase.getCustomerName(),
                        purchase.getStatus().toString()
                );
                out.write(line);
                out.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving purchases to file", e);
        }
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    private List<Chocolate> parseChocolates(String chocolatesString) {
        List<Chocolate> chocolates = new ArrayList<>();
        if (chocolatesString == null || chocolatesString.isEmpty()) {
            return chocolates;
        }

        String[] chocolateIds = chocolatesString.split(",");
        for (String chocolateId : chocolateIds) {
            chocolateId = chocolateId.trim();
            if (!chocolateId.isEmpty()) {
                Chocolate chocolate = loadChocolateById(chocolateId);
                if (chocolate != null) {
                    chocolates.add(chocolate);
                }
            }
        }
        return chocolates;
    }

    private Chocolate loadChocolateById(String chocolateId) {
        ChocolateDAO chocolateDAO = new ChocolateDAO(contextPath);
        Chocolate chocolate = chocolateDAO.findChocolateById(chocolateId);

       

        return chocolate;
    }

    private void initializeStaticPurchases() {
        List<Chocolate> chocolates = new ArrayList<>();
        String factoryId = "staticFactoryID1";
        Purchase purchase = new Purchase("staticID1", chocolates, factoryId, LocalDateTime.now(), 100.0, "John Doe", PurchaseStatus.PROCESSING);
        purchases.put(purchase.getId(), purchase);
    }

    public Collection<Purchase> findByFactoryId(String factoryId) {
        return purchases.values().stream()
            .filter(purchase -> factoryId.equals(purchase.getFactoryId()))
            .collect(Collectors.toList());
    }

    public Collection<Purchase> getPurchasesByCustomerName(String customerName) {
        return purchases.values().stream()
            .filter(purchase -> purchase.getCustomerName().equalsIgnoreCase(customerName))
            .collect(Collectors.toList());
    }



}
