import java.rmi.Naming;
import java.util.List;

public class ApplicationClient {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage : java ApplicationClient <sourceAppName> <destAppName> <message>");
            System.exit(1);
        }
        String sourceName = args[0];
        String destName = args[1];
        String message = args[2];
        try {
            ApplicationInterface source = (ApplicationInterface) Naming.lookup("rmi://localhost/" + sourceName);
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
                if (AuthorizationManager.authorize(current, next, message)) {
                    next.sendMessage(message, current.getLogicalAddress());
                    System.out.println("Message de " + current.getLogicalAddress() + " transmis à " + next.getLogicalAddress());
                } else {
                    System.out.println("Transmission interrompue à " + current.getLogicalAddress());
                    return;
                }
            }
            System.out.println("Le message a atteint " + destination.getLogicalAddress());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
