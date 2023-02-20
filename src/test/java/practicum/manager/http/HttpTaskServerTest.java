package practicum.manager.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import practicum.manager.Managers;
import practicum.manager.TaskManager;
import practicum.tasks.EpicTask;
import practicum.tasks.SubTask;
import practicum.tasks.Task;
import practicum.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.http.HttpResponse.BodyHandler;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    static final String BASE_URL = "http://localhost:8080/tasks/";
    static final BodyHandler<String> STRING_BODY_HANDLER = HttpResponse.BodyHandlers.ofString();
    static final BodyHandler<Void> VOID_BODY_HANDLER = HttpResponse.BodyHandlers.discarding();
    static HttpClient client;
    KVServer kvServer;
    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson = Managers.getGson();

    Task task;
    EpicTask epicTask;
    SubTask subTask;


    @BeforeAll
    public static void beforeAll() {
        client = HttpClient.newHttpClient();
    }
    @BeforeEach
    public void beforeEach () throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();

        task = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null);
        taskManager.addNewTask(task);
        epicTask = new EpicTask("Test epicTask 1", "Test epicTask 1 description");
        taskManager.addNewEpicTask(epicTask);
        subTask = new SubTask("Test SubTask 1", "Test SubTask 1 description", TaskStatus.NEW, epicTask.getId(), 0, null);
        taskManager.addNewSubTask(subTask);

        taskManager.addNewTask(new Task("Test task 2", "Test task 2 description", TaskStatus.DONE, 0, null));
        taskManager.addNewEpicTask(new EpicTask("Test epicTask 1", "Test epicTask 1 description"));
        taskManager.addNewSubTask(new SubTask("Test SubTask 2", "Test SubTask 2 description", TaskStatus.NEW, epicTask.getId(), 0, null));
    }
    @AfterEach
    public void afterEach() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    public void getPriorTasks() throws IOException, InterruptedException {

        URI uri = URI.create(BASE_URL + "");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, STRING_BODY_HANDLER);

        String expected = gson.toJson(taskManager.getPrioritizedTasks());

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body());
    }
    @Test
    public void getHistory() throws IOException, InterruptedException {
        taskManager.getSubTask(subTask.getId());
        taskManager.getTask(task.getId());

        URI uri = URI.create(BASE_URL + "history");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, STRING_BODY_HANDLER);

        String expected = gson.toJson(taskManager.getHistory());
        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body());
    }
    @Test
    public void getEpicSubtasks() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "subtask/epic?id=" + epicTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, STRING_BODY_HANDLER);

        String expected = gson.toJson(taskManager.getEpicSubTasks(epicTask.getId()));
        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body());
    }
    @Test
    public void getTasks() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "task");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, STRING_BODY_HANDLER);

        String expected = gson.toJson(taskManager.getTasks());
        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body());
    }
    @Test
    public void getTaskById() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "task?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, STRING_BODY_HANDLER);

        String expected = gson.toJson(taskManager.getTask(task.getId()));
        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body());
    }
    @Test
    public void getEpicTasks() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "epic");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, STRING_BODY_HANDLER);

        String expected = gson.toJson(taskManager.getEpicTasks());
        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body());
    }
    @Test
    public void getEpicTaskById() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "epic?id=" + epicTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, STRING_BODY_HANDLER);

        String expected = gson.toJson(taskManager.getEpicTask(epicTask.getId()));
        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body());
    }
    @Test
    public void getSubTasks() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, STRING_BODY_HANDLER);

        String expected = gson.toJson(taskManager.getSubTasks());
        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body());
    }
    @Test
    public void getSubTaskById() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "subtask?id=" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, STRING_BODY_HANDLER);

        String expected = gson.toJson(taskManager.getSubTask(subTask.getId()));
        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body());
    }
    @Test
    public void deleteTasks() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "task");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, VOID_BODY_HANDLER);

        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getTasks().isEmpty());
    }
    @Test
    public void deleteTaskById() throws IOException, InterruptedException {
        int tasksSize = taskManager.getTasks().size();

        URI uri = URI.create(BASE_URL + "task?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, VOID_BODY_HANDLER);



        assertEquals(200, response.statusCode());
        assertEquals(tasksSize - 1, taskManager.getTasks().size());
    }
    @Test
    public void deleteEpicTasks() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "epic");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, VOID_BODY_HANDLER);

        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getEpicTasks().isEmpty());
    }
    @Test
    public void deleteEpicTaskById() throws IOException, InterruptedException {
        int epicTasksSize = taskManager.getEpicTasks().size();

        URI uri = URI.create(BASE_URL + "epic?id=" + epicTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, VOID_BODY_HANDLER);



        assertEquals(200, response.statusCode());
        assertEquals(epicTasksSize - 1, taskManager.getEpicTasks().size());
    }
    @Test
    public void deleteSubTasks() throws IOException, InterruptedException {
        URI uri = URI.create(BASE_URL + "subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, VOID_BODY_HANDLER);

        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getSubTasks().isEmpty());
    }
    @Test
    public void deleteSubTaskById() throws IOException, InterruptedException {
        int subTasksSize = taskManager.getSubTasks().size();

        URI uri = URI.create(BASE_URL + "subtask?id=" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, VOID_BODY_HANDLER);



        assertEquals(200, response.statusCode());
        assertEquals(subTasksSize - 1, taskManager.getSubTasks().size());
    }
    @Test
    public void addNewTask() throws IOException, InterruptedException {
        int tasksSize = taskManager.getTasks().size();
        String jsonTask = gson.toJson(new Task("Test task 3", "Test task 3 description",
                TaskStatus.DONE, 0, null));
        URI uri = URI.create(BASE_URL + "task");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, VOID_BODY_HANDLER);

        assertEquals(200, response.statusCode());
        assertEquals(tasksSize + 1, taskManager.getTasks().size());
    }
    @Test
    public void updateTask() throws IOException, InterruptedException {
        int tasksSize = taskManager.getTasks().size();
        String jsonTask = gson.toJson(new Task(task.getId(),"Test task 1->3", "Test task 1->3 description",
                TaskStatus.DONE, 0, null));
        URI uri = URI.create(BASE_URL + "task");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, VOID_BODY_HANDLER);

        assertEquals(200, response.statusCode());
        assertEquals(tasksSize, taskManager.getTasks().size());
        assertEquals(jsonTask, gson.toJson(taskManager.getTask(task.getId())));
    }
    @Test
    public void addNewEpicTask() throws IOException, InterruptedException {
        int epicTasksSize = taskManager.getEpicTasks().size();
        String jsonTask = gson.toJson(new EpicTask("Test epicTask 3", "Test epicTask 3 description"));
        URI uri = URI.create(BASE_URL + "epic");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, VOID_BODY_HANDLER);

        assertEquals(200, response.statusCode());
        assertEquals(epicTasksSize + 1, taskManager.getEpicTasks().size());
    }
    @Test
    public void updateEpicTask() throws IOException, InterruptedException {
        int epicTasksSize = taskManager.getEpicTasks().size();
        String jsonTask = gson.toJson(new EpicTask(epicTask.getId(),"Test epicTask 1->3", "Test epicTask 1->3 description"));
        URI uri = URI.create(BASE_URL + "epic");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, VOID_BODY_HANDLER);

        assertEquals(200, response.statusCode());
        assertEquals(epicTasksSize, taskManager.getEpicTasks().size());
        assertEquals("Test epicTask 1->3", taskManager.getEpicTask(epicTask.getId()).getName());
        assertEquals("Test epicTask 1->3 description", taskManager.getEpicTask(epicTask.getId()).getDescription());
    }
    @Test
    public void addNewSubTask() throws IOException, InterruptedException {
        int subTasksSize = taskManager.getSubTasks().size();
        String jsonTask = gson.toJson(new SubTask("Test SubTask 3", "Test SubTask 3 description",
                TaskStatus.NEW, epicTask.getId(), 0, null));
        URI uri = URI.create(BASE_URL + "subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, VOID_BODY_HANDLER);

        assertEquals(200, response.statusCode());
        assertEquals(subTasksSize + 1, taskManager.getSubTasks().size());
    }
    @Test
    public void updateSubTask() throws IOException, InterruptedException {
        int subTasksSize = taskManager.getSubTasks().size();
        String jsonTask = gson.toJson(new SubTask(subTask.getId(),"Test SubTask 1->3", "Test SubTask 1->3 description",
                TaskStatus.NEW, epicTask.getId(), 0, null));
        URI uri = URI.create(BASE_URL + "subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, VOID_BODY_HANDLER);

        assertEquals(200, response.statusCode());
        assertEquals(subTasksSize, taskManager.getSubTasks().size());
        assertEquals(jsonTask, gson.toJson(taskManager.getSubTask(subTask.getId())));
    }
}
