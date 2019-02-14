package io.depa.bot;

import io.depa.bot.service.BotService;
import io.depa.bot.util.Runner;
import io.reactivex.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.SingleHelper;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;

public class BotVerticle extends RestfulVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotVerticle.class);

    private static final int DEFAULT_PORT = 443;

    private BotService botService;

    public static void main(String[] args) {
        Runner.run(BotVerticle.class);
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();

        int port = DEFAULT_PORT;
        String host = "localhost";

        botService = BotService.create(vertx);

        OpenAPI3RouterFactory.rxCreate(vertx, "swagger.yaml").subscribe(factory -> {

            factory.addHandlerByOperationId("root", this::root);
            factory.addHandlerByOperationId("getByCategoryId", this::getByCategoryId);

            vertx.createHttpServer()
                    .requestHandler(factory.getRouter())
                    .rxListen(port, host)
                    .ignoreElement()
                    .subscribe(() -> LOGGER.info("Service <" + BotService.SERVICE_NAME + "> start at port: " + port),
                            LOGGER::error);
            }, LOGGER::error);
    }

    private void root(RoutingContext context) {
        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<page version=\"2.0\">\n" +
                "<title>Выберите категорию</title>\n" +
                "<navigation>\n" +
                "<link pageId=\"https://real-depa-bot.herokuapp.com/category/3\">Новости</link>\n" +
                "<link pageId=\"https://real-depa-bot.herokuapp.com/category/1\">Статьи</link>\n" +
                "<link pageId=\"https://real-depa-bot.herokuapp.com/category/2\">Блоги</link>\n" +
                "</navigation>\n" +
                "</page>";
        Single.just(body).subscribe(SingleHelper.toObserver(xmlResultHandler(context)));
    }

    private void getByCategoryId(RoutingContext context) {
        int categoryId = Integer.valueOf(context.pathParam("categoryId"));
        botService.getByCategoryId(categoryId, xmlResultHandler(context));
    }

    private Handler<AsyncResult<String>> xmlResultHandler(RoutingContext context) {
        return ar -> {
            if (ar.succeeded()) {
                if (ar.result() == null) {
                    notFound(context);
                } else {
                    context.response()
                            .setStatusCode(200)
                            .putHeader("content-type", "application/xml")
                            .end(ar.result());
                }
            } else internalError(context, ar.cause());
        };
    }
}
