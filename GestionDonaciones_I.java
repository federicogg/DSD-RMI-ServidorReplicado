import java.rmi.Remote;
import java.rmi.RemoteException;


public interface GestionDonaciones_I extends Remote {
    public Boolean registro (String usuario) throws RemoteException;
    public Boolean iniciarSesion(String usuario) throws RemoteException;
    public Boolean donar(String usuario, Double donacion) throws RemoteException;
    public Double getTotal() throws RemoteException;
    public Double getUsuario(String usuario) throws RemoteException;
}