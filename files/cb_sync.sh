#!/data/data/ch.waut/files/bin/busybox sh

ARG=$1

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

COMMIT=10

if [ "x$ARG" != "x" ]; then
  COMMIT=1
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

if [ -e ../IO_LOCK ]; then 
  busybox sleep 5  
fi
  
for j in $(busybox df -aP  2>/dev/null | busybox awk '{ print $1, $NF }' 2>/dev/null);
do
  busybox mount -o remount,sync $j
  busybox mount -o remount,discard $j
  busybox mount -o remount,commit=$COMMIT $j
  busybox mount -o remount,async $j
done;

for j in $(busybox mount 2>/dev/null | busybox awk '{ print $1, $3 }' 2>/dev/null);
do
  busybox mount -o remount,sync $j 
  busybox mount -o remount,discard $j
  busybox mount -o remount,commit=$COMMIT $j
  busybox mount -o remount,async $j 
done;

busybox mount -o remount,commit=60 /system
