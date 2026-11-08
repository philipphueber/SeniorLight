# Architecture

Package `app.phueber.seniorlight`. No screen at all: the app icon switches the
light, and its picture shows whether the light is on. A home-screen widget with
the same face does the same, for launchers that do not redraw the swapped icon.

## Files

| Class | Role |
|---|---|
| `Torch` | The light, through the platform torch API (`CameraManager.setTorchMode`). Picks the camera, asks the system for the *real* current state (a `TorchCallback` reports it on registration) and switches to the opposite, so the result is right even after a change in quick settings. Remembers the last state and hands it to `AppIcon` and `LightWidget`. |
| `AppIcon` | Shows the state on the app icon by enabling exactly one of two launcher entries (see below). |
| `CameraPick` | Pure: which camera's flash to use (back camera first, else any flash, else none). Unit tested. |
| `ToggleActivity` | Behind the app icon (both launcher entries are aliases of it). Invisible (translucent, no animation): switches the light and finishes. |
| `LightWidget` | Optional home-screen widget: one disc showing `ic_launcher_on`/`ic_launcher_off`, a tap goes to `ToggleReceiver`. Redrawn whenever `Torch` remembers a new state; never on a timer (`updatePeriodMillis="0"`). |
| `ToggleReceiver` | Target of the widget and of the notification's Turn off; same toggle, via `goAsync()`. |
| `BootReceiver` | After a restart the light is off: puts the icon and widget back to "off". |
| `LightService` | Foreground service (type `specialUse`) that runs only while the light is on. See below. |
| `LightApp` | Registers a `TorchCallback` for the process lifetime, so the icon follows every change (quick settings, other apps, the system) and the service stops when the light goes off by any route. |

Pure classes have JVM tests in `app/src/test`; Android-facing classes have none.

## How the icon changes

Android has no API to change an app icon at runtime. The manifest therefore
declares two launcher entries, `IconOff` and `IconOn`, as `activity-alias`es of
`ToggleActivity` with different icons (`ic_launcher_off.xml`, `ic_launcher_on.xml`),
and `AppIcon` keeps exactly one enabled (`setComponentEnabledSetting`, with
`DONT_KILL_APP` because the process holds the light; the new one is enabled before
the old one is disabled). A fresh install starts with `IconOff`.

Tested with the Pixel launcher on Android 11 and 15: an icon placed on the home
screen stays in place, switches picture with every tap and keeps working. Other
launchers may redraw with a delay; that is the known risk of this technique.
`LightWidget` is the fallback there: a widget is redrawn through
`AppWidgetManager`, which every launcher honours. The app still works from its
icon alone; the widget is optional.

A tap on the widget is a broadcast, not an activity start. Starting
`LightService` from it is still allowed on Android 12+, because the system
exempts widget clicks from the background-start limits (tested on an Android 15
emulator: light on, service running, widget and icon both "on").

## Why a foreground service

The camera service turns the torch off when the process that switched it on
dies, and Android ends cached background processes whenever it needs memory. On
the emulator, `am kill` turned the light off at once. `LightService` keeps the
process in the foreground while the light is on; with it, the same kill leaves the
process and the light alone.

- It is started from `ToggleActivity`, which is in the foreground at that moment,
  or from a widget tap, which Android exempts; so the background-start limits
  do not apply.
- `specialUse` because no standard type fits; `camera` would need the CAMERA
  permission and a runtime prompt.
- Its notification (with **Turn off**) only shows on Android 13+ if notifications
  are allowed. The app never asks: a tap must only switch the light. The service
  keeps the process alive either way.

## Look

Two vector launcher icons on a 108dp canvas, content inside the 66dp safe zone
(the widget shows the same two drawables, scaled with `fitCenter`):
yellow `#FFC107` disc with a near-black Material flashlight for on, `#424242` disc
with a white crossed-out flashlight for off. The state reads from the colour and
from the shape of the icon. The notification channel has badges turned off, so
no dot sits on the icon.

## Permissions

| Permission | Why |
|---|---|
| `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE` | `LightService`. Granted at install, no prompt. |
| `RECEIVE_BOOT_COMPLETED` | `BootReceiver` resets the icon after a restart. Granted at install. |

`setTorchMode` needs no permission, and the camera is never opened. No internet.

## Known limits

- If the process dies anyway (for example force-stopped in Settings), the light
  goes off and the icon may show "on" until the app runs again or the phone
  restarts. The next tap
  still does the right thing, because it asks the system for the real state.
- While another app holds the camera, the torch is unavailable; a tap shows a
  message.
