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
            // Recharger toutes les App dans le RoutingManager depuis le registre RMI
            String[] allApps = {"App1", "App2", "App3", "App4", "App5", "App6"};
            for (String appName : allApps) {
                try {
                    ApplicationInterface app = (ApplicationInterface) Naming.lookup("rmi://localhost/" + appName);
                    RoutingManager.addApplication(app);
                } catch (Exception e) {
                    System.out.println("Erreur lors du chargement de " + appName + " dans le RoutingManager");
                }
            }

            List<ApplicationInterface> route = RoutingManager.findRoute(source, destination);
            if (route == null) {
                System.out.println("Aucun chemin trouvé de " + sourceName + " à " + destName);
                return;
            }
            for (int i = 0; i < route.size() - 1; i++) {
                ApplicationInterface current = route.get(i);
                ApplicationInterface next = route.get(i + 1);
                current.forwardMessage(message, current.getLogicalAddress(), next.getLogicalAddress());
                System.out.println("Message transmis de " + current.getLogicalAddress() + " à " + next.getLogicalAddress());
            }
            System.out.println("Le message a atteint " + destination.getLogicalAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
