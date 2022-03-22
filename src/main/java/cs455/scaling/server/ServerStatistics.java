package cs455.scaling.server;

import java.net.SocketAddress;
import java.util.*;

public class ServerStatistics extends TimerTask {

    private final Server server;

    public ServerStatistics( Server server ) {
        this.server = server;
    }

    @Override
    public void run() {

        String date = new Date().toString(); // Get timestamp

        Hashtable<SocketAddress, Integer> clientStatistics = server.getClientStatistics(); // Retrieve the HashMap with the stats

        int activeClientConnections = clientStatistics.size(); // Number of client connections
        double throughput = getThroughput(clientStatistics); // Average number of messages processed per second in the last 20 seconds
        double meanPerClientThroughput = getMeanPerClientThroughput(clientStatistics, activeClientConnections); // Mean of the per client throughput
        double sdPerClientThroughput = getStdDevPerClientThroughput(clientStatistics, meanPerClientThroughput, activeClientConnections); // Standard Deviation of the per client throughput

        // TODO: Format floating point numbers to limit length
        System.out.printf("[%s] Server Throughput: %f messages/s, Active Client Connections: %d, " +
                        "Mean Per-Client Throughput: %f messages/s, Std. Dev. Of Per-Client Throughput: %f messages/s\n",
                date, throughput, activeClientConnections, meanPerClientThroughput, sdPerClientThroughput);
    }

    private double getThroughput(Hashtable<SocketAddress, Integer> clientStatistics) {
        long totalMsgCount = 0; // total messages sent in the last 20 seconds
        for (Integer count : clientStatistics.values()) {
            totalMsgCount += count;
        }
        return (totalMsgCount / 20F); // Messages sent per second
    }

    private double getMeanPerClientThroughput(Hashtable<SocketAddress, Integer> clientStatistics, int clients) {

        // If no active clients
        if (clients == 0) {
            return 0.0;
        }
        System.out.println(clientStatistics);
        // else...
        double totalMsgCount = 0.0;
        for (Integer count : clientStatistics.values()) {
            totalMsgCount += count;
        }
        return (totalMsgCount / clients) / 20;

    }

    public double getStdDevPerClientThroughput(Hashtable<SocketAddress, Integer> clientStatistics, double mean, int clients) {
        // If no active clients
        if (clients == 0) {
            return 0.0;
        }

        // else...
        double stdDev = 0.0;
        for (Integer count : clientStatistics.values()) {
            stdDev += Math.pow((count/20F) - mean, 2);
        }
        return Math.sqrt( stdDev / clients);
    }

}
