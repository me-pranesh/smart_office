package hi.smartofficemanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BookingManagement {

    public static void bookRoom(String username) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Room ID to book: ");
        int roomId;
        try {
            roomId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Room ID must be a digit. Please enter a valid Room ID.");
            scanner.nextLine(); // Clear the invalid input
            return;
        }
        
        if (!isRoomIdExists(roomId)) {
            System.out.println("Room ID " + roomId + " does not exist. Please enter a valid Room ID.");
            return;
        }
        
        System.out.print("Enter start time (HH:mm): ");
        String startTimeStr = scanner.nextLine();
        System.out.print("Enter duration (in minutes): ");
        int durationMinutes;
        try {
            durationMinutes = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Duration must be a number. Please enter a valid duration.");
            scanner.nextLine(); // Clear the invalid input
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date startTime;
        try {
            startTime = sdf.parse(startTimeStr);
        } catch (ParseException e) {
            System.out.println("Invalid time format.");
            return;
        }

        // Adjust date to today
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.set(Calendar.YEAR, today.getYear() + 1900); // Adjust for Calendar year starting at 1900
        calendar.set(Calendar.MONTH, today.getMonth());
        calendar.set(Calendar.DATE, today.getDate());
        startTime = calendar.getTime();

        // Calculate end time
        calendar.add(Calendar.MINUTE, durationMinutes);
        Date endTime = calendar.getTime();

        if (isRoomBooked(roomId, startTime, endTime)) {
            System.out.println("Room " + roomId + " is already booked during this time. Cannot book.");
            return;
        }

        Connection conn = DatabaseConnection.getInstance().getConnection();
        String query = "INSERT INTO bookings (roomId, username, startTime, endTime) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, roomId);
            stmt.setString(2, username);
            stmt.setTimestamp(3, new java.sql.Timestamp(startTime.getTime()));
            stmt.setTimestamp(4, new java.sql.Timestamp(endTime.getTime()));
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int bookingId = generatedKeys.getInt(1);
                System.out.println("Room " + roomId + " booked from " + startTimeStr + " for " + durationMinutes + " minutes. Booking ID: " + bookingId);
            }

            RoomManagement.updateRoomOccupancy(roomId, true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isRoomIdExists(int roomId) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String query = "SELECT roomId FROM rooms WHERE roomId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isRoomBooked(int roomId, Date startTime, Date endTime) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String query = "SELECT * FROM bookings WHERE roomId = ? AND ((startTime <= ? AND endTime >= ?) OR (startTime <= ? AND endTime >= ?))";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            stmt.setTimestamp(2, new java.sql.Timestamp(startTime.getTime()));
            stmt.setTimestamp(3, new java.sql.Timestamp(startTime.getTime()));
            stmt.setTimestamp(4, new java.sql.Timestamp(endTime.getTime()));
            stmt.setTimestamp(5, new java.sql.Timestamp(endTime.getTime()));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void cancelBooking() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Booking ID to cancel: ");
        int bookingId;
        try {
            bookingId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Booking ID must be a digit. Please enter a valid Booking ID.");
            scanner.nextLine(); // Clear the invalid input
            return;
        }

        Connection conn = DatabaseConnection.getInstance().getConnection();
        String query = "SELECT roomId FROM bookings WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int roomId = rs.getInt("roomId");

                // Delete booking
                query = "DELETE FROM bookings WHERE id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(query)) {
                    deleteStmt.setInt(1, bookingId);
                    int affectedRows = deleteStmt.executeUpdate();
                    if (affectedRows > 0) {
                        RoomManagement.updateRoomOccupancy(roomId, false); // Update room occupancy
                        OccupancySensor.updateOccupancy(roomId, 0); // Reset occupants
                        System.out.println("Booking cancelled successfully.");
                    } else {
                        System.out.println("Room Booking ID not found. Cannot cancel booking.");
                    }
                }
            } else {
                System.out.println("Booking ID not found. Cannot cancel booking.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getBookingId(int roomId, Date startTime, Date endTime) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String query = "SELECT id FROM bookings WHERE roomId = ? AND startTime = ? AND endTime = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            stmt.setTimestamp(2, new java.sql.Timestamp(startTime.getTime()));
            stmt.setTimestamp(3, new java.sql.Timestamp(endTime.getTime()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
