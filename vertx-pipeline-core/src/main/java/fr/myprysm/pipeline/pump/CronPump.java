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

import fr.myprysm.pipeline.validation.ValidationResult;
import io.reactivex.*;
import io.vertx.core.json.JsonObject;
import org.quartz.*;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class CronPump extends BaseJsonPump<CronPumpOptions> implements FlowableOnSubscribe<JsonObject> {
    private static final Logger LOG = LoggerFactory.getLogger(CronPump.class);
    private Scheduler scheduler;
    private FlowableEmitter<JsonObject> emitter;
    private JsonObject data;
    private AtomicLong tick = new AtomicLong();
    private JobDetail job;
    private Trigger trigger;

    @Override
    public Flowable<JsonObject> pump() {
        return Flowable.create(this, BackpressureStrategy.BUFFER);
    }

    @Override
    protected Completable startVerticle() {
        try {
            scheduler.start();
            return Completable.complete();
        } catch (SchedulerException exc) {
            error("Unable to start quartz scheduler: ", exc);
            return Completable.error(exc);
        }
    }

    @Override
    public CronPumpOptions readConfiguration(JsonObject config) {
        return new CronPumpOptions(config);
    }

    @Override
    public Completable configure(CronPumpOptions config) {
        trigger = newTrigger()
                .withSchedule(cronSchedule(config.getCron()))
                .withIdentity(name() + ".trigger").build();

        job = newJob(CronEmitter.class)
                .withIdentity(name() + ".job")
                .build();

        data = config.getData();
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            error("Unable to get default quartz scheduler: ", e);
            return Completable.error(e);
        }

        return Completable.complete();
    }

    @Override
    public ValidationResult validate(JsonObject config) {
        return CronPumpOptionsValidation.validate(config);
    }

    @Override
    public void subscribe(FlowableEmitter<JsonObject> emitter) throws Exception {
        this.emitter = emitter;
        JobDataMap jobData = job.getJobDataMap();
        jobData.put("emitter", emitter);
        jobData.put("tick", tick);
        jobData.put("data", data);
        Date ft = scheduler.scheduleJob(job, trigger);
        info("Next cron will be executed at {}", ft);
    }

    @Override
    protected Logger delegate() {
        return LOG;
    }

}
