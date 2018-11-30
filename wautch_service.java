package ch.waut;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class wautch_service extends  android.app.Service{
	public static class wautch_service_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
            BA.LogInfo("** Receiver (wautch_service) OnReceive **");
			android.content.Intent in = new android.content.Intent(context, wautch_service.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
            ServiceHelper.StarterHelper.startServiceFromReceiver (context, in, false, BA.class);
		}

	}
    static wautch_service mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return wautch_service.class;
	}
	@Override
	public void onCreate() {
        super.onCreate();
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "ch.waut", "ch.waut.wautch_service");
            if (BA.isShellModeRuntimeCheck(processBA)) {
                processBA.raiseEvent2(null, true, "SHELL", false);
		    }
            try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            processBA.loadHtSubs(this.getClass());
            ServiceHelper.init();
        }
        _service = new ServiceHelper(this);
        processBA.service = this;
        
        if (BA.isShellModeRuntimeCheck(processBA)) {
			processBA.raiseEvent2(null, true, "CREATE", true, "ch.waut.wautch_service", processBA, _service, anywheresoftware.b4a.keywords.Common.Density);
		}
        if (!false && ServiceHelper.StarterHelper.startFromServiceCreate(processBA, true) == false) {
				
		}
		else {
            processBA.setActivityPaused(false);
            BA.LogInfo("*** Service (wautch_service) Create ***");
            processBA.raiseEvent(null, "service_create");
        }
        processBA.runHook("oncreate", this, null);
        if (false) {
			ServiceHelper.StarterHelper.runWaitForLayouts();
		}
    }
		@Override
	public void onStart(android.content.Intent intent, int startId) {
		onStartCommand(intent, 0, 0);
    }
    @Override
    public int onStartCommand(final android.content.Intent intent, int flags, int startId) {
    	if (ServiceHelper.StarterHelper.onStartCommand(processBA, new Runnable() {
            public void run() {
                handleStart(intent);
            }}))
			;
		else {
			ServiceHelper.StarterHelper.addWaitForLayout (new Runnable() {
				public void run() {
                    processBA.setActivityPaused(false);
                    BA.LogInfo("** Service (wautch_service) Create **");
                    processBA.raiseEvent(null, "service_create");
					handleStart(intent);
                    ServiceHelper.StarterHelper.removeWaitForLayout();
				}
			});
		}
        processBA.runHook("onstartcommand", this, new Object[] {intent, flags, startId});
		return android.app.Service.START_NOT_STICKY;
    }
    public void onTaskRemoved(android.content.Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (false)
            processBA.raiseEvent(null, "service_taskremoved");
            
    }
    private void handleStart(android.content.Intent intent) {
    	BA.LogInfo("** Service (wautch_service) Start **");
    	java.lang.reflect.Method startEvent = processBA.htSubs.get("service_start");
    	if (startEvent != null) {
    		if (startEvent.getParameterTypes().length > 0) {
    			anywheresoftware.b4a.objects.IntentWrapper iw = ServiceHelper.StarterHelper.handleStartIntent(intent, _service, processBA);
    			processBA.raiseEvent(null, "service_start", iw);
    		}
    		else {
    			processBA.raiseEvent(null, "service_start");
    		}
    	}
    }
	
	@Override
	public void onDestroy() {
        super.onDestroy();
        if (false) {
            BA.LogInfo("** Service (wautch_service) Destroy (ignored)**");
        }
        else {
            BA.LogInfo("** Service (wautch_service) Destroy **");
		    processBA.raiseEvent(null, "service_destroy");
            processBA.service = null;
		    mostCurrent = null;
		    processBA.setActivityPaused(true);
            processBA.runHook("ondestroy", this, null);
        }
	}

