package cs455.scaling.server;

import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;

public class ServerStatistics extends TimerTask {

    private final Server server;
    private HashMap clientStatistics;

    public ServerStatistics( Server server ) {
        this.server = server;
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
        int activeClientConnections = clientStatistics.size();
        double meanPerClientThroughput = -99; //TODO: do the math
        double sdPerClientThroughput = -99; //TODO: do the math

        // TODO: Format floating point numbers of limit length
        System.out.printf("[%s] Server Throughput: %f messages/s, Active Client Connections: %d, " +
                        "Mean Per-Client Throughput: %f messages/s, Std. Dev. Of Per-Client Throughput: %f messages/s",
                date, messagesPerClient, activeClientConnections, meanPerClientThroughput, sdPerClientThroughput);
    }
}
