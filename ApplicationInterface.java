import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ApplicationInterface extends Remote {
    void sendMessage(String message, String sender) throws RemoteException;
    void forwardMessage(String message, String sender, String nextHop) throws RemoteException;
    void addNeighbor(ApplicationInterface neighbor) throws RemoteException;
    String getLogicalAddress() throws RemoteException;
    List<ApplicationInterface> getNeighbors() throws RemoteException;
    String getIpAddress() throws RemoteException;
}
