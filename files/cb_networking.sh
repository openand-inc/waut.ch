#!/data/data/ch.waut/files/bin/busybox sh

ARG=$1

if [ "x$ARG" = "xRUN" ]; then
  cd /data/data/ch.waut/files/bin && PATH=. busybox nice -n +5 busybox sh -x cb_init.sh $2 > ../cb_init.log 2>&1 &
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

#SETPROP dalvik.vm.checkjni false
#SETPROP ro.kernel.android.checkjni 0

SETPROP ro.ril.hep 0
SETPROP ro.ril.hsxpa 2
SETPROP ro.ril.gprsclass 12
SETPROP ro.ril.enable.dtm 1
SETPROP ro.ril.hsdpa.category 8
SETPROP ro.ril.enable.a53 1
SETPROP ro.ril.enable.3g.prefix 1
SETPROP ro.ril.htcmaskw1.bitmask 4294967295
SETPROP ro.ril.htcmaskw1 14449
SETPROP ro.ril.hsupa.category 6

SETPROP persist.telephony.support.ipv6 1
SETPROP persist.telephony.support.ipv4 1

SYSCTL net.ipv4.tcp_tw_recycle=1
SYSCTL net.ipv4.tcp_tw_reuse=1
SYSCTL net.ipv4.tcp_moderate_rcvbuf=1
SYSCTL net.ipv4.tcp_low_latency=0
SYSCTL net.ipv4.tcp_slow_start_after_idle=1
SYSCTL net.ipv4.tcp_window_scaling=1
SYSCTL net.ipv4.tcp_sack=1
SYSCTL net.ipv4.tcp_fack=1
SYSCTL net.ipv4.tcp_dsack=1
SYSCTL net.ipv4.tcp_thin_dupack=1
SYSCTL net.ipv4.tcp_thin_linear_timeouts=1
SYSCTL net.ipv4.tcp_ecn=1
SYSCTL net.ipv4.tcp_no_metrics_save=1

SYSCTL net.core.somaxconn=128
SYSCTL net.core.netdev_max_backlog=262144
SYSCTL net.ipv4.tcp_max_syn_backlog=128 

SYSCTL net.netfilter.nf_conntrack_tcp_timeout_established=120
SYSCTL net.netfilter.nf_conntrack_tcp_timeout_fin_wait=60
SYSCTL net.netfilter.nf_conntrack_tcp_timeout_close_wait=60
SYSCTL net.netfilter.nf_conntrack_tcp_timeout_last_ack=60
SYSCTL net.netfilter.nf_conntrack_tcp_timeout_time_wait=60
SYSCTL net.netfilter.nf_conntrack_tcp_timeout_close=60
SYSCTL net.ipv4.netfilter.ip_conntrack_tcp_timeout_established=120
SYSCTL net.ipv4.netfilter.ip_conntrack_tcp_timeout_fin_wait=60
SYSCTL net.ipv4.netfilter.ip_conntrack_tcp_timeout_close_wait=60
SYSCTL net.ipv4.netfilter.ip_conntrack_tcp_timeout_last_ack=60
SYSCTL net.ipv4.netfilter.ip_conntrack_tcp_timeout_time_wait=60
SYSCTL net.ipv4.netfilter.ip_conntrack_tcp_timeout_close=60

SYSCTL net.ipv4.tcp_keepalive_time=120
SYSCTL net.ipv4.tcp_keepalive_probes=3
SYSCTL net.ipv4.tcp_keepalive_intvl=60
SYSCTL net.ipv4.tcp_fin_timeout=60

SYSCTL net.ipv4.tcp_syn_retries=3
SYSCTL net.ipv4.tcp_synack_retries=3
SYSCTL net.ipv4.tcp_syncookies=1

SYSCTL net.ipv4.ip_local_port_range='2048 65000'

for interface in $(GETPROP | busybox grep -i net.tcp.buffersize | busybox cut -d\] -f1 | busybox cut -d\. -f4); do
  SETPROP net.tcp.buffersize.${interface} 4096,65536,87380,4096,16384,24576
done

SYSCTL net.ipv4.icmp_echo_ignore_all=1
SYSCTL net.ipv4.tcp_timestamps=1