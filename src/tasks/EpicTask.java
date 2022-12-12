package tasks;

import java.util.ArrayList;
import java.util.Map;

public class EpicTask extends Task {
    protected ArrayList<Integer> subTaskIds;

    public EpicTask(Integer id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        subTaskIds = new ArrayList<>();
    }

    public EpicTask(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subTaskIds = new ArrayList<>();
    }

    public void updateStatus(Map<Integer, SubTask> subTasks) {
        /* Комметарий по ревью:
            мне понравилось предложенное Вами решение по обновлению статуса Эпика (переопределить метод getStatus),
             я поппытался его реализовать, но возникла проблема:
            - т.к. мне порекомендовали хранить в Эпиках не сами подзадачи, а их айдишники, то я не могу рассчитать статус
            эпика из самого эпика, тк сам Эпик не имеет доступа к его подзадачам. Перегружать метод и передавать все саб-
            таски через параметр метода мне показалось глупо, учитывая то что откроется возможность нечаянно вызвать
            геттер предка (Task) и получить непересчитанный статус.

            В прежнем подходе я использовал метод setStatus(...) предка (Task) для установки значения в менеджере, но
            согласно вашему следующему критическому комментарию я не должен оставлять возможность изменить статус эпика 
            в обход пересчета статусов подзадач.

            Согласно ТЗ: имя, описание и статус задач не должны изменяться отдельно (они обновляются по новому объекту
            целиком), в связи с чем я убрал сеттеры для этих полей и вынужден объявить этот специальный метод для Эпика
            и по-прежнему вызывать его при каждом изменении саб-тасков
         */

        if (getSubTaskIds().isEmpty()) {
            status = TaskStatus.NEW;
            return;
        }

        ArrayList<TaskStatus> subTaskStatuses = new ArrayList<>(); /* Я не разобрался с коллекцией Set еще, посмотрю
                                            попозже, но вроде бы она должна быть у нас в курсе через пару спринтов */
        for (Integer subTaskId : getSubTaskIds()) {
            TaskStatus subTaskStatus = subTasks.get(subTaskId).getStatus();
            subTaskStatuses.add(subTaskStatus);
        }

        if (subTaskStatuses.contains(TaskStatus.NEW)
                && !subTaskStatuses.contains(TaskStatus.IN_PROGRESS) && !subTaskStatuses.contains(TaskStatus.DONE)) {
            status = TaskStatus.NEW;

        } else if (subTaskStatuses.contains(TaskStatus.DONE)
                && !subTaskStatuses.contains(TaskStatus.IN_PROGRESS) && !subTaskStatuses.contains(TaskStatus.NEW)) {
            status = TaskStatus.DONE;
        } else {
            status = TaskStatus.IN_PROGRESS;
        }
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTaskId(int id) {
        subTaskIds.add(id);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTaskIds=" + subTaskIds +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
