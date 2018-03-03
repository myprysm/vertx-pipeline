/*
 * Copyright 2018 the original author or the original authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.myprysm.pipeline.pump;

import fr.myprysm.pipeline.util.Options;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject(generateConverter = true)
public class PumpOptions implements Options {
    public static final String DEFAULT_NAME = "timer-pump";
    public static final String DEFAULT_TYPE = "fr.myprysm.pipeline.pump.TimerPump";


    private String name = DEFAULT_NAME;
    private String type = DEFAULT_TYPE;

    public PumpOptions() {

    }

    public PumpOptions(PumpOptions other) {
        name = other.name;
        type = other.type;
    }

    public PumpOptions(JsonObject json) {
        PumpOptionsConverter.fromJson(json, this);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        PumpOptionsConverter.toJson(this, json);
        return json;
    }

    /**
     * The name of the pump.
     *
     * @return the name of the pump
     */
    public String getName() {
        return name;
    }

    /**
     * The name of the pump.
     * <p>
     * This is automatically populated when the pipeline configuration is a list.
     * <p>
     * You still can name your pump for any purpose by using a map instead of a list
     * when you describe your pipeline.
     *
     * @param name the name of the pump.
     * @return this
     */
    public PumpOptions setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * The type of the pump.
     *
     * @return the type of the pump.
     */
    public String getType() {
        return type;
    }

    /**
     * The type of the pump.
     * <p>
     * This is the fully qualified name of the <code>class</code> that acts as pump.
     *
     * @param type the type of the pump.
     * @return this
     */
    public PumpOptions setType(String type) {
        this.type = type;
        return this;
    }


    @Override
    public String toString() {
        return "PumpOptions{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PumpOptions)) return false;
        PumpOptions that = (PumpOptions) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }


}
