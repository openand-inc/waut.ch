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
export APP=/data/data/ch.waut/files/bin
export PATH=${APP}
alias [='busybox ['
alias [[='busybox [['
alias ECHO='busybox timeout -t 1 -s KILL busybox echo '
alias SYSCTL='busybox timeout -t 3 -s KILL busybox sysctl -e -w '
alias SETPROP='/system/bin/setprop '
alias GETPROP='/system/bin/getprop '
if [ "$(GETPROP persist.cb.enabled 2>/dev/null)" = "FALSE" ]; then return 0; fi

MEM=$(busybox free 2>/dev/null | busybox grep Mem 2>/dev/null | busybox awk '{ print $2 }' 2>/dev/null)

if [ ! -d /dev/entropy ]; then 
  busybox mkdir -p /dev/entropy
  busybox chown 0.0 /dev/entropy
  busybox chmod 750 /dev/entropy  
fi

if [ ! -c /dev/entropy/random ]; then 
  busybox mkdir -p /dev/entropy
  busybox chown 0.0 /dev/entropy
  busybox chmod 750 /dev/entropy
  busybox mknod -m 640 /dev/entropy/random c 1 8
  busybox chown 0.0 /dev/entropy/random
fi

busybox killall -9 CB_RunHaveged
busybox killall -9 haveged 

  busybox chmod 644 /dev/random
  busybox chmod 644 /dev/urandom

  busybox chown 0.0 /dev/entropy/random
  busybox chmod 640 /dev/entropy/random
  
#( busybox nice -n -1 haveged -r 0 -o ta8bcb ) <&- >/dev/null &
#( busybox nice -n -1 haveged -r 0 -o tbca8wbw ) <&- >/dev/null &
( busybox nice -n +1 CB_RunHaveged ) <&- >/dev/null &

SETPROP persist.sys.scrollingcache 1

SETPROP windowsmgr.max_events_per_sec 240

# This defines the min duration between two pointer events
#SETPROP ro.min_pointer_dur 1
#SETPROP ro.max_pointer_dur 999
SETPROP ro.max.fling_velocity 12000
SETPROP ro.min.fling_velocity 8000
#SETPROP ro.product.multi_touch_enabled true
#SETPROP ro.product.max_num_touch 999
#SETPROP persist.sys.use_dithering 1

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
    ECHO -17 > /proc/$pid/oom_adj
  fi
  busybox ionice -c 2 -n 4 -p $pid
  busybox chrt -o -p 40 $pid
done

for pid in $(busybox ps -T -o pid,args 2>/dev/null | busybox grep -i 'netd$' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
  busybox renice 1 $pid
  busybox ionice -c 1 -n 5 -p $pid
  busybox chrt -o -p 25 $pid
done

for pid in $(busybox ps -T -o pid,args 2>/dev/null | busybox egrep -i 'jbd2|flush-|pdflush' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
  busybox renice 2 $pid
  busybox ionice -c 2 -n 5 -p $pid
  busybox chrt -o -p 75 $pid
done

for pid in $(busybox ps -T -o pid,args 2>/dev/null | busybox grep -i 'surfaceflinger$' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
  busybox renice -1 $pid
  busybox ionice -c 1 -n 4 -p $pid
  busybox chrt -o -p 15 $pid
done

for pid in $(busybox ps -T -o pid,args 2>/dev/null | busybox grep -i 'zygote$' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
  busybox renice -1 $pid
  busybox ionice -c 1 -n 3 -p $pid
  busybox chrt -o -p 5 $pid 
done

for pid in $(busybox ps -T -o pid,args 2>/dev/null | busybox grep -i 'system_server$' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
  busybox renice -1 $pid
  busybox ionice -c 1 -n 3 -p $pid
  busybox chrt -o -p 5 $pid
done

for pid in $(busybox ps -T -o pid,user 2>/dev/null | busybox awk '{ if ( $2 ~ /^app_/) print $1 }' 2>/dev/null); do
  busybox renice -1 $pid
  busybox ionice -c 1 -n 3 -p $pid 
  busybox chrt -o -p 5 $pid
done

for pid in $(/system/bin/dumpsys activity services | busybox grep -i app=ProcessRecord | busybox awk '{ print $2 }' | busybox cut -d: -f1); do
 if [ "$pid" -gt "1024" ]; then 
  busybox renice 2 $pid
  busybox ionice -c 2 -n 2 -p $pid
  busybox chrt -o -p 25 $pid
 fi
done

if [ -e /dev/cpuctl/bg_non_interactive/cpu.shares ]; then 
  ECHO 64 > /dev/cpuctl/bg_non_interactive/cpu.shares
fi

if [ -e /dev/cpuctl/cpu.shares ]; then 
  ECHO 512 > /dev/cpuctl/cpu.shares
fi

if [ -e /dev/cpuctl/fg_boost/cpu.shares ]; then 
  ECHO 768 > /dev/cpuctl/fg_boost/cpu.shares
fi

if [ -e /dev/cpuctl/cpu.rt_period_us ]; then
  ECHO 1000000 > /dev/cpuctl/cpu.rt_period_us 
fi

if [ -e /dev/cpuctl/cpu.rt_runtime_us ]; then
  ECHO 900000 > /dev/cpuctl/cpu.rt_runtime_us 
fi

if [ -e /dev/cpuctl/apps/cpu.rt_period_us ]; then
  ECHO 1000000 > /dev/cpuctl/apps/cpu.rt_period_us 
fi

if [ -e /dev/cpuctl/apps/cpu.rt_runtime_us ]; then
  ECHO 900000 > /dev/cpuctl/apps/cpu.rt_runtime_us 
fi

if [ -e /dev/cpuctl/bg_non_interactive/cpu.rt_period_us ]; then
  ECHO 900000 > /dev/cpuctl/bg_non_interactive/cpu.rt_period_us 
fi

if [ -e /dev/cpuctl/bg_non_interactive/cpu.rt_runtime_us ]; then
  ECHO 700000 > /dev/cpuctl/bg_non_interactive/cpu.rt_runtime_us 
fi

if [ -e /dev/cpuctl/apps/bg_non_interactive/cpu.rt_period_us ]; then
  ECHO 900000 > /dev/cpuctl/apps/bg_non_interactive/cpu.rt_period_us 
fi

if [ -e /dev/cpuctl/apps/bg_non_interactive/cpu.rt_runtime_us ]; then
  ECHO 700000 > /dev/cpuctl/apps/bg_non_interactive/cpu.rt_runtime_us 
fi

i=$(busybox pgrep haveged 2>/dev/null | busybox wc -l 2>/dev/null)
if [ "$i" -ne "0" ]; then 

#if [ "$(busybox cat /proc/sys/kernel/random/read_wakeup_threshold 2>/dev/null)" != "8" ]; then 
#   SYSCTL kernel.random.read_wakeup_threshold=8
#fi

#SYSCTL kernel.random.read_wakeup_threshold=256
#SYSCTL kernel.random.read_wakeup_threshold=8
#SYSCTL kernel.random.read_wakeup_threshold=4064
SYSCTL kernel.random.read_wakeup_threshold=64

#POOLSIZE=4064
#POOLSIZE=320
#POOLSIZE=0
#POOLSIZE=4000
POOLSIZE=64
#POOLSIZE="$(busybox cat /proc/sys/kernel/random/poolsize 2>/dev/null)"
#if [ "$(busybox cat /proc/sys/kernel/random/write_wakeup_threshold 2>/dev/null)" != "${POOLSIZE}" ]; then 
   SYSCTL kernel.random.write_wakeup_threshold="${POOLSIZE}"
#fi

for i in $(busybox timeout -t 15 -s KILL busybox find /sys/devices /sys/block /dev/block -name add_random -print 2>/dev/null); do ECHO 0 > $i; done 

for pid in $(busybox ps -T -o pid,args 2>/dev/null | busybox grep haveged 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
#  busybox renice +1 $pid
  busybox ionice -c 2 -n 5 -p $pid
  busybox chrt -o -p 50 $pid
  if [ -f /proc/$pid/oom_adj ]; then
	ECHO -17 > /proc/$pid/oom_adj
  fi
done

  busybox chmod 644 /dev/random
  busybox chmod 644 /dev/urandom
  busybox chmod 640 /dev/entropy/random
  
else
   SYSCTL kernel.random.read_wakeup_threshold=256
   SYSCTL kernel.random.write_wakeup_threshold=256
   
#  ( busybox nice -n +5 haveged -r 0 -o tbca8wbw ) <&- >/dev/null &
#  ( busybox nice -n +5 haveged -r 0 -o tba8cba8 ) <&- >/dev/null &
  ( busybox nice -n +1 haveged -r 0 ) <&- >/dev/null &

fi

#( busybox sh cb_io.sh ) <&- >/dev/null

#( busybox sh cb_init.sh ) <&- >/dev/null
