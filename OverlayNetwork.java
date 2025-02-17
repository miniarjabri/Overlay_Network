import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class OverlayNetwork {
    public static void main(String[] args) {
        try {
            // Démarrer le registre RMI sur le port 1099
            LocateRegistry.createRegistry(1099);

            // Créer les applications
            Application app1 = new Application("App1", "192.168.1.1", 8080);
            Application app2 = new Application("App2", "192.168.1.2", 8081);
            Application app3 = new Application("App3", "192.168.1.3", 8082);

            // Enregistrer les applications sur RMI
            Naming.rebind("rmi://localhost/App1", app1);
            Naming.rebind("rmi://localhost/App2", app2);
            Naming.rebind("rmi://localhost/App3", app3);

            System.out.println("Applications enregistrées sur RMI.");

            // Récupérer les applications distantes
            ApplicationInterface remoteApp1 = (ApplicationInterface) Naming.lookup("rmi://localhost/App1");
            ApplicationInterface remoteApp2 = (ApplicationInterface) Naming.lookup("rmi://localhost/App2");
            ApplicationInterface remoteApp3 = (ApplicationInterface) Naming.lookup("rmi://localhost/App3");

            // Créer les connexions
            Connection connection1 = new Connection(remoteApp1, remoteApp2);
            Connection connection2 = new Connection(remoteApp2, remoteApp3);
            Connection connection3 = new Connection(remoteApp3, remoteApp1);

            // Établir les connexions
            connection1.establishConnection();
            connection2.establishConnection();
            connection3.establishConnection();

            // Envoyer des messages via RMI
            connection1.sendMessage("Hello, App2 !");
            connection2.sendMessage("Hello, App3 !");
            connection3.sendMessage("Hello, App1 !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
