#!/data/data/ch.waut/files/bin/busybox sh

ARG=$1

if [ "x$ARG" = "xRUN" ]; then
  cd /data/data/ch.waut/files/bin && PATH=. busybox setsid busybox sh -x cb.sh $2 > ../cb.log 2>&1 &
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
alias ECHO='busybox echo '
alias SYSCTL='busybox timeout -t 3 -s KILL busybox sysctl -e -w '
alias SETPROP='/system/bin/setprop '
alias GETPROP='/system/bin/getprop '
if [ "$(GETPROP persist.cb.enabled 2>/dev/null)" = "FALSE" ]; then return 0; fi

HOUR_NOW=$(busybox date -u 2>/dev/null | busybox awk '{ print $4 }' 2>/dev/null | busybox cut -d: -f1 2>/dev/null)

if [ "x$(GETPROP cb.92d6d8e3.run 2>/dev/null)" = "x" ]; then 
  busybox rm -f /dev/COLD_REBOOT
  busybox rm -f /data/property/persist.cb.run 
  busybox rm -f /data/data/ch.waut/files/bin/cb_reboot.sh
  busybox rm -f /data/data/ch.waut/files/cb_reboot.log  
  SETPROP persist.cb_reboot.enabled FALSE
fi

  if [ "x$(GETPROP cb.92d6d8e3.run 2>/dev/null)" = "x${HOUR_NOW}" ]; then 
    SYSCTL vm.vfs_cache_pressure=1	
	SYSCTL kernel.random.read_wakeup_threshold=4000
	SYSCTL kernel.random.write_wakeup_threshold=4000
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

    return 0
  fi

SETPROP cb.92d6d8e3.run ${HOUR_NOW} 

MEM=$(busybox free 2>/dev/null | busybox grep Mem 2>/dev/null | busybox awk '{ print $2 }' 2>/dev/null)

if [ 1 = 0 ]; then 
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
fi

busybox killall -9 cb_runhaveged
busybox killall -9 haveged 

  busybox chmod 444 /dev/random
  busybox chmod 444 /dev/urandom

#  busybox chown 0.0 /dev/entropy/random
#  busybox chmod 640 /dev/entropy/random
  
#( busybox nice -n -1 haveged -r 0 -o ta8bcb ) <&- >/dev/null &
#( busybox nice -n -1 haveged -r 0 -o tbca8wbw ) <&- >/dev/null &

( busybox nice -n 0 cb_runhaveged ) <&- >/dev/null &

SETPROP persist.sys.scrollingcache 1

SETPROP windowsmgr.max_events_per_sec 60

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

#SYSCTL vm.vfs_cache_pressure=32767

SYSCTL vm.vfs_cache_pressure=1
#SYSCTL vm.vfs_cache_pressure=9000000000

#SYSCTL vm.vfs_cache_pressure=65536
#SYSCTL vm.vfs_cache_pressure=1000

SYSCTL vm.dirty_background_ratio=1
SYSCTL vm.dirty_ratio=99

SYSCTL vm.dirty_writeback_centisecs=0
SYSCTL vm.dirty_expire_centisecs=0

#if [ 1 = 0 ]; then 

#for pid in $(busybox ps | busybox awk '{ if ($2 !~ /^app_/) print $1 }'); do
#  if [ -f /proc/$pid/oom_adj ]; then 
#    ECHO -17 > /proc/$pid/oom_adj
#  fi
#  busybox renice 1 $pid
#  busybox ionice -c 2 -n 1 -p $pid
#  busybox chrt -o -p 0 $pid
#done

