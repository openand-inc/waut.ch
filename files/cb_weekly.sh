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

CHECK_SLEEP() {
if [ "x$ARG" != "xFORCE" ]; then
  busybox timeout -t 0 -s KILL busybox cat /sys/power/wait_for_fb_wake >/dev/null 2>&1
  ret=$?
  if [ $ret -eq 0 ]; then 
    exec busybox sh cb_sync.sh 10
    return 0; 
  fi
fi
}

DATE=$(busybox date +%d 2>/dev/null)
if [ "x$DATE" != "x" ]; then 
  if [ "x$DATE" = "x15" ]; then
    if [ -e ../CLEAR_DALVIK ]; then  
	  busybox rm -f ../CLEAR_DALVIK
	fi
    if [ -e /data/CLEAR_DALVIK ]; then  
	  busybox rm -f /data/CLEAR_DALVIK
	fi
	
	busybox sleep 2h
	
	exec busybox sh cb_reboot.sh
	
	return 0
  fi
fi

CHECK_SLEEP

exec busybox sh cb_sync.sh 1

SETTINGS_DB="/data/data/com.android.providers.settings/databases/settings.db"
VERSION=$(GETPROP ro.build.version.release 2>/dev/null | busybox awk -F\. '{ print $1 }' 2>/dev/null)

BOOT_ID=$(busybox cat /proc/sys/kernel/random/boot_id 2>/dev/null)

for DB in $(busybox timeout -t 15 -s KILL busybox find /data/data -name *.db 2>/dev/null); do 

  CHECK_SLEEP
  
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

#  busybox sleep 1

done

exec busybox sh cb_sync.sh 10

