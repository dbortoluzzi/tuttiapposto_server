package it.dbortoluzzi.tuttiapposto.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FireStoreConfig {

	@Bean
	public Firestore getFireStore(@Value("${firebase.serviceAccount.path}") String credentialPath, @Value("${firebase.serviceAccount.json}") String credentialsJson) throws IOException {

		ByteArrayInputStream inputStream = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));
		GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);

		FirestoreOptions options = FirestoreOptions.newBuilder()
				.setCredentials(credentials).build();

		return options.getService();
	}

}