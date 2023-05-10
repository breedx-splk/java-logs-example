package com.splunk.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.QueryParamsMap;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class Server {

    public static final int PORT = 8182;
    Logger logger = LogManager.getLogger(Server.class);

    public void runForever() {
        logger.info("Server is starting on port " + PORT);
        port(PORT);
        get("/greeting", (req, res) -> {
            var name = req.queryParams("name");
            logger.info("Got request from " + name);
            var num = Integer.parseInt(req.queryParams("num"));
            logger.info(name + " has requested " + num + " random emoji");

            StringBuilder emojis = new StringBuilder();
            for(int i = 0; i < num; i++){
                if(emojis.length() > 0){
                    emojis.append(" ");
                }
                emojis.append(Emojis.random());
                TimeUnit.MILLISECONDS.sleep(200L);
            }

            res.type("text/plain");
            res.header("originator", name);
            String response = "Hello " + name + "\n" + emojis + "\n";
            logger.info("Sending response to " + name + ": " + response);
            return response;
        });
    }
}
