package io.depa.bot.service.impl;

import io.depa.bot.service.BotService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.SingleHelper;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

public class BotServiceImpl implements BotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotServiceImpl.class);

    private final WebClient webClient;

    public BotServiceImpl(Vertx vertx, JsonObject config) {
        webClient = WebClient.create(vertx, new WebClientOptions().setTrustAll(true));
    }

    @Override
    public BotService getByCategoryId(int categoryId, Handler<AsyncResult<String>> handler) {
        webClient.getAbs("https://api.depa.io/post/page/0/1/category/" + categoryId)
                .rxSend()
                .doOnError(Throwable::printStackTrace)
                .map(HttpResponse::bodyAsJsonObject)
                .map(jsonObject -> jsonObject.getJsonArray("list"))
                .map(jsonArray -> jsonArray.getJsonObject(jsonArray.size() - 1))
                .map(jsonObject -> "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<page version=\"2.0\">\n" +
                        "<title>" + jsonObject.getString("title") + "</title>\n" +
                        "<attachment type=\"photo\" src=\"" + jsonObject.getString("imageUrl") + "\"/>\n" +
                        "<div>" + jsonObject.getString("body") + "</div>\n" +
                        "</page>")
                .subscribe(SingleHelper.toObserver(handler));
        return this;
    }
}
