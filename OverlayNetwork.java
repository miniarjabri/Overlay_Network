// OverlayNetwork class to manage the entire network setup
public class OverlayNetwork {
    public static void main(String[] args) {
        // Create instances of the applications with their logical addresses, IP addresses, ports, and RMI URLs
        Application app1 = new Application("App2-1", "192.168.1.1", 8080, "rmi://app1");
        // L'URL rmi://app1 fait référence à l'utilisation de RMI (Remote Method Invocation), un mécanisme de communication entre objets dans des applications distribuées en Java
        Application app2 = new Application("App2-2", "192.168.1.2", 8081, "rmi://app2");
        Application app3 = new Application("App3-1", "192.168.1.3", 8082, "rmi://app3");

        // Creating connections between applications
        Connection connection1 = new Connection(app1, app2);
        Connection connection2 = new Connection(app2, app3);
        Connection connection3 = new Connection(app3, app1);
        
        // Establish the connections
        connection1.establishConnection();
        connection2.establishConnection();
        connection3.establishConnection();

        // Close the connections (if needed)
        connection1.closeConnection();
        connection2.closeConnection();
        connection3.closeConnection();
    }
}
/* Le réseau de recouvrement: comme dans la photo
Niveau 2 : "App2-1", "App2-2" pour les applications de niveau 2.
Niveau 3 : "App3-1", "App3-2" pour les applications de niveau 3. */