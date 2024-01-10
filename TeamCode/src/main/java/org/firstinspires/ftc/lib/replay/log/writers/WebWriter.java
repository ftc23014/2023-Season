package org.firstinspires.ftc.lib.replay.log.writers;

import androidx.core.util.Pair;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.iki.elonen.NanoHTTPD;
import org.checkerframework.checker.units.qual.A;
import org.firstinspires.ftc.lib.server.api.ApiHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;

public class WebWriter extends LogWriter {
    static protected WebWriter instance;
    static protected Endpoint webhostInstance;

    public static class Endpoint extends ApiHandler {
        protected boolean enabled;

        private ArrayList<Pair<String, Long>> cache;

        private long lastClearout = 0;

        public Endpoint() {
            super();

            WebWriter.webhostInstance = this;
            cache = new ArrayList<>();
        }

        public void addToCache(String str) {
            cache.add(new Pair<>(str, System.currentTimeMillis()));
        }

        public List<Pair<String, Long>> getAfterTimestamp(long timestamp) {
            if (cache.isEmpty()) {
                return new ArrayList<>();
            }

            if (timestamp < cache.get(0).second) {
                return cache;
            }

            if (timestamp > cache.get(cache.size() - 1).second) {
                return new ArrayList<>();
            }

            //if (timestamp > (cache.get(cache.size() -1).second + cache.get(0).second) / 2) {
                for (int i = cache.size() - 1; i >= 0; i--) {
                    if (cache.get(i).second < timestamp) {
                        return cache.subList(i + 1, cache.size());
                    }
                }
//            } else {
//                for (int i = 0; i < cache.size(); i++) {
//                    if (cache.get(i).second > timestamp) {
//                        return cache.subList(i + 1, cache.size());
//                    }
//                }
//            }

            return cache;
        }

        public void clearCache() {
            cache.clear();

            lastClearout = System.currentTimeMillis();
        }

        public void clearBeforeTimestamp(long timestamp) {
            cache.removeIf((e) -> {
                return e.second < timestamp;
            });

            lastClearout = System.currentTimeMillis();
        }

        @Override
        public String getRoute() {
            return "/api/log";
        }

        @Override
        public boolean exactMatch() {
            return false;
        }

        @Override
        public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
            if (!enabled) {
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND,"text/plain","404 NOT FOUND");
            }

            JsonObject log = new JsonObject();

            Gson gson = new Gson();

            if (session.getMethod() == NanoHTTPD.Method.POST) {
                Map<String, String> files = new HashMap<>();

                //get body
                try {
                    session.parseBody(files);
                } catch (IOException | NanoHTTPD.ResponseException ioe) {
                    return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
                }

                String json = files.get("postData");

                JsonObject object = gson.fromJson(json, JsonObject.class);

                long timestamp = object.get("timestamp").getAsLong();

                JsonArray arr = new JsonArray();

                for (Pair<String, Long> msg : getAfterTimestamp(timestamp)) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("msg", msg.first);
                    obj.addProperty("time", msg.second);

                    arr.add(obj);
                }

                log.add("log", arr);
                log.addProperty("total_cache", cache.size());
                log.addProperty("last_clear", lastClearout);

                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(log));
            } else {
                JsonArray arr = new JsonArray();

                for (Pair<String, Long> msg : cache) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("msg", msg.first);
                    obj.addProperty("time", msg.second);

                    arr.add(obj);
                }

                log.add("log", arr);
                log.addProperty("total_cache", cache.size());
                log.addProperty("last_clear", lastClearout);

                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(log));
            }
        }
    }

    public WebWriter() {
        instance = this;
    }

    @Override
    public void initialize() {
        webhostInstance.enabled = true;
    }

    @Override
    public void saveLine(String line) {
        webhostInstance.addToCache(line);
    }

    @Override
    public void saveInfo(String encodedInfo) {
        //todo: implement later
    }

    @Override
    public void close() {
        webhostInstance.clearCache();
        webhostInstance.enabled = false;
    }
}
