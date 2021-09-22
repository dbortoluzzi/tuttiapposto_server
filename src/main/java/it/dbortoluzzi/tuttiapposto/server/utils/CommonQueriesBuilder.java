package it.dbortoluzzi.tuttiapposto.server.utils;

import com.google.cloud.firestore.Query;
import it.dbortoluzzi.tuttiapposto.server.models.Booking;

import java.util.Optional;

public class CommonQueriesBuilder {

    private Query query;

    public CommonQueriesBuilder(Query query) {
        this.query = query;
    }

    public static CommonQueriesBuilder newBuilder(Query query) {
        return new CommonQueriesBuilder(query);
    }

    public CommonQueriesBuilder id(String id) {
        query = query.whereEqualTo("uID", id);
        return this;
    }

    public CommonQueriesBuilder company(String companyId) {
        query = query.whereEqualTo("companyId", companyId);
        return this;
    }

    public CommonQueriesBuilder company(Optional<String> companyIdOpt) {
        companyIdOpt.ifPresent(s -> query = query.whereEqualTo("companyId", s));
        return this;
    }

    public CommonQueriesBuilder building(String buildingId) {
        query = query.whereEqualTo("buildingId", buildingId);
        return this;
    }

    public CommonQueriesBuilder building(Optional<String> buildingIdOpt) {
        buildingIdOpt.ifPresent(s -> query = query.whereEqualTo("buildingId", s));
        return this;
    }

    public CommonQueriesBuilder room(String roomId) {
        query = query.whereEqualTo("roomId", roomId);
        return this;
    }

    public CommonQueriesBuilder room(Optional<String> roomIdOpt) {
        roomIdOpt.ifPresent(s -> query = query.whereEqualTo("roomId", s));
        return this;
    }

    public CommonQueriesBuilder table(String tableId) {
        query = query.whereEqualTo("tableId", tableId);
        return this;
    }

    public CommonQueriesBuilder active(Boolean active) {
        query = query.whereEqualTo("active", active);
        return this;
    }

    public CommonQueriesBuilder user(Optional<String> userIdOpt) {
        userIdOpt.ifPresent(s -> query = query.whereEqualTo("userId", s));
        return this;
    }

    public Query buildQuery() {
        return query;
    }
}
