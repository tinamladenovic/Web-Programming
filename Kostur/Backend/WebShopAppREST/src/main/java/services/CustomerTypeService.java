package services;

import beans.CustomerType;
import beans.enums.CustomerTypeName;
import dao.CustomerTypeDAO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/customer-types")
public class CustomerTypeService {

    private CustomerTypeDAO customerTypeDAO;

    public CustomerTypeService() {
        String contextPath = getClass().getClassLoader().getResource("").getPath();
        this.customerTypeDAO = new CustomerTypeDAO(contextPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<CustomerType> getCustomerTypes() {
        return customerTypeDAO.findAll();
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerType(@PathParam("name") String name) {
        try {
            CustomerTypeName typeName = CustomerTypeName.valueOf(name.toUpperCase());
            CustomerType customerType = customerTypeDAO.findCustomerType(typeName);
            if (customerType != null) {
                return Response.ok(customerType).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Customer type not found").build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid customer type name").build();
        }
    }
}
