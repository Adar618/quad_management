# Security Patch (JWT + RBAC)

**Add these to `application.yaml`:**

```yaml
jwt:
  secret: ${JWT_SECRET:change-me}
  expiration: ${JWT_EXPIRATION:86400000}
```

**Add dependencies to `pom.xml` inside `<dependencies>`:**

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.11.5</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.11.5</version>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.11.5</version>
  <scope>runtime</scope>
</dependency>
```

**Endpoints:**
- `POST /auth/login` (public) → returns `{ accessToken, tokenType, userId, username, role }`
- `GET /api/v1/me` (auth)

**Role enforcement:**
- Admin: all actions.
- Operator: self-only for `/api/v1/users/{id}`, `/api/v1/users/{id}/bookings`, booking create (forced to self), and cancel (only own & not started).
