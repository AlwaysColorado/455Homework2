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
        int[] msgCounts = getCounts(); // Get values from Hashtable

        int activeClientConnections = msgCounts.length; // Number of client connections
        double throughput = getThroughput(msgCounts); // Average number of messages processed per second in the last 20 seconds
        double meanPerClientThroughput = getMeanPerClientThroughput(msgCounts, activeClientConnections); // Mean of the per client throughput
        double sdPerClientThroughput = getStdDevPerClientThroughput(msgCounts, meanPerClientThroughput, activeClientConnections); // Standard Deviation of the per client throughput

        // TODO: Format floating point numbers to limit length
        System.out.printf("[%s] Server Throughput: %f messages/s, Active Client Connections: %d, " +
                        "Mean Per-Client Throughput: %f messages/s, Std. Dev. Of Per-Client Throughput: %f messages/s",
                date, throughput, activeClientConnections, meanPerClientThroughput, sdPerClientThroughput);
    }

    private int[] getCounts() {
        int[] counts = new int[clientStatistics.size()];

        int i = 0;
        for (Integer count : clientStatistics.values()) {
            counts[i] = count;
            i++;
        }
        return counts;
    }

    private double getThroughput(int[] msgCounts) {
        long totalMsgCount = 0; // total messages sent in the last 20 seconds
        for (int count : msgCounts) {
            totalMsgCount += count;
        }
        return (totalMsgCount / 20F); // Messages sent per second
    }

    private double getMeanPerClientThroughput(int[] msgCounts, int clients) {

        // If no active clients
        if (clients == 0) {
            return 0.0;
        }

        // else...
        double totalMsgCount = 0.0;
        for (int count : msgCounts) {
            totalMsgCount += count;
        }
        return (totalMsgCount / clients);

    }

    public double getStdDevPerClientThroughput(int[] msgCounts, double mean, int clients) {
        // If no active clients
        if (clients == 0) {
            return 0.0;
        }

        // else...
        double stdDev = 0.0;
        for (int count : msgCounts) {
            stdDev += Math.pow(count - mean, 2);
        }
        return Math.sqrt( stdDev / clients);
    }

}
