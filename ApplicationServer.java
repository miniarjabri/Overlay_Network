import java.io.FileReader;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import org.json.JSONArray;
import org.json.JSONObject;

public class ApplicationServer {

    public static void main(String[] args) {
        try {
            // Charger la topologie à partir du fichier JSON
            String jsonFile = "topologie.json";
            String jsonContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(jsonFile)));

            JSONArray nodes = new JSONObject(jsonContent).getJSONArray("nodes");

            // Démarrage du registre RMI (si non déjà lancé)
            try {
                LocateRegistry.createRegistry(1099);
            } catch (Exception e) {
                // Le registre existe déjà
            }

            // Création des applications dynamiquement
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                String appName = node.getString("name");
                String ip = node.getString("ip");
                int port = node.getInt("port");

                // Création de l'application
                Application app = new Application(appName, ip, port);

                // Enregistrement via RMI
                Naming.rebind("rmi://localhost/" + appName, app);
                System.out.println(appName + " est enregistré et en attente de messages.");

                // Ajouter les voisins de chaque application
                // Ajouter les voisins de chaque application
                JSONArray neighbors = node.getJSONArray("neighbors");
                for (int j = 0; j < neighbors.length(); j++) {
                    String neighborName = neighbors.getString(j);
                    ApplicationInterface neighbor = lookupNeighbor(neighborName);
                    if (neighbor != null) {
                        app.addNeighbor(neighbor);
                        System.out.println("Voisin ajouté : " + neighborName);
                    } else {
                        System.out.println("Erreur : " + neighborName + " n'a pas pu être trouvé.");
                    }
                }


                // Petite pause pour permettre aux autres serveurs de démarrer
                Thread.sleep(3000);
            }

            // Le serveur reste actif pour répondre aux appels RMI
            while (true) {
                Thread.sleep(10000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
}
