package practicum.manager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import practicum.manager.Managers;
import practicum.manager.TaskManager;
import practicum.tasks.EpicTask;
import practicum.tasks.SubTask;
import practicum.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    private final TaskManager taskManager;

    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }
    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handler);
    }
    public void start() {
        server.start();
    }
    public void stop() {
        server.stop(1);
    }
    private void handler(HttpExchange h) {
        try {
            System.out.println("\n/tasks: " + h.getRequestURI());
            final String path = h.getRequestURI().getPath().substring(7);
            switch (path) {
                case "":
                    if (!h.getRequestMethod().equals("GET")) {
                        System.out.println("/ Ждет GET запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(400, 0);
                    }
                    sendJsonBody(h, gson.toJson(taskManager.getPrioritizedTasks()));
                    break;
                case "history":
                    if (!h.getRequestMethod().equals("GET")) {
                        System.out.println("/ Ждет GET запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(400, 0);
                    }
                    sendJsonBody(h, gson.toJson(taskManager.getHistory()));
                    break;
                case "task":
                    handleTask(h);
                    break;
                case "subtask":
                    handleSubTask(h);
                    break;
                case "subtask/epic":
                    if (!h.getRequestMethod().equals("GET")) {
                        System.out.println("/ Ждет GET запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(400, 0);
                    }
                    final String query = h.getRequestURI().getQuery();
                    String idParam = query.substring(3); //?id=
                    final int id = Integer.parseInt(idParam);
                    List<SubTask> epicSubtasks = taskManager.getEpicSubTasks(id);
                    sendJsonBody(h, gson.toJson(epicSubtasks));
                    break;
                case "epic":
                    handleEpicTask(h);
                    break;
                default:
                    System.out.println("Неизветный запрос: "+ h.getRequestURI());
                    h.sendResponseHeaders(404,0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            h.close();
        }
    }
    private void handleTask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET":
                if (query == null) {
                    List<Task> tasks = taskManager.getTasks();
                    sendJsonBody(h, gson.toJson(tasks));
                    return;
                }
                String idParam = query.substring(3); //?id=
                int id = Integer.parseInt(idParam);
                sendJsonBody(h, gson.toJson(taskManager.getTask(id)));
                break;
            case "POST":
                String json = readBodyText(h);
                if (json.isEmpty()) {
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                Task task = gson.fromJson(json, Task.class);
                if (task.getId() != null) {
                    taskManager.updateTask(task);
                    h.sendResponseHeaders(200, 0);
                } else {
                    taskManager.addNewTask(task);
                    sendJsonBody(h, gson.toJson(task));
                }
                break;
            case "DELETE":
                if (query == null) {
                    taskManager.clearTasks();
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                idParam = query.substring(3); //?id=
                id = Integer.parseInt(idParam);
                taskManager.deleteTask(id);
                h.sendResponseHeaders(200, 0);
                break;
            default:
                System.out.println("Неизветный запрос: "+ h.getRequestURI());
                h.sendResponseHeaders(404,0);
        }
    }
    private void handleSubTask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET":
                if (query == null) {
                    List<SubTask> subTasks = taskManager.getSubTasks();
                    sendJsonBody(h, gson.toJson(subTasks));
                    return;
                }
                String idParam = query.substring(3); //?id=
                int id = Integer.parseInt(idParam);
                sendJsonBody(h, gson.toJson(taskManager.getSubTask(id)));
                break;
            case "POST":
                String json = readBodyText(h);
                if (json.isEmpty()) {
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                SubTask subTask = gson.fromJson(json, SubTask.class);
                if (subTask.getId() != null) {
                    taskManager.updateSubTask(subTask);
                    h.sendResponseHeaders(200, 0);
                } else {
                    taskManager.addNewSubTask(subTask);
                    sendJsonBody(h, gson.toJson(subTask));
                }
                break;
            case "DELETE":
                if (query == null) {
                    taskManager.clearSubTasks();
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                idParam = query.substring(3); //?id=
                id = Integer.parseInt(idParam);
                taskManager.deleteSubTask(id);
                h.sendResponseHeaders(200, 0);
                break;
            default:
                System.out.println("Неизветный запрос: "+ h.getRequestURI());
                h.sendResponseHeaders(404,0);
        }
    }
    private void handleEpicTask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET":
                if (query == null) {
                    List<EpicTask> epicTasks = taskManager.getEpicTasks();
                    sendJsonBody(h, gson.toJson(epicTasks));
                    return;
                }
                String idParam = query.substring(3); //?id=
                int id = Integer.parseInt(idParam);
                sendJsonBody(h, gson.toJson(taskManager.getEpicTask(id)));
                break;
            case "POST":
                String json = readBodyText(h);
                if (json.isEmpty()) {
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                EpicTask epicTask = gson.fromJson(json, EpicTask.class);
                if (epicTask.getId() != null) {
                    taskManager.updateEpicTask(epicTask);
                    h.sendResponseHeaders(200, 0);
                } else {
                    taskManager.addNewEpicTask(epicTask);
                    sendJsonBody(h, gson.toJson(epicTask));
                }
                break;
            case "DELETE":
                if (query == null) {
                    taskManager.clearEpicTasks();
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                idParam = query.substring(3); //?id=
                id = Integer.parseInt(idParam);
                taskManager.deleteEpicTask(id);
                h.sendResponseHeaders(200, 0);
                break;
            default:
                System.out.println("Неизветный запрос: "+ h.getRequestURI());
                h.sendResponseHeaders(404,0);
        }
    }
    private void sendJsonBody(HttpExchange h, String json) throws IOException {
        byte[] resp = json.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
    private String readBodyText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }
}
