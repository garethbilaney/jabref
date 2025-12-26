package org.jabref.logic.citedrive;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthSessionRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthSessionRegistry.class);
    private static final OAuthSessionRegistry INSTANCE = new OAuthSessionRegistry();

    private final Map<String, CompletableFuture<String>> pending = new ConcurrentHashMap<>();

    public static OAuthSessionRegistry getInstance() {
        return INSTANCE;
    }

    private OAuthSessionRegistry() {
    }

    public CompletableFuture<String> register(String state) {
        var future = new CompletableFuture<String>();
        pending.put(state, future);
        return future;
    }

    public void complete(String state, String code) {
        var future = pending.remove(state);
        if (future != null) {
            future.complete(code);
        } else {
            LOGGER.warn("No pending OAuth session for state {}", state);
        }
    }

    public void fail(String state, Throwable t) {
        var future = pending.remove(state);
        if (future != null) {
            future.completeExceptionally(t);
        } else {
            LOGGER.warn("No pending OAuth session for state {} (fail)", state);
        }
    }
}
