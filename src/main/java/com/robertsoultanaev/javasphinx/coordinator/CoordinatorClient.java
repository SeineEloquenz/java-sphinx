package com.robertsoultanaev.javasphinx.coordinator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.robertsoultanaev.javasphinx.SerializationUtils;
import com.robertsoultanaev.javasphinx.routing.MixNode;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CoordinatorClient {

    private final OkHttpClient client;
    private final String host;
    private final int port;

    public CoordinatorClient(String host, int port) {
        this.client = new OkHttpClient();
        this.host = host;
        this.port = port;
    }

    public Set<MixNode> getAllMixes() throws IOException {
        final var request = new Request.Builder()
                .url("%s:%s/api/mixes/all".formatted(host, port))
                .build();
        final var mixes = new HashSet<MixNode>();
        try (final var response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Call to coordinator was not successful. Error code %s".formatted(response.code()));
            } else {
                assert response.body() != null;
                final var mixArray = JsonParser.parseString(response.body().string()).getAsJsonArray();
                mixArray.asList().stream()
                        .map(JsonElement::getAsJsonObject)
                        .map(this::parseFromJson)
                        .forEach(mixes::add);
                return mixes;
            }
        }
    }

    private MixNode parseFromJson(JsonObject mixJson) {
        final var id = mixJson.get("id").getAsInt();
        final var host = mixJson.get("host").getAsString();
        final var port = mixJson.get("port").getAsInt();
        final var pubKey = SerializationUtils.decodeECPoint(SerializationUtils.base64decode(mixJson.get("pubKey").getAsString()));
        return new MixNode(id, host, port, pubKey);
    }
}
