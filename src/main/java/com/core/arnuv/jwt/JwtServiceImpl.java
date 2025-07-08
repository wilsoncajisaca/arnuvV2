package com.core.arnuv.jwt;

import com.core.arnuv.services.imp.UserServicesAuth;
import com.core.arnuv.services.imp.UsuarioDetalleServiceImp;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;


/**
 * Servicio encargado de la generación y validación de los tokens JWT
 * utilizados por la aplicación.
 */
@Service
public class JwtServiceImpl implements IJwtService {

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private String tokenSession;

    @Autowired
    private UserServicesAuth userServicesAuth;

    /**
     * Obtiene el nombre del usuario codificado dentro de un token JWT.
     *
     * @param token cadena JWT de la cual se quiere extraer el sujeto
     * @return nombre de usuario contenido en el token
     */
    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Verifica si el token es válido para el usuario indicado.
     *
     * @param token       token JWT a validar
     * @param userDetails datos del usuario autenticado
     * @return {@code true} si el token pertenece al usuario y no ha expirado
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Devuelve todas las reclamaciones almacenadas en el token de la sesión.
     *
     * @return {@link Claims} extraídas del token actual de sesión
     */
    @Override
    public Claims extraerTokenData() {
        return this.extractAllClaims(getTokenSession());
    }

    /**
     * Extrae una reclamación específica de un token.
     *
     * @param token          token JWT
     * @param claimsResolvers función que indica qué dato obtener de las reclamaciones
     * @param <T>            tipo de dato esperado
     * @return valor solicitado desde el token
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Construye un token JWT con vigencia de un día.
     *
     * @param extraClaims datos adicionales que se incluirán en el token
     * @param userDetails detalles del usuario propietario del token
     * @return token JWT generado
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        var diaexp = System.currentTimeMillis() + (1000 * 60 * 60 * 24); // expira al siguiente dia
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(diaexp))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Genera un token para el usuario indicado y lo envía en la cabecera Authorization.
     *
     * @param extraClaims información adicional para el token
     * @param userDetails detalles del usuario autenticado
     * @return cabeceras HTTP con el token generado
     */
    @Override
    public HttpHeaders generaToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        var jwt = this.generateToken(extraClaims, userDetails);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", jwt);
        return responseHeaders;
    }

    /**
     * Devuelve el token de la sesión actual dentro de la cabecera Authorization.
     *
     * @return cabeceras HTTP que contienen el token vigente
     */
    @Override
    public HttpHeaders regeneraToken() {
        HttpHeaders responseHeaders = new HttpHeaders();
        if (tokenSession != null) {
            responseHeaders.set("Authorization", tokenSession);
        }
        return responseHeaders;
    }

    /**
     * Verifica si un token ha expirado comparando la fecha actual con la de expiración.
     *
     * @param token token JWT a revisar
     * @return {@code true} si la fecha de expiración es anterior a "ahora"
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Obtiene la fecha de expiración de un token JWT.
     *
     * @param token token del que se quiere conocer la expiración
     * @return fecha de expiración del token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae todas las reclamaciones presentes en el token.
     *
     * @param token token JWT
     * @return objeto {@link Claims} con toda la información del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    /**
     * Almacena el token recibido en la sesión actual para reutilizarlo en las respuestas.
     *
     * @param token token JWT que se registrará en sesión
     */
    @Override
    public void setTokenSession(String token) {
        this.tokenSession = token;
    }

    /**
     * Obtiene el token almacenado para la sesión actual.
     *
     * @return token JWT de la sesión
     */
    @Override
    public String getTokenSession() {
        return tokenSession;
    }

    /**
     * Obtiene la llave criptográfica utilizada para firmar los tokens.
     *
     * @return instancia {@link Key} para firmar o verificar tokens
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token de corta duración utilizado para usuarios de reciente creación.
     *
     * @param extraClaims información adicional que se incluirá en el token
     * @param userDetails datos del usuario para el cual se genera el token
     * @return token JWT con una validez aproximada de una hora
     */
    public String generateTokenNuevoUser(Map<String, Object> extraClaims, UserDetails userDetails) {
        var diaexp = System.currentTimeMillis() + (1000 * 60 * 60); // expira en una hora
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(diaexp))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Carga los detalles del usuario identificado por su nombre de usuario o correo electrónico.
     *
     * @param email nombre de usuario o correo electrónico
     * @return detalles de autenticación del usuario
     */
    public UserDetails loadUserByUsername(String email) {
        return userServicesAuth.loadUserByUsername(email);
    }
}
