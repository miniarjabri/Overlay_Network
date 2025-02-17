import java.rmi.Remote;
import java.rmi.RemoteException;

// Interface RMI pour envoyer des messages
public interface ApplicationInterface extends Remote {
    void sendMessage(String message, String sender) throws RemoteException;
    String getLogicalAddress() throws RemoteException;
}
