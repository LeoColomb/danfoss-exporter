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
        addPoint(Point.measurement(String.format("%s_v1", BINDING_ID))
            .setField(ch, value)
        );
    }

    public void updateStatus(@NonNull String status, @NonNull String statusDetail, String description) {
        logger.info("Status {}, {}, {}", status, statusDetail, description);
    }

    public void updateStatus(@NonNull String status) {
        logger.info("Status {}", status);
    }

    protected void addPoint(Point point) {
        points.add(point
            .setTag("location", getName())
            .setTimestamp(null)
        );
    }

    protected abstract String getName();

    public List<Point> getPoints() {
        List<Point> l_points = new ArrayList<Point>(this.points);
        this.points.clear();

        return l_points;
    }
}
