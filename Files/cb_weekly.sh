#!/data/data/ch.waut/files/bin/busybox sh

ARG=$1

if [ "x$ARG" = "xRUN" ]; then
  cd /data/data/ch.waut/files/bin && PATH=. busybox nice -n +5 busybox sh -x cb_weekly.sh $2 > /data/data/ch.waut/files/cb_weekly.log 2>&1
  return 0
fi

set +e
trap " " 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
export APP=/data/data/ch.waut/files/bin
export PATH=${APP}
alias [='busybox ['
alias [[='busybox [['
alias ECHO='busybox echo '
alias SYSCTL='busybox timeout -t 3 -s KILL busybox sysctl -e -w '
alias SETPROP='/system/bin/setprop '
alias GETPROP='/system/bin/getprop '
if [ "$(GETPROP persist.cb_weekly.enabled 2>/dev/null)" = "FALSE" ]; then return 0; fi

CHECK_SLEEP() {
if [ "x$ARG" != "xFORCE" ]; then
  if [ -f AWAKE ]; then exit 0; fi
  if [ ! -f SLEEPING ]; then exit 0; fi
  
  busybox timeout -t 0 -s KILL busybox cat /sys/power/wait_for_fb_wake >/dev/null 2>&1
  ret=$?
  if [ $ret -eq 0 ]; then 
    exit 0; 
  fi
fi
}

CHECK_SLEEP

#busybox sh cb_sync.sh RUN 1

SETTINGS_DB="/data/data/com.android.providers.settings/databases/settings.db"
VERSION=$(GETPROP ro.build.version.release 2>/dev/null | busybox awk -F\. '{ print $1 }' 2>/dev/null)

BOOT_ID=$(busybox cat /proc/sys/kernel/random/boot_id 2>/dev/null)

    SYSCTL vm.vfs_cache_pressure=999999
    SYSCTL vm.vfs_cache_pressure=10

SWAP=$(busybox free 2>/dev/null | busybox grep Swap 2>/dev/null | busybox awk '{ print $2 }' 2>/dev/null)

if [ "x$SWAP" != "x" ]; then 
  if [ "$SWAP" -gt "10000" ]; then  
    SYSCTL vm.swappiness=2
    SYSCTL vm.swappiness=1
  fi
fi
	
#    SYSCTL vm.vfs_cache_pressure=10
	SYSCTL kernel.random.read_wakeup_threshold=3968
	SYSCTL kernel.random.write_wakeup_threshold=3968
	busybox touch /proc/sys/kernel/random/entropy_avail
	busybox touch /dev/random 
	busybox dd if=/dev/random of=/dev/null bs=1 count=1
	busybox touch /dev/random 
	busybox dd if=/dev/random of=/dev/null bs=1 count=1
	busybox touch /dev/random 
	busybox dd if=/dev/random of=/dev/null bs=1 count=1
	busybox touch /dev/random 
	busybox dd if=/dev/random of=/dev/null bs=1 count=1
	/system/bin/logcat -c

busybox sysctl -w vm.drop_caches=1

for DB in $(busybox timeout -t 15 -s KILL busybox find /data/data -name *.db 2>/dev/null); do 

#  CHECK_SLEEP
 
  if [ -f AWAKE ]; then return 0; fi
  if [ ! -f SLEEPING ]; then return 0; fi
  
  if [ "x$DB" = "x$SETTINGS_DB" ]; then continue; fi

  NAME=$(busybox echo $DB 2>/dev/null | busybox sed 's/\"//g' 2>/dev/null)

  sqlite3 "$NAME" ";;PRAGMA synchronous=FULL;;REINDEX;;VACUUM;;" 

#  busybox fsync "$DB"

# busybox sleep 0.1

done

DAY_NOW=$(busybox date -u 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null)

 if [ "x$DAY_NOW" = "xTue" ]; then 
	busybox fstrim -v /data 
 fi

busybox sysctl -w vm.drop_caches=3

busybox sync

# ECHO 64 > /proc/sys/kernel/random/write_wakeup_threshold
# ECHO 64 > /proc/sys/kernel/random/read_wakeup_threshold
# ECHO 10 > /proc/sys/vm/vfs_cache_pressure
#ECHO 99 | busybox tee /proc/sys/vm/dirty_ratio
ECHO 1048576 | busybox tee /proc/sys/vm/dirty_background_bytes
#ECHO 99 | busybox tee /proc/sys/vm/dirty_background_ratio
ECHO 1048576 | busybox tee /proc/sys/vm/dirty_bytes
#SYSCTL vm.dirty_background_bytes=8192
SYSCTL vm.dirty_ratio=1048576
#SYSCTL vm.dirty_bytes=8192
SYSCTL vm.dirty_background_ratio=1048576
# ECHO 49 > /proc/sys/vm/overcommit_ratio
# ECHO 1 > /proc/sys/vm/overcommit_memory
# busybox chmod 666 /proc/sys/net/ipv4/icmp_echo_ignore_all
# ECHO 0 > /proc/sys/net/ipv4/icmp_echo_ignore_all
# busybox chmod 444 /proc/sys/net/ipv4/icmp_echo_ignore_all
# busybox chmod 666 /proc/sys/net/ipv4/tcp_timestamps
# ECHO 1 > /proc/sys/net/ipv4/tcp_timestamps
# busybox chmod 444 /proc/sys/net/ipv4/tcp_timestamps

#busybox sh cb_sync.sh RUN 6

#CHECK_SLEEP

#exec busybox sh cb_reboot.sh RUN

