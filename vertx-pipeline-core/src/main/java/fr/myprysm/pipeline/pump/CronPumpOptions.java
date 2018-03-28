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
    private String cron;
    private JsonObject data = DEFAULT_DATA;

    public CronPumpOptions() {

    }

    public CronPumpOptions(CronPumpOptions other) {
        super(other);
        cron = other.cron;
    }

    public CronPumpOptions(PumpOptions other) {
        super(other);
    }

    public CronPumpOptions(JsonObject json) {
        super(json);
        CronPumpOptionsConverter.fromJson(json, this);
    }

    public String getCron() {
        return cron;
    }

    public CronPumpOptions setCron(String cron) {
        this.cron = cron;
        return this;
    }

    public JsonObject getData() {
        return data;
    }

    public CronPumpOptions setData(JsonObject data) {
        this.data = data;
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
