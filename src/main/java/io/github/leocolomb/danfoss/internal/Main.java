package io.github.leocolomb.danfoss.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import com.influxdb.v3.client.config.ClientConfig;

import io.github.cdimascio.dotenv.Dotenv;
import io.sentry.Sentry;

public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    private final static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final static List<Point> g_points = new ArrayList<Point>();
    private static ClientConfig config;
    private static IconMasterHandler masterHandler;

    static {
        // We are in the process of development, so we want to see everything
        System.setProperty("org.slf4j.simplelogger.defaultlog", "info");
    }

    public static void main(String[] args) throws Exception {
        logger.info("Starting exporter");

        logger.info("Loading configurations");
        Dotenv dotenv = Dotenv.load();
        config = new ClientConfig.Builder()
            .host(dotenv.get("INFLUXDB_HOST"))
            .token(dotenv.get("INFLUXDB_TOKEN").toCharArray())
            .database(dotenv.get("INFLUXDB_DB"))
            .build();
        DanfossBindingConfig.update(dotenv.get("SDG_PRIVATE_KEY"), null);

        logger.info("Initializing exception handler");
        Sentry.init(options -> {
            options.setDsn(dotenv.get("SENTRY_DSN", ""));
            options.setTracesSampleRate(1.0);
        });

        logger.info("Registering database handler");
        InfluxDBClient client = InfluxDBClient.getInstance(config);

        logger.info("Registering shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                Main.close();
            }
        }, "Shutdown thread"));

        logger.info("Connecting to the grid");
        masterHandler = new IconMasterHandler(dotenv.get("SDG_PEER_ID"));
        masterHandler.initialize();
        masterHandler.scanRooms();
        // masterHandler.refresh();

        scheduler.scheduleAtFixedRate(() -> {
            g_points.addAll(masterHandler.getPoints());
            if (!g_points.isEmpty()) {
                logger.info("Sending {} point to database", g_points.size());
                try {
                    client.writePoints(g_points);
                    g_points.clear();
                } catch (Exception e) {
                    Sentry.captureException(e);
                }
            } else {
                logger.trace("No point to send to database");
            }
        }, 5, 5, TimeUnit.SECONDS);
    }
    
    public static void close() {
        g_points.addAll(masterHandler.getPoints());

        if (!g_points.isEmpty()) {
            try (InfluxDBClient client = InfluxDBClient.getInstance(config)) {
                client.writePoints(g_points);
                client.close();
            } catch (Exception e) {
                // ignore
            }
        }

        g_points.clear();
        masterHandler.dispose();
    }
}
