package fr.myprysm.pipeline.sink;

import fr.myprysm.pipeline.validation.JsonValidation;
import fr.myprysm.pipeline.validation.ValidationResult;
import io.vertx.core.json.JsonObject;

import static fr.myprysm.pipeline.validation.JsonValidation.arrayOf;
import static fr.myprysm.pipeline.validation.JsonValidation.isNull;

public interface EventBusSinkOptionsValidation {
    static ValidationResult validate(JsonObject config) {
        return hasAtLeastPublishOrSend()
                .or(arrayOf("publish", String.class).and(arrayOf("send", String.class)))
                .apply(config);
    }

    static JsonValidation hasAtLeastPublishOrSend() {
        return arrayOf("publish", String.class).and(isNull("send"))
                .or(arrayOf("send", String.class).and(isNull("publish")));
    }
}
