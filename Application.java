import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Application extends UnicastRemoteObject implements ApplicationInterface {
    private static final long serialVersionUID = 1L;
    private String logicalAddress;
    private String ipAddress;
    private int port;
    private List<ApplicationInterface> neighbors;

    public Application(String logicalAddress, String ipAddress, int port) throws RemoteException {
        super();
        this.logicalAddress = logicalAddress;
        this.ipAddress = ipAddress;
        this.port = port;
        this.neighbors = new ArrayList<>();
    }

    public void addNeighbor(ApplicationInterface neighbor) {
        neighbors.add(neighbor);
    }

    @Override
    public List<ApplicationInterface> getNeighbors() throws RemoteException {
        return neighbors;
    }

    @Override
    public void sendMessage(String message, String sender) throws RemoteException {
        // Affichage du message reçu
        System.out.println("[" + logicalAddress + "] Message reçu de " + sender + " : " + message);
    }

    @Override
    public String getLogicalAddress() throws RemoteException {
        return logicalAddress;
    }

    @Override
    public String getIpAddress() throws RemoteException {
        return ipAddress;
    }

    @Override
    public String toString() {
        return "Application{" +
                "logicalAddress='" + logicalAddress + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                '}';
    }
}