for pid in $(busybox ps -o pid,args 2>/dev/null | busybox grep -i 'netd$' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
#  busybox renice 1 $pid
  busybox ionice -c 2 -n 1 -p $pid
  busybox chrt -o -p 0 $pid
done

#for pid in $(busybox ps -o pid,args 2>/dev/null | busybox egrep -i 'jbd2|flush-|pdflush' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
#  busybox renice 2 -p $pid
#  busybox ionice -c 2 -n 2 -p $pid
#  busybox chrt -o -p 0 $pid
#done

#for pid in $(busybox ps -o pid,args 2>/dev/null | busybox grep -i 'surfaceflinger$' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
#  busybox renice -1 $pid
#  busybox ionice -c 1 -n 2 -p $pid
#  busybox chrt -f -p 30 $pid
#done

#for pid in $(busybox ps -o pid,args 2>/dev/null | busybox grep -i 'zygote$' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
#  busybox renice -1 $pid
#  busybox ionice -c 1 -n 4 -p $pid
#  busybox chrt -f -p 30 $pid 
#done

#for pid in $(busybox ps -o pid,args 2>/dev/null | busybox grep -i 'system_server$' 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
#  busybox renice -1 $pid
#  busybox ionice -c 1 -n 4 -p $pid
#  busybox chrt -f -p 30 $pid
#done

for pid in $(busybox ps -o pid,user 2>/dev/null | busybox awk '{ if ( $2 ~ /^app_/) print $1 }' 2>/dev/null); do
#  busybox renice -1 -p $pid
  busybox ionice -c 1 -n 3 -p $pid 
  busybox chrt -f -p 30 $pid
done

for pid in $(/system/bin/dumpsys activity services | busybox grep -i app=ProcessRecord | busybox awk '{ print $2 }' | busybox cut -d: -f1); do
 if [ "$pid" -gt "1024" ]; then 
#  busybox renice 1 -p $pid
  busybox ionice -c 2 -n 3 -p $pid
  busybox chrt -o -p 0 $pid
 fi
done

#fi

if [ -e /dev/cpuctl/bg_non_interactive/cpu.shares ]; then 
  ECHO 50 > /dev/cpuctl/bg_non_interactive/cpu.shares
fi

if [ -e /dev/cpuctl/cpu.shares ]; then 
  ECHO 1000 > /dev/cpuctl/cpu.shares
fi

if [ -e /dev/cpuctl/fg_boost/cpu.shares ]; then 
  ECHO 1250 > /dev/cpuctl/fg_boost/cpu.shares
fi

#####

if [ 1 = 0 ]; then 

# Less is better
if [ -e /dev/cpuctl/cpu.rt_period_us ]; then
  ECHO 1000000 > /dev/cpuctl/cpu.rt_period_us 
#  ECHO 900000 > /dev/cpuctl/cpu.rt_period_us 
fi

# More is better
if [ -e /dev/cpuctl/cpu.rt_runtime_us ]; then
#  ECHO 900000 > /dev/cpuctl/cpu.rt_runtime_us 
  ECHO 800000 > /dev/cpuctl/cpu.rt_runtime_us 
fi

#####

# Less is better
if [ -e /dev/cpuctl/apps/cpu.rt_period_us ]; then
  ECHO 1000000 > /dev/cpuctl/apps/cpu.rt_period_us 
#  ECHO 950000 > /dev/cpuctl/apps/cpu.rt_period_us 
fi

# More is better
if [ -e /dev/cpuctl/apps/cpu.rt_runtime_us ]; then
  ECHO 800000 > /dev/cpuctl/apps/cpu.rt_runtime_us 
fi

#####

# Less is better
if [ -e /dev/cpuctl/bg_non_interactive/cpu.rt_period_us ]; then
#  ECHO 900000 > /dev/cpuctl/bg_non_interactive/cpu.rt_period_us 
  ECHO 1000000 > /dev/cpuctl/bg_non_interactive/cpu.rt_period_us 
fi

# More is better
if [ -e /dev/cpuctl/bg_non_interactive/cpu.rt_runtime_us ]; then
#  ECHO 700000 > /dev/cpuctl/bg_non_interactive/cpu.rt_runtime_us 
  ECHO 800000 > /dev/cpuctl/bg_non_interactive/cpu.rt_runtime_us 
fi

#####

# Less is better

if [ -e /dev/cpuctl/apps/bg_non_interactive/cpu.rt_period_us ]; then
#  ECHO 900000 > /dev/cpuctl/apps/bg_non_interactive/cpu.rt_period_us 
  ECHO 1000000 > /dev/cpuctl/apps/bg_non_interactive/cpu.rt_period_us 
fi

# More is better

if [ -e /dev/cpuctl/apps/bg_non_interactive/cpu.rt_runtime_us ]; then
#  ECHO 700000 > /dev/cpuctl/apps/bg_non_interactive/cpu.rt_runtime_us 
  ECHO 800000 > /dev/cpuctl/apps/bg_non_interactive/cpu.rt_runtime_us 
fi

#####

fi

i=$(busybox pgrep haveged 2>/dev/null | busybox wc -l 2>/dev/null)
if [ "$i" -ne "0" ]; then 

#if [ "$(busybox cat /proc/sys/kernel/random/read_wakeup_threshold 2>/dev/null)" != "8" ]; then 
#   SYSCTL kernel.random.read_wakeup_threshold=8
#fi

#SYSCTL kernel.random.read_wakeup_threshold=256
#SYSCTL kernel.random.read_wakeup_threshold=8
#SYSCTL kernel.random.read_wakeup_threshold=4064
SYSCTL kernel.random.read_wakeup_threshold=4000

#POOLSIZE=4064
#POOLSIZE=320
#POOLSIZE=0
POOLSIZE=4000
#POOLSIZE=64
#POOLSIZE="$(busybox cat /proc/sys/kernel/random/poolsize 2>/dev/null)"
#if [ "$(busybox cat /proc/sys/kernel/random/write_wakeup_threshold 2>/dev/null)" != "${POOLSIZE}" ]; then 
   SYSCTL kernel.random.write_wakeup_threshold="${POOLSIZE}"
#fi

for i in $(busybox timeout -t 15 -s KILL busybox find /sys/devices /sys/block /dev/block -name add_random -print 2>/dev/null); do ECHO 0 > $i; done 

#for pid in $(busybox ps -o pid,args 2>/dev/null | busybox grep -i haveged 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
for pid in $(busybox pgrep haveged 2>/dev/null); do
#  busybox renice 1 $pid
#  busybox ionice -c 2 -n 4 -p $pid
#  busybox chrt -o -p 0 $pid
  if [ -f /proc/$pid/oom_adj ]; then
    ECHO -17 > /proc/$pid/oom_adj
  fi
done

  busybox chmod 444 /dev/random
  busybox chmod 444 /dev/urandom
#  busybox chmod 640 /dev/entropy/random
  
else
   SYSCTL kernel.random.read_wakeup_threshold=4000
   SYSCTL kernel.random.write_wakeup_threshold=4000
   
#  ( busybox nice -n +5 haveged -r 0 -o tbca8wbw ) <&- >/dev/null &
#   ( busybox nice haveged -F -o tbc ) <&- >/dev/null &
#   ( haveged -F -o tbc ) <&- >/dev/null &

   ( busybox nice -n 0 cb_runhaveged ) <&- >/dev/null &
      
#for pid in $(busybox ps -o pid,args 2>/dev/null | busybox grep -i haveged 2>/dev/null | busybox awk '{ print $1 }' 2>/dev/null); do
for pid in $(busybox pgrep haveged 2>/dev/null); do
#  busybox renice 1 $pid
#  busybox ionice -c 2 -n 4 -p $pid
#  busybox chrt -o -p 0 $pid
  if [ -f /proc/$pid/oom_adj ]; then
    ECHO -17 > /proc/$pid/oom_adj
  fi
done

fi

( busybox sh -x cb_networking.sh RUN FORCE ) <&- >/dev/null &

( busybox sh -x cb_io.sh RUN FORCE ) <&- >/dev/null &

( busybox sh -x cb_init.sh RUN FORCE ) <&- >/dev/null &

#busybox sh cb_networking.sh RUN FORCE

#busybox sh cb_io.sh RUN FORCE

#busybox sh cb_init.sh RUN FORCE