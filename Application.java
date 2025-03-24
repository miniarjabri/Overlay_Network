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
    private List<String> groups;
    private List<String> seenMessageIds = new ArrayList<>();

    private static final List<String> forbiddenWords = Arrays.asList("spam", "forbidden", "blocked");
    private static final int maxMessageLength = 200;

    public Application(String logicalAddress, String ipAddress, int port, List<String> groups) throws RemoteException {
        super();
        this.logicalAddress = logicalAddress;
        this.ipAddress = ipAddress;
        this.port = port;
        this.neighbors = new ArrayList<>();
        this.neighborNames = new ArrayList<>();
        this.groups = groups != null ? new ArrayList<>(groups) : new ArrayList<>();
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

    public List<String> getGroups() throws RemoteException {
        return groups;
    }

    @Override
    public void sendMessage(String message, String sender) throws RemoteException {
        if (!authorize(message)) {
            System.out.println("[" + logicalAddress + "] Autorisation refusée pour le message de " + sender + " : " + message);
            return;
        }
        System.out.println("[" + logicalAddress + "] Message recu directement de " + sender + " : " + message);
    }

    @Override
    public void forwardMessage(String message, String sender, String nextHop) throws RemoteException {
        if (!authorize(message)) {
            System.out.println("[" + logicalAddress + "] Autorisation refusée pour le message de " + sender + " : " + message);
            return;
        }
        if (this.logicalAddress.equals(nextHop)) {
            System.out.println("[" + logicalAddress + "] J'ai recu le message de " + sender + " : " + message);
            System.out.println("Le message a atteint " + logicalAddress);
        } else {
            System.out.println("[" + logicalAddress + "] Message reçu de " + sender + " pour " + nextHop + " : " + message);
            try {
                ApplicationInterface next = (ApplicationInterface) Naming.lookup("rmi://localhost/" + nextHop);
                next.forwardMessage(message, this.logicalAddress, nextHop);
                System.out.println("[" + logicalAddress + "] Message transmis de " + logicalAddress + " à " + nextHop);
            } catch (Exception e) {
                System.out.println("[" + logicalAddress + "] Erreur lors du forward vers " + nextHop + " : " + e.getMessage());
            }
        }
    }

    @Override
    public void broadcastMessage(String message, String sender, String messageId) throws RemoteException {
        if (seenMessageIds.contains(messageId)) {
            return;
        }
        seenMessageIds.add(messageId);

        if (!authorize(message)) {
            return;
        }

        System.out.println("[" + logicalAddress + "] J'ai recu le message de " + sender + " : " + message);

        String[] allApps = {"App1", "App2", "App3", "App4", "App5", "App6"};
        String originalSender = messageId.split("_")[0];
        for (String appName : allApps) {
            if (!appName.equals(logicalAddress) && !appName.equals(sender) && !appName.equals(originalSender)) {
                try {
                    ApplicationInterface app = (ApplicationInterface) Naming.lookup("rmi://localhost/" + appName);
                    app.broadcastMessage(message, originalSender, messageId);
                } catch (Exception e) {
                    // Ignorer les erreurs silencieusement
                }
            }
        }
    }

    @Override
    public void multicastMessage(String message, String sender, String messageId) throws RemoteException {
        if (seenMessageIds.contains(messageId)) {
            return; // Ignorer les messages déjà vus
        }
        seenMessageIds.add(messageId);
    
        if (!authorize(message)) {
            return; // Ignorer les messages non autorisés
        }
    
        // Extraire le groupe et l'émetteur original du messageId
        String[] messageIdParts = messageId.split("_");
        String group = messageIdParts.length > 1 ? messageIdParts[1] : "default";
        String originalSender = messageIdParts[0];
    
        // Vérifier si l'émetteur initial appartient au groupe
        if (logicalAddress.equals(originalSender) && !groups.contains(group)) {
            System.out.println("[" + logicalAddress + "] Je ne peux pas envoyer un multicast au groupe " + group + " car je n'y appartient pas.");
            return;
        }
    
        // Afficher le message uniquement si ce nœud appartient au groupe ET n'est pas l'émetteur initial
        if (groups.contains(group) && !logicalAddress.equals(originalSender)) {
            System.out.println("[" + logicalAddress + "] J'ai reçu le message de " + originalSender + " pour groupe " + group + " : " + message);
        }
    
        // Propager aux voisins, même si ce nœud n'est pas dans le groupe
        for (ApplicationInterface neighbor : neighbors) {
            String neighborAddr = neighbor.getLogicalAddress();
            if (!neighborAddr.equals(sender) && !neighborAddr.equals(originalSender)) {
                try {
                    neighbor.multicastMessage(message, logicalAddress, messageId);
                } catch (Exception e) {
                    // Ignorer les erreurs silencieusement
                }
            }
        }
    }

    private boolean authorize(String message) {
        if (message == null || message.trim().isEmpty()) return false;
        if (message.length() > maxMessageLength) return false;
        for (String word : forbiddenWords) {
            if (message.toLowerCase().contains(word.toLowerCase())) return false;
        }
        return true;
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
        return "Application{" + "logicalAddress='" + logicalAddress + '\'' + ", ipAddress='" + ipAddress + '\'' + ", port=" + port + ", groups=" + groups + '}';
    }
}