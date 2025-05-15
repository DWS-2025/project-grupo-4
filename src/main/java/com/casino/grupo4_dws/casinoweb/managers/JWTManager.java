package com.casino.grupo4_dws.casinoweb.managers;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.ExpiredJwtException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Date;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Service
public class JWTManager {

    @Autowired
    private UserMapper userMapper;
    // Private KEY used to encript token
    private final String SECRET_KEY = "SeCrEtJWT123456789SeCrEtJWT123456789";

    // Receives userDTO and creates a token encapsulating its data
    public String generateToken(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);

        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claim("username", user.getUserName())
                .claim("isAdmin", user.getIsadmin())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(key)
                .compact();
    }

    public Claims verifyToken(String token) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Returns -1 if invalid token, 0 if standard user, 1 if admin
    public int userPrivileges(String token) {
        try {
            Claims claims = verifyToken(token);  // Uses Verifytoken
            if(claims.get("isAdmin", Boolean.class)) {
                return 1;
            }
            return 0;

        } catch (JwtException | IllegalArgumentException e) {
            return -1;
        }
    }
}
