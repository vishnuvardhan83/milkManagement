# CURL Commands for Milk Management API

## 1. Customer Signup (Create User Account)

This endpoint creates a new customer account with username, email, and password.

```bash
curl -X POST http://localhost:8080/api/auth/customer/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "john_doe",
  "email": "john.doe@example.com",
  "mobileNumber": "0000000000",
  "dailyMilkQuantity": 0,
  "milkType": "COW",
  "deliveryStatus": "ACTIVE"
}
```

---

## 2. Login (Authenticate User)

After signup, use the same username and password to login and get a JWT token.

```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huX2RvZSIsImlhdCI6MTY5ODc2NTQzMiwiZXhwIjoxNjk4ODUxODMyfQ...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john.doe@example.com",
  "roles": ["ROLE_CUSTOMER"]
}
```

---

## 3. Alternative: Simple User Signup

If you prefer the simpler signup endpoint (email is auto-generated):

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jane_smith",
    "password": "password123"
  }'
```

**Expected Response:**
```
User registered successfully!
```

Then login with:
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jane_smith",
    "password": "password123"
  }'
```

---

## 4. Using the JWT Token for Authenticated Requests

After login, save the token and use it in subsequent requests:

```bash
# Example: Get all customers (requires ADMIN or MANAGER role)
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## Complete Example Workflow

```bash
# Step 1: Signup
curl -X POST http://localhost:8080/api/auth/customer/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "testuser@example.com",
    "password": "testpass123"
  }'

# Step 2: Login (copy the token from response)
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123"
  }' | jq -r '.token')

# Step 3: Use token for authenticated requests
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer $TOKEN"
```

---

## Notes

- Replace `localhost:8080` with your actual server address if different
- The customer signup creates both a User account and a Customer record
- Passwords should be at least 6 characters long
- Email must be a valid email format
- Username must be unique
- Email must be unique
