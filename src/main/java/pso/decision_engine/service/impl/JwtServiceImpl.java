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
	
	@Autowired
	private AppConfig appConfig;
	
	private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    
    private JWTVerifier jwtVerifier;

	@PostConstruct
	public void init() throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(appConfig.getPrivateKey()));
		privateKey = (RSAPrivateKey) kf.generatePrivate(keySpecPKCS8);

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(appConfig.getPublicKey()));
        publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

        jwtVerifier=JWT.require(Algorithm.RSA256(publicKey, null)).build();
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

}
