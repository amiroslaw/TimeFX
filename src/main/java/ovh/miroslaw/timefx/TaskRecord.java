package ovh.miroslaw.timefx;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

//@JsonSerialize
//@JsonAutoDetect(fieldVisibility = NONE, getterVisibility = ANY, setterVisibility = ANY)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TaskRecord(
        @JsonProperty("id") long id, String start, String end, List<String> tags
) {

}
