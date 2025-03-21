/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package io.github.leocolomb.danfoss.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link DanfossBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Pavel Fedin - Initial contribution
 */
@NonNullByDefault
public class DanfossBindingConstants {

    public static final String BINDING_ID = "danfoss";

    public static final int ICON_MAX_ROOMS = 45;

    // List of all Channel ids
    public static final String CHANNEL_TEMPERATURE_FLOOR = "temperature_floor";
    public static final String CHANNEL_TEMPERATURE_ROOM = "temperature_room";
    public static final String CHANNEL_SETPOINT_MAXIMUM = "setpoint_maximum";
    public static final String CHANNEL_SETPOINT_MINIMUM = "setpoint_minimum";
    public static final String CHANNEL_SETPOINT_COMFORT = "setpoint_comfort";
    public static final String CHANNEL_SETPOINT_ECONOMY = "setpoint_economy";
    public static final String CHANNEL_SETPOINT_ASLEEP = "setpoint_asleep";
    public static final String CHANNEL_SETPOINT_MANUAL = "setpoint_manual";
    public static final String CHANNEL_SETPOINT_AWAY = "setpoint_away";
    public static final String CHANNEL_SETPOINT_ANTIFREEZE = "setpoint_antifreeze";
    public static final String CHANNEL_SETPOINT_MIN_FLOOR = "setpoint_min_floor";
    public static final String CHANNEL_SETPOINT_MIN_FLOOR_ENABLE = "setpoint_min_floor_enable";
    public static final String CHANNEL_SETPOINT_MAX_FLOOR = "setpoint_max_floor";
    public static final String CHANNEL_SETPOINT_WARNING = "setpoint_warning";
    public static final String CHANNEL_CONTROL_STATE = "control_state";
    public static final String CHANNEL_CONTROL_MODE = "control_mode";
    public static final String CHANNEL_CONTROL_ROOM = "control_room";
    public static final String CHANNEL_WINDOW_DETECTION = "window_detection";
    public static final String CHANNEL_WINDOW_OPEN = "window_open";
    public static final String CHANNEL_FORECAST = "forecast";
    public static final String CHANNEL_SCREEN_LOCK = "screen_lock";
    public static final String CHANNEL_BRIGHTNESS = "brightness";
    public static final String CHANNEL_BATTERY = "battery";
    public static final String CHANNEL_HEATING_STATE = "heating_state";
    public static final String CHANNEL_WARMUP_STATE = "warmup_state";
    public static final String CHANNEL_ON_TIME_7_DAYS = "on_time_7_days";
    public static final String CHANNEL_ON_TIME_30_DAYS = "on_time_30_days";
    public static final String CHANNEL_ON_TIME_TOTAL = "on_time_total";

    public static final String CHANNEL_DISCONNECTED = "sensor_disconnected";
    public static final String CHANNEL_SHORTED = "sensor_shorted";
    public static final String CHANNEL_OVERHEAT = "overheat";
    public static final String CHANNEL_UNRECOVERABLE = "unrecoverable";

    public static final String[] ALARM_CHANNELS = {
        CHANNEL_DISCONNECTED, // 0
        CHANNEL_SHORTED,      // 1
        CHANNEL_OVERHEAT,     // 2
        CHANNEL_UNRECOVERABLE // 3
    };

    public static final String CONTROL_MODE_MANUAL = "MANUAL";
    public static final String CONTROL_MODE_OVERRIDE = "OVERRIDE";
    public static final String CONTROL_MODE_SCHEDULE = "SCHEDULE";
    public static final String CONTROL_MODE_VACATION = "VACATION";
    public static final String CONTROL_MODE_PAUSE = "PAUSE";
    public static final String CONTROL_MODE_OFF = "OFF";

    public static final String[] CONTROL_MODES = {
        CONTROL_MODE_SCHEDULE, // 0
        CONTROL_MODE_VACATION, // 1
        CONTROL_MODE_PAUSE,    // 2
        CONTROL_MODE_OVERRIDE  // 3
    };

    public static final String CONTROL_STATE_HOME = "HOME";
    public static final String CONTROL_STATE_AWAY = "AWAY";
    public static final String CONTROL_STATE_ASLEEP = "ASLEEP";
    public static final String CONTROL_STATE_FATAL = "FATAL";

    public static final String[] CONTROL_STATES = {
        CONTROL_STATE_HOME,   // 0
        CONTROL_STATE_AWAY,   // 1
        CONTROL_STATE_ASLEEP, // 2
        CONTROL_STATE_FATAL   // 3
    };

    public static final String CONTROL_ROOM_HOME = "MANUAL";
    public static final String CONTROL_ROOM_AUTO = "AUTO";
    public static final String CONTROL_ROOM_SCHEDULE = "SCHEDULE";

    public static final String[] CONTROL_ROOMS = {
        CONTROL_ROOM_HOME,    // 0
        CONTROL_ROOM_AUTO,    // 1
        CONTROL_ROOM_SCHEDULE // 20
    };
}
