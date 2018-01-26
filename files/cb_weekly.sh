#!/data/data/ch.waut/files/bin/busybox sh

ARG=$1

if [ "x$ARG" = "xRUN" ]; then
  cd /data/data/ch.waut/files/bin && PATH=. busybox nice -n +5 busybox sh -x cb_weekly.sh $2 > ../cb_weekly.log 2>&1 &
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

HEAP=$(GETPROP dalvik.vm.heapsize 2>/dev/null | busybox cut -dm -f1 2>/dev/null )

if [ "x$MEM" != "x" ]; then 
  if [ "$MEM" -gt "1000000" ]; then 		  
	if [ "x$HEAP" != "x" ]; then 
	  if [ "$HEAP" -gt "128" ]; then 
		SETPROP dalvik.vm.heapsize 128m
	  fi
	fi
  else		  
	if [ "x$HEAP" != "x" ]; then 
	  if [ "$HEAP" -gt "64" ]; then 
		SETPROP dalvik.vm.heapsize 64m
	  fi
	fi
  fi
fi	

DATE=$(busybox date +%d 2>/dev/null)
if [ "x$DATE" != "x" ]; then 
  if [ "x$DATE" = "x15" ]; then
    if [ -e ../CLEAR_DALVIK ]; then  
	  busybox rm -f ../CLEAR_DALVIK
	  busybox sleep 3h
	fi
  fi
fi

busybox fstrim /system

busybox fstrim /data

busybox fstrim /cache

busybox fsync /data

busybox fsync /system

busybox fsync /cache

busybox fsync /sdcard

busybox sysctl -w vm.drop_caches=1

busybox sync

for j in $(busybox df -aP 2>/dev/null | busybox awk '{ print $1, $NF }' 2>/dev/null);
do
  busybox mount -o remount,sync $j 2>&1
  busybox mount -o remount,discard $j 2>&1
  busybox mount -o remount,commit=1 $j 2>&1
  busybox mount -o remount,async $j 2>&1
done;

for j in $(busybox mount 2>/dev/null | busybox awk '{ print $1, $3 }' 2>/dev/null);
do
  busybox mount -o remount,sync $j 2>&1
  busybox mount -o remount,discard $j 2>&1
  busybox mount -o remount,commit=1 $j 2>&1  
  busybox mount -o remount,async $j 2>&1
done;

if [ ! -e ../CLEAR_DALVIK ]; then

#  SETPROP dalvik.vm.checkjni false
#  SETPROP ro.kernel.android.checkjni 0

  busybox touch ../CLEAR_DALVIK
  
  if [ -e ../CLEAR_DALVIK ]; then 
  
  for APPDIR in $(busybox timeout -t 15 -s KILL busybox find /data/data -name cache 2>/dev/null); do 
     cd ${APPDIR}
	 if [ "$(busybox pwd 2>/dev/null)" = "${APPDIR}" ]; then 
	   busybox rm -fr ${APPDIR}/cache/*
	 fi
	 cd ${APP}
  done
  
   busybox rm -fr /data/dalvik-cache
   
   am broadcast android.intent.action.ACTION_SHUTDOWN
   busybox sleep 5
   am start -a android.intent.action.REBOOT
   busybox sleep 5
   if [ -x /system/bin/svc ]; then 
     svc power reboot dalvik
   fi

   busybox sleep 5
   busybox reboot -f -n

   busybox sleep 5
   busybox halt -f -n

   exit 1   
  fi
fi

CHECK_SLEEP() {
if [ "x$ARG" != "xFORCE" ]; then
  busybox timeout -t 0 -s KILL busybox cat /sys/power/wait_for_fb_wake >/dev/null 2>&1
  ret=$?
  if [ $ret -eq 0 ]; then return 0; fi
fi
}

# Run this before first reboot

CHECK_SLEEP

SETTINGS_DB="/data/data/com.android.providers.settings/databases/settings.db"
VERSION=$(GETPROP ro.build.version.release 2>/dev/null | busybox awk -F\. '{ print $1 }' 2>/dev/null)

BOOT_ID=$(busybox cat /proc/sys/kernel/random/boot_id 2>/dev/null)

for DB in $(busybox timeout -t 15 -s KILL busybox find /data/data -name *.db 2>/dev/null); do 

if [ "x$ARG" != "xFORCE" ]; then
  busybox timeout -t 0 -s KILL busybox cat /sys/power/wait_for_fb_wake >/dev/null 2>&1
  ret=$?
  if [ $ret -eq 0 ]; then return 0; fi
fi 
  if [ "x$DB" = "x$SETTINGS_DB" ]; then continue; fi

  NAME=$(busybox echo $DB 2>/dev/null | busybox sed 's/\"//g' 2>/dev/null)

  if [ "x$VERSION" != "x" ]; then 
    if [ "$VERSION" -ge "4" ]; then 
#	  sqlite3 "$NAME" ";;PRAGMA synchronous=OFF;;PRAGMA journal_mode=WAL;;"
	  sqlite3 "$NAME" ";;PRAGMA synchronous=FULL;;PRAGMA journal_mode=WAL;;"
	fi
  fi
#  sqlite3 "$NAME" ";;PRAGMA synchronous=OFF;;REINDEX;;VACUUM;;" 
  sqlite3 "$NAME" ";;PRAGMA synchronous=FULL;;REINDEX;;VACUUM;;" 

  busybox fsync "$DB"

  busybox sleep 0.1

done

busybox fstrim /system

busybox fstrim /data

busybox fstrim /cache

busybox fsync /data

busybox fsync /system

busybox fsync /cache

busybox fsync /sdcard

busybox sysctl -w vm.drop_caches=1

busybox sync

for j in $(busybox df -aP  2>/dev/null | busybox awk '{ print $1, $NF }' 2>/dev/null);
do
  busybox mount -o remount,sync $j
  busybox mount -o remount,discard $j
  busybox mount -o remount,commit=4 $j
  busybox mount -o remount,async $j
done;

for j in $(busybox mount 2>/dev/null | busybox awk '{ print $1, $3 }' 2>/dev/null);
do
  busybox mount -o remount,sync $j 
  busybox mount -o remount,discard $j
  busybox mount -o remount,commit=4 $j 
  busybox mount -o remount,async $j 
done;

# There is a bug here with pathnames containing space. Upgrade busybox so that it does not crash on find exec and upgrade this script

#( busybox sh oo_init.sh ) <&- >/dev/null

if [ 1 = 0 ]; then 

BOOT_ID=$(busybox cat /proc/sys/kernel/random/boot_id 2>/dev/null)

	if [ "x${BOOT_ID}" != "x" ]; then
	  if [ ! -e /dev/REBOOT ]; then 
	  	  
	  busybox touch /dev/REBOOT
	  if [ -e /dev/REBOOT ]; then 
		busybox killall system_server
	  fi
	  fi
	fi

fi