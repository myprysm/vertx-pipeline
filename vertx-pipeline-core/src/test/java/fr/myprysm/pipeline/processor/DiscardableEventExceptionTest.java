package fr.myprysm.pipeline.processor;

import fr.myprysm.pipeline.VertxTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Discardable event exception test")
class DiscardableEventExceptionTest implements VertxTest {

    @Test
    @DisplayName("It can provide the discarded event")
    void itCanProvideTheDiscardedEvent() {
        JsonObject data = obj().put("some", "content");
        DiscardableEventException some_error = new DiscardableEventException(new NullPointerException("some error"), data);

        assertThat(some_error.getEvent()).isEqualTo(data);
    }

}