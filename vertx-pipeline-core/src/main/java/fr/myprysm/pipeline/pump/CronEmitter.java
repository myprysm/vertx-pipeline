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

import io.reactivex.FlowableEmitter;
import io.vertx.core.json.JsonObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.concurrent.atomic.AtomicLong;

public class CronEmitter implements Job {
    public CronEmitter() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobData = context.getMergedJobDataMap();
        FlowableEmitter<JsonObject> emitter = (FlowableEmitter<JsonObject>) jobData.get("emitter");
        AtomicLong tick = (AtomicLong) jobData.get("tick");
        JsonObject data = (JsonObject) jobData.get("data");
        if (emitter != null) {
            emitter.onNext(data.copy().put("counter", tick.incrementAndGet()).put("timestamp", System.currentTimeMillis()));
        }
    }
}
