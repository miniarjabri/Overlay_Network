import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import org.json.JSONArray;
import org.json.JSONObject;

public class ApplicationServer {
    public static void main(String[] args) {
        try {
            String jsonFile = "topologie.json";
            String jsonContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(jsonFile)));
            JSONArray nodes = new JSONObject(jsonContent).getJSONArray("nodes");

            try { LocateRegistry.createRegistry(1099); } catch (Exception ignored) {}

            ApplicationInterface[] apps = new ApplicationInterface[nodes.length()];

            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                String appName = node.getString("name");
                String ip = node.getString("ip");
                int port = node.getInt("port");

                Application app = new Application(appName, ip, port);
                Naming.rebind("rmi://localhost/" + appName, app);
                RoutingManager.addApplication(app);
                apps[i] = app;
                System.out.println(appName + " est enregistré.");
            }

            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                JSONArray neighbors = node.getJSONArray("neighbors");
                ApplicationInterface app = apps[i];

                for (int j = 0; j < neighbors.length(); j++) {
                    String neighborName = neighbors.getString(j);
                    ApplicationInterface neighbor = lookupNeighbor(neighborName);
                    if (neighbor != null) {
                        app.addNeighbor(neighbor);
                        System.out.println("Voisin ajouté : " + neighborName + " pour " + app.getLogicalAddress());
                    } else {
                        System.out.println("Erreur : " + neighborName + " n'a pas pu être trouvé pour " + app.getLogicalAddress());
                    }
                }
            }

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
        while (neighbor == null && attempts < 10) {
            try {
                neighbor = (ApplicationInterface) Naming.lookup("rmi://localhost/" + neighborName);
            } catch (Exception e) {
                attempts++;
                System.out.println("En attente de " + neighborName + " (" + attempts + "/10)");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }
        return neighbor;
    }
}
