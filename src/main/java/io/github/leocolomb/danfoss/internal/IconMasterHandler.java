package io.github.leocolomb.danfoss.internal;

import static io.github.leocolomb.danfoss.internal.DanfossBindingConstants.*;
import static io.github.leocolomb.danfoss.internal.protocol.Icon.MsgClass.*;
import static io.github.leocolomb.danfoss.internal.protocol.Icon.MsgCode.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.influxdb.v3.client.Point;

import io.github.leocolomb.danfoss.internal.protocol.Dominion;

public class IconMasterHandler extends BaseHandler implements ISDGPeerHandler {
    private final Logger logger = LoggerFactory.getLogger(IconMasterHandler.class);
    private SDGPeerConnector connHandler = new SDGPeerConnector(this, scheduler);

    private String peerId;
    private IconRoomHandler[] rooms = new IconRoomHandler[ICON_MAX_ROOMS];

    public IconMasterHandler(String peerId) {
        this.peerId = peerId;
    }

    public void initialize() {
        connHandler.initialize(peerId);
    }

    public void dispose() {
        for (BaseHandler room : rooms) {
            childHandlerDisposed(room);
        }
        connHandler.dispose();
    }

    @Override
    public void reportStatus(@NonNull String status, @NonNull String statusDetail, String description) {
        updateStatus(status, statusDetail, description);
    }

    @Override
    public void handlePacket(Dominion.@NonNull Packet pkt) {
        int msgClass = pkt.getMsgClass();

        if (msgClass >= ROOM_FIRST && msgClass <= ROOM_LAST) {
            IconRoomHandler room = rooms[msgClass - ROOM_FIRST];

            if (room != null) {
                room.handlePacket(pkt);
            } else {
                if (pkt.getMsgCode() == ROOMNAME) {
                    String name = pkt.getString();

                    // Unused rooms are reported as having empty names, ignore them.
                    if (!name.isEmpty()) {
                        int number = pkt.getMsgClass() - ROOM_FIRST;
                        logger.info("Detected Icon Room #{} \"{}\" on master {}", number, name, peerId);
                        this.childHandlerInitialized(new IconRoomHandler(number, name));
                    }
                }
            }
        } else {
            switch (pkt.getMsgCode()) {
                case VACATION_SETPOINT:
                    reportTemperature(CHANNEL_SETPOINT_AWAY, pkt.getDecimal());
                    break;
                case PAUSE_SETPOINT:
                    reportTemperature(CHANNEL_SETPOINT_ANTIFREEZE, pkt.getDecimal());
                    break;
                case ROOMMODE:
                    reportState(CHANNEL_CONTROL_MODE, CONTROL_MODES, pkt.getByte())
                    break;
                case SYSTEM_TIME:
                    break;
                case GLOBAL_HARDWAREREVISION:
                    // updateProperty("hardware", pkt.getVersion().toString());
                    break;
                case GLOBAL_SOFTWAREREVISION:
                    // firmwareVer = pkt.getVersion();
                    // reportFirmware();
                    break;
                case GLOBAL_SOFTWAREBUILDREVISION:
                    // firmwareBuild = Short.toUnsignedInt(pkt.getShort());
                    // reportFirmware();
                    break;
                case GLOBAL_SERIALNUMBER:
                    // updateProperty("serial_number", String.valueOf(pkt.getInt()));
                    break;
                case GLOBAL_PRODUCTIONDATE:
                    // updateProperty("production_date", DateFormat.getDateTimeInstance().format(pkt.getDate(0)));
                    break;
                case MDG_CONNECTION_COUNT:
                    // updateProperty("connection_count", String.valueOf(pkt.getByte()));
                    break;
                case RAIL_INPUTHEATORCOOL:
                    // updateProperty("cooling", String.valueOf(pkt.getBoolean()));
                    break;
                // default:
                //     updateStatus(String.valueOf(pkt.getMsgCode()), String.valueOf(pkt.getByte()), "");
            }
        }
    }

    // private void reportFirmware() {
    //     if (firmwareVer != null && firmwareBuild != -1) {
    //         updateProperty("firmware", firmwareVer.toString() + "." + String.valueOf(firmwareBuild));
    //     }
    // }

    @Override
    public void ping() {
        // Need to request something small. Let's use VACATION_SETPOINT.
        connHandler.sendRefresh(ALL_ROOMS, VACATION_SETPOINT);
    }

    public void scanRooms() {
        // Request names for all the rooms
        for (int msgClass = ROOM_FIRST; msgClass <= ROOM_LAST; msgClass++) {
            connHandler.sendRefresh(msgClass, ROOMNAME);
        }
    }

    private void childHandlerInitialized(BaseHandler handler) {
        if (handler instanceof IconRoomHandler) {
            IconRoomHandler room = (IconRoomHandler) handler;
            int roomId = room.getNumber();

            if (rooms[roomId] != null) {
                logger.error("Room number {} is already in use", roomId);
                room.setConfigError("Room number is already in use");
            } else {
                logger.trace("Room {} initialized", roomId);
                room.setConnectionHandler(connHandler);
                room.refresh();
                rooms[roomId] = room;
            }
        }
    }

    private void childHandlerDisposed(BaseHandler handler) {
        if (handler instanceof IconRoomHandler) {
            IconRoomHandler room = (IconRoomHandler) handler;
            int roomId = room.getNumber();

            logger.trace("Room {} disposed", roomId);
            rooms[roomId] = null;
        }
    }

    @Override
    public void refresh() {
        connHandler.sendRefresh(ALL_ROOMS, VACATION_SETPOINT);
        connHandler.sendRefresh(ALL_ROOMS, PAUSE_SETPOINT);
        connHandler.sendRefresh(ALL_ROOMS, ROOMMODE);

        for (IconRoomHandler room : rooms) {
            if (room != null) {
                room.refresh();
            }
        }
    }

    protected String getMeasurement() {
        return String.format("%s_master_v1", BINDING_ID);
    }

    public List<Point> getPoints() {
        List<Point> l_points = new ArrayList<Point>();

        l_points.addAll(super.getPoints());

        for (IconRoomHandler room : rooms) {
            if (room != null) {
                l_points.addAll(room.getPoints());
            }
        }

        return l_points;
    }
}
