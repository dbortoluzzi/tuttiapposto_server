package it.dbortoluzzi.tuttiapposto.server.repositories;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import javax.print.Doc;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractFirestoreRepository<T> {
    public final CollectionReference collectionReference;
    protected final String collectionName;
    protected final Class<T> parameterizedType;

    protected AbstractFirestoreRepository(Firestore firestore, String collection) {
        this.collectionReference = firestore.collection(collection);
        this.collectionName = collection;
        this.parameterizedType = getParameterizedType();
    }

    private Class<T> getParameterizedType() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }

    public Optional<String> save(T model) {
        String documentId = getDocumentId(model);
        ApiFuture<WriteResult> resultApiFuture = collectionReference.document(documentId).set(model);

        try {
            log.info("{}-{} saved at {}", collectionName, documentId, resultApiFuture.get().getUpdateTime());
            return Optional.of(documentId);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error saving {}={} {}", collectionName, documentId, e.getMessage());
        }

        return Optional.empty();

    }

    public boolean delete(String documentId) {
        try {
            collectionReference.document(documentId).delete();
            return true;
        } catch (Exception e) {
            log.error("Exception occurred while deleting document for {}", collectionName, e);
        }
        return false;
    }

    public List<T> findByQuery(Query query) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = query.get();

        try {
            List<QueryDocumentSnapshot> queryDocumentSnapshots = querySnapshotApiFuture.get().getDocuments();

            return queryDocumentSnapshots.stream()
                    .map(queryDocumentSnapshot -> queryDocumentSnapshot.toObject(parameterizedType))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception occurred while retrieving filtered document for {} with filters {}", collectionName, query, e);
            throw e;
        }
    }

    public List<T> findByQueryPaginated(Query query, Optional<String> lastDocumentIdOpt) {
        if (lastDocumentIdOpt.isPresent()) {
            Optional<DocumentSnapshot> lastDocumentSnapshot = getDocSnapshot(lastDocumentIdOpt.get());
            if (lastDocumentSnapshot.isPresent()) {
                query = query.startAfter(lastDocumentSnapshot.get());
            }
        }
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = query.get();

        try {

            List<QueryDocumentSnapshot> queryDocumentSnapshots = querySnapshotApiFuture.get().getDocuments();

            return queryDocumentSnapshots.stream()
                    .map(queryDocumentSnapshot -> queryDocumentSnapshot.toObject(parameterizedType))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception occurred while retrieving filtered document for {}", collectionName, e);
        }
        return Collections.<T>emptyList();

    }

    public List<T> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = collectionReference.get();

        try {
            List<QueryDocumentSnapshot> queryDocumentSnapshots = querySnapshotApiFuture.get().getDocuments();

            return queryDocumentSnapshots.stream()
                    .map(queryDocumentSnapshot -> queryDocumentSnapshot.toObject(parameterizedType))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception occurred while retrieving all document for {}", collectionName, e);
            throw e;
        }

    }


    private Optional<DocumentSnapshot> getDocSnapshot(String documentId) {
        DocumentReference documentReference = collectionReference.document(documentId);
        ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = documentReference.get();

        try {
            DocumentSnapshot documentSnapshot = documentSnapshotApiFuture.get();

            if (documentSnapshot.exists()) {
                return Optional.ofNullable(documentSnapshot);
            }

        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception occurred retrieving: {} {}, {}", collectionName, documentId, e.getMessage());
        }

        return Optional.empty();

    }

    public Optional<T> get(String documentId) {
        DocumentReference documentReference = collectionReference.document(documentId);
        ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = documentReference.get();

        try {
            DocumentSnapshot documentSnapshot = documentSnapshotApiFuture.get();

            if (documentSnapshot.exists()) {
                return Optional.ofNullable(documentSnapshot.toObject(parameterizedType));
            }

        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception occurred retrieving: {} {}, {}", collectionName, documentId, e.getMessage());
        }

        return Optional.empty();

    }


    protected String getDocumentId(T t) {
        Object key;
        Class clzz = t.getClass();
        do {
            key = getKeyFromFields(clzz, t);
            clzz = clzz.getSuperclass();
        } while (key == null && clzz != null);

        if (key == null) {
            return collectionReference.document().getId();
        }
        return String.valueOf(key);
    }

    private Object getKeyFromFields(Class<?> clazz, Object t) {

        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(DocumentId.class))
                .findFirst()
                .map(field -> getValue(t, field))
                .orElse(null);
    }

    @Nullable
    private Object getValue(Object t, java.lang.reflect.Field field) {
        field.setAccessible(true);
        try {
            return field.get(t);
        } catch (IllegalAccessException e) {
            log.error("Error in getting documentId key", e);
        }
        return null;
    }

    protected CollectionReference getCollectionReference() {
        return this.collectionReference;
    }

    protected Class<T> getType() {
        return this.parameterizedType;
    }
}