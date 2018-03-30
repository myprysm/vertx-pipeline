/*
 * Copyright 2018 the original author or the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.myprysm.pipeline.pump;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import static fr.myprysm.pipeline.util.JsonHelpers.obj;

@DataObject(generateConverter = true)
public class CronPumpOptions extends PumpOptions {
    public static final JsonObject DEFAULT_DATA = obj();
    private static final String DEFAULT_EMITTER = "fr.myprysm.pipeline.pump.CronEmitter";
    private String cron;
    private JsonObject data = DEFAULT_DATA;
    private String emitter = DEFAULT_EMITTER;

    public CronPumpOptions() {

    }

    public CronPumpOptions(CronPumpOptions other) {
        super(other);
        cron = other.cron;
        data = other.data;
        emitter = other.emitter;
    }

    public CronPumpOptions(PumpOptions other) {
        super(other);
    }

    public CronPumpOptions(JsonObject json) {
        super(json);
        CronPumpOptionsConverter.fromJson(json, this);
    }

    /**
     * Quartz cron expression
     *
     * @return quartz cron expression
     */
    public String getCron() {
        return cron;
    }

    /**
     * Quartz cron expression
     * <p>
     * The official documentation to write your own epression is available at
     * <a href="http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html">this address</a>.
     *
     * @param cron quartz cron expression
     * @return this
     */
    public CronPumpOptions setCron(String cron) {
        this.cron = cron;
        return this;
    }

    /**
     * The custom data to add with the tick
     *
     * @return the custom data to add with the tick
     */
    public JsonObject getData() {
        return data;
    }

    /**
     * The custom data to add with the tick
     * <p>
     * It can be any arbitrary json/yaml data.
     * <p>
     * No additional data is sent when it is null or empty
     *
     * @param data the additional data
     * @return this
     */
    public CronPumpOptions setData(JsonObject data) {
        this.data = data;
        return this;
    }

    /**
     * The emitter class
     *
     * @return the emitter class
     */
    public String getEmitter() {
        return emitter;
    }

    /**
     * The emitter class
     * Provides the ability to execute custom job by providing a custom implementation of a <code>CronEmitter</code>
     * <p>
     * This can be useful to extract a dataset for batch process on a bunch of single items.
     * <p>
     * Defaults to <code>fr.myprysm.pipeline.pump.CronEmitter</code>
     *
     * @param emitter the emitter class
     * @return this
     */
    public CronPumpOptions setEmitter(String emitter) {
        this.emitter = emitter;
        return this;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public CronPumpOptions setName(String name) {
        return (CronPumpOptions) super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public CronPumpOptions setType(String type) {
        return (CronPumpOptions) super.setType(type);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        CronPumpOptionsConverter.toJson(this, json);

        return json;
    }


}
