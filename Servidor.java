import java.rmi.*;
import java.rmi.registry.LocateRegistry;

public class Servidor{
    public static void main(String[] args) {

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {

            LocateRegistry.createRegistry(1099);
            GestionDonaciones servidor0 = new GestionDonaciones("servidor0", 0);
            Naming.rebind("servidor0", servidor0);
            System.out.println("Servidor 0 preparado");

            GestionDonaciones servidor1 = new GestionDonaciones("servidor1", 1);
            Naming.rebind("servidor1", servidor1);
            System.out.println("Servidor 1 preparado");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}