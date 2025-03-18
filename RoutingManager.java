import java.rmi.RemoteException;
import java.util.*;

public class RoutingManager {
    private static Map<String, ApplicationInterface> appMap = new HashMap<>();

    public static void addApplication(ApplicationInterface app) throws RemoteException {
        appMap.put(app.getLogicalAddress(), app);
    }

    public static List<ApplicationInterface> findRoute(ApplicationInterface source, ApplicationInterface destination) throws RemoteException {
        String sourceAddr = source.getLogicalAddress();
        String destAddr = destination.getLogicalAddress();

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String addr : appMap.keySet()) {
            distances.put(addr, Integer.MAX_VALUE);
        }
        distances.put(sourceAddr, 0);
        queue.add(sourceAddr);

        while (!queue.isEmpty()) {
            String currentAddr = queue.poll();
            ApplicationInterface currentApp = appMap.get(currentAddr);

            if (currentApp == null) {
                System.out.println("Erreur : application " + currentAddr + " inconnue dans RoutingManager !");
                return null;
            }

            if (currentAddr.equals(destAddr)) break;

            for (ApplicationInterface neighbor : currentApp.getNeighbors()) {
                String neighborAddr = neighbor.getLogicalAddress();

                if (!distances.containsKey(neighborAddr)) {
                    distances.put(neighborAddr, Integer.MAX_VALUE);
                }

                int newDist = distances.get(currentAddr) + getDistance(currentAddr, neighborAddr);
                if (newDist < distances.get(neighborAddr)) {
                    distances.put(neighborAddr, newDist);
                    previous.put(neighborAddr, currentAddr);
                    queue.add(neighborAddr);
                }
            }
        }

        List<ApplicationInterface> path = new LinkedList<>();
        String currentAddr = destAddr;
        while (currentAddr != null) {
            ApplicationInterface currentApp = appMap.get(currentAddr);
            if (currentApp != null) {
                path.add(0, currentApp);
            }
            currentAddr = previous.get(currentAddr);
        }

        return path.size() > 1 ? path : null;
    }

    private static int getDistance(String addr1, String addr2) {
        return 1;
    }
}

