# SmartOfficeManagement
For a comprehensive README file on GitHub, you should include the following sections to help users configure and run the Smart Office Management System project:

### README File Template

---

# Smart Office Management System

## Overview

The Smart Office Management System is a console-based application designed to manage office space efficiently. It handles room configurations, bookings, and occupancy tracking, including automated control of room amenities and notifications for bookings.
# Problem Description



## Features

### Admin Functions
- Configure Rooms
- Set Room  MaxCapacity
- Add Room
- List Rooms
- Cancel Booking
- View Room Usage Statistics
- Add occupants

### User Functions
- Book Room
- Cancel Booking
- Add Occupants
  
<img src="https://github.com/user-attachments/assets/13a53dbd-7cf1-43df-b39f-a2026b891deb" width=300>


**Booking room**

<img src="https://github.com/user-attachments/assets/fe9d7709-0048-4abc-827e-e0c57e03dfc0" width=300>

**Cancel booking**

<img src="https://github.com/user-attachments/assets/1d86441f-9b9c-4e48-8282-f0e62236816a " width=300>

**Configure room**

<img src="https://github.com/user-attachments/assets/d72e71d5-9ddd-419a-8e0e-dd02496f8186" width=300>

**Add a occupant**

<img src="https://github.com/user-attachments/assets/28034350-1ae0-4e2d-b23b-6b3c14f48bd7" width=300>

**Set max room capacity**

<img src="https://github.com/user-attachments/assets/5e3df6dc-d6e8-4c11-a6d7-5bf20f906893" width=300>

**Add a room**

<img src="https://github.com/user-attachments/assets/046877e5-ab04-4685-8d61-bed58da221e7" width=300>

**Room Usage Statistics**

<img src="https://github.com/user-attachments/assets/95526bce-1d6e-4d94-bba3-f147a157d4aa" width=300>


### Root directory:

<img src="https://github.com/user-attachments/assets/4bb00603-4188-4d35-8ea3-0c3a33fa40e6" width=300>

## Prerequisites

To run the Smart Office Management System, you will need the following software installed:

### MySQL Workbench

- [MySQL Workbench 8.0 CE (Community Edition) for Windows](https://dev.mysql.com/downloads/workbench/)
- [MySQL Workbench 8.0 CE (Community Edition) for macOS](https://dev.mysql.com/downloads/workbench/)

### MySQL Server

- [MySQL Server for Windows](https://dev.mysql.com/downloads/mysql/)
- [MySQL Server for macOS](https://dev.mysql.com/downloads/mysql/)

### Apache NetBeans IDE

- [Apache NetBeans IDE 20](https://netbeans.apache.org/download/index.html)

### Java Development Kit (JDK)

- [JDK 19](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html)
- [JDK 22](https://www.oracle.com/java/technologies/javase/jdk22-archive-downloads.html)

### JDBC Driver

- [MySQL Connector/J 8.0.26](https://dev.mysql.com/downloads/connector/j/)

## Installation and Setup

### 1. Install MySQL Server

Download and install the MySQL Server from the links provided above for your respective operating system.

### 2. Install MySQL Workbench

Download and install MySQL Workbench 8.0 CE from the links provided above for your respective operating system.

### 3. Install Apache NetBeans IDE

Download and install Apache NetBeans IDE 20 from the provided link.

### 4. Install JDK

Download and install JDK 19 or JDK 22 from the provided links.
### 5. Configure Database Connection

Ensure that the `DatabaseConnection` class in your project is configured with the correct database connection details.

### 6. Add MySQL Connector/J to Your Project

Download the MySQL Connector/J 8.0.26 and add it to your project's classpath in Apache NetBeans IDE.

## Setup and Configuration

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/smart-office-management.git
cd smart-office-management
```

### 2. Configure Database

1. **Create Database and Tables:**
   - Run the provided SQL scripts to create necessary tables:
     - `rooms`
     - `bookings`
     - `occupants`
     - `users`

2. **Database Schema:**
   ```sql
   CREATE TABLE rooms (
       roomId INT AUTO_INCREMENT PRIMARY KEY,
       occupied BOOLEAN,
       maxCapacity INT
   );

   CREATE TABLE bookings (
       id INT AUTO_INCREMENT PRIMARY KEY,
       roomId INT,
       username VARCHAR(255),
       startTime DATETIME,
       endTime DATETIME,
       FOREIGN KEY (roomId) REFERENCES rooms(roomId)
   );

   CREATE TABLE occupants (
       roomId INT,
       numberOfOccupants INT,
       currentOccupants INT,
       PRIMARY KEY (roomId),
       FOREIGN KEY (roomId) REFERENCES rooms(roomId)
   );
   
   CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('user', 'admin') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

   ```


### 3. Update Database Connection

- Modify the `DatabaseConnection` class in your Java code to include the correct database URL, username, and password.

### 4. Build and Run the Application

1. **Compile and Build:**
   - Use your preferred IDE (e.g., NetBeans, IntelliJ IDEA) or command line to compile the Java files.

2. **Run the Application:**
   - Execute the main class (e.g., `SmartOfficeManagement`) from your IDE or use the command line:
     ```bash
     java -cp target/smart-office-management.jar hi.smartofficemanagement.SmartOfficeManagement
     ```

## Usage

- **Configure Rooms:** Set up initial room configurations.
- **Set Room Capacity:** Adjust the maximum capacity of rooms.
- **Add Room:** Add new rooms to the system.
- **List Rooms:** View current room statuses.
- **Book Room:** Reserve rooms for meetings.
- **Cancel Booking:** Cancel existing bookings.
- **Add Occupants:** Manage the number of people in a room.
- **Room Usage Statistics:** Get statistics on room usage.



## Troubleshooting

- Common issues and their resolutions.

## License

- Include licensing information if applicable.

---

Adjust the placeholders and specifics according to your actual project details and dependencies.
