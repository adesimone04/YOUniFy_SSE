package it.younify.modules.sgac.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The {@code JwtService} class is responsible for handling JSON Web Tokens (JWTs).
 * This includes generating new tokens, validating existing tokens, and extracting claims from them.
 * <p>
 * This service uses the {@code jjwt} library to perform JWT operations.
 */
@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Generates a new JWT for the given user details.
     *
     * @param userDetails The user details for whom the token is to be generated.
     * @return A JWT string.
     */
    public String generateToken(UserDetails userDetails) {

        Map<String, Object> extraClaims = new HashMap<>();

        if (userDetails instanceof User) {

            extraClaims.put("accountId", ((User) userDetails).getID());
            extraClaims.put("userType", "STUDENT");
        }
        else if (userDetails instanceof Host) {

            extraClaims.put("accountId", ((Host) userDetails).getID());
            extraClaims.put("userType", "HOST");
        }
        else {

            throw new IllegalArgumentException("USER_NOT_ABILITATED_FOR_TOKEN");
        }

        return generateToken(extraClaims, userDetails);
    }

    /**
     * Generates a new JWT with extra claims for the given user details.
     *
     * @param extraClaims Extra claims to be added to the JWT payload.
     * @param userDetails The user details for whom the token is to be generated.
     * @return A JWT string.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a JWT.
     * A token is considered valid if the username in the token matches the one in the
     * {@link UserDetails} and the token has not expired.
     *
     * @param token       The JWT to validate.
     * @param userDetails The user details to validate against.
     * @return {@code true} if the token is valid, {@code false} otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Extracts the username (subject) from a JWT.
     *
     * @param token The JWT from which to extract the username.
     * @return The username.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * A generic method to extract a single claim from a JWT.
     *
     * @param token          The JWT from which to extract the claim.
     * @param claimsResolver A function to apply to the claims to extract the desired value.
     * @param <T>            The type of the claim to be extracted.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
