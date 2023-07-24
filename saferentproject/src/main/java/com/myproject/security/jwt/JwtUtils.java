package com.myproject.security.jwt;



import com.myproject.exception.message.ErrorMessage;
import io.jsonwebtoken.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;

import java.util.Date;

@Component
public class JwtUtils {


    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${saferentproject.app.jwtSecret}")
    private String jwtSecret ;
    @Value("${saferentproject.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    // Generate JWT Token
    public String generateJwtToken(UserDetails userDetails){
        return Jwts.builder().
                setSubject(userDetails.getUsername()).
                setIssuedAt(new Date()).
                setExpiration(new Date(new Date().getTime() + jwtExpirationMs)).
                signWith(SignatureAlgorithm.HS512, jwtSecret).
                compact();
    }

    // JWT token icinden email bilgisine ulasacagım
    public String getEmailFromToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).
                parseClaimsJws(token).
                getBody().
                getSubject();
    }

    // JWT validate

    public boolean validateJwtToken(String token){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | IllegalArgumentException | SignatureException | MalformedJwtException |
                 UnsupportedJwtException e) {
            logger.error(String.format(ErrorMessage.JWTTOKEN_ERROR_MESSAGE,e.getMessage()));
        }
        return false;
    }


}
