### **Product Requirements Document: Digigoods API (v1)**

#### **1. Introduction**

This document specifies the functional and non-functional requirements for the Digigoods API. The purpose of this API is to provide a secure, reliable, and transactionally-safe endpoint for processing customer orders, including the validation and application of various percentage-based discounts.

**User Story:**
> *As a customer, I want to securely check out the items in my shopping cart and apply valid discount codes, so that I can complete my purchase at the correct final price.*

---

#### **2. Functional Requirements**

##### **2.1. API Endpoint Specification**

*   **Endpoint:** `POST /orders`
*   **Description:** Initiates the checkout process to create a new order.
*   **Request Body (JSON):**
    ```json
    {
      "userId": 123,
      "productIds": [101, 102, 101],
      "discountCodes": ["SUMMER20", "SPECIALSHIRT10"]
    }
    ```
*   **Success Response (`200 OK`):**
    ```json
    {
      "message": "Order created successfully!",
      "finalPrice": 85.50
    }
    ```
*   **Error Responses:**
    *   `400 Bad Request`: For invalid request data, including invalid or excessive discounts.
    *   `401 Unauthorized`: If the request is missing a valid JWT token.
    *   `403 Forbidden`: If the authenticated user's ID does not match the `userId` in the request body.
    *   `404 Not Found`: If a specified `productId` does not exist.
    *   `500 Internal Server Error`: For unexpected server-side errors.

##### **2.2. Core Business Logic & Rules**

The checkout process must be executed in the following sequence:

1.  **Authentication & Authorisation:** The system must verify the user's JWT token and ensure the user ID in the token matches the `userId` in the request body.
2.  **Product Validation:** The system must validate that all `productIds` exist in the database. If any ID is invalid, the process must be rejected (`404 Not Found`).
3.  **Original Subtotal Calculation:** The system calculates the `original subtotal` by summing the prices of all items in the cart.
4.  **Discount Validation:** The system validates all `discountCodes` against the database for existence, expiration, and usage limits. If *any* code is invalid, the process is rejected (`400 Bad Request`). This is an "all-or-nothing" check.
5.  **Discount Application Sequence:**
    1.  **Product-Specific Discounts:** The system first applies all `PRODUCT_SPECIFIC` percentage discounts to the prices of their corresponding items.
    2.  **Intermediate Subtotal:** A new intermediate subtotal is calculated by summing the discounted prices of specific items and the original prices of non-discounted items.
    3.  **General Discounts:** The system then applies all `GENERAL` percentage discounts sequentially to this intermediate subtotal.
6.  **Maximum Discount Rule:** The system calculates the total effective discount (`original subtotal` - `final price`). If this value exceeds 75% of the `original subtotal`, the process is rejected (`400 Bad Request`).
7.  **Final Commit:** If all checks pass, the system commits the following changes to the database within a single transaction:
    *   Creates a new `Order` record.
    *   Decrements the `stock` count for each purchased `Product`.
    *   Decrements the `remainingUses` count for each applied `Discount`.

---

#### **3. Non-Functional Requirements**

*   **3.1. Security:**
    *   All authenticated endpoints, including `/orders`, must be protected using JWT-based authentication.
    *   A separate `POST /auth/login` endpoint will handle username/password authentication and issue JWT tokens.
    *   The system must prevent a user from placing an order on behalf of another user.
*   **3.2. Data Integrity & Atomicity:**
    *   The entire checkout operation (order creation, inventory update, discount decrement) must be executed within a single, atomic database transaction.
    *   A failure at any step must trigger a complete rollback of all database changes for that transaction, ensuring data consistency.
*   **3.3. Technology Stack:**
    *   **Framework:** Spring Boot
    *   **Database:** PostgreSQL
    *   **Data Access:** Spring Data JPA / Hibernate
    *   **Data Migration:** Liquibase

---

#### **4. System Architecture & Component Design**

The application will follow a standard three-tier architecture (Controller, Service, Repository).

