import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class GestionDonaciones extends UnicastRemoteObject implements GestionDonaciones_I, 
GestionServidores_I {
    private String nombre;
    private int num;
    private String replica;
    private Double subtotal;
    private HashMap<String, Double> clientes;

    /****Gestion de donaciones*****/

    public GestionDonaciones(String nombre, int num) throws RemoteException {
        super();
        this.nombre = nombre;
        this.num = num;
        if (this.num == 0) {
            this.replica = "servidor1";
        } else {
            this.replica = "servidor0";
        }

        subtotal = new Double (0.0);
        clientes = new HashMap<>();
    }

    public Boolean registro(String usuario) throws RemoteException {

        Boolean existe = false;

        // Miramos si existe en nuestra replica
        if (clientes.get(usuario) == null) {

            GestionServidores_I replicaServ = getReplica();
            // Si no es así miramos en la otra réplica
            if (!replicaServ.existeUsuario(usuario))
            {
                //Si no existe en ninguna de las réplicas 
                //la introducimos en la que tenga menos clientes
                if (this.clientes.size() <= replicaServ.getSize())
                {

                    this.introducirUsuario(usuario);
                    System.out.println("Se ha introducido un usuario en el " + this.nombre);
                }
                else
                {
                    replicaServ.introducirUsuario(usuario);
                    System.out.println("Se ha introducido un usuario en el " + this.replica);
                }
            }
            else
            {
                existe = true;
            }


        }
        else
        {
            existe = true;
        }

        return existe;
    }

    public Boolean iniciarSesion(String usuario)throws RemoteException
    {
        Boolean existe = false;
        GestionServidores_I replicaServ = getReplica();
        if (clientes.get(usuario) != null || replicaServ.existeUsuario(usuario))
            existe = true;

        return existe;
    }

    public Boolean donar(String usuario, Double donacion) throws RemoteException
    {
        Boolean existe = false;
        GestionServidores_I replicaServ = getReplica();
        if (donacion != null)
        {
            if (clientes.get(usuario) != null)
            {
                existe = true;
                this.sumarDonacion(usuario, donacion);


            }
            else if (replicaServ.existeUsuario(usuario))
            {
                existe = true;
                replicaServ.sumarDonacion(usuario, donacion);

            }
        }

        return existe;   
    }

    public Double getTotal () throws RemoteException
    {
        Double total = new Double(0.0);
        total += this.subtotal;
        GestionServidores_I replicaServ = this.getReplica();
        total += replicaServ.getSubtotal();
        return total;
    }

    public Double getUsuario(String usuario) throws RemoteException
    {
        Double donacion = new Double (0.0);
        GestionServidores_I replicaServ = this.getReplica();
        if (clientes.get(usuario) != null)
        {
            donacion = clientes.get(usuario);

        }
        else if (replicaServ.existeUsuario(usuario))
        {
            donacion = replicaServ.getDonacionUsuario(usuario);
        }

        return donacion;

    }


    /****Gestion de servidores*****/

    public Boolean existeUsuario (String usuario) throws RemoteException
    {
        Boolean existe = false;

        if (this.clientes.get(usuario) != null)
            existe = true;
            
        return existe;
    }

    public GestionServidores_I getReplica() throws RemoteException
    {
        GestionServidores_I servReplica = null;
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
            servReplica = (GestionServidores_I) registry.lookup(this.replica);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return servReplica;
    }

    public void introducirUsuario (String usuario) throws RemoteException
    {
        this.clientes.put(usuario, 0.0);
    }

    public int getSize() throws RemoteException
    {
        return this.clientes.size();
    }

    public Double getSubtotal()
    {
        return this.subtotal;
    }

    public void sumarDonacion(String usuario, Double donacion)
    {
        this.clientes.put(usuario, clientes.get(usuario) + donacion);
        this.subtotal += donacion;
        System.out.println("Se han donado " + donacion + "$ al " + this.nombre);
    }

    public Double getDonacionUsuario(String usuario) throws RemoteException
    {
        Double donacion = new Double (0.0);
        donacion = this.clientes.get(usuario);
        return donacion;
    }

}