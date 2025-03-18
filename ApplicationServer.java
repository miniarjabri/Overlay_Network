// ========================= ApplicationServer.java (version finale) =========================
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ApplicationServer {
    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname", "127.0.0.1");

        if (args.length < 1) {
            System.out.println("Usage : java ApplicationServer <AppName>");
            System.exit(1);
        }

        String localAppName = args[0];

        try {
            String jsonFile = "topologie.json";
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile)));
            JSONArray nodes = new JSONObject(jsonContent).getJSONArray("nodes");

            try { LocateRegistry.createRegistry(1099); } catch (Exception ignored) {}

            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                String appName = node.getString("name");

                if (appName.equals(localAppName)) {
                    String ip = node.getString("ip");
                    int port = node.getInt("port");

                    Application app = new Application(appName, ip, port);
                    Naming.rebind("rmi://localhost/" + appName, app);
                    System.out.println(appName + " est enregistré et en attente de messages.");

                    JSONArray neighbors = node.getJSONArray("neighbors");
                    for (int j = 0; j < neighbors.length(); j++) {
                        String neighborName = neighbors.getString(j);
                        app.addNeighborName(neighborName);
                        try {
                            ApplicationInterface neighbor = (ApplicationInterface) Naming.lookup("rmi://localhost/" + neighborName);
                            app.addNeighbor(neighbor);
                            System.out.println("Voisin ajouté : " + neighborName);
                        } catch (Exception e) {
                            System.out.println("Voisin non disponible : " + neighborName);
                        }
                    }
                    break;
                }
            }

            while (true) {
                Thread.sleep(10000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}