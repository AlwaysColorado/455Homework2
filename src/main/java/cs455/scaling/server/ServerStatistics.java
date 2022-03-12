package cs455.scaling.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TimerTask;

public class ServerStatistics extends TimerTask {

    private final Server server;
    private Hashtable clientStatistics;

    public ServerStatistics( Server server ) {
        this.server = server;
        this.clientStatistics = server.getClientStatistics();
    }

    @Override
    public void run() {

        // Get timestamp
        String date = new Date().toString();

        // Retrieve the HashMap with the stats
        clientStatistics = server.getClientStatistics();

        // DO THE MATH!
        // ------------
        double messagesPerClient = -99; //TODO: do the math
        int activeClientConnections = getActiveClients();
        double meanPerClientThroughput = -99; //TODO: do the math
        double sdPerClientThroughput = -99; //TODO: do the math

        // TODO: Format floating point numbers of limit length
        System.out.printf("[%s] Server Throughput: %f messages/s, Active Client Connections: %d, " +
                        "Mean Per-Client Throughput: %f messages/s, Std. Dev. Of Per-Client Throughput: %f messages/s",
                date, messagesPerClient, activeClientConnections, meanPerClientThroughput, sdPerClientThroughput);
    }

    private int getActiveClients() {
        int clientCount = 0;

        // Increment client count if value > 0
        
        return clientCount;
    }


}
