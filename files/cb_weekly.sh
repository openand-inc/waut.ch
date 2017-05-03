#!/data/data/ch.waut/files/bin/busybox sh

ARG=$1

if [ "x$ARG" = "xRUN" ]; then
  cd /data/data/ch.waut/files/bin && PATH=. busybox nice -n +5 busybox sh -x cb_weekly.sh $2 > ../cb_weekly.log 2>&1 &
  return 0
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

for DB in $(busybox timeout -t 60 -s KILL busybox find /data/data -name *.db 2>/dev/null); do 

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
  
  busybox fsync "$DB" 2>/dev/null  
done

busybox fsync /data
CHECK_SLEEP
busybox fsync /system
CHECK_SLEEP
busybox fsync /cache
CHECK_SLEEP
busybox fsync /sdcard
CHECK_SLEEP
busybox sync

CHECK_SLEEP
busybox sysctl -w vm.drop_caches=3

CHECK_SLEEP
busybox fstrim /system 2>/dev/null

CHECK_SLEEP
busybox fstrim /data 2>/dev/null

CHECK_SLEEP
busybox fstrim /cache 2>/dev/null

CHECK_SLEEP
for j in $(busybox df -aP | busybox awk '{ print $1, $NF }');
do
  busybox mount -o remount,sync $j 2>/dev/null
  busybox mount -o remount,async $j 2>/dev/null
done;

CHECK_SLEEP
for j in $(busybox mount | busybox awk '{ print $1, $3 }');
do
  busybox mount -o remount,sync $j 2>/dev/null
  busybox mount -o remount,async $j 2>/dev/null
done;

# There is a bug here with pathnames containing space. Upgrade busybox so that it does not crash on find exec and upgrade this script

#( busybox sh oo_init.sh ) <&- >/dev/null

if [ ! -e /data/data/ch.waut/files/REBOOT ]; then 
  busybox touch /data/data/ch.waut/files/REBOOT
  if [ -e /data/data/ch.waut/files/REBOOT ]; then 
    busybox rm -fr /data/dalvik-cache
    /system/bin/reboot
  fi
fi
