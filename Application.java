class Application {
    private String logicalAddress;  // Logical address (e.g., "App1", "App2")
    private String ipAddress;       // IP address (e.g., "192.168.1.1")
    private int port;               // Port number (e.g., 8080)
    private String urlRmi;          // RMI URL (optional, e.g., "rmi://app1")
    
    // Constructor to initialize the application
    public Application(String logicalAddress, String ipAddress, int port, String urlRmi) {
        this.logicalAddress = logicalAddress;
        this.ipAddress = ipAddress;
        this.port = port;
        this.urlRmi = urlRmi;
    }

    // Getters for the attributes
    public String getLogicalAddress() {
        return logicalAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public String getUrlRmi() {
        return urlRmi;
    }
    
    // Override toString for easy debugging
    @Override
    public String toString() {
        return "Application{logicalAddress='" + logicalAddress + "', ipAddress='" + ipAddress + "', port=" + port + ", urlRmi='" + urlRmi + "'}";
    }

    // Methods to send and receive messages
    public void sendMessage(String message, Application destination) {
        System.out.println("Sending message from " + this.logicalAddress + " to " + destination.getLogicalAddress() + ": " + message);
        // Here you could implement real messaging logic, e.g., via sockets or RMI
        destination.receiveMessage(message);
    }

    public void receiveMessage(String message) {
        System.out.println("Message received at " + this.logicalAddress + ": " + message);
        // Additional logic to handle received message could be implemented here
    }
}
