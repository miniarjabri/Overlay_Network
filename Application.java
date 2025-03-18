import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application extends UnicastRemoteObject implements ApplicationInterface {
    private static final long serialVersionUID = 1L;
    private String logicalAddress;
    private String ipAddress;
    private int port;
    private List<ApplicationInterface> neighbors;
    private static final List<String> forbiddenWords = Arrays.asList("spam", "forbidden", "blocked");
    private static final int maxMessageLength = 200;

    public Application(String logicalAddress, String ipAddress, int port) throws RemoteException {
        super();
        this.logicalAddress = logicalAddress;
        this.ipAddress = ipAddress;
        this.port = port;
        this.neighbors = new ArrayList<>();
    }

    @Override
    public void addNeighbor(ApplicationInterface neighbor) throws RemoteException {
        neighbors.add(neighbor);
    }

    @Override
    public List<ApplicationInterface> getNeighbors() throws RemoteException {
        return neighbors;
    }

    @Override
    public void sendMessage(String message, String sender) throws RemoteException {
        if (!authorize(message)) {
            System.out.println("Autorisation refusée pour le message de " + sender + " : " + message);
            return;
        }
        System.out.println("[" + logicalAddress + "] Message reçu de " + sender + " : " + message);
        for (ApplicationInterface neighbor : neighbors) {
            neighbor.sendMessage(message, logicalAddress);
        }
    }

    @Override
    public void forwardMessage(String message, String sender, String nextHop) throws RemoteException {
        if (!authorize(message)) {
            System.out.println("Autorisation refusée pour le message de " + sender + " : " + message);
            return;
        }
        System.out.println("[" + logicalAddress + "] Message reçu de " + sender + " pour " + nextHop + " : " + message);
        for (ApplicationInterface neighbor : neighbors) {
            if (neighbor.getLogicalAddress().equals(nextHop)) {
                neighbor.forwardMessage(message, logicalAddress, nextHop);
                break;
            }
        }
    }

    private boolean authorize(String message) {
        if (message == null || message.trim().isEmpty()) return false;
        if (message.length() > maxMessageLength) return false;
        for (String word : forbiddenWords) {
            if (message.toLowerCase().contains(word.toLowerCase())) return false;
        }
        return Character.isUpperCase(message.charAt(0));
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
        return "Application{" + "logicalAddress='" + logicalAddress + '\'' + ", ipAddress='" + ipAddress + '\'' + ", port=" + port + '}';
    }
}
