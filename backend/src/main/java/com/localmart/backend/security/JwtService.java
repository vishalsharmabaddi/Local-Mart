package com.localmart.backend.security;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.localmart.backend.user.User;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirySeconds;

    public JwtService(JwtEncoder jwtEncoder, @Value("${localmart.jwt.expiry-seconds}") long expirySeconds) {
        this.jwtEncoder = jwtEncoder;
        this.expirySeconds = expirySeconds;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();

        // payload = what the token says. Anyone can READ it, so no phone/password here.
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("localmart")
                .subject(String.valueOf(user.getId()))   // who: user id
                .claim("role", user.getRole().name())    // what they can do
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirySeconds))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getExpirySeconds() {
        return expirySeconds;
    }
}
