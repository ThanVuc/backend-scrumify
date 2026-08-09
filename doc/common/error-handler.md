Implement a simple global error handling mechanism for my Spring Boot application.

Architecture:

api/
application/
domain/
infrastructure/

The design goal is:

Concrete domain/application exceptions
        ↓
Exception category
        ↓
GlobalExceptionHandler
        ↓
HTTP response

Do NOT create one @ExceptionHandler per concrete business exception.

==================================================
1. ERROR RESPONSE
==================================================

Create:

api/error/ApiErrorResponse.java

Use exactly:

public record ApiErrorResponse(
        String code,
        String detail,
        String path,
        String traceId
) {}

Do NOT add:
- errors
- ValidationError
- status
- timestamp
- stackTrace

Keep this response model simple.

Example:

{
  "code": "core.order.cannot_be_modified",
  "detail": "Order cannot be modified in its current status",
  "path": "/api/v1/orders/123",
  "traceId": "a1b2c3d4"
}

==================================================
2. DOMAIN EXCEPTION HIERARCHY
==================================================

Create:

domain/exception/BaseException.java
domain/exception/ResourceNotFoundException.java
domain/exception/ConflictException.java
domain/exception/BusinessValidationException.java

BaseException:

public abstract class BaseException extends RuntimeException {

    private final String code;

    protected BaseException(String code, String detail) {
        super(detail);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getDetail() {
        return getMessage();
    }
}

Category hierarchy:

BaseException
├── ResourceNotFoundException       -> 404
├── ConflictException               -> 409
└── BusinessValidationException     -> 422

These exceptions MUST NOT know anything about HTTP or Spring.

Do NOT put HttpStatus inside BaseException.

Do NOT put ResponseEntity inside exceptions.

==================================================
3. CONCRETE EXCEPTION EXAMPLES
==================================================

Create examples to demonstrate the pattern.

Example:

public class OrderCannotBeModifiedException
        extends BusinessValidationException {

    public OrderCannotBeModifiedException() {
        super(
            "core.order.cannot_be_modified",
            "Order cannot be modified in its current status"
        );
    }
}

Another example:

public class OrderNotFoundException
        extends ResourceNotFoundException {

    public OrderNotFoundException() {
        super(
            "core.order.not_found",
            "Order not found"
        );
    }
}

The important rule:

Adding a new concrete exception must NOT require adding a new
method to GlobalExceptionHandler.

For example:

UserNotFoundException
OrderNotFoundException
ProductNotFoundException

should all be handled by:

@ExceptionHandler(ResourceNotFoundException.class)

==================================================
4. GLOBAL EXCEPTION HANDLER
==================================================

Create:

api/advice/GlobalExceptionHandler.java

It should contain ONLY these handlers:

1. ResourceNotFoundException -> 404
2. ConflictException -> 409
3. BusinessValidationException -> 422
4. Exception -> 500

Example structure:

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        ...
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request) {
        ...
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessValidation(
            BusinessValidationException ex,
            HttpServletRequest request) {
        ...
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request) {
        ...
    }
}

Use one private helper to avoid duplicate response-building code.

For BaseException categories:

code   = ex.getCode()
detail = ex.getDetail()
path   = request.getRequestURI()
traceId = current trace ID

For unexpected exceptions:

code = "internal.server_error"
detail = "An unexpected error occurred"

Do NOT expose the original exception message to the client.

Log the full exception server-side.

==================================================
5. HTTP 400
==================================================

Do NOT create a custom validation exception.

Do NOT create ValidationError.

Do NOT add an "errors" field to ApiErrorResponse.

Use normal Spring Bean Validation:

@Valid
@RequestBody ...

Let Spring handle MethodArgumentNotValidException using its normal
validation mechanism.

Do not add a custom 400 handler unless the project explicitly requires
a custom 400 response later.

==================================================
6. HTTP 401 / 403
==================================================

Do NOT handle these in GlobalExceptionHandler.

Use Spring Security mechanisms:

401:
AuthenticationEntryPoint

403:
AccessDeniedHandler

Create:

api/security/RestAuthenticationEntryPoint.java
api/security/RestAccessDeniedHandler.java

Both should return ApiErrorResponse.

401 example:

{
  "code": "auth.token.invalid",
  "detail": "Access token is invalid or expired",
  "path": "/api/v1/profile",
  "traceId": "a1b2c3d4"
}

403 example:

{
  "code": "core.user.insufficient_permission",
  "detail": "You do not have permission to perform this action",
  "path": "/api/v1/admin/users",
  "traceId": "a1b2c3d4"
}

==================================================
7. HTTP 429
==================================================

Do NOT create a domain exception for 429.

