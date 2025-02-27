import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ApplicationServer {
    
    // Méthode utilitaire pour chercher un voisin avec plusieurs tentatives
    private static ApplicationInterface lookupNeighbor(String neighborName) {
        ApplicationInterface neighbor = null;
        int attempts = 0;
        while (neighbor == null && attempts < 10) { // réessayer jusqu'à 10 fois
            try {
                neighbor = (ApplicationInterface) Naming.lookup("rmi://localhost/" + neighborName);
            } catch (Exception e) {
                attempts++;
                System.out.println("En attente de " + neighborName + " (" + attempts + "/10)");
                try {
                    Thread.sleep(2000); // attente de 2 secondes avant de réessayer
                } catch (InterruptedException ie) {
                    // Ignorer l'interruption
                }
            }
        }
        return neighbor;
    }
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage : java ApplicationServer <AppName>");
            System.exit(1);
        }
        String appName = args[0];
        try {
            // Démarrage du registre RMI (si non déjà lancé)
            try {
                LocateRegistry.createRegistry(1099);
            } catch (Exception e) {
                // Le registre existe déjà
            }
            
            Application app;
            // Attribution d'une adresse IP et d'un port selon l'application
            switch (appName) {
                case "App1":
                    app = new Application("App1", "192.168.1.1", 8080);
                    break;
                case "App2":
                    // App2 sur un réseau différent pour simuler la contrainte
                    app = new Application("App2", "10.0.0.2", 8081);
                    break;
                case "App3":
                    app = new Application("App3", "192.168.1.3", 8082);
                    break;
                default:
                    System.out.println("Application inconnue.");
                    return;
            }
            
            // Enregistrement via RMI
            Naming.rebind("rmi://localhost/" + appName, app);
            System.out.println(appName + " est enregistré et en attente de messages.");
            
            // Petite pause pour permettre aux autres serveurs de démarrer
            Thread.sleep(3000);
            
            // Configuration des voisins selon la topologie
            if (appName.equals("App1")) {
                // App1 doit transmettre via App3
                ApplicationInterface neighbor = lookupNeighbor("App3");
                if (neighbor != null) {
                    app.addNeighbor(neighbor);
                    System.out.println("Voisin ajouté : App3");
                } else {
                    System.out.println("Impossible de trouver App3 après plusieurs tentatives.");
                }
            }
            if (appName.equals("App3")) {
                // App3 transmet à App2
                ApplicationInterface neighbor = lookupNeighbor("App2");
                if (neighbor != null) {
                    app.addNeighbor(neighbor);
                    System.out.println("Voisin ajouté : App2");
                } else {
                    System.out.println("Impossible de trouver App2 après plusieurs tentatives.");
                }
            }
            
            // Le serveur reste actif pour répondre aux appels RMI
            while (true) {
                Thread.sleep(10000);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
