#!/data/data/ch.waut/files/bin/busybox sh

ARG=$1

if [ "x$ARG" = "xRUN" ]; then
  cd /data/data/ch.waut/files/bin && PATH=. busybox nice -n +5 busybox sh -x cb.sh $2 > ../cb.log 2>&1 &
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

busybox killall -9 CB_RunHaveged > /dev/null 2>&1
busybox killall -9 haveged >/dev/null 2>&1

#( busybox nice -n -1 haveged -r 0 -o ta8bcb ) <&- >/dev/null &
#( busybox nice -n -1 haveged -r 0 -o tbca8wbw ) <&- >/dev/null &
( busybox nice -n +1 CB_RunHaveged ) <&- >/dev/null &

SETPROP persist.sys.scrollingcache 4

SETPROP windowsmgr.max_events_per_sec 108
SETPROP ro.max.fling_velocity 6000
SETPROP ro.min.fling_velocity 6000

SYSCTL vm.laptop_mode=1

SYSCTL vm.oom_kill_allocating_task=0
SYSCTL vm.oom_dump_tasks=0
SYSCTL vm.panic_on_oom=0
SYSCTL kernel.panic_on_oops=0
SYSCTL kernel.panic_on_warn=0
SYSCTL kernel.panic=0
SYSCTL vm.vfs_cache_pressure=9000000000
#SYSCTL vm.vfs_cache_pressure=65536

SYSCTL vm.dirty_background_ratio=1
SYSCTL vm.dirty_ratio=99

SYSCTL vm.dirty_writeback_centisecs=0
SYSCTL vm.dirty_expire_centisecs=0

for pid in $(busybox ps | busybox awk '{ if ($2 !~ /^app_/) print $1 }'); do
  if [ -f /proc/$pid/oom_adj ]; then 
    ECHO -17 > /proc/$pid/oom_adj 2>/dev/null
  fi
  busybox ionice -c 2 -n 0 -p $pid 2>/dev/null
  busybox chrt -o -p 10 $pid 2>/dev/null
done

