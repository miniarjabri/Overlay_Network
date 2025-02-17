class Connection {
    private ApplicationInterface from;
    private ApplicationInterface to;

    public Connection(ApplicationInterface from, ApplicationInterface to) {
        this.from = from;
        this.to = to;
    }

    // Établir une connexion entre deux applications
    public void establishConnection() {
        try {
            System.out.println("Connexion établie entre " + from.getLogicalAddress() + " et " + to.getLogicalAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Envoyer un message via RMI
    public void sendMessage(String message) {
        try {
            to.sendMessage(message, from.getLogicalAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
