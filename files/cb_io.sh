#!/data/data/ch.waut/files/bin/busybox sh

ARG=$1

if [ "x$ARG" = "xRUN" ]; then
  cd /data/data/ch.waut/files/bin && PATH=. busybox nice -n +5 busybox sh -x cb_io.sh $2 > ../cb_io.log 2>&1 &
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

HEAP=$(GETPROP dalvik.vm.heapsize 2>/dev/null | busybox cut -dm -f1 2>/dev/null )

if [ 1 = 0 ]; then 

for m in $(busybox mount | busybox awk '{ if ($5 ~ /^ext/) print $1,$3 }' | busybox grep "/data" | busybox awk '{ print $1 }');
do
  tune2fs -f -o journal_data_writeback $m >/dev/null 2>&1
  tune2fs -f -O dir_index $m >/dev/null 2>&1  
  # busybox hdparm -S 4 $m >/dev/null 2>&1
done;

for m in $(busybox mount | busybox awk '{ if ($5 ~ /^ext/) print $1,$3 }' | busybox grep "/system" | busybox awk '{ print $1 }');
do
  tune2fs -f -o journal_data_writeback $m >/dev/null 2>&1
  debugfs -w -R "feature ^needs_recovery" $m >/dev/null 2>&1
#  tune2fs -f -O ^needs_recovery $m >/dev/null 2>&1  
#  tune2fs -f -O ^has_journal $m >/dev/null 2>&1
  tune2fs -f -O dir_index $m >/dev/null 2>&1  
# busybox hdparm -r 1 $m >/dev/null 2>&1
done;

# Change Android to check filesystem before each mount

fi

busybox fstrim /system 
busybox fstrim /data 
busybox fstrim /cache 
busybox fsync /data 
busybox fsync /system 
busybox fsync /cache 
busybox fsync /sdcard 

busybox touch ../IO_LOCK
  
for j in $(busybox df -aP 2>/dev/null | busybox awk '{ print $1, $NF }' 2>/dev/null);
do
  busybox mount -o remount,sync $j   
  busybox mount -o remount,noatime $j 
  busybox mount -o remount,nodiratime $j 
  busybox mount -o remount,discard $j 
#  busybox mount -o remount,barrier=1 $j 
  busybox mount -o remount,commit=10 $j 
# busybox mount -o remount,data=writeback $j 
#  busybox mount -o remount,data=ordered $j 
  busybox mount -o remount,journal_async_commit $j 
#  busybox mount -o remount,journal_checksum $j 
  busybox mount -o remount,journal_ioprio=5 $j 
#  busybox mount -o remount,errors=remount-ro $j 
  busybox mount -o remount,async $j   
done;

for j in $(busybox mount 2>/dev/null | busybox awk '{ print $1, $3 }' 2>/dev/null);
do
  busybox mount -o remount,sync $j   
  busybox mount -o remount,noatime $j 
  busybox mount -o remount,nodiratime $j 
  busybox mount -o remount,discard $j 
#  busybox mount -o remount,barrier=1 $j 
  busybox mount -o remount,commit=10 $j 
#  busybox mount -o remount,data=writeback $j 
#  busybox mount -o remount,data=ordered $j 
  busybox mount -o remount,journal_async_commit $j 
#  busybox mount -o remount,journal_checksum $j 
  busybox mount -o remount,journal_ioprio=5 $j 
#  busybox mount -o remount,errors=remount-ro $j 
  busybox mount -o remount,async $j 
done;

busybox sysctl -w vm.drop_caches=1 
busybox sync 

busybox mount -o remount,commit=60 /system

busybox rm -f ../IO_LOCK

busybox mount -t debugfs -o rw none /sys/kernel/debug 

if [ -e /sys/kernel/debug/sched_features ]; then
  ECHO NO_NORMALIZED_SLEEPER > /sys/kernel/debug/sched_features 
  ECHO GENTLE_FAIR_SLEEPERS > /sys/kernel/debug/sched_features 
  ECHO NO_NEW_FAIR_SLEEPERS > /sys/kernel/debug/sched_features 
