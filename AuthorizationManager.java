import java.util.Arrays;
import java.util.List;

public class AuthorizationManager {
    // Liste de mots interdits et longueur maximale
    private static final List<String> forbiddenWords = Arrays.asList("spam", "forbidden", "blocked");
    private static final int maxMessageLength = 200;
    
    public static boolean authorize(ApplicationInterface app, ApplicationInterface next, String message) throws Exception {
        // Vérifications de base
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Autorisation refusée : le message est vide.");
            return false;
        }
        if (message.length() > maxMessageLength) {
            System.out.println("Autorisation refusée : le message est trop long (" + message.length() + " caractères).");
            return false;
        }
        for (String word : forbiddenWords) {
            if (message.toLowerCase().contains(word.toLowerCase())) {
                System.out.println("Autorisation refusée : le message contient le mot interdit \"" + word + "\".");
                return false;
            }
        }
        if (!Character.isUpperCase(message.charAt(0))) {
            System.out.println("Autorisation refusée : le message doit commencer par une majuscule.");
            return false;
        }
        // Vérification de la connexion réseau
        String ip1 = app.getIpAddress();
        String ip2 = next.getIpAddress();
        String prefix1 = ip1.substring(0, ip1.lastIndexOf('.'));
        String prefix2 = ip2.substring(0, ip2.lastIndexOf('.'));
        if (!prefix1.equals(prefix2)) {
            if (!app.getLogicalAddress().equals("App3")) {
                System.out.println("Autorisation refusée : " + app.getLogicalAddress() 
                    + " n'est pas habilité à transmettre vers " + next.getLogicalAddress() 
                    + " car ils ne sont pas sur le même réseau.");
                return false;
            }
        }
        System.out.println("Autorisation accordée pour " + app.getLogicalAddress() + " vers " + next.getLogicalAddress() + ".");
        return true;
    }
}
