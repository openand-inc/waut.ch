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

# If data full then delete cache

FOUND=0
CHARGE=0

  CHARGING=$(adb shell status | busybox grep -i status | busybox awk '{ print $NF }' 2>/dev/null)
  if [ "x$CHARGING" != "x" ]; then 
    if [ "x$CHARGING" = "x2" ]; then 
	  LEVEL=$(adb shell status | busybox grep -i level | busybox awk '{ print $NF }' 2>/dev/null)
      if [ "x$LEVEL" != "x" ]; then 
        if [ "$LEVEL" -gt "70" ]; then 
		  CHARGE=1
        fi
      fi	  
    fi
  fi

#DATE=$(busybox date +%d 2>/dev/null)
#if [ "x$DATE" != "x" ]; then 
#  if [ "x$DATE" = "x15" ]; then   
	DATASPACE=$(busybox df /data 2>/dev/null| busybox grep 'data' 2>/dev/null| busybox awk 'BEGIN{print ""} {percent+=$(NF-1);} END{print percent}' 2>/dev/null| busybox tail -1 2>/dev/null)
	if [ "x$DATASPACE" != "x" ]; then 
      if [ "x$DATASPACE" = "x99" ]; then   
	    FOUND=1
      fi       	
      if [ "x$DATASPACE" = "x100" ]; then   
	    FOUND=1
      fi       	
	fi
#  fi	
#fi

CHECK_SLEEP

if [ ${FOUND} -eq 1 ]; then 
  if [ ${CHARGE} -eq 1 ]; then 

  for APPDIR in $(busybox timeout -t 15 -s KILL busybox find /data/data -name cache 2>/dev/null); do 
     cd ${APPDIR}
	 if [ "$(busybox pwd 2>/dev/null)" = "${APPDIR}" ]; then 
	   busybox rm -fr ${APPDIR}/*
	 fi
	 cd ${APP}
  done
    	   
   busybox rm -fr /data/dalvik-cache
   
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

   busybox sleep 30
   busybox halt -f -n

   exit 1   
  fi
fi

