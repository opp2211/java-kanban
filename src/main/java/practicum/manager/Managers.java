package practicum.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import practicum.manager.http.HttpTaskManager;
import practicum.historymanager.HistoryManager;
import practicum.historymanager.InMemoryHistoryManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Managers {
    private static final File DEFAULT_FILE = new File("taskManagerData.csv");
    private static final String DEFAULT_HOST = "localhost";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy--HH:mm");

    public static TaskManager getDefault() {
        return HttpTaskManager.getLoaded(DEFAULT_HOST);
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
            @Override
            public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
                if (localDateTime != null)
                    jsonWriter.value(localDateTime.format(DATE_TIME_FORMATTER));
                else
                    jsonWriter.nullValue();
            }

            @Override
            public LocalDateTime read(JsonReader jsonReader) throws IOException {
                return LocalDateTime.parse(jsonReader.nextString(), DATE_TIME_FORMATTER);
            }
        });
        return gsonBuilder.create();
    }
}
