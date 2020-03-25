package pso.decision_engine.service.impl;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import pso.decision_engine.config.AppConfig;
import pso.decision_engine.service.JwtService;

@Service
public class JwtServiceImpl implements JwtService {
	
	private static final Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	
	@Autowired
	private AppConfig appConfig;
	
	private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    
    private RSAPublicKey sqeUMPublicKey;
    
    private JWTVerifier jwtVerifier;
    private JWTVerifier sqeUMJwtVerifier;

	@PostConstruct
	public void init() throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(appConfig.getPrivateKey()));
		privateKey = (RSAPrivateKey) kf.generatePrivate(keySpecPKCS8);

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(appConfig.getPublicKey()));
        publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
        
        keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(appConfig.getSqeUMPublicKey()));
        sqeUMPublicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

        jwtVerifier=JWT.require(Algorithm.RSA256(publicKey, null)).build();
        sqeUMJwtVerifier=JWT.require(Algorithm.RSA256(sqeUMPublicKey, null)).build();
	}
    
	@Override
	public String generateJwt(String userId) {
		String signed = JWT.create()
                .withIssuer("DE")
                .withClaim("user", userId)
                .withIssuedAt(new Date())
                .withExpiresAt(createExpireDate(12* 60 * 60))
                .sign(Algorithm.RSA256(null, privateKey));
        return signed;
	}
	
	@Override
	public String getJwtPayload(String jwt) {
		if (jwt==null || jwt.isEmpty()) return null;
		int i=jwt.indexOf('.');
		if (i<=0) return null;
		i=jwt.indexOf('.', i+1);
		if (i<=0) return null;
		return jwt.substring(0, i);
	}

	@Override
	public String verifyJwt(String jwt) {
		try {
			DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
			if (!"DE".equals(decodedJWT.getIssuer())) return null;
			return decodedJWT.getClaim("user").asString();
		} catch (JWTVerificationException e) {
    		return null;
    	}
	}
	
	
	private static Date createExpireDate(long seconds) {
        return new Date(System.currentTimeMillis() + seconds * 1000l);
    }
	
	static public void main(String[] args) {
		try {
			KeyPairGenerator kpg=KeyPairGenerator.getInstance("RSA");
	        kpg.initialize(2048);
	        KeyPair kp = kpg.genKeyPair();
	        Key publicKey = kp.getPublic();
	        Key privateKey = kp.getPrivate();

	        String publicKeyBase64=Base64.getEncoder().encodeToString(publicKey.getEncoded());
	        System.out.println("publicKey="+publicKeyBase64);
	        
	        String privateKeyBase64=Base64.getEncoder().encodeToString(privateKey.getEncoded());
	        System.out.println("privateKey="+privateKeyBase64);
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public String verifySqeUMJwt(String jwt) {
		try {
			DecodedJWT decodedJWT = sqeUMJwtVerifier.verify(jwt);
			return decodedJWT.getClaim("user").asString();
		} catch (JWTVerificationException e) {
			logger.error("Error validating UM JWT "+jwt,e);
    		return null;
    	}
	}

}