429 is an infrastructure/rate-limiting concern.

If rate limiting is implemented by an API Gateway, the application does
not need to handle it.

If rate limiting is implemented inside Spring, handle it in the
appropriate filter/infrastructure layer.

==================================================
8. TRACE ID
==================================================

Create:

api/filter/TraceIdFilter.java

Use OncePerRequestFilter.

Behavior:

1. Read X-Trace-Id from request header.
2. If missing, generate a UUID.
3. Put it into MDC using key "traceId".
4. Add X-Trace-Id to the response.
5. Remove MDC value in finally.

Example:

X-Trace-Id: a1b2c3d4

The trace ID must be available to:
- GlobalExceptionHandler
- AuthenticationEntryPoint
- AccessDeniedHandler
- application logs

Do not leak MDC values between requests.

==================================================
9. ERROR CODE CONVENTION
==================================================

Use stable machine-readable codes:

service.entity.error

Examples:

auth.token.invalid
core.user.not_found
core.user.email_already_exists
core.order.not_found
core.order.cannot_be_modified
internal.server_error

Clients should use "code" for business logic and i18n.

Do not rely on "detail".

==================================================
10. DATABASE / INFRASTRUCTURE ERRORS
==================================================

Technical exceptions must not leak to the API.

For example:

DataIntegrityViolationException
        ↓
EmailAlreadyExistsException
        ↓
ConflictException
        ↓
GlobalExceptionHandler
        ↓
409

Optimistic locking conflict:

OptimisticLockException
        ↓
appropriate ConflictException
        ↓
GlobalExceptionHandler
        ↓
409

Translate infrastructure exceptions into appropriate application/domain
exceptions where necessary.

==================================================
11. FOLDER STRUCTURE
==================================================

Use approximately:

src/main/java/.../
├── api/
│   ├── advice/
│   │   └── GlobalExceptionHandler.java
│   ├── error/
│   │   └── ApiErrorResponse.java
│   ├── filter/
│   │   └── TraceIdFilter.java
│   └── security/
│       ├── RestAuthenticationEntryPoint.java
│       └── RestAccessDeniedHandler.java
│
├── application/
│   └── ...
│
├── domain/
│   ├── exception/
│   │   ├── BaseException.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── ConflictException.java
│   │   └── BusinessValidationException.java
│   └── ...
│
└── infrastructure/
    └── ...

Concrete domain exceptions can live close to their domain:

domain/order/exception/OrderCannotBeModifiedException.java

==================================================
12. IMPORTANT DDD RULES
==================================================

Domain and application layers must NOT depend on:

- HttpStatus
- ResponseEntity
- HttpServletRequest
- Spring MVC
- REST controllers
- HTTP status codes

Only the API layer maps exceptions to HTTP.

The domain only expresses business meaning.

Example:

OrderCannotBeModifiedException

NOT:

OrderCannotBeModified422Exception

==================================================
13. FINAL HTTP MAPPING
==================================================

400 -> Spring validation
401 -> AuthenticationEntryPoint
403 -> AccessDeniedHandler
404 -> ResourceNotFoundException
409 -> ConflictException
422 -> BusinessValidationException
429 -> Rate limiter / Gateway
500 -> generic Exception handler

==================================================
14. KEEP IT SIMPLE
==================================================

Do NOT introduce unnecessary abstractions such as:

- ExceptionHttpStatusMapper
- ErrorResponseFactory
- ErrorResponseBuilder
- custom ValidationError
- custom validation hierarchy
- one handler per concrete exception
- HTTP status inside domain exceptions

Use straightforward Spring mechanisms.

The final GlobalExceptionHandler should have only 4 handlers:

handleNotFound()
handleConflict()
handleBusinessValidation()
handleUnexpected()

==================================================
15. DELIVERABLE
==================================================

Implement the complete mechanism in the existing project.

Before changing files:
1. Inspect the existing package structure.
2. Reuse existing conventions/classes if appropriate.
3. Do not duplicate an existing error-handling mechanism.

Then implement:
- exception hierarchy
- ApiErrorResponse
- GlobalExceptionHandler
- TraceIdFilter
- AuthenticationEntryPoint
- AccessDeniedHandler
- required Spring Security configuration
- representative concrete exceptions
- representative usage example

Also add tests for:

1. ResourceNotFoundException -> 404
2. ConflictException -> 409
3. BusinessValidationException -> 422
4. Unexpected exception -> 500
5. Unauthenticated request -> 401
6. Unauthorized request -> 403
7. Trace ID is present in error responses
8. Concrete exceptions are handled through their category
   rather than individual handlers

Keep the implementation minimal and production-ready.