package io.github.leocolomb.danfoss.internal;

import static io.github.leocolomb.danfoss.internal.DanfossBindingConstants.*;
import static io.github.leocolomb.danfoss.internal.protocol.Icon.MsgClass.ROOM_FIRST;
import static io.github.leocolomb.danfoss.internal.protocol.Icon.MsgCode.*;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.influxdb.v3.client.Point;

import io.github.leocolomb.danfoss.internal.protocol.Dominion;
import io.github.leocolomb.danfoss.internal.protocol.Icon.RoomControl;
import io.github.leocolomb.danfoss.internal.protocol.Icon.RoomMode;

public class IconRoomHandler extends BaseHandler {

    private final Logger logger = LoggerFactory.getLogger(IconRoomHandler.class);
    private int roomNumber;
    private String roomName;
    private SDGPeerConnector connHandler;
    private boolean isOnline;

    public IconRoomHandler(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public IconRoomHandler(int roomNumber, @NonNull String roomName) {
        this.roomNumber = roomNumber;
        this.roomName = roomName;
    }

    public int getNumber() {
        return roomNumber;
    }

    public String getName() {
        if (!roomName.isEmpty()) {
            return roomName;
        }
        return String.valueOf(roomNumber);
    }

    @Override
    protected void addPoint(Point point) {
        super.addPoint(point
            .setTag("room_name", getName())
        );
        // return String.format("room_%d", getNumber());

    }

    public void setConnectionHandler(SDGPeerConnector h) {
        connHandler = h;
    }

    public void setConfigError(String reason) {
        updateStatus("OFFLINE", "OFFLINE.CONFIGURATION_ERROR", reason);
    }

    @Override
    public void refresh() {
        sendRefresh(ROOM_FLOORTEMPERATURE);
        sendRefresh(ROOM_ROOMTEMPERATURE);
        sendRefresh(ROOM_SETPOINTMAXIMUM);
        sendRefresh(ROOM_SETPOINTMINIMUM);
        sendRefresh(ROOM_SETPOINTATHOME);
        sendRefresh(ROOM_SETPOINTASLEEP);
        sendRefresh(ROOM_SETPOINTAWAY);
        sendRefresh(ROOM_FLOORTEMPERATUREMINIMUM);
        sendRefresh(ROOM_FLOORTEMPERATUREMAXIMUM);
        sendRefresh(ROOM_BATTERYINDICATIONPERCENT);
        sendRefresh(ROOM_ROOMMODE);
        sendRefresh(ROOM_ROOMCONTROL);
        sendRefresh(ROOM_HEATINGCOOLINGSTATE);
    }

    public void sendRefresh(int msgCode) {
        if (connHandler != null) {
            connHandler.sendRefresh(ROOM_FIRST + roomNumber, msgCode);
        }
    }

    public void initialize() {
        logger.trace("Initializing room {}", roomNumber);
        isOnline = false;
        updateStatus("UNKNOWN");
    }

    public void handlePacket(Dominion.@NonNull Packet pkt) {
        if (!isOnline) {
            isOnline = true;
            updateStatus("ONLINE");
        }

        switch (pkt.getMsgCode()) {
            case ROOM_FLOORTEMPERATURE:
                reportTemperature(CHANNEL_TEMPERATURE_FLOOR, pkt.getDecimal());
                break;
            case ROOM_ROOMTEMPERATURE:
                reportTemperature(CHANNEL_TEMPERATURE_ROOM, pkt.getDecimal());
                break;
            case ROOM_SETPOINTMAXIMUM:
                reportTemperature(CHANNEL_SETPOINT_MAXIMUM, pkt.getDecimal());
                break;
            case ROOM_SETPOINTMINIMUM:
                reportTemperature(CHANNEL_SETPOINT_MINIMUM, pkt.getDecimal());
                break;
            case ROOM_SETPOINTATHOME:
                reportTemperature(CHANNEL_SETPOINT_COMFORT, pkt.getDecimal());
                break;
            case ROOM_SETPOINTASLEEP:
                reportTemperature(CHANNEL_SETPOINT_ASLEEP, pkt.getDecimal());
                break;
            case ROOM_SETPOINTAWAY:
                reportTemperature(CHANNEL_SETPOINT_ECONOMY, pkt.getDecimal());
                break;
            case ROOM_FLOORTEMPERATUREMINIMUM:
                reportTemperature(CHANNEL_SETPOINT_MIN_FLOOR, pkt.getDecimal());
                break;
            case ROOM_FLOORTEMPERATUREMAXIMUM:
                reportTemperature(CHANNEL_SETPOINT_MAX_FLOOR, pkt.getDecimal());
                break;
            case ROOM_BATTERYINDICATIONPERCENT:
                reportDecimal(CHANNEL_BATTERY, pkt.getByte());
                break;
            case ROOM_ROOMMODE:
                reportControlState(pkt.getByte());
                break;
            case ROOM_ROOMCONTROL:
                reportSwitch(CHANNEL_MANUAL_MODE, pkt.getByte() == RoomControl.Manual);
                updateState(CHANNEL_MANUAL_MODE.concat("_number"), pkt.getByte());
                break;
            case ROOM_HEATINGCOOLINGSTATE:
                reportSwitch(CHANNEL_HEATING_STATE, pkt.getBoolean());
                break;
            case ROOM_WARMUPACTIVATED:
                reportSwitch(CHANNEL_WARMUP_STATE, pkt.getBoolean());
                break;
            case ROOMNAME:
                roomName = pkt.getString();
                refresh();
                break;
            // default:
            //     updateStatus(String.valueOf(pkt.getMsgCode()), String.valueOf(pkt.getByte()), "");
        }
    }

    private void reportDecimal(String ch, long value) {
        logger.trace("Received {} = {}", ch, value);
        updateState(ch, value);
    }

    private void reportSwitch(String ch, boolean on) {
        logger.trace("Received {} = {}", ch, on);
        updateState(ch, on);
    }

    private void reportControlState(byte info) {
        String state;

        if (info >= 0 && info < CONTROL_STATES.length) {
            state = CONTROL_STATES[info];
        } else {
            state = String.valueOf(info);
        }

        logger.trace("Received {} = {}", CHANNEL_CONTROL_STATE, state);
        updateState(CHANNEL_CONTROL_STATE, state);
    }

    // private void updateProperty(String ch, Object prop) {
    //     logger.trace("Received {} = {}", ch, prop);

    //     updateState(ch, prop);
    // }
}
