// ========================= ApplicationClient.java (version finale corrigée) =========================
import java.rmi.Naming;
import java.util.*;

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
            String[] allApps = {"App1", "App2", "App3", "App4", "App5", "App6"};
            Map<String, ApplicationInterface> appMap = new HashMap<>();
            Map<String, List<String>> neighborMap = new HashMap<>();

            for (String appName : allApps) {
                try {
                    ApplicationInterface app = (ApplicationInterface) Naming.lookup("rmi://localhost/" + appName);
                    appMap.put(appName, app);
                    // Correction ici : utilisation d'une méthode statique de RoutingManager ou une structure locale de topologie si ApplicationInterface ne possède pas getNeighborNames()
                    // Si getNeighborNames() n'existe pas, remplacer ceci par une topologie définie en dur :
                    List<String> staticNeighbors = getStaticNeighbors(appName);
                    neighborMap.put(appName, staticNeighbors);
                } catch (Exception e) {
                    System.out.println("[WARN] Application non joignable : " + appName);
                }
            }

            if (!appMap.containsKey(sourceName) || !appMap.containsKey(destName)) {
                System.out.println("Source ou destination non trouvée dans RMI");
                return;
            }

            // Dijkstra sur le graphe logique
            Map<String, Integer> dist = new HashMap<>();
            Map<String, String> prev = new HashMap<>();
            PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(dist::get));

            for (String node : appMap.keySet()) dist.put(node, Integer.MAX_VALUE);
            dist.put(sourceName, 0);
            queue.add(sourceName);

            while (!queue.isEmpty()) {
                String current = queue.poll();
                List<String> neighbors = neighborMap.get(current);
                if (neighbors == null) continue;
                for (String neighbor : neighbors) {
                    if (!dist.containsKey(neighbor)) continue;
                    int alt = dist.get(current) + 1;
                    if (alt < dist.get(neighbor)) {
                        dist.put(neighbor, alt);
                        prev.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }

            List<String> path = new ArrayList<>();
            String step = destName;
            while (step != null) {
                path.add(0, step);
                step = prev.get(step);
            }

            if (path.size() <= 1 || !path.get(0).equals(sourceName)) {
                System.out.println("Aucun chemin trouvé de " + sourceName + " à " + destName);
                return;
            }

            // Envoi du message via forwardMessage
            for (int i = 0; i < path.size() - 1; i++) {
                String current = path.get(i);
                String next = path.get(i + 1);
                ApplicationInterface currentApp = appMap.get(current);
                currentApp.forwardMessage(message, sourceName, next);
                System.out.println("Message transmis de " + current + " à " + next);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode temporaire pour charger une topologie locale statique (si getNeighborNames() non dispo)
    private static List<String> getStaticNeighbors(String appName) {
        Map<String, List<String>> topology = new HashMap<>();
        topology.put("App1", Arrays.asList("App2", "App3", "App4"));
        topology.put("App2", Arrays.asList("App1", "App4"));
        topology.put("App3", Arrays.asList("App1", "App4"));
        topology.put("App4", Arrays.asList("App1", "App2", "App3", "App5", "App6"));
        topology.put("App5", Arrays.asList("App4", "App6"));
        topology.put("App6", Arrays.asList("App4", "App5"));
        return topology.getOrDefault(appName, new ArrayList<>());
    }
}