# FoodieHub - Online Food Ordering System

FoodieHub is a full-stack web application for ordering food online. Customers can create an account, browse the menu, search and filter dishes, fill a shopping cart, place orders and follow their order history. The backend also exposes APIs for managing food items and updating order status.

**Stack:** React (Vite) frontend, Java Spring Boot REST API, MySQL database, tested with Postman.

## Features

**Customer**
- Registration and login (passwords stored as BCrypt hashes, never in plain text)
- Browse the menu with food images, descriptions and prices
- Search dishes by name and filter by category
- Shopping cart saved in the database: add items, change quantity, remove items, live total
- Checkout with a delivery address
- Order history with order status (Placed, Preparing, Out for delivery, Delivered)

**Admin (REST APIs, tested in Postman)**
- Add, update and delete food items
- Mark items as unavailable
- View all orders and update order status

## Tech stack

| Layer | Technology |
|---|---|
| Frontend | React 18, Vite, Axios, CSS |
| Backend | Java, Spring Boot, Spring Web, Spring Data JPA, Bean Validation |
| Security | BCrypt password hashing (`spring-security-crypto`) |
| Database | MySQL 8 |
| Build tools | Maven (wrapper included), npm |
| Testing | Postman |
| IDE | Eclipse |

## Architecture

```text
 React frontend (localhost:5173)
          |
          |  REST / JSON over HTTP (Axios)
          v
 Spring Boot backend (localhost:8080)
   Controller -> Service -> Repository
          |
          v
 MySQL database (food_ordering_db)
```

## Project structure

```text
FoodieHub/
├── backend/
│   ├── pom.xml
│   ├── mvnw
│   └── src/main/
│       ├── java/com/foodiehub/foodordering/
│       │   ├── config/        PasswordConfig, CorsConfig
│       │   ├── controller/    FoodController, UserController, CartController, OrderController
│       │   ├── dto/           request and response classes
│       │   ├── entity/        Food, User, CartItem, Order, OrderItem
│       │   ├── repository/    Spring Data JPA repositories
│       │   ├── service/       UserService, CartService
│       │   └── FoodOrderingApplication.java
│       └── resources/
│           └── application.properties.example
├── frontend/
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── main.jsx
│       ├── App.jsx            menu, cart drawer, login/register, orders
│       ├── api.js             Axios setup
│       └── styles.css
└── README.md
```

## Database tables

Hibernate creates these automatically on first run (`spring.jpa.hibernate.ddl-auto=update`):

| Table | Purpose |
|---|---|
| `users` | Customer accounts (name, email, hashed password, phone, address, role) |
| `food` | Menu items (name, description, price, category, image URL, availability) |
| `cart_items` | Items in each user's cart |
| `orders` | Placed orders (user, total, status, address, time) |
| `order_items` | Dishes inside each order, stored with name and price at the time of purchase |

## Getting started

### Prerequisites

- JDK (developed with JDK 25)
- Node.js 18 or newer and npm
- MySQL 8
- Maven is not required, because the project includes the Maven Wrapper (`mvnw`)

### 1. Clone the repository

```bash
git clone https://github.com/Tejas3377/FoodieHub.git
cd FoodieHub
```

### 2. Create the database

Open MySQL (`sudo mysql`) and run:

```sql
CREATE DATABASE food_ordering_db;
CREATE USER 'fooduser'@'localhost' IDENTIFIED BY 'YOUR_DB_PASSWORD';
GRANT ALL PRIVILEGES ON food_ordering_db.* TO 'fooduser'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Run the backend (port 8080)

```bash
cd backend
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Open `application.properties` and set `spring.datasource.password` to the password you chose above. Then:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Wait for the `Started FoodOrderingApplication` message. The real `application.properties` is git-ignored so that database passwords are never published.

### 4. Add sample menu items

Run this in MySQL once the backend has created the tables:

```sql
USE food_ordering_db;
INSERT INTO food (name, description, price, category, image_url, available) VALUES
('Veg Burger', 'Fresh vegetable patty with lettuce and mayo', 99, 'Burger', 'https://placehold.co/400x300?text=Veg+Burger', true),
('Cheese Burger', 'Double cheese with crispy patty', 129, 'Burger', 'https://placehold.co/400x300?text=Cheese+Burger', true),
('Margherita Pizza', 'Classic tomato, basil and mozzarella', 199, 'Pizza', 'https://placehold.co/400x300?text=Margherita', true),
('Paneer Tikka Pizza', 'Spicy paneer tikka topping', 249, 'Pizza', 'https://placehold.co/400x300?text=Paneer+Pizza', true),
('Veg Biryani', 'Fragrant basmati rice with vegetables', 179, 'Biryani', 'https://placehold.co/400x300?text=Veg+Biryani', true),
('Chicken Biryani', 'Hyderabadi style chicken biryani', 249, 'Biryani', 'https://placehold.co/400x300?text=Chicken+Biryani', true),
('Cold Coffee', 'Chilled coffee with ice cream', 89, 'Beverages', 'https://placehold.co/400x300?text=Cold+Coffee', true),
('Fresh Lime Soda', 'Sweet or salted', 59, 'Beverages', 'https://placehold.co/400x300?text=Lime+Soda', true);
```

You can also add items through the API (`POST /api/foods`) in Postman.

### 5. Run the frontend (port 5173)

In a second terminal:

```bash
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173**, create an account and start ordering.

## API reference

Base URL: `http://localhost:8080/api`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/foods` | List food. Optional filters: `?search=burger`, `?category=Pizza` |
| GET | `/foods/categories` | List category names |
| GET | `/foods/{id}` | Get one food item |
| POST | `/foods` | Add a food item |
| PUT | `/foods/{id}` | Update a food item |
| DELETE | `/foods/{id}` | Delete a food item |
| POST | `/users/register` | Create an account |
| POST | `/users/login` | Log in |
| GET | `/cart/{userId}` | View cart with total |
| POST | `/cart/{userId}/items` | Add an item (`{"foodId": 1, "quantity": 2}`) |
| PUT | `/cart/{userId}/items/{itemId}` | Change quantity |
| DELETE | `/cart/{userId}/items/{itemId}` | Remove an item |
| DELETE | `/cart/{userId}` | Empty the cart |
| POST | `/orders/{userId}` | Place an order from the cart (`{"address": "..."}`) |
| GET | `/orders/user/{userId}` | Order history of a user |
| GET | `/orders` | All orders (admin) |
| PUT | `/orders/{id}/status?status=DELIVERED` | Update order status (admin) |

Example request:

```http
POST /api/users/register
Content-Type: application/json

{
  "name": "Tejas Gosavi",
  "email": "tejas@example.com",
  "password": "secret123",
  "phone": "9999999999",
  "address": "Pune"
}
```

## Testing

All endpoints were tested in Postman, covering success cases and error cases (duplicate email returns 409, wrong password returns 401, invalid input returns 400, missing records return 404). The full customer flow of register, login, add to cart, checkout and order history was also tested through the React interface.

## Known limitations and future work

- Login returns the user details but does not issue a token or session, so JWT-based authentication is the next security step
- An admin dashboard screen in React (the admin APIs already exist)
- Online payment integration
- Food image uploads instead of image URLs
- Email notifications and real-time order tracking

## Author

**Tejas Gosavi** - [GitHub @Tejas3377](https://github.com/Tejas3377)
