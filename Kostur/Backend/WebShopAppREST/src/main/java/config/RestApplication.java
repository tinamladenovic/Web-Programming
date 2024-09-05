package config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rest")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(services.CorsFilter.class); // Registracija CORS filtera
        resources.add(services.ChocolateFactoryObjectService.class);
        resources.add(services.ChocolateService.class);
        resources.add(services.CommentService.class);
        resources.add(services.CustomerTypeService.class);
        resources.add(services.LocationService.class);
        resources.add(services.ProductService.class);
        resources.add(services.PurchaseService.class);
        resources.add(services.ShoppingCartItemService.class);
        resources.add(services.ShoppingCartService.class);
        resources.add(services.UserService.class);
        resources.add(services.LoginAndRegistrationService.class); // Dodajte LoginService klasu ovde
        return resources;
    }
}