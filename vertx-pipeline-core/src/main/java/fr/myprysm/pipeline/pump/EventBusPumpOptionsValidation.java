package fr.myprysm.pipeline.pump;

import fr.myprysm.pipeline.validation.ValidationResult;
import io.vertx.core.json.JsonObject;

import static fr.myprysm.pipeline.validation.JsonValidation.isString;

public interface EventBusPumpOptionsValidation {

    static ValidationResult validate(JsonObject config) {
        return isString("address").apply(config);
    }
}
