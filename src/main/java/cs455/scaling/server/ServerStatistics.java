package cs455.scaling.server;

import java.net.SocketAddress;
import java.util.*;

public class ServerStatistics extends TimerTask {

    private final Server server;
    private Hashtable<SocketAddress, Integer> clientStatistics;

    public ServerStatistics( Server server ) {
        this.server = server;
        clientStatistics = server.getClientStatistics(); // Should be empty now
    }

    @Override
    public void run() {

        String date = new Date().toString(); // Get timestamp

        clientStatistics = server.getClientStatistics(); // Retrieve the HashMap with the stats
        List<Integer> msgCounts = (List<Integer>) clientStatistics.values(); // Get values from Hashtable

        int activeClientConnections = msgCounts.size(); // Number of client connections
        double throughput = getThroughput(msgCounts); // Average number of messages processed per second in the last 20 seconds
        double meanPerClientThroughput = getMeanPerClientThroughput(msgCounts, activeClientConnections); // Mean of the per client throughput
        double sdPerClientThroughput = getStdDevPerClientThroughput(msgCounts, meanPerClientThroughput, activeClientConnections); // Standard Deviation of the per client throughput

        // TODO: Format floating point numbers to limit length
        System.out.printf("[%s] Server Throughput: %f messages/s, Active Client Connections: %d, " +
                        "Mean Per-Client Throughput: %f messages/s, Std. Dev. Of Per-Client Throughput: %f messages/s",
                date, throughput, activeClientConnections, meanPerClientThroughput, sdPerClientThroughput);
    }


    private double getThroughput(List<Integer> msgCounts) {
        long totalMsgCount = 0; // total messages sent in the last 20 seconds
        for (Integer count : msgCounts) {
            totalMsgCount += count;
        }
        return (totalMsgCount / 20F); // Messages sent per second
    }

    private double getMeanPerClientThroughput(List<Integer> perClientCounts, int clients) {

        // If no active clients
        if (clients == 0) {
            return 0.0;
        }

        // else...
        double sum = 0.0;
        for (int count : perClientCounts) {
            sum += count;
        }
        return (sum / clients);

    }

    public double getStdDevPerClientThroughput(List<Integer> counts, double mean, int clients) {
        // If no active clients
        if (clients == 0) {
            return 0.0;
        }

        // else...
        double stdDev = 0.0;
        for (int count : counts) {
            stdDev += Math.pow(count - mean, 2);
        }
        return Math.sqrt( stdDev / clients);
    }

}
