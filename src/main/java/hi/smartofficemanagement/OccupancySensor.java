package hi.smartofficemanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class OccupancySensor {

    private static final int UNOCCUPIED_THRESHOLD_MINUTES = 5; // Threshold in minutes

    public static void addOccupants(int roomId, int numberOfOccupants) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try {
            // Fetch current occupancy and max capacity
            String query = "SELECT occupied, maxCapacity, currentOccupants FROM rooms LEFT JOIN occupants ON rooms.roomId = occupants.roomId WHERE rooms.roomId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                boolean isOccupied = rs.getBoolean("occupied");
                int maxCapacity = rs.getInt("maxCapacity");
                int currentOccupants = rs.getInt("currentOccupants");

                if (numberOfOccupants < 0 || numberOfOccupants > maxCapacity) {
                    System.out.println("Invalid number of occupants. Please enter a number between 0 and " + maxCapacity + ".");
                    System.out.println("Ensure the room maximum capacity is set by admin");
                    return;
                }

                if (numberOfOccupants == 0) {
                    // Vacating the room
                    if (isOccupied && currentOccupants > 0) {
                        updateOccupancy(roomId, 0); // Set currentOccupants to 0 and update room status
                        System.out.println("Room " + roomId + " is now unoccupied. AC and lights turned off.");
                        startUnoccupiedTimer(roomId);
                    } else {
                        updateOccupancy(roomId, 0);
                    }
                } else {
                    if (isOccupied) {
                        // Room is already occupied; update occupants
                        query = "UPDATE occupants SET currentOccupants = currentOccupants + ? WHERE roomId = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
                            updateStmt.setInt(1, numberOfOccupants);
                            updateStmt.setInt(2, roomId);
                            updateStmt.executeUpdate();
                            currentOccupants += numberOfOccupants;
                            updateRoomStatus(roomId, currentOccupants);
                        }
                    } else {
                        // Room is not currently occupied
                        System.out.println("Room " + roomId + " is not currently booked. Please book the room before adding occupants.");
                    }
                }
            } else {
                System.out.println("Room " + roomId + " does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateOccupancy(int roomId, int numberOfOccupants) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String query;
        try {
            if (numberOfOccupants == 0) {
                // Turn off AC and lights
                query = "UPDATE rooms SET occupied = FALSE WHERE roomId = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
                    updateStmt.setInt(1, roomId);
                    updateStmt.executeUpdate();
                }
                startUnoccupiedTimer(roomId); // Start timer for unoccupied state
            }

            // Update currentOccupants
            query = "INSERT INTO occupants (roomId, currentOccupants) VALUES (?, ?) ON DUPLICATE KEY UPDATE currentOccupants = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
                updateStmt.setInt(1, roomId);
                updateStmt.setInt(2, numberOfOccupants);
                updateStmt.setInt(3, numberOfOccupants);
                updateStmt.executeUpdate();
            }

            updateRoomStatus(roomId, numberOfOccupants);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateRoomStatus(int roomId, int numberOfOccupants) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String query;
        try {
            if (numberOfOccupants >= 2) {
                // Turn on AC and lights
                query = "UPDATE rooms SET occupied = TRUE WHERE roomId = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
                    updateStmt.setInt(1, roomId);
                    updateStmt.executeUpdate();
                }
                System.out.println("Room " + roomId + " now occupied by " + numberOfOccupants + " persons. AC and lights turned on.");
            } else if (numberOfOccupants == 1) {
                System.out.println("Room " + roomId + " occupied by 1 person. One more occupant needed to turn on AC and lights.");
            } else {
                // Turn off AC and lights
                query = "UPDATE rooms SET occupied = FALSE WHERE roomId = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
                    updateStmt.setInt(1, roomId);
                    updateStmt.executeUpdate();
                }
                System.out.println("Room " + roomId + " is now unoccupied. AC and lights turned off.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void startUnoccupiedTimer(int roomId) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkAndReleaseRoom(roomId);
            }
        }, UNOCCUPIED_THRESHOLD_MINUTES * 60 * 1000); // Convert minutes to milliseconds
    }

    private static void checkAndReleaseRoom(int roomId) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String query = "SELECT currentOccupants FROM occupants WHERE roomId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int currentOccupants = rs.getInt("currentOccupants");
                if (currentOccupants == 0) {
                    // Room is still unoccupied, release booking
                    query = "DELETE FROM bookings WHERE roomId = ?";
                    try (PreparedStatement deleteStmt = conn.prepareStatement(query)) {
                        deleteStmt.setInt(1, roomId);
                        deleteStmt.executeUpdate();
                    }
                    System.out.println("Room " + roomId + " is now unoccupied for more than " + UNOCCUPIED_THRESHOLD_MINUTES + " minute(s). Booking released. AC and lights off.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