@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}public anywheresoftware.b4a.keywords.Common __c = null;
public static ice.rootcmd.RootCmd _v5 = null;
public static MLfiles.Fileslib.MLfiles _v6 = null;
public ch.waut.main _vv4 = null;
public static String  _vv3() throws Exception{
 //BA.debugLineNum = 29;BA.debugLine="Sub myStart";
 //BA.debugLineNum = 30;BA.debugLine="fs.GetRoot";
_v6.GetRoot();
 //BA.debugLineNum = 31;BA.debugLine="If rc.haveRoot Then";
if (_v5.haveRoot()) { 
 //BA.debugLineNum = 32;BA.debugLine="fs.GetRoot";
_v6.GetRoot();
 //BA.debugLineNum = 34;BA.debugLine="File.MakeDir(File.DirInternal, \"bin\")";
anywheresoftware.b4a.keywords.Common.File.MakeDir(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"bin");
 //BA.debugLineNum = 35;BA.debugLine="File.Copy(File.DirAssets,\"busybox\",File.DirInter";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"busybox",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/busybox");
 //BA.debugLineNum = 37;BA.debugLine="File.Copy(File.DirAssets,\"cb_runhaveged\",File.Di";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cb_runhaveged",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/cb_runhaveged");
 //BA.debugLineNum = 38;BA.debugLine="File.Copy(File.DirAssets,\"haveged\",File.DirInter";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"haveged",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/haveged");
 //BA.debugLineNum = 39;BA.debugLine="File.Copy(File.DirAssets,\"sqlite3\",File.DirInter";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"sqlite3",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/sqlite3");
 //BA.debugLineNum = 40;BA.debugLine="File.Copy(File.DirAssets,\"cb.sh\",File.DirInterna";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cb.sh",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/cb.sh");
 //BA.debugLineNum = 41;BA.debugLine="File.Copy(File.DirAssets,\"cb_init.sh\",File.DirIn";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cb_init.sh",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/cb_init.sh");
 //BA.debugLineNum = 42;BA.debugLine="File.Copy(File.DirAssets,\"cb_io.sh\",File.DirInte";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cb_io.sh",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/cb_io.sh");
 //BA.debugLineNum = 44;BA.debugLine="File.Copy(File.DirAssets,\"cb_weekly.sh\",File.Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cb_weekly.sh",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/cb_weekly.sh");
 //BA.debugLineNum = 45;BA.debugLine="File.Copy(File.DirAssets,\"cb_reboot.sh\",File.Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cb_reboot.sh",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/cb_reboot.sh");
 //BA.debugLineNum = 48;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/busybox\",755)";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox",BA.NumberToString(755));
 //BA.debugLineNum = 49;BA.debugLine="rc.execRootCmdSilent( \"/system/bin/toolbox chmod";
_v5.execRootCmdSilent("/system/bin/toolbox chmod -R 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin");
 //BA.debugLineNum = 50;BA.debugLine="rc.execRootCmdSilent( \"/system/bin/toolbox chmod";
_v5.execRootCmdSilent("/system/bin/toolbox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox");
 //BA.debugLineNum = 52;BA.debugLine="rc.execRootCmdSilent(\"/system/bin/toolbox chmod";
_v5.execRootCmdSilent("/system/bin/toolbox chmod -R 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin");
 //BA.debugLineNum = 53;BA.debugLine="rc.execRootCmdSilent(\"/system/bin/toolbox chmod";
_v5.execRootCmdSilent("/system/bin/toolbox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox");
 //BA.debugLineNum = 55;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/cb_runhaveged\"";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_runhaveged",BA.NumberToString(755));
 //BA.debugLineNum = 56;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/haveged\",755)";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/haveged",BA.NumberToString(755));
 //BA.debugLineNum = 57;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/sqlite3\",755)";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/sqlite3",BA.NumberToString(755));
 //BA.debugLineNum = 58;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/cb.sh\",755)";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb.sh",BA.NumberToString(755));
 //BA.debugLineNum = 59;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/cb_init.sh\",75";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_init.sh",BA.NumberToString(755));
 //BA.debugLineNum = 60;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/cb_io.sh\",755)";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_io.sh",BA.NumberToString(755));
 //BA.debugLineNum = 62;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/cb_weekly.sh\",";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_weekly.sh",BA.NumberToString(755));
 //BA.debugLineNum = 63;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/cb_reboot.sh\",";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_reboot.sh",BA.NumberToString(755));
 //BA.debugLineNum = 66;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_runhaveged");
 //BA.debugLineNum = 67;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/haveged");
 //BA.debugLineNum = 68;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/sqlite3");
 //BA.debugLineNum = 69;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb.sh");
 //BA.debugLineNum = 70;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_init.sh");
 //BA.debugLineNum = 71;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_io.sh");
 //BA.debugLineNum = 73;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_weekly.sh");
 //BA.debugLineNum = 74;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_reboot.sh");
 //BA.debugLineNum = 77;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox dos2unix "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb.sh");
 //BA.debugLineNum = 78;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox dos2unix "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_init.sh");
 //BA.debugLineNum = 79;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox dos2unix "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_io.sh");
 //BA.debugLineNum = 81;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox dos2unix "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_weekly.sh");
 //BA.debugLineNum = 82;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox dos2unix "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_reboot.sh");
 //BA.debugLineNum = 84;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chown 0.0 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/*");
 //BA.debugLineNum = 108;BA.debugLine="rc.execRootCmd( File.DirInternal & \"/bin/busybox";
_v5.execRootCmd(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox setsid "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox sh "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb.sh RUN FORCE");
 //BA.debugLineNum = 110;BA.debugLine="ToastMessageShow(\"running cold...\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("running cold..."),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 133;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 5;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Dim rc As RootCmd";
_v5 = new ice.rootcmd.RootCmd();
 //BA.debugLineNum = 10;BA.debugLine="Dim fs As MLfiles";
_v6 = new MLfiles.Fileslib.MLfiles();
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 25;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 146;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 148;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
long _timeofday = 0L;
 //BA.debugLineNum = 135;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 138;BA.debugLine="Dim timeofday As Long";
_timeofday = 0L;
 //BA.debugLineNum = 139;BA.debugLine="timeofday = DateTime.Now + ( ( 27 - DateTime.GetH";
_timeofday = (long) (anywheresoftware.b4a.keywords.Common.DateTime.getNow()+((27-anywheresoftware.b4a.keywords.Common.DateTime.GetHour(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))*3600000));
 //BA.debugLineNum = 140;BA.debugLine="StartServiceAt(\"\", timeofday , True)";
anywheresoftware.b4a.keywords.Common.StartServiceAt(processBA,(Object)(""),_timeofday,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 142;BA.debugLine="myStart";
_vv3();
 //BA.debugLineNum = 144;BA.debugLine="End Sub";
return "";
}
}
