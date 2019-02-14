package io.depa.bot.service;

import io.depa.bot.service.impl.BotServiceImpl;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.reactivex.core.Vertx;

@VertxGen
@ProxyGen
public interface BotService {

    String SERVICE_ADDRESS = "service.bot";
    String SERVICE_NAME = "bot-service";

    @GenIgnore
    static BotService create(Vertx vertx) {
        return new BotServiceImpl(vertx);
    }

    @Fluent
    BotService getByCategoryId(int categoryId, Handler<AsyncResult<String>> handler);
}
