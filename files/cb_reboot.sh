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

CHECK_SLEEP() {
if [ "x$ARG" != "xFORCE" ]; then
  busybox timeout -t 0 -s KILL busybox cat /sys/power/wait_for_fb_wake >/dev/null 2>&1
  ret=$?
  if [ $ret -eq 0 ]; then 
#    exec busybox sh cb_sync.sh 6
    return 0; 
  fi
fi
}

VERSION=$(GETPROP ro.build.version.release 2>/dev/null | busybox busybox cut -d. -f1 2>/dev/null)

MEM=$(busybox free 2>/dev/null | busybox grep Mem 2>/dev/null | busybox awk '{ print $2 }' 2>/dev/null)

CHECK_SLEEP

FOUND=0

if [ -e ../CLEAR_DALVIK ]; then 
  FOUND=1
fi

if [ -e /data/CLEAR_DALVIK ]; then 
  FOUND=1
fi

if [ ! -e /data/CLEAR_DALVIK ]; then
  busybox touch /data/CLEAR_DALVIK  
fi

if [ ! -e ../CLEAR_DALVIK ]; then

  busybox touch ../CLEAR_DALVIK
  
  if [ ! -e ../CLEAR_DALVIK ]; then 
    FOUND=1
  fi
fi

if [ ${FOUND} -eq 0 ]; then 
    
#   busybox rm -fr /data/dalvik-cache
   
   exec busybox sh -x cb_sync.sh 1 > ../cb_sync.log 2>&1
   
   busybox sysctl -w vm.drop_caches=3
   
   am broadcast android.intent.action.ACTION_SHUTDOWN
   busybox sleep 15
   am broadcast android.intent.action.ACTION_SHUTDOWN
   busybox sleep 15
   am start -a android.intent.action.REBOOT
   busybox sleep 15
   am start -a android.intent.action.REBOOT
   busybox sleep 15
   if [ -x /system/bin/svc ]; then 
     svc power reboot dalvik
   fi
   busybox sleep 15
   if [ -x /system/bin/svc ]; then 
     svc power reboot dalvik
   fi

   busybox sleep 30
   busybox reboot -f -n

#   busybox sleep 30
#   busybox halt -f -n

   exit 1   
fi

