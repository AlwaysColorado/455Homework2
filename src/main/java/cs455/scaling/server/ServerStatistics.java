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

        // Get timestamp
        String date = new Date().toString();

        // Retrieve the HashMap with the stats
        clientStatistics = server.getClientStatistics();
        List<Integer> msgCounts = clientBreakdown();
        long totalMsgCt = getTotalMsgCount(msgCounts); // total messages sent in the last 20 seconds

        // DO THE MATH!
        // ------------
        int activeClientConnections = msgCounts.size(); // Number of active client connections (in last 20 seconds)
        double throughput = getThroughput(totalMsgCt); // Average number of messages processed per second in the last 20 seconds
        double meanPerClientThroughput = getMeanPerClientThroughput(msgCounts, activeClientConnections); // Mean of the per client throughput
        double sdPerClientThroughput = getStdDevPerClientThroughput(msgCounts, meanPerClientThroughput, activeClientConnections); // Standard Deviation of the per client throughput

        // TODO: Format floating point numbers to limit length
        System.out.printf("[%s] Server Throughput: %f messages/s, Active Client Connections: %d, " +
                        "Mean Per-Client Throughput: %f messages/s, Std. Dev. Of Per-Client Throughput: %f messages/s",
                date, throughput, activeClientConnections, meanPerClientThroughput, sdPerClientThroughput);
    }


    private List<Integer> clientBreakdown() {
        int clientCount = 0;

        // Increment client count if value > 0
        List<Integer> messageCounts = (List<Integer>) clientStatistics.values(); // Get the values
        messageCounts.removeIf(v -> v == 0); // Remove 'zero' entries

        return messageCounts;
    }

    private long getTotalMsgCount(List<Integer> msgCounts) {
        long totalMsgCount = 0;
        for (Integer count : msgCounts) {
            totalMsgCount += count;
        }
        return totalMsgCount;
    }

    private double getThroughput(long totalMsgCount) {
        return (totalMsgCount / 20F);
    }

    private double getMeanPerClientThroughput(List<Integer> perClientCounts, int clients) {
        double sum = 0.0;
        for (int count : perClientCounts) {
            sum += count;
        }
        return (sum / clients);
    }

    public double getStdDevPerClientThroughput(List<Integer> counts, double mean, int clients) {
        double stdDev = 0.0;
        for (int count : counts) {
            stdDev = Math.pow(count - mean, 2);
        }
        return Math.sqrt( stdDev / clients);
    }

}
