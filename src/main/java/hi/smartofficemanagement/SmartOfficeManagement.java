package hi.smartofficemanagement;

import java.util.Scanner;

public class SmartOfficeManagement {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to Smart Office Management System");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                // Login
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                String role = UserAuthentication.login(username, password);
                if (role != null) {
                    System.out.println("Login successful!");
                    showMenu(scanner, username, role);
                } else {
                    System.out.println("Invalid credentials!");
                }
            } else if (choice == 2) {
                // Register
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();
                System.out.print("Enter role (user/admin): ");
                String role = scanner.nextLine();

                if (UserAuthentication.register(username, password, role)) {
                    System.out.println("Registration successful!");
                } else {
                    System.out.println("Registration failed!");
                }
            } else if (choice == 3) {
                System.out.println("Exiting...");
                break;
            } else {
                System.out.println("Invalid choice! Please try again.");
            }
        }

        scanner.close();
    }

    private static void showMenu(Scanner scanner, String username, String role) {
        if (role.equals("admin")) {
            showAdminMenu(scanner, username);
        } else {
            showUserMenu(scanner, username);
        }
    }

    private static void showAdminMenu(Scanner scanner, String username) {
        while (true) {
            System.out.println("1. List Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Configure Rooms");
            System.out.println("5. Set Room Capacity");
            System.out.println("6. Add Room");
            System.out.println("7. Add Occupants");
            System.out.println("8. Room Usage Statistics");
            System.out.println("9. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    RoomManagement.listRooms();
                    break;
                case 2:
                    BookingManagement.bookRoom(username);
                    break;
                case 3:
                    BookingManagement.cancelBooking();
                    break;
                case 4:
                    System.out.print("Enter the number of meeting rooms: ");
                    int numberOfRooms = scanner.nextInt();
                    RoomManagement.configureRooms(numberOfRooms);
                    break;
                case 5:
                    System.out.print("Enter Room ID to set capacity: ");
                    int roomId = scanner.nextInt();
                    System.out.print("Enter the new capacity: ");
                    int capacity = scanner.nextInt();
                    RoomManagement.setRoomCapacity(roomId, capacity);
                    break;
                case 6:
                    RoomManagement.addRoom();
                    break;
                case 7:
                    System.out.print("Enter room number to add occupants: ");
                    int roomNumber = scanner.nextInt();
                    System.out.print("Enter the number of occupants: ");
                    int occupants = scanner.nextInt();
                    OccupancySensor.addOccupants(roomNumber, occupants);
                    break;
                case 8:
                    RoomUsageStatistics.displayRoomUsageStatistics();
                    break;
                case 9:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void showUserMenu(Scanner scanner, String username) {
        while (true) {
            System.out.println("1. List Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    RoomManagement.listRooms();
                    break;
                case 2:
                    BookingManagement.bookRoom(username);
                    break;
                case 3:
                    BookingManagement.cancelBooking();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
}
