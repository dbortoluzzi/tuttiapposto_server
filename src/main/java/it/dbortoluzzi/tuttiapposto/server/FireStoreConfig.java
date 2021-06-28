package it.dbortoluzzi.tuttiapposto.server;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FireStoreConfig {

	@Bean
	public Firestore getFireStore(@Value("${firebase.serviceAccount.path}") String credentialPath) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream serviceAccount = classLoader.getResourceAsStream(credentialPath);

		GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

		FirestoreOptions options = FirestoreOptions.newBuilder()
				.setCredentials(credentials).build();

		return options.getService();
	}

}