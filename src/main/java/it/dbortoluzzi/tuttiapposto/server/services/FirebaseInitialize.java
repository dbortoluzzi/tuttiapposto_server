package it.dbortoluzzi.tuttiapposto.server.services;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class FirebaseInitialize {

    @Value( "${firebase.serviceAccount.path}" )
    private String serviceAccountPath;

    @PostConstruct
    public void initialize() {

        try {

            ClassLoader classLoader = getClass().getClassLoader();
            InputStream serviceAccount = classLoader.getResourceAsStream(serviceAccountPath);

            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
