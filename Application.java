// ========================= Application.java (version finale) =========================
import java.rmi.Naming;
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
    private List<String> neighborNames;

    private static final List<String> forbiddenWords = Arrays.asList("spam", "forbidden", "blocked");
    private static final int maxMessageLength = 200;

    public Application(String logicalAddress, String ipAddress, int port) throws RemoteException {
        super();
        this.logicalAddress = logicalAddress;
        this.ipAddress = ipAddress;
        this.port = port;
        this.neighbors = new ArrayList<>();
        this.neighborNames = new ArrayList<>();
    }

    @Override
    public void addNeighbor(ApplicationInterface neighbor) throws RemoteException {
        neighbors.add(neighbor);
    }

    public void addNeighborName(String name) {
        neighborNames.add(name);
    }

    @Override
    public List<ApplicationInterface> getNeighbors() throws RemoteException {
        return neighbors;
    }

    public List<String> getNeighborNames() throws RemoteException {
        return neighborNames;
    }

    @Override
    public void sendMessage(String message, String sender) throws RemoteException {
        if (!authorize(message)) {
            System.out.println("[" + logicalAddress + "] Autorisation refusée pour le message de " + sender + " : " + message);
            return;
        }
        System.out.println("[" + logicalAddress + "] Message reçu directement de " + sender + " : " + message);
    }

        @Override
    public void forwardMessage(String message, String sender, String nextHop) throws RemoteException {
        if (!authorize(message)) {
            System.out.println("[" + logicalAddress + "] Autorisation refusée pour le message de " + sender + " : " + message);
            return;
        }

        if (this.logicalAddress.equals(nextHop)) {
            System.out.println("[" + logicalAddress + "] Message reçu de " + sender + " pour moi : " + message);
            System.out.println("Le message a atteint " + logicalAddress);
        } else {
            System.out.println("[" + logicalAddress + "] Message reçu de " + sender + " pour " + nextHop + " : " + message);
            try {
                ApplicationInterface next = (ApplicationInterface) Naming.lookup("rmi://localhost/" + nextHop);
                next.forwardMessage(message, this.logicalAddress, nextHop);
            } catch (Exception e) {
                System.out.println("[" + logicalAddress + "] Erreur lors du forward vers " + nextHop + " : " + e.getMessage());
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