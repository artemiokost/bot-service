package io.depa.bot.util;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;

public interface Runner {

    Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    String PATH = "src/config/local.json";

    static void run(Class clazz){

        final ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", PATH));
        final ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions().addStore(fileStore);

        Vertx vertx = Vertx.vertx();

        ConfigRetriever.create(vertx, configRetrieverOptions).rxGetConfig().doOnSuccess(config ->
                vertx.rxDeployVerticle(clazz.getName(), new DeploymentOptions().setConfig(config))
                        .subscribe(result -> LOGGER.info("Deployment id is: " + result),
                                error -> LOGGER.error("Deployment is failed: " + error.getCause())))
                .subscribe(result -> LOGGER.info("Configuration succeeded"),
                        error -> LOGGER.error("Configuration failed: " + error.getCause()));
    }
}
