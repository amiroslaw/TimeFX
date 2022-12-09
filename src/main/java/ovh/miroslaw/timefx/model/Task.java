package ovh.miroslaw.timefx.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ovh.miroslaw.timefx.LocalDateTimeDeserializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Task {

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime start;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime end;
    private List<String> tags;

    public Task() {
        super();
    }

    public Task(LocalDateTime start, LocalDateTime end, List<String> tags) {
        this.start = start;
        this.end = end;
        this.tags = tags;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public TagDuration mapToTagDuration(TagType type) {
        final String tag = getTags().stream()
                .filter(type::test)
                .findFirst()
                .orElse(" without");
        final Duration duration = end == null ? Duration.ofMinutes(0) : Duration.between(end, start);
        return new TagDuration(tag, duration);
    }

    public Optional<TagTask> mapTagName(TagType type) {
               return getTags().stream()
                .filter(type::test)
                .findFirst()
                .map(tag -> new TagTask(tag, this));
    }

    @Override
    public String toString() {
        return "Task{" +
               "start=" + start +
               ", end=" + end +
               ", tags=" + tags +
               '}';
    }
}
