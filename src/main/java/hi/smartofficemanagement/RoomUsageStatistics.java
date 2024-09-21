package hi.smartofficemanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class RoomUsageStatistics {

    public static void displayRoomUsageStatistics() {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String query = "SELECT roomId, COUNT(*) AS totalBookings, SUM(TIMESTAMPDIFF(MINUTE, startTime, endTime)) AS totalOccupancyMinutes " +
                       "FROM bookings GROUP BY roomId";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            
            System.out.println("Room Usage Statistics:");
            while (rs.next()) {
                int roomId = rs.getInt("roomId");
                int totalBookings = rs.getInt("totalBookings");
                int totalOccupancyMinutes = rs.getInt("totalOccupancyMinutes");

                double averageOccupancyMinutes = totalBookings > 0 ? (double) totalOccupancyMinutes / totalBookings : 0;

                // Fetch room capacity
                query = "SELECT maxCapacity FROM rooms WHERE roomId = ?";
                try (PreparedStatement roomStmt = conn.prepareStatement(query)) {
                    roomStmt.setInt(1, roomId);
                    ResultSet roomRs = roomStmt.executeQuery();
                    if (roomRs.next()) {
                        int maxCapacity = roomRs.getInt("maxCapacity");

                        // Calculate utilization percentage
                        int totalAvailableMinutes = getTotalAvailableMinutes();
                        double utilizationPercentage = totalAvailableMinutes > 0 ? 
                            (double) totalOccupancyMinutes / totalAvailableMinutes * 100 : 0;

                        System.out.printf("Room ID: %d%n", roomId);
                        System.out.printf("Total Bookings: %d%n", totalBookings);
                        System.out.printf("Total Occupancy Time: %d minutes%n", totalOccupancyMinutes);
                        System.out.printf("Average Occupancy Time per Booking: %.2f minutes%n", averageOccupancyMinutes);
                        System.out.printf("Utilization Percentage: %.2f%%%n", utilizationPercentage);
                        System.out.println();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getTotalAvailableMinutes() {
        // This method should return the total number of available minutes in a given period
        // For simplicity, assume a workday of 8 hours (480 minutes)
        return 480;
    }
}
