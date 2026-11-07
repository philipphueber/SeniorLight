#!/usr/bin/env bash
#
# One on-device check: tapping the app icon switches the flashlight on, tapping it
# again switches it off, and the keep-alive service runs exactly while it is on.
# Reads the torch state from the camera service's log, so it checks the real light.
#
# Usage: scripts/smoke.sh [adb-serial]     (needs a device or emulator with a flash)
#
set -uo pipefail
cd "$(dirname "$0")/.."
SERIAL=${1:-${ANDROID_SERIAL:-}}
S=(); [ -n "$SERIAL" ] && S=(-s "$SERIAL")
PKG=app.phueber.seniorlight
SH() { adb "${S[@]}" shell "$@"; }
pass() { echo "PASS $*"; }
fail() { echo "FAIL $*"; exit 1; }

./gradlew -q assembleDebug >/dev/null 2>&1 || fail "assembleDebug"
APK=$(ls app/build/outputs/apk/debug/SeniorLight-*-debug.apk | head -1)
adb "${S[@]}" uninstall $PKG >/dev/null 2>&1
adb "${S[@]}" install "$APK" >/dev/null || fail "install"
pass "install"
SH input keyevent KEYCODE_WAKEUP; SH wm dismiss-keyguard >/dev/null 2>&1; SH input keyevent KEYCODE_HOME

tap() { # tap the app icon, then report "on"/"off" from the camera service log
  adb "${S[@]}" logcat -c
  SH monkey -p $PKG -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1
  sleep 3
  case "$(adb "${S[@]}" logcat -d | grep 'Torch status changed' | tail -1 | grep -o 'newStatus=[0-9]')" in
    newStatus=2) echo on ;; newStatus=1) echo off ;; *) echo none ;;
  esac
}
svc() { SH dumpsys activity services $PKG | grep -c 'ServiceRecord.*LightService'; }

[ "$(tap)" = on ] && pass "first tap: light on" || fail "first tap did not turn the light on"
[ "$(svc)" = 1 ] && pass "keep-alive service running" || fail "service not running while on"
[ "$(tap)" = off ] && pass "second tap: light off" || fail "second tap did not turn the light off"
sleep 1
[ "$(svc)" = 0 ] && pass "service stopped" || fail "service still running while off"
echo "ALL PASS"
