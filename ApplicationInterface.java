import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ApplicationInterface extends Remote {
    void addNeighbor(ApplicationInterface neighbor) throws RemoteException;
    List<ApplicationInterface> getNeighbors() throws RemoteException;
    void sendMessage(String message, String sender) throws RemoteException;
    void forwardMessage(String message, String sender, String nextHop) throws RemoteException;
    void broadcastMessage(String message, String sender, String messageId) throws RemoteException;
    void multicastMessage(String message, String sender, String messageId) throws RemoteException;
    String getLogicalAddress() throws RemoteException;
    String getIpAddress() throws RemoteException;
    List<String> getGroups() throws RemoteException; // Added method
}