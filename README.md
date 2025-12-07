# RideShare Backend – Spring Boot + MongoDB

This project is a simple ride-sharing backend built using Spring Boot and MongoDB.  
It supports user registration, login with JWT, ride creation, driver ride acceptance, and ride completion.

---

## Features

### 1. User Authentication (JWT)
- Users can register and log in.
- Passwords are encrypted using BCrypt.
- JWT token is returned after login.
- Role-based access (ROLE_USER / ROLE_DRIVER).

### 2. Ride Management
- Users can request rides.
- Drivers can view all pending rides.
- Drivers can accept rides.
- User or driver can complete a ride.
- Users can view only their own rides.

### 3. Input Validation
All request bodies are validated using Jakarta Validation:
- `@NotBlank`
- `@Size`

### 4. Global Exception Handling
Errors return a proper JSON response containing:
- error type
- message
- timestamp

---

## Technology Stack

- **Spring Boot (3.x)**
- **MongoDB**
- **Spring Security + JWT**
- **Java 17**
- **Maven**

---

## How to Run

### 1. Start MongoDB
Make sure MongoDB is running locally:

mongodb://localhost:27017

perl
Copy code

### 2. Run the Spring Boot Application

Using Maven:

```bash
mvn spring-boot:run
Or run RideShareApplication.java directly from IntelliJ.

The backend runs on:

arduino
Copy code
http://localhost:8081
API Endpoints
AUTH
Method	Endpoint	Description
POST	/api/auth/register	Register user or driver
POST	/api/auth/login	Login & get JWT token

USER
Method	Endpoint	Description
POST	/api/v1/rides	Create a new ride
GET	/api/v1/user/rides	View rides created by logged-in user

DRIVER
Method	Endpoint	Description
GET	/api/v1/driver/rides/requests	View all pending rides
POST	/api/v1/driver/rides/{id}/accept	Accept a specific ride

COMMON
Method	Endpoint	Description
POST	/api/v1/rides/{id}/complete	Complete a ride

Authentication Format (Important)
Every request after login must include:

makefile
Copy code
Authorization: Bearer <token>
Example:

makefile
Copy code
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9....
MongoDB Collections
The application uses the database:

nginx
Copy code
rideshare_db
Collections created:

users

rides

Directory Structure
css
Copy code
src/
 └── main/
      ├── java/
      │    └── org/example/rideshare/
      │           ├── model/
      │           ├── repository/
      │           ├── service/
      │           ├── controller/
      │           ├── config/
      │           ├── dto/
      │           ├── exception/
      │           └── util/
      └── resources/
           └── application.properties
How to Test (Basic Workflow)
1. Register a User
json
Copy code
{
  "username": "john",
  "password": "1234",
  "role": "ROLE_USER"
}
2. Login as User → Copy Token
3. Create a Ride
json
Copy code
{
  "pickupLocation": "Koramangala",
  "dropLocation": "Indiranagar"
}
4. Register a Driver
json
Copy code
{
  "username": "driver1",
  "password": "abcd",
  "role": "ROLE_DRIVER"
}
5. Login as Driver

Use this JSON body:

{
  "username": "driver1",
  "password": "abcd"
}


Copy the returned JWT token — this token is required for all driver-protected endpoints.

6. Driver Accepts Ride

After logging in as a driver, use the driver token to accept a pending ride.

POST /api/v1/driver/rides/{rideId}/accept
Authorization: Bearer <driverToken>


Replace {rideId} with the actual ride ID you created earlier.

7. Complete Ride (User or Driver)

Once a ride is in ACCEPTED status, it can be completed by either user or driver:

POST /api/v1/rides/{rideId}/complete
Authorization: Bearer <userToken or driverToken>


This updates the ride status to COMPLETED.
