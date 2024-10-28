package io.github.leocolomb.danfoss.internal;

import static io.github.leocolomb.danfoss.internal.DanfossBindingConstants.BINDING_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.influxdb.v3.client.Point;

abstract public class BaseHandler {
    private Logger logger = LoggerFactory.getLogger(BaseHandler.class);
    private List<Point> points = new ArrayList<Point>();

    protected final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void updateState(String ch, Object value) {
        addPoint(
            Point
                .measurement(getMeasurement())
                .setField(ch, value)
                .setTimestamp(null)
        );
    }

    public void updateStatus(@NonNull String status, @NonNull String statusDetail, String description) {
        logger.info("Status {}, {}, {}", status, statusDetail, description);
    }

    public void updateStatus(@NonNull String status) {
        logger.info("Status {}", status);
    }

    protected void addPoint(Point point) {
        points.add(point);
    }

    protected void reportDecimal(String ch, long value) {
        logger.trace("Received {} = {}", ch, value);
        updateState(ch, value);
    }

    protected void reportSwitch(String ch, boolean on) {
        logger.trace("Received {} = {}", ch, on);
        updateState(ch, on);
    }

    // private void updateProperty(String ch, Object prop) {
    //     logger.trace("Received {} = {}", ch, prop);
    //     updateState(ch, prop);
    // }

    protected void reportTemperature(String channel, double temp) {
        logger.trace("Received {} = {}", channel, temp);
        updateState(channel, temp);
    }

    protected void reportState(String channel, String[] statesMap, byte value) {
        String state;

        if (value >= 0 && value < statesMap.length) {
            state = statesMap[value];
        } else {
            state = String.valueOf(value);
        }

        logger.trace("Received {} = {}", channel, state);
        updateState(channel, state);
    }

    protected String getMeasurement() {
        return String.format("%s_v2", BINDING_ID);
    }

    public abstract void refresh();

    public List<Point> getPoints() {
        List<Point> l_points = new ArrayList<Point>(this.points);
        this.points.clear();

        return l_points;
    }
}