fi

busybox umount /sys/kernel/debug 

busybox umount -l /sys/kernel/debug 

for j in $(busybox df -aP | busybox awk '{ if ( $1 ~ /^\// ) print $1 }');
do
  busybox hdparm -a 0 $j 
# hdparm -W0 $j   
done;

for j in $(busybox mount | busybox awk '{ if ( $1 ~ /^\// ) print $1 }');
do
  busybox hdparm -a 0 $j 
# hdparm -W0 $j   
done;

for i in $(busybox timeout -t 15 -s KILL busybox find /sys/devices /sys/block /dev/block -name read_ahead_kb 2>/dev/null); do ECHO 0 | busybox tee $i; done

for i in $(busybox timeout -t 15 -s KILL busybox find /sys/devices /sys/block /dev/block -name nr_requests 2>/dev/null); do ECHO 100000 | busybox tee $i ; done

for i in $(busybox timeout -t 15 -s KILL busybox find /sys/devices /sys/block /dev/block -name rq_affinity 2>/dev/null); do ECHO 2 | busybox tee $i ; done
for i in $(busybox timeout -t 15 -s KILL busybox find /sys/devices /sys/block /dev/block -name rotational 2>/dev/null); do ECHO 0 | busybox tee $i ; done
for i in $(busybox timeout -t 15 -s KILL busybox find /sys/devices /sys/block /dev/block -name nomerges 2>/dev/null); do ECHO 0 | busybox tee $i ; done
for i in $(busybox timeout -t 15 -s KILL busybox find /sys/devices /sys/block /dev/block -name iostats 2>/dev/null); do ECHO 0 | busybox tee $i ; done
for i in $(busybox timeout -t 15 -s KILL busybox find /sys/devices /sys/block /dev/block -name low_latency 2>/dev/null); do ECHO 0 | busybox tee $i ; done

for i in $(busybox timeout -t 15 -s KILL busybox find /sys/devices /sys/block /dev/block -name scheduler 2>/dev/null); do 
  busybox chmod 666 $i
  ECHO noop | busybox tee $i ; 
  busybox chmod 444 $i
done

for i in 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15; do
#for i in 0 1; do
 if [ -e /sys/devices/system/cpu/cpu$i/cpufreq/scaling_governor ]; then 
  busybox chmod 666 /sys/devices/system/cpu/cpu$i/cpufreq/scaling_governor 
  ECHO interactive | busybox tee /sys/devices/system/cpu/cpu$i/cpufreq/scaling_governor 
  busybox chmod 444 /sys/devices/system/cpu/cpu$i/cpufreq/scaling_governor 
 fi
done

cd /sys/devices/system/cpu/cpufreq/interactive

if [ "$(busybox pwd 2>/dev/null)" = "/sys/devices/system/cpu/cpufreq/interactive" ]; then 
	ECHO 20000 | busybox tee above_hispeed_delay 
	ECHO 0 | busybox tee boost 
	ECHO 80000 | busybox tee boostpulse_duration 
	ECHO 80000 | busybox tee boosttop_duration 
	ECHO 99 | busybox tee go_hispeed_load 
	ECHO 99 | busybox tee go_maxspeed_load 
	ECHO 1 | busybox tee input_dev_monitor 
	ECHO 1 | busybox tee input_boost 
	ECHO 0 | busybox tee io_is_busy 
	ECHO 80000 | busybox tee min_sample_time 
	ECHO 90 | busybox tee target_loads 
	ECHO 90 | busybox tee sustain_load 
	ECHO 20000 | busybox tee timer_rate 
	ECHO 80000 | busybox tee timer_slack 
fi

cd ${APP}

SYSCTL vm.overcommit_ratio=49
SYSCTL vm.overcommit_memory=1

# Put heap size max = 128

# Put GPU code here

