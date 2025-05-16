package com.casino.grupo4_dws.casinoweb.managers;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Service
public class JWTManager {

    @Autowired
    private UserMapper userMapper;
    // Private KEY used to encrypt token
    private final String SECRET_KEY = "SeCrEtJWT123456789SeCrEtJWT123456789";

    // Receives userDTO and creates a token encapsulating its data
    public String generateToken(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);

        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claim("username", user.getUserName())
                .claim("isAdmin", user.getIsadmin())
                .claim("id", user.getId())
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
    public int getTokenPrivileges(String token) {
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
    // Check if a resource can be modified, either if the user has access or is an admin
    public boolean tokenHasPermission(String token, int id){
        Claims claims = verifyToken(token);
        Integer userId = claims.get("id", Integer.class);
        Boolean isAdmin = claims.get("isAdmin", Boolean.class);
        return (userId != null && userId.equals(id)) || (isAdmin != null && isAdmin);
    }

    // Check is token has admin permissions
    public boolean tokenBelongsToAdmin(String token){
        Claims claims = verifyToken(token);
        return claims.get("isAdmin", Boolean.class);
    }
}
