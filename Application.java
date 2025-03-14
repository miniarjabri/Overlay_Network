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
    // Liste de mots interdits et longueur maximale
    private static final List<String> forbiddenWords = Arrays.asList("spam", "forbidden", "blocked");
    private static final int maxMessageLength = 200;

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
        // Vérifications d'autorisation
        if (!authorize(message)) {
            System.out.println("Autorisation refusée pour le message de " + sender + " : " + message);
            return;
        }
        
        System.out.println("[" + logicalAddress + "] Message reçu de " + sender + " : " + message);

        // Diffusion du message à tous les voisins
        for (ApplicationInterface neighbor : neighbors) {
            neighbor.sendMessage(message, logicalAddress);
        }
    }

    // Nouvelle méthode pour recevoir un message
    public void receiveMessage(String message) {
        System.out.println("[" + logicalAddress + "] Message reçu : " + message);
        // Traitement supplémentaire pour le message
    }

    // Méthode d'autorisation directement intégrée dans la classe Application
    private boolean authorize(String message) {
        // Vérifications de base
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Autorisation refusée : le message est vide.");
            return false;
        }
        if (message.length() > maxMessageLength) {
            System.out.println("Autorisation refusée : le message est trop long (" + message.length() + " caractères).");
            return false;
        }
        for (String word : forbiddenWords) {
            if (message.toLowerCase().contains(word.toLowerCase())) {
                System.out.println("Autorisation refusée : le message contient le mot interdit \"" + word + "\".");
                return false;
            }
        }
        if (!Character.isUpperCase(message.charAt(0))) {
            System.out.println("Autorisation refusée : le message doit commencer par une majuscule.");
            return false;
        }
        
        System.out.println("Autorisation accordée pour le message : " + message);
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
        return "Application{" +
                "logicalAddress='" + logicalAddress + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                '}';
    }
}
