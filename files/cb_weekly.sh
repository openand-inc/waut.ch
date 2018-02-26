#!/data/data/ch.waut/files/bin/busybox sh

ARG=$1

if [ "x$ARG" = "xRUN" ]; then
  cd /data/data/ch.waut/files/bin && PATH=. busybox nice -n +5 busybox sh -x cb_weekly.sh $2 > ../cb_weekly.log 2>&1
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
    exec busybox sh cb_sync.sh RUN 6
    return 0; 
  fi
fi
}

CHECK_SLEEP

exec busybox sh cb_sync.sh RUN 1

SETTINGS_DB="/data/data/com.android.providers.settings/databases/settings.db"
VERSION=$(GETPROP ro.build.version.release 2>/dev/null | busybox awk -F\. '{ print $1 }' 2>/dev/null)

BOOT_ID=$(busybox cat /proc/sys/kernel/random/boot_id 2>/dev/null)

for DB in $(busybox timeout -t 15 -s KILL busybox find /data/data -name *.db 2>/dev/null); do 

  CHECK_SLEEP
  
  if [ "x$DB" = "x$SETTINGS_DB" ]; then continue; fi

  NAME=$(busybox echo $DB 2>/dev/null | busybox sed 's/\"//g' 2>/dev/null)

  sqlite3 "$NAME" ";;PRAGMA synchronous=FULL;;REINDEX;;VACUUM;;" 

  busybox fsync "$DB"

  busybox sleep 0.1

done

 ECHO interactive > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor
 ECHO interactive > /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor
 ECHO 128 > /proc/sys/kernel/random/write_wakeup_threshold
 ECHO 320 > /proc/sys/kernel/random/read_wakeup_threshold
 ECHO 9000000000 > /proc/sys/vm/vfs_cache_pressure
 ECHO 99 > /proc/sys/vm/dirty_ratio
 ECHO 1 > /proc/sys/vm/dirty_background_ratio
 ECHO 1 > /proc/sys/net/ipv4/icmp_echo_ignore_all
 ECHO 1 > /proc/sys/net/ipv4/tcp_timestamps

exec busybox sh cb_sync.sh RUN 6

