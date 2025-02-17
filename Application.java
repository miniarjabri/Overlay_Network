// Application class to represent each application
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
}