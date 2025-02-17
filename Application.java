import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Implémentation de l'application en tant que service RMI
public class Application extends UnicastRemoteObject implements ApplicationInterface {
    private String logicalAddress;  // Adresse logique (ex: "App1")
    private String ipAddress;       // Adresse IP (ex: "192.168.1.1")
    private int port;               // Port de communication

    public Application(String logicalAddress, String ipAddress, int port) throws RemoteException {
        super(); // Permet l'exportation de l'objet en RMI
        this.logicalAddress = logicalAddress;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public String getLogicalAddress() throws RemoteException {
        return logicalAddress;
    }

    @Override
    public void sendMessage(String message, String sender) throws RemoteException {
        System.out.println(sender + " => " + logicalAddress + " : " + message);
    }

    @Override
    public String toString() {
        return "Application{" +
                "logicalAddress='" + logicalAddress + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", port=" + port + '}';
    }
}
