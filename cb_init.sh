#!/data/data/ch.waut/files/bin/busybox sh

ARG=$1

if [ "x$ARG" = "xRUN" ]; then
  cd /data/data/ch.waut/files/bin && PATH=. busybox nice -n +5 busybox sh -x cb_init.sh $2 > ../cb_init.log 2>&1 &
  return 0
fi

set +e
trap " " 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
export APP=/data/data/ch.waut/files/bin
export PATH=${APP}
alias [='busybox ['
alias [[='busybox [['
alias ECHO='busybox timeout -t 1 -s KILL busybox echo '
alias SYSCTL='busybox timeout -t 3 -s KILL busybox sysctl -e -w '
alias SETPROP='/system/bin/setprop '
alias GETPROP='/system/bin/getprop '
if [ "$(GETPROP persist.cb.enabled 2>/dev/null)" = "FALSE" ]; then return 0; fi

VERSION=$(GETPROP ro.build.version.release 2>/dev/null | busybox busybox cut -d. -f1 2>/dev/null)

MEM=$(busybox free 2>/dev/null | busybox grep Mem 2>/dev/null | busybox awk '{ print $2 }' 2>/dev/null)

SETTINGS_DB="/data/data/com.android.providers.settings/databases/settings.db"
TABLE=""
VARIABLE=""
VALUE=""

#Put pragma journal mode=sync here

UPDATE_TABLES() {
  TABLE="$1"
  VARIABLE="$2"
  VALUE="$3"
  
  sqlite3 $SETTINGS_DB 'update '"$TABLE"' set value="'"$VALUE"'" where name="'"$VARIABLE"'";'

# if [ -e /system/bin/settings ]; then 
  if [ 1 = 0 ]; then
    STRING=$(PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings get $TABLE $VARIABLE 2>/dev/null)
	if [ "x$STRING" = "xnull" ]; then return 0; fi

	if [ "x$VALUE" = "x" ]; then 
      PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put $TABLE $VARIABLE ""
	else
      PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put $TABLE $VARIABLE $VALUE
	fi
  fi
}

UPDATE_TABLES GLOBAL transition_animation_scale 0
UPDATE_TABLES GLOBAL window_animation_scale 0
UPDATE_TABLES GLOBAL animator_duration_scale 0

UPDATE_TABLES SYSTEM transition_animation_scale 0
UPDATE_TABLES SYSTEM window_animation_scale 0
UPDATE_TABLES SYSTEM animator_duration_scale 0

UPDATE_TABLES SECURE transition_animation_scale 0
UPDATE_TABLES SECURE window_animation_scale 0
UPDATE_TABLES SECURE animator_duration_scale 0

if [ 1 = 0 ]; then

if [ "x$VERSION" != "x" ]; then 
	if [ "$VERSION" -ge "5" ]; then	
		if [ "x$MEM" != "x" ]; then 
		  if [ "$MEM" -gt "800000" ]; then 		  
			
			UPDATE_TABLES GLOBAL transition_animation_scale 0.5
			UPDATE_TABLES GLOBAL window_animation_scale 0.5
			UPDATE_TABLES GLOBAL animator_duration_scale 0.5

			UPDATE_TABLES SYSTEM transition_animation_scale 0.5
			UPDATE_TABLES SYSTEM window_animation_scale 0.5
			UPDATE_TABLES SYSTEM animator_duration_scale 0.5
		  else		  
		  
			UPDATE_TABLES GLOBAL transition_animation_scale 0.25
			UPDATE_TABLES GLOBAL window_animation_scale 0.25
			UPDATE_TABLES GLOBAL animator_duration_scale 0.25

			UPDATE_TABLES SYSTEM transition_animation_scale 0.25
			UPDATE_TABLES SYSTEM window_animation_scale 0.25
			UPDATE_TABLES SYSTEM animator_duration_scale 0.25
		  fi
		fi
	fi
fi

fi

SWAP=$(busybox free 2>/dev/null | busybox grep Swap 2>/dev/null | busybox awk '{ print $2 }' 2>/dev/null)
if [ "x$SWAP" != "x" ]; then 
  if [ "$SWAP" -gt "10000" ]; then 	
  	SYSCTL vm.swappiness=80
	
if [ 1 = 0 ]; then
	UPDATE_TABLES GLOBAL transition_animation_scale 0.25
	UPDATE_TABLES GLOBAL window_animation_scale 0.25
	UPDATE_TABLES GLOBAL animator_duration_scale 0.25

	UPDATE_TABLES SYSTEM transition_animation_scale 0.25
	UPDATE_TABLES SYSTEM window_animation_scale 0.25
	UPDATE_TABLES SYSTEM animator_duration_scale 0.25
fi

  fi
fi

#UPDATE_TABLES GLOBAL location_mode 2
#UPDATE_TABLES SYSTEM location_mode 2
#UPDATE_TABLES SECURE location_mode 2

UPDATE_TABLES GLOBAL location_mode 0
UPDATE_TABLES SYSTEM location_mode 0
UPDATE_TABLES SECURE location_mode 0

LPA=0
if [ "x$VERSION" != "x" ]; then 
  if [ "$VERSION" -ge "6" ]; then 
	LPA=1
    if [ -e /system/bin/settings ]; then 
#	  PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put SECURE location_providers_allowed +network
	  PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put SECURE location_providers_allowed -network
#	  PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put SECURE location_providers_allowed +gps
	  PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put SECURE location_providers_allowed -gps
	  PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put SECURE location_providers_allowed -wifi
#	  PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put SECURE location_providers_allowed +wifi
	fi
  fi	  
fi

if [ $LPA -eq 0 ]; then 
#  UPDATE_TABLES GLOBAL location_providers_allowed "wifi"
#  UPDATE_TABLES SYSTEM location_providers_allowed "wifi"
#  UPDATE_TABLES SECURE location_providers_allowed "wifi"
  UPDATE_TABLES SECURE location_providers_allowed ""
fi

busybox fsync $SETTINGS_DB

SETPROP ro.telephony.call_ring.delay 0
SETPROP ring.delay 0

SETPROP ro.media.enc.jpeg.quality 100

SETPROP pm.sleep_mode 1

SETPROP wifi.supplicant_scan_interval 30

# Put outgoing only IPSEC logic here

SETPROP persist.sys.ui.hw true
SETPROP debug.sf.hw 1
SETPROP debug.performance.tuning 1
SETPROP video.accelerate.hw 1

#SETPROP persist.sys.ui.hw false
#SETPROP debug.sf.hw 0
#SETPROP debug.performance.tuning 0
#SETPROP video.accelerate.hw 0

#SETPROP debug.composition.type cpu
#SETPROP persist.sys.composition.type cpu

SETPROP debug.composition.type gpu
SETPROP persist.sys.composition.type gpu
SETPROP debug.composition.type dyn
SETPROP persist.sys.composition.type dyn

SETPROP ro.media.dec.jpeg.memcap 8000000
SETPROP ro.media.enc.hprof.vid.bps 8000000

SETPROP persist.service.adb.enable 0

UPDATE_TABLES GLOBAL adb_enabled 0
UPDATE_TABLES SYSTEM adb_enabled 0
UPDATE_TABLES SECURE adb_enabled 0

SETPROP logcat.live disable
busybox rm -f /dev/log/main

/system/bin/stop adbd

if [ "x$ARG" = "xFORCE" ]; then
  exec busybox sh cb_weekly.sh RUN  
else
  exec busybox sh cb_sync.sh RUN 6
fi


