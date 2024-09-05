package dao;

import beans.CustomerType;
import beans.enums.CustomerTypeName;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerTypeDAO {

    private Map<CustomerTypeName, CustomerType> customerTypes = new HashMap<>();
    private String filePath;
    private static final Logger LOGGER = Logger.getLogger(CustomerTypeDAO.class.getName());

    public CustomerTypeDAO(String contextPath) {
        this.filePath = contextPath + "/customerTypes.txt";
        loadCustomerTypes();
    }

    public CustomerType findCustomerType(CustomerTypeName name) {
        return customerTypes.get(name);
    }

    public Collection<CustomerType> findAll() {
        return customerTypes.values();
    }

    private void loadCustomerTypes() {
        File file = new File(filePath);
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length != 3) {
                    LOGGER.log(Level.WARNING, "Invalid format for line: " + line);
                    continue;
                }

                try {
                    CustomerTypeName typeName = CustomerTypeName.valueOf(parts[0].trim());
                    Double discount = Double.parseDouble(parts[1].trim());
                    Integer requiredPoints = Integer.parseInt(parts[2].trim());

                    CustomerType customerType = new CustomerType(typeName, discount, requiredPoints);
                    customerTypes.put(typeName, customerType);
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.WARNING, "Invalid data in line: " + line, e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading customer types from file", e);
        }
    }
}
