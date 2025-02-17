// Connection class to represent the link between two applications
class Connection {
    private Application from;  // Starting application
    private Application to;    // Target application
    
    // Constructor to initialize the connection
    public Connection(Application from, Application to) {
        this.from = from;
        this.to = to;
    }
    
    // Method to simulate establishing a connection
    public void establishConnection() {
        System.out.println("Establishing connection from " + from.getLogicalAddress() + " to " + to.getLogicalAddress());
        // Additional connection logic can be implemented here (e.g., sockets, RMI)
    }

    // Method to close the connection (if applicable)
    public void closeConnection() {
        System.out.println("Closing connection from " + from.getLogicalAddress() + " to " + to.getLogicalAddress());
    }
}
