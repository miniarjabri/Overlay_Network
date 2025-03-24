import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ApplicationServer {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
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

            Application app = null;

            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                String appName = node.getString("name");

                if (appName.equals(localAppName)) {
                    String ip = node.getString("ip");
                    int port = node.getInt("port");
                    JSONArray groupsArray = node.getJSONArray("groups");
                    List<String> groups = new ArrayList<>();
                    for (int j = 0; j < groupsArray.length(); j++) {
                        groups.add(groupsArray.getString(j));
                    }

                    app = new Application(appName, ip, port, groups);
                    Naming.rebind("rmi://localhost/" + appName, app);
                    System.out.println(appName + " est enregistré et en attente de messages.");

                    JSONArray neighbors = node.getJSONArray("neighbors");
                    for (int j = 0; j < neighbors.length(); j++) {
                        String neighborName = neighbors.getString(j);
                        app.addNeighborName(neighborName);
                        System.out.println("Voisin ajouté (nom) : " + neighborName);
                    }
                    break;
                }
            }

            if (app == null) {
                System.out.println("Application non trouvée dans la topologie.");
                System.exit(1);
            }

            List<String> neighborNames = app.getNeighborNames();
            System.out.println("Attente des voisins : " + neighborNames);
            boolean allNeighborsReady = false;
            while (!allNeighborsReady) {
                allNeighborsReady = true;
                for (String neighborName : neighborNames) {
                    try {
                        ApplicationInterface neighbor = (ApplicationInterface) Naming.lookup("rmi://localhost/" + neighborName);
                        if (!app.getNeighbors().contains(neighbor)) {
                            app.addNeighbor(neighbor);
                        }
                    } catch (Exception e) {
                        allNeighborsReady = false;
                        Thread.sleep(2000);
                    }
                }
            }
            System.out.println("Tous les voisins sont connectés : " + neighborNames);

            while (true) {
                System.out.println("\nEntrez une commande (envoyer <destAppName> <message>, 'envoyer Multicast to <group> <message>', 'envoyer Broadcast <message>', ou 'exit' pour quitter):");
                String input = sc.nextLine();

                if (input.equalsIgnoreCase("exit")) {
                    break;
                }

                String[] parts = input.split(" ", 5);
                if (parts.length < 2 || !parts[0].equalsIgnoreCase("envoyer")) {
                    System.out.println("Commande invalide. Utilisation : envoyer <destAppName> <message>, envoyer Multicast to <group> <message>, ou envoyer Broadcast <message>");
                    continue;
                }

                String commandType = parts[1];

                if (commandType.equalsIgnoreCase("Broadcast")) {
                    if (parts.length >= 4 && parts[2].equalsIgnoreCase("to")) {
                        // Cas : "envoyer Broadcast to <group> <message>"
                        String group = parts[3];
                        String message = parts.length == 5 ? parts[4] : "";
                        String messageId = localAppName + "_" + System.currentTimeMillis();

                        // Vérifier si l'app appartient au groupe
                        List<String> localGroups = app.getGroups();
                        if (!localGroups.contains(group)) {
                            System.out.println("[" + localAppName + "] Je ne peux pas envoyer un broadcast au groupe " + group + " car je n'y appartient pas.");
                            continue;
                        }

                        try {
                            app.broadcastMessage(message, localAppName, messageId);
                            System.out.println("Broadcast initié depuis " + localAppName + " pour le groupe " + group);
                        } catch (Exception e) {
                            System.out.println("Erreur lors du broadcast : " + e.getMessage());
                        }
                    } else {
                        // Cas : "envoyer Broadcast <message>" (broadcast classique à tous)
                        String message = parts.length == 3 ? parts[2] : "";
                        String messageId = localAppName + "_" + System.currentTimeMillis();
                        try {
                            app.broadcastMessage(message, localAppName, messageId);
                            System.out.println("Broadcast initié depuis " + localAppName);
                        } catch (Exception e) {
                            System.out.println("Erreur lors du broadcast : " + e.getMessage());
                        }
                    }
                } else if (commandType.equalsIgnoreCase("Multicast")) {
                    if (parts.length != 5 || !parts[2].equalsIgnoreCase("to")) {
                        System.out.println("Utilisation : envoyer Multicast to <group> <message>");
                        continue;
                    }
                    String group = parts[3];
                    String message = parts[4];
                    String messageId = localAppName + "_" + group + "_" + System.currentTimeMillis();
                    try {
                        app.multicastMessage(message, localAppName, messageId);
                        System.out.println("Multicast initié depuis " + localAppName + " pour groupe " + group);
                    } catch (Exception e) {
                        System.out.println("Erreur lors du multicast : " + e.getMessage());
                    }
                } else {
                    String destAppName = commandType;
                    String message = parts.length == 3 ? parts[2] : "";
                    try {
                        Map<String, Integer> dist = new HashMap<>();
                        Map<String, String> prev = new HashMap<>();
                        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(dist::get));

                        for (String node : getAllNodes(nodes)) dist.put(node, Integer.MAX_VALUE);
                        dist.put(localAppName, 0);
                        queue.add(localAppName);

                        while (!queue.isEmpty()) {
                            String current = queue.poll();
                            List<String> neighbors = getNeighbors(current, nodes);
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
                        String step = destAppName;
                        while (step != null) {
                            path.add(0, step);
                            step = prev.get(step);
                        }

                        System.out.println("Chemin calculé : " + path);

                        if (path.size() <= 1 || !path.get(0).equals(localAppName)) {
                            System.out.println("Aucun chemin trouvé de " + localAppName + " à " + destAppName);
                            continue;
                        }

                        boolean messageDelivered = true;
                        for (int i = 0; i < path.size() - 1; i++) {
                            String current = path.get(i);
                            String next = path.get(i + 1);
                            try {
                                ApplicationInterface currentApp = (ApplicationInterface) Naming.lookup("rmi://localhost/" + current);
                                currentApp.forwardMessage(message, localAppName, next);
                                System.out.println("Message transmis de " + current + " à " + next);
                            } catch (Exception e) {
                                System.out.println("Can't reach app " + next);
                                messageDelivered = false;
                                break;
                            }
                        }

                        if (messageDelivered) {
                            System.out.println("Le message a atteint " + destAppName);
                        }
                    } catch (Exception e) {
                        System.out.println("Erreur lors de l'envoi du message : " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> getAllNodes(JSONArray nodes) {
        List<String> allNodes = new ArrayList<>();
        for (int i = 0; i < nodes.length(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            allNodes.add(node.getString("name"));
        }
        return allNodes;
    }

    private static List<String> getNeighbors(String appName, JSONArray nodes) {
        for (int i = 0; i < nodes.length(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (node.getString("name").equals(appName)) {
                JSONArray neighbors = node.getJSONArray("neighbors");
                List<String> neighborList = new ArrayList<>();
                for (int j = 0; j < neighbors.length(); j++) {
                    neighborList.add(neighbors.getString(j));
                }
                return neighborList;
            }
        }
        return null;
    }
}