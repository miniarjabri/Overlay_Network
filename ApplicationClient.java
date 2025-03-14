import java.rmi.Naming;
import java.util.List;

public class ApplicationClient {
    public static void main(String[] args) {
        // Vérification des arguments
        if (args.length < 3) {
            System.out.println("Usage : java ApplicationClient <sourceAppName> <destAppName> <message>");
            System.exit(1);
        }
        String sourceName = args[0];
        String destName = args[1];
        String message = args[2];
        
        try {
            // Recherche des applications source et destination via RMI
            ApplicationInterface source = (ApplicationInterface) Naming.lookup("rmi://localhost/App1");

//          ApplicationInterface source = (ApplicationInterface) Naming.lookup("rmi://localhost/" + sourceName);
            ApplicationInterface destination = (ApplicationInterface) Naming.lookup("rmi://localhost/" + destName);
            
            // Calcul de la route
            List<ApplicationInterface> route = RoutingManager.findRoute(source, destination);
            if (route == null) {
                System.out.println("Aucun chemin trouvé de " + sourceName + " à " + destName);
                return;
            }
            System.out.print("Route trouvée : ");
            for (ApplicationInterface app : route) {
                System.out.print(app.getLogicalAddress() + " ");
            }
            System.out.println();

            // Envoi du message de chaque nœud vers le suivant
            for (int i = 0; i < route.size() - 1; i++) {
                ApplicationInterface current = route.get(i);
                ApplicationInterface next = route.get(i + 1);
                
                // Envoi du message en vérifiant l'autorisation dans la méthode sendMessage
                current.sendMessage(message, current.getLogicalAddress());
                System.out.println("Message de " + current.getLogicalAddress() + " transmis à " + next.getLogicalAddress());
            }
            
            System.out.println("Le message a atteint " + destination.getLogicalAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
