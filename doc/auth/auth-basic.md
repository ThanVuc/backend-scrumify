## 1. Register flow
1. User enter username + email + password
2. Validate input format
3. Validate business: is email exist?
4. Insert account to user table
 + Hash password first with BCryptPasswordEncoder
 + Insert the password hash to database
5. Generate JWT token (access token - 1 day expired)
6. Write the token to cookie: (in controller)
 + Http only
 + Same site
 + Lax
 + Secure = false
7. Return 201 attach with the cookie

## 2. Login flow
1. User enter email + password
2. Validate input format
3. Query to validate account (BCryptPasswordEncoder to verify password)
4. Generate JWT token (access token - 1 day expired)
5. Write the token to cookie: (in controller)
 + Http only
 + Same site
 + Lax
 + Secure = false
6. Return 200 attach with the cookie

## 3. Request Authen Flow
1. User sends request with authentication cookie
2. Security filter reads JWT from cookie
3. Verify JWT:
   - Signature
   - Expiration
   - Required claims
4. Extract user identity from JWT
5. Put authenticated user/principal into SecurityContext
6. Continue to business logic

## 4. API /me
1. User send cookie to server
2. Server verify cookie, get sub id
3. Query db, select all user infor to return
 + id, username (fullname), email, roles, status, avatar_url, createdAt, updatedAt, lastLoginAt
*note: api with @PreAuthorize("hasRole('MEMBER')")

## 5. API /logout
1. User send cookie to server
2. Server send the clear cookie response to user
*note: api with @PreAuthorize("hasRole('MEMBER')")