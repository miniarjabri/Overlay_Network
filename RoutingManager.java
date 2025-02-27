import java.rmi.RemoteException;
import java.util.*;

public class RoutingManager {
    public static List<ApplicationInterface> findRoute(ApplicationInterface source, ApplicationInterface destination) throws RemoteException {
        Map<ApplicationInterface, ApplicationInterface> prev = new HashMap<>();
        Queue<ApplicationInterface> queue = new LinkedList<>();
        Set<ApplicationInterface> visited = new HashSet<>();
        
        queue.add(source);
        visited.add(source);
        
        boolean found = false;
        while (!queue.isEmpty()) {
            ApplicationInterface current = queue.poll();
            if (current.getLogicalAddress().equals(destination.getLogicalAddress())) {
                found = true;
                break;
            }
            for (ApplicationInterface neighbor : current.getNeighbors()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    prev.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        
        if (!found) return null;
        
        LinkedList<ApplicationInterface> path = new LinkedList<>();
        ApplicationInterface current = destination;
        while (current != null) {
            path.addFirst(current);
            current = prev.get(current);
        }
        return path;
    }
}
