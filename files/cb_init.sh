#!/data/data/ch.waut/files/bin/busybox sh

ARG=$1

if [ "x$ARG" = "xRUN" ]; then
  cd /data/data/ch.waut/files/bin && PATH=. busybox nice -n +5 busybox sh -x cb_init.sh $2 > ../cb_init.log 2>&1 &
  return 0
fi

if [ "x$ARG" != "xFORCE" ]; then
  return 1
fi

set +e
trap " " 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
export PATH=/data/data/ch.waut/files/bin
alias [='busybox ['
alias [[='busybox [['
alias ECHO='busybox timeout -t 1 -s KILL busybox echo '
alias SYSCTL='busybox timeout -t 3 -s KILL busybox sysctl -e -w '
alias SETPROP='/system/bin/setprop '
alias GETPROP='/system/bin/getprop '
if [ "$(GETPROP persist.cb.enabled 2>/dev/null)" = "FALSE" ]; then return 0; fi

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

  if [ -e /system/bin/settings ]; then 
    STRING=$(PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings get $TABLE $VARIABLE 2>/dev/null)
	if [ "x$STRING" = "xnull" ]; then return 0; fi

	if [ "x$VALUE" = "x" ]; then 
      PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put $TABLE $VARIABLE ""
	else
      PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put $TABLE $VARIABLE $VALUE
	fi
  fi

  return 0
}

# If Mem < 768 then . Also heap size = 128 if > 128 for > 768

MEM=$(busybox free 2>/dev/null | busybox grep Mem 2>/dev/null | busybox awk '{ print $2 }' 2>/dev/null)

VERSION=$(GETPROP ro.build.version.release 2>/dev/null | busybox busybox cut -d. -f1 2>/dev/null)

HEAP=$(GETPROP dalvik.vm.heapsize 2>/dev/null | busybox cut -dm -f1 2>/dev/null )

UPDATE_TABLES GLOBAL transition_animation_scale 0.5
UPDATE_TABLES GLOBAL window_animation_scale 0.5
UPDATE_TABLES GLOBAL animator_duration_scale 0.5

UPDATE_TABLES SYSTEM transition_animation_scale 0.5
UPDATE_TABLES SYSTEM window_animation_scale 0.5
UPDATE_TABLES SYSTEM animator_duration_scale 0.5

if [ "x$MEM" != "x" ]; then 
  if [ "$MEM" -gt "800000" ]; then 		  
	if [ "x$HEAP" != "x" ]; then 
	  if [ "$HEAP" -gt "192" ]; then 
		SETPROP dalvik.vm.heapsize 192m
	  fi
	fi
  else		  
	if [ "x$HEAP" != "x" ]; then 
	  if [ "$HEAP" -gt "128" ]; then 
		SETPROP dalvik.vm.heapsize 128m
	  fi
	fi
  fi
fi	

if [ "x$VERSION" != "x" ]; then 
	if [ "$VERSION" -ge "5" ]; then	
		if [ "x$MEM" != "x" ]; then 
		  if [ "$MEM" -gt "800000" ]; then 		  
			
			UPDATE_TABLES GLOBAL transition_animation_scale 0.4
			UPDATE_TABLES GLOBAL window_animation_scale 0.4
			UPDATE_TABLES GLOBAL animator_duration_scale 0.4

			UPDATE_TABLES SYSTEM transition_animation_scale 0.4
			UPDATE_TABLES SYSTEM window_animation_scale 0.4
			UPDATE_TABLES SYSTEM animator_duration_scale 0.4
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

SWAP=$(busybox free 2>/dev/null | busybox grep Swap 2>/dev/null | busybox awk '{ print $2 }' 2>/dev/null)
if [ "x$SWAP" != "x" ]; then 
  if [ "$SWAP" -gt "10000" ]; then 	
  	SYSCTL vm.swappiness=80
	
	UPDATE_TABLES GLOBAL transition_animation_scale 0.1
	UPDATE_TABLES GLOBAL window_animation_scale 0.1
	UPDATE_TABLES GLOBAL animator_duration_scale 0.1

	UPDATE_TABLES SYSTEM transition_animation_scale 0.1
	UPDATE_TABLES SYSTEM window_animation_scale 0.1
	UPDATE_TABLES SYSTEM animator_duration_scale 0.1	
  fi
fi

UPDATE_TABLES GLOBAL install_non_market_apps 0
UPDATE_TABLES SYSTEM install_non_market_apps 0
UPDATE_TABLES SECURE install_non_market_apps 0

#UPDATE_TABLES GLOBAL location_mode 2
#UPDATE_TABLES SYSTEM location_mode 2
#UPDATE_TABLES SECURE location_mode 2
UPDATE_TABLES SECURE location_mode 0
UPDATE_TABLES SYSTEM location_mode 0
UPDATE_TABLES GLOBAL location_mode 0

LPA=0
if [ "x$VERSION" != "x" ]; then 
  if [ "$VERSION" -ge "6" ]; then 
    if [ -e /system/bin/settings ]; then 
	  LPA=1
#	  PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put SECURE location_providers_allowed +network
	  PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put SECURE location_providers_allowed -network
	  PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put SECURE location_providers_allowed -gps
	  PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put SECURE location_providers_allowed -wifi
#	  PATH=/data/data/ch.waut/files/bin:/system/bin busybox sh /system/bin/settings put SECURE location_providers_allowed +wifi
	fi
  fi	  
fi

if [ $LPA -eq 0 ]; then 
  UPDATE_TABLES GLOBAL location_providers_allowed ""
  UPDATE_TABLES SYSTEM location_providers_allowed ""
  UPDATE_TABLES SECURE location_providers_allowed ""
#  UPDATE_TABLES SECURE location_providers_allowed "wifi,network"
fi

UPDATE_TABLES GLOBAL adb_enabled 0
UPDATE_TABLES SYSTEM adb_enabled 0
UPDATE_TABLES SECURE adb_enabled 0

busybox fsync $SETTINGS_DB

# Put checkjni false here
# Put clean dalvik cache here
# Put heap size logic here

SETPROP ro.ril.enable.amr.wideband 1
SETPROP ro.telephony.call_ring.delay 0
SETPROP ring.delay 0

SETPROP ro.media.enc.jpeg.quality 100

SETPROP pm.sleep_mode 1
SETPROP ro.ril.disable.power.collapse 0

#SETPROP wifi.supplicant_scan_interval 0

#SYSCTL net.ipv4.icmp_echo_ignore_all=1
#SYSCTL net.ipv4.tcp_timestamps=0

# Put outgoing only IPSEC logic here

SETPROP persist.sys.ui.hw false
SETPROP debug.sf.hw 0

/system/bin/stop adbd

#SETPROP dalvik.vm.checkjni false
#SETPROP ro.kernel.android.checkjni 0

#if [ ! -e /system/xbin/busybox ]; then 
#  busybox mount -o remount,rw /system 2>/dev/null   
#  busybox cp /data/local/tmp/busybox /system/xbin/busybox
#  busybox chown 0.0 /system/xbin/busybox 
#  busybox chmod 755 /system/xbin/busybox
# /system/xbin/busybox --install -s /system/xbin 2>/dev/null
#  busybox mount -o remount,ro /system 2>/dev/null   
#fi
