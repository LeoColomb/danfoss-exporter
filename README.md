# Danfoss InfluxDB Exporter

This binding allows you to integrate heating solutions by Danfoss company. These solutions communicate over Wi-Fi using a
proprietary cloud via a protocol called SecureDeviceGrid(tm)
(http://securedevicegrid.com/).

The cloud solution is developed by Trifork company. This binding relies on OpenSDG (https://github.com/Sonic-Amiga/opensdg), the
free and opensource implementation of this protocol. The library must be installed in your system in order for this binding to
operate.

## Supported Things

- DeviReg(tm) Smart floor thermostat (https://www.devismart.com/)
- Danfoss Icon controller (https://icon.danfoss.com/)
- Danfors Icon room

Note that Danfoss Icon support is currently incomplete and in beta state. It was done entirely with user's
support; i, the developer, don't have Icon hardware on my disposal.

## Installation and supported architectures

This binding relies on OpenSDG library (https://github.com/Sonic-Amiga/opensdg-java) for communicating with the hardware.
On Linux OS It is necessary to manually install the library in your system before using this binding. On Windows no extra
components need to be installed.

## Discovery

> TBD

## Binding Configuration

| Parameter  | Meaning                                                                                  |
|------------|------------------------------------------------------------------------------------------|
| privateKey | Private key, used for communication.                                                     |
| userName   | User name, which will represent the exporter in DeviReg(tm) Smart smartphone application. Used for configuration sharing. |
