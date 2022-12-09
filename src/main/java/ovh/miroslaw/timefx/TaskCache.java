package ovh.miroslaw.timefx;

import ovh.miroslaw.timefx.model.Task;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TaskCache {

    private static final String ALL = "All";
    private static final Map<String, List<Task>> cache = new HashMap<>();

    private TaskCache() {
    }

    public static List<Task> getTasks(Boundary boundary) {
        if (cache.isEmpty()) {
            final List<Task> tasks = DataReader.getData();
            cache.put(ALL, Collections.unmodifiableList(tasks));
            return tasks;
        } else {
            return cache.computeIfAbsent(boundary.getRangeName(), k ->
                    cache.get(ALL).parallelStream()
                            .filter(t -> t.getEnd() != null)
                            .filter(boundary.getIsInRange())
                            .toList()
            );
        }
    }
}