*   **`CheckoutController`**
    *   Handles incoming `POST /orders` requests.
    *   Works with Spring Security to enforce JWT authentication and authorisation.
    *   Delegates all business logic to the `CheckoutService`.
    *   Translates service-layer exceptions (e.g., `ProductNotFoundException`) into appropriate HTTP error responses.

*   **`CheckoutService`**
    *   Contains the primary `@Transactional` method (`processCheckout`) that orchestrates the entire checkout logic.
    *   Coordinates with `ProductService` and `DiscountService` to gather and validate data.
    *   Performs all calculations and enforces business rules.
    *   Is responsible for throwing specific, custom exceptions upon business rule violations.

*   **`DiscountService`**
    *   Encapsulates all logic related to discount validation.
    *   Contains a method to fetch and validate a list of discount codes from the database.
    *   Returns rich `Discount` objects for valid codes or throws `InvalidDiscountException` if any code fails validation.

*   **`ProductService`**
    *   Encapsulates all logic related to products.
    *   Contains a method to fetch product details by their IDs.
    *   Throws `ProductNotFoundException` if any requested product ID does not exist.

*   **Repositories (`ProductRepository`, `DiscountRepository`, `OrderRepository`)**
    *   Interfaces extending Spring Data JPA's `JpaRepository`.
    *   Provide the data access layer for interacting with the database.

---

#### **5. Data Model Specification (JPA Entities)**

##### **5.1. User**
| Field Name | Data Type | Constraints | Description |
| --- | --- | --- | --- |
| `id` | `Long` | Primary Key, Auto-generated | Unique identifier for the user. |
| `username` | `String` | Not Null, Unique | User's login name. |
| `password` | `String` | Not Null | Hashed password for the user. |

##### **5.2. Product**
| Field Name | Data Type | Constraints | Description |
| --- | --- | --- | --- |
| `id` | `Long` | Primary Key, Auto-generated | Unique identifier for the product. |
| `name` | `String` | Not Null | Name of the product. |
| `price` | `BigDecimal` | Not Null, `precision=10`, `scale=2` | The original price of the product. |
| `stock` | `Integer` | Not Null | The number of units currently in stock. |

##### **5.3. Discount**
| Field Name | Data Type | Constraints | Description |
| --- | --- | --- | --- |
| `id` | `Long` | Primary Key, Auto-generated | Unique identifier for the discount. |
| `code` | `String` | Not Null, Unique | The code customers use (e.g., "SUMMER20"). |
| `percentage` | `BigDecimal` | Not Null, `precision=5`, `scale=2` | The discount percentage (e.g., 20.00 for 20%). |
| `type` | `Enum (String)` | Not Null | `PRODUCT_SPECIFIC` or `GENERAL`. |
| `validFrom` | `LocalDate` | Not Null | The first day the discount is valid. |
| `validUntil`| `LocalDate` | Not Null | The last day the discount is valid. |
| `remainingUses` | `Integer` | Not Null | Number of times the discount can still be used. |
| `applicableProducts` | `Set<Product>`| Many-to-Many | For `PRODUCT_SPECIFIC` types, the set of products it applies to. |

##### **5.4. Order**
| Field Name | Data Type | Constraints | Description |
| --- | --- | --- | --- |
| `id` | `Long` | Primary Key, Auto-generated | Unique identifier for the order. |
| `user` | `User` | Not Null, Many-to-One | The user who placed the order. |
| `products` | `Set<Product>`| Many-to-Many | A snapshot of the products in the order. |
| `appliedDiscounts` | `Set<Discount>`| Many-to-Many | A snapshot of the discounts used in the order. |
| `originalSubtotal` | `BigDecimal` | Not Null, `precision=10`, `scale=2` | The subtotal before any discounts were applied. |
| `finalPrice` | `BigDecimal` | Not Null, `precision=10`, `scale=2`| The final price paid after all discounts. |
| `orderDate` | `LocalDateTime` | Not Null, Auto-generated | Timestamp of when the order was created. |
