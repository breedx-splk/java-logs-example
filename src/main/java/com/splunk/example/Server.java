package com.splunk.example;

import spark.QueryParamsMap;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class Server {
    public void runForever() {
        port(8182);
        get("/greeting", (req, res) -> {
            var name = req.queryParams("name");
            var num = Integer.parseInt(req.queryParams("num"));

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
            return "Hello " + name + "\n" + emojis + "\n";
        });
    }
}