for pid in $(busybox ps -T -o pid,args 2>/dev/null | busybox grep -i 'netd$' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
  busybox renice +1 $pid 2>/dev/null
  busybox ionice -c 2 -n 3 -p $pid 2>/dev/null
  busybox chrt -o -p 30 $pid 2>/dev/null
done

for pid in $(busybox ps -T -o pid,args 2>/dev/null | busybox egrep -i 'jbd2|flush-|pdflush' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
  busybox renice +2 $pid 2>/dev/null
  busybox ionice -c 3 -n 5 -p $pid 2>/dev/null
  busybox chrt -o -p 75 $pid 2>/dev/null
done

for pid in $(busybox ps -T -o pid,args 2>/dev/null | busybox grep -i 'surfaceflinger$' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
  busybox renice +1 $pid 2>/dev/null
  busybox ionice -c 1 -n 4 -p $pid 2>/dev/null
  busybox chrt -o -p 5 $pid 2>/dev/null
done

for pid in $(busybox ps -T -o pid,args 2>/dev/null | busybox grep -i 'zygote$' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
  busybox renice -1 $pid 2>/dev/null
  busybox ionice -c 1 -n 4 -p $pid 2>/dev/null
  busybox chrt -r -p 50 $pid 2>/dev/null
done

for pid in $(busybox ps -T -o pid,args 2>/dev/null | busybox grep -i 'system_server$' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
  busybox renice -1 $pid 2>/dev/null
  busybox ionice -c 1 -n 4 -p $pid 2>/dev/null
  busybox chrt -r -p 50 $pid 2>/dev/null
done

for pid in $(busybox ps -T -o pid,user 2>/dev/null | busybox awk '{ if ( $2 ~ /^app_/) print $1 }' 2>/dev/null); do
  busybox renice -1 $pid 2>/dev/null
  busybox ionice -c 1 -n 4 -p $pid 2>/dev/null
  busybox chrt -r -p 50 $pid 2>/dev/null
done

for pid in $(/system/bin/dumpsys activity services | busybox grep -i app=ProcessRecord | busybox awk '{ print $2 }' | busybox cut -d: -f1); do
 if [ "$pid" -gt "1024" ]; then 
  busybox renice 1 $pid 2>/dev/null
  busybox ionice -c 2 -n 2 -p $pid 2>/dev/null
  busybox chrt -o -p 20 $pid 2>/dev/null
 fi
done

if [ -e /dev/cpuctl/bg_non_interactive/cpu.shares ]; then 
  ECHO 32 > /dev/cpuctl/bg_non_interactive/cpu.shares 2>/dev/null
fi

if [ -e /dev/cpuctl/cpu.shares ]; then 
  ECHO 1024 > /dev/cpuctl/cpu.shares 2>/dev/null
fi

if [ -e /dev/cpuctl/fg_boost/cpu.shares ]; then 
  ECHO 1536 > /dev/cpuctl/fg_boost/cpu.shares 2>/dev/null
fi

if [ -e /dev/cpuctl/cpu.rt_period_us ]; then
  ECHO 1000000 > /dev/cpuctl/cpu.rt_period_us 2>/dev/null
fi

if [ -e /dev/cpuctl/cpu.rt_runtime_us ]; then
  ECHO 900000 > /dev/cpuctl/cpu.rt_runtime_us 2>/dev/null
fi

if [ -e /dev/cpuctl/apps/cpu.rt_period_us ]; then
  ECHO 1000000 > /dev/cpuctl/apps/cpu.rt_period_us 2>/dev/null
fi

if [ -e /dev/cpuctl/apps/cpu.rt_runtime_us ]; then
  ECHO 900000 > /dev/cpuctl/apps/cpu.rt_runtime_us 2>/dev/null
fi

if [ -e /dev/cpuctl/bg_non_interactive/cpu.rt_period_us ]; then
  ECHO 900000 > /dev/cpuctl/bg_non_interactive/cpu.rt_period_us 2>/dev/null
fi

if [ -e /dev/cpuctl/bg_non_interactive/cpu.rt_runtime_us ]; then
  ECHO 700000 > /dev/cpuctl/bg_non_interactive/cpu.rt_runtime_us 2>/dev/null
fi

if [ -e /dev/cpuctl/apps/bg_non_interactive/cpu.rt_period_us ]; then
  ECHO 900000 > /dev/cpuctl/apps/bg_non_interactive/cpu.rt_period_us 2>/dev/null
fi

if [ -e /dev/cpuctl/apps/bg_non_interactive/cpu.rt_runtime_us ]; then
  ECHO 700000 > /dev/cpuctl/apps/bg_non_interactive/cpu.rt_runtime_us 2>/dev/null
fi

i=$(busybox pgrep haveged 2>/dev/null | busybox wc -l 2>/dev/null)
if [ "$i" -ne "0" ]; then 

if [ "$(busybox cat /proc/sys/kernel/random/read_wakeup_threshold 2>/dev/null)" != "8" ]; then 
   SYSCTL kernel.random.read_wakeup_threshold=8
fi

#POOLSIZE="$(busybox cat /proc/sys/kernel/random/poolsize 2>/dev/null)"
#if [ "$(busybox cat /proc/sys/kernel/random/write_wakeup_threshold 2>/dev/null)" != "${POOLSIZE}" ]; then 
#   SYSCTL kernel.random.write_wakeup_threshold="${POOLSIZE}"
#fi

for i in $(busybox timeout -t 15 -s KILL busybox find /sys/devices /sys/block /dev/block -name add_random -print 2>/dev/null); do ECHO 0 > $i; done 

for pid in $(busybox ps -T -o pid,args 2>/dev/null | busybox grep haveged 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
  busybox renice +1 $pid 2>/dev/null
  busybox ionice -c 2 -n 3 -p $pid 2>/dev/null
  busybox chrt -o -p 50 $pid 2>/dev/null
  if [ -f /proc/$pid/oom_adj ]; then
	ECHO -17 > /proc/$pid/oom_adj 2>/dev/null
  fi
done

  busybox chmod 444 /dev/random
  busybox chmod 444 /dev/urandom
  
else
   SYSCTL kernel.random.read_wakeup_threshold=256
   SYSCTL kernel.random.write_wakeup_threshold=320
   
  ( busybox nice -n +1 haveged -r 0 -o tbca8wbw ) <&- >/dev/null &
fi

#( busybox sh cb_io.sh ) <&- >/dev/null

#( busybox sh cb_init.sh ) <&- >/dev/null
