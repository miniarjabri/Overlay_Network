import java.rmi.RemoteException;
import java.util.*;
public class RoutingManager {
    private static List<ApplicationInterface> applications = new ArrayList<>();

    public static void addApplication(ApplicationInterface app) {
        applications.add(app);
    }

    public static List<ApplicationInterface> findRoute(ApplicationInterface source, ApplicationInterface destination) throws RemoteException {
        Map<ApplicationInterface, Integer> distances = new HashMap<>();
        Map<ApplicationInterface, ApplicationInterface> previous = new HashMap<>();
        PriorityQueue<ApplicationInterface> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // Initialiser les distances et la queue
        for (ApplicationInterface app : applications) {
            distances.put(app, Integer.MAX_VALUE); // Distance infinie par défaut
        }
        distances.put(source, 0); // La distance de la source à elle-même est 0
        queue.add(source);

        while (!queue.isEmpty()) {
            ApplicationInterface current = queue.poll();

            // Si la destination est atteinte, on reconstruit le chemin
            if (current.getLogicalAddress().equals(destination.getLogicalAddress())) {
                break;
            }

            // Vérification des voisins et relaxation des distances
            for (ApplicationInterface neighbor : current.getNeighbors()) {
                // Si le voisin n'a pas de distance initialisée, initialiser une valeur par défaut
                if (distances.containsKey(current) && distances.get(current) != Integer.MAX_VALUE) {
                    int newDist = distances.get(current) + getDistance(current, neighbor);
                    if (newDist < distances.get(neighbor)) {
                        distances.put(neighbor, newDist);
                        previous.put(neighbor, current);
                        queue.add(neighbor);
                    }
                } else {
                    System.out.println("Erreur : Le nœud actuel " + current.getLogicalAddress() + " n'a pas de distance initialisée.");
                }
                
            }
        }

        // Reconstruction du chemin
        List<ApplicationInterface> path = new LinkedList<>();
        ApplicationInterface current = destination;
        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }
        Collections.reverse(path);
        return path.size() > 1 ? path : null;
    }

    private static int getDistance(ApplicationInterface app1, ApplicationInterface app2) {
        return 1; // Valeur par défaut, ajustable selon la topologie
    }
}
