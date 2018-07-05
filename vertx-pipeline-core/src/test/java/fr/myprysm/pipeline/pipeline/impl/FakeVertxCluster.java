package fr.myprysm.pipeline.pipeline.impl;

import io.vertx.core.Vertx;
import lombok.experimental.Delegate;


public class FakeVertxCluster implements Vertx {

    @Delegate(excludes = Cluster.class)
    private final Vertx vertx;

    public FakeVertxCluster(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public boolean isClustered() {
        return true;
    }

    private interface Cluster {
        boolean isClustered();
    }
}
