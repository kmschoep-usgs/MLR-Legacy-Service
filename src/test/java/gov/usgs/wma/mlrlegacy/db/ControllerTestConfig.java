package gov.usgs.wma.mlrlegacy.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@Profile("it")
public class ControllerTestConfig {

	@Bean
	@Primary
	public JwtDecoder jwtDecoder() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		return NimbusJwtDecoder.withPublicKey(testPublicKey()).signatureAlgorithm(SignatureAlgorithm.RS256).build();
	}

	@Bean
    public static RSAPublicKey testPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String keyString;

		try (InputStream inputStream = new ClassPathResource("test-it.pub").getInputStream()) {
			keyString = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8)
				.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
		}
		RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
			.generatePublic(new X509EncodedKeySpec(Base64.getMimeDecoder().decode(keyString)));
		
		return publicKey;
	}

    @Bean
	public static RSAPrivateKey testPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String keyString;

		try (InputStream inputStream = new ClassPathResource("test-it.pem").getInputStream()) {
			keyString = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8)
				.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
		}
		RSAPrivateKey privateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
			.generatePrivate(new PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(keyString)));
		
		return privateKey;
	}
}