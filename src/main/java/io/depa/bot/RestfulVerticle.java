package io.depa.bot;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.CorsHandler;

import java.time.Instant;

/**
 * An abstract base reactive verticle that provides several methods for RESTful API.
 *
 * @author Artem Kostritsa
 */
public abstract class RestfulVerticle extends AbstractVerticle {

    /**
     * Enable CORS support.
     *
     * @param router router instance
     */
    protected void enableCorsSupport(Router router) {
        router.route().handler(CorsHandler.create("*")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Content-Type")
                .allowedHeader("accept")
                .allowedHeader("origin")
                .allowedHeader("x-requested-with")
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.PATCH)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
        );
    }

    /**
     * This method generates handler for async methods in REST APIs.
     * The result requires non-empty. If empty, return <em>404 Not Found</em> status.
     *
     * @param context routing context instance
     * @return handler
     */
    protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context) {
        return ar -> {
            if (ar.succeeded()) {
                if (ar.result() == null) {
                    notFound(context);
                } else {
                    context.response()
                            .setStatusCode(200)
                            .putHeader("content-type", "application/json")
                            .end(Json.encodePrettily(ar.result()));
                }
            } else {
                internalError(context, ar.cause());
            }
        };
    }

    /**
     * This method generates handler for async methods in REST APIs.
     * The result is not needed. Only the state of the async result is required.
     *
     * @param context routing context instance
     * @return handler
     */
    protected Handler<AsyncResult<Void>> resultVoidHandler(RoutingContext context) {
        return ar -> {
            if (ar.succeeded()) {
                context.response()
                        .setStatusCode(200)
                        .putHeader("content-type", "application/json")
                        .end(context.getBody());
            } else {
                internalError(context, ar.cause());
            }
        };
    }

    protected void badGateway(RoutingContext context) {
        context.response()
                .setStatusCode(502)
                .putHeader("content-type", "application/json")
                .end(new JsonObject()
                        .put("timestamp", Instant.now())
                        .put("status", 502)
                        .put("error", "bad_gateway")
                        .put("path", context.normalisedPath())
                        .encodePrettily());
    }

    protected void badRequest(RoutingContext context, Throwable cause) {
        context.response().setStatusCode(400)
                .putHeader("content-type", "application/json")
                .end(new JsonObject()
                        .put("timestamp", Instant.now())
                        .put("status", 400)
                        .put("error", cause.getMessage())
                        .put("path", context.normalisedPath())
                        .encodePrettily());
    }

    protected void notFound(RoutingContext context) {
        context.response().setStatusCode(404)
                .putHeader("content-type", "application/json")
                .end(new JsonObject()
                        .put("timestamp", Instant.now())
                        .put("status", 404)
                        .put("error", "Not Found")
                        .put("path", context.normalisedPath())
                        .encodePrettily());
    }

    protected void internalError(RoutingContext context, Throwable cause) {
        context.response().setStatusCode(500)
                .putHeader("content-type", "application/json")
                .end(new JsonObject()
                        .put("timestamp", Instant.now())
                        .put("status", 500)
                        .put("error", cause.getMessage())
                        .put("path", context.normalisedPath())
                        .encodePrettily());
    }
}
