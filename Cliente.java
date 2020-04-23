import java.rmi.registry.Registry;
import java.util.Scanner;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


public class Cliente {

    public String usuario;
    private final Scanner teclado;
    private final GestionDonaciones_I gestor;

    public Cliente(final GestionDonaciones_I gestor)
    {
        this.usuario = null;
        this.gestor = gestor;
        teclado = new Scanner(System.in);
    }

    public void cerrarTeclado()
    {
        this.teclado.close();
    }

    public String getUsuario()
    {
        return this.usuario;
    }

    public void registro()throws RemoteException
    {
        System.out.println("Escriba su nombre de usuario:");
        String usuarioT = teclado.nextLine();
        Boolean existe = gestor.registro(usuarioT);

        if (existe)
            System.out.println("El usuario ya existe");

        this.usuario = usuarioT;
        System.out.println("Se ha iniciado sesión con el usuario: " + this.getUsuario());
    }

    public void iniciarSesion() throws RemoteException
    {
        System.out.println("Escriba su nombre de usuario:");
        String usuarioT = teclado.nextLine();
        Boolean existe = gestor.iniciarSesion(usuarioT);
        if (existe)
            this.usuario = usuarioT;
        else
            System.out.println("El usuario no se encuentra registrado");
    }

    public void donar() throws RemoteException
    {
        System.out.println("Escriba la cantidad de dinero a donar:");
        String donacion = teclado.nextLine();
        Double donacionD = null;

        try {
            donacionD = Double.parseDouble(donacion);
        } catch (NumberFormatException e) {
            System.err.println("Debe de ser un número reconocible");
        }

        Boolean existe = gestor.donar(this.getUsuario(), donacionD);
        if (existe)
            System.out.println("La donacion se ha realizado con éxito");
        else
            System.out.println("No está registrado en el sistema");
        
    }

    public void consultarTotal() throws RemoteException
    {
        Double totalDonado = gestor.getTotal();
        System.out.println("El total donado hasta ahora es de " + totalDonado.toString() + " $");
    }

    public void consultarUsuario() throws RemoteException
    {
        if (this.usuario != null)
        {
            Double donacionUsuario = gestor.getUsuario(this.usuario);
            System.out.println(this.usuario + " ha donado " + donacionUsuario.toString() + " $");
        }
        else
        {
            System.out.println("Debe estar registrado en el sistema");
        }
    }


    //MAIN
    public static void main(final String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {

            final Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
            final GestionDonaciones_I gestor = (GestionDonaciones_I) registry.lookup("servidor0");
            
            System.out.println("BIENVENIDO A LA GESTIÓN DE DONACIONES");

            final String menu = "\nELIGA UNA OPCIÓN:"+
            "\n\tR: Registro de usuario"+
            "\n\tI: Iniciar sesión" +
            "\n\tD: Donar"+
            "\n\tT: Consultar total donado"+
            "\n\tU: Consultar donacion del usuario"+
            "\n\tS: Salir";

            final Scanner teclado = new Scanner (System.in);
            String opcion;
            final Cliente cliente = new Cliente(gestor);

            do {

                System.out.println(menu);
                opcion = teclado.nextLine();
                opcion = opcion.toUpperCase();
                
                switch(opcion)
                {
                    case "R":
                        cliente.registro();
                        break;

                    case "I":
                        cliente.iniciarSesion();
                        break;

                    case "D":
                        cliente.donar();
                        break;

                    case "S":
                        System.out.println("Gracias por usar nuestros servicios");
                        break;
                    case "T":
                        cliente.consultarTotal();
                        break;
                    case "U":
                        cliente.consultarUsuario();
                        break;
                    default:
                        System.out.println("Debe elegir una opcion correcta");
                        break;
                }

            }while (!opcion.equals("S"));
            
            teclado.close();
            cliente.cerrarTeclado();
            
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}