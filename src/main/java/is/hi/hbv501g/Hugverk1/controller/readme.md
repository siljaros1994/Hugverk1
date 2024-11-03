# Controller package:

## Description

This package contains the controllers for the application,
acting as the entry points for client requests. Controllers are
responsible for handling incoming HTTP requests, processing them
via services, and sending appropriate HTTP responses back to the
client. They coordinate between the client and the service layer,
ensuring data flows smoothly within the application.

# Classes:
### **HomeController:**
Manages requests for the homepage, displaying welcome messages and
sidepanel with options. There are two HomeControllers to manage
diffrent userstypes.

This class provides the following options for the client:

### **UserController:**

Handles user-related operations, like login, registration, and profile management.
Connects with services to register new users and verify login credentials.

This class provides the following options for the client:

- `GET` `/login` - the login page using thymeleaf.
- `POST` `/login` - the login request.
- `GET` `/register` - the register page using thymeleaf.
- `POST` `/register` - the register request.

### **ProfileController:**

Manages donor and recipient profile operations, including viewing, editing, and saving profiles.
Ensures user profile information is validated and stored correctly.

This class provides the following options for the client:

### **MessageController:**

Handles messaging functionalities, including sending and receiving messages between users.
Ensures messages are properly stored and retrieved from the database.

This class provides the following options for the client:

### **SearchController:**

Manages search operations for finding and filtering donors based on specific criteria set by recipients.
Connects with search services to perform filtering.

This class provides the following options for the client:

### **AuthenticationController:**
### **ContentController:**
### **DonorHomeController:**
### **DrController:**
### **FavoriteController:**
### **ARecipientHomeController:**