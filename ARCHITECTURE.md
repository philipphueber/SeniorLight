# Architecture

Package `app.phueber.seniorlight`. No screen at all: a home-screen widget and an
app icon, both of which only switch the light.

## Files

| Class | Role |
|---|---|
| `Torch` | The light, through the platform torch API (`CameraManager.setTorchMode`). Picks the camera, asks the system for the *real* current state (a `TorchCallback` reports it on registration) and switches to the opposite, so the result is right even after a change in quick settings. Remembers the last state for drawing the widget. |
| `CameraPick` | Pure: which camera's flash to use (back camera first, else any flash, else none). Unit tested. |
| `LightWidget` | The 1×1 home-screen widget (`AppWidgetProvider`): yellow disc with a flashlight when on, dark grey with a crossed-out flashlight when off. A tap broadcasts `TOGGLE` back to it, handled with `goAsync()`. |
| `ToggleActivity` | Behind the app icon. Invisible (translucent, no animation): switches the light and finishes. |
| `LightService` | Foreground service (type `specialUse`) that runs only while the light is on. See below. |
| `LightApp` | Registers a `TorchCallback` for the process lifetime, so the widget follows every change (quick settings, other apps, the system) and the service stops when the light goes off by any route. |

Pure classes have JVM tests in `app/src/test`; Android-facing classes have none.

## Why a widget, not a changing app icon

Android has no API to change an app icon at runtime. The usual workaround,
enabling and disabling `activity-alias` launcher entries with different icons,
makes Launcher3-based home screens (Pixel and many others) remove the icon from
the home screen whenever its entry is disabled, and can restart the app. A widget
is the platform's way to show live state on the home screen.

## Why a foreground service

The camera service turns the torch off when the process that switched it on
dies, and Android ends cached background processes whenever it needs memory. On
the emulator, `am kill` turned the light off at once. `LightService` keeps the
process in the foreground while the light is on; with it, the same kill leaves the
process and the light alone.

- Starting it from the widget or the icon is allowed in the background, because
  both are user interactions with the app.
- `specialUse` because no standard type fits; `camera` would need the CAMERA
  permission and a runtime prompt.
- Its notification (with **Turn off**) only shows on Android 13+ if notifications
  are allowed. The app never asks: a tap must only switch the light. The service
  keeps the process alive either way.

## Look

Resources only: `widget_on.xml` / `widget_off.xml` are layer-lists (disc plus
Material flashlight vector), shown with `fitCenter` so they stay round in any cell
shape. Yellow `#FFC107` with a near-black icon for on, `#424242` with a white
icon for off: state reads from colour and from the shape of the icon.

## Permissions

| Permission | Why |
|---|---|
| `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE` | `LightService`. Both are granted at install, no prompt. |

`setTorchMode` needs no permission, and the camera is never opened. No internet.

## Known limits

- If the process dies anyway (for example force-stopped in Settings), the light
  goes off and the widget may show "on" until the app runs again. The next tap
  still does the right thing, because it asks the system for the real state.
- While another app holds the camera, the torch is unavailable; a tap shows a
  message.
