package ch.waut;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class wautch_service extends  android.app.Service{
	public static class wautch_service_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
			android.content.Intent in = new android.content.Intent(context, wautch_service.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
			context.startService(in);
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
    			anywheresoftware.b4a.objects.IntentWrapper iw = new anywheresoftware.b4a.objects.IntentWrapper();
    			if (intent != null) {
    				if (intent.hasExtra("b4a_internal_intent"))
    					iw.setObject((android.content.Intent) intent.getParcelableExtra("b4a_internal_intent"));
    				else
    					iw.setObject(intent);
    			}
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
        BA.LogInfo("** Service (wautch_service) Destroy **");
		processBA.raiseEvent(null, "service_destroy");
        processBA.service = null;
		mostCurrent = null;
		processBA.setActivityPaused(true);
        processBA.runHook("ondestroy", this, null);
	}

@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}public anywheresoftware.b4a.keywords.Common __c = null;
public static ice.rootcmd.RootCmd _v5 = null;
public static MLfiles.Fileslib.MLfiles _v6 = null;
public ch.waut.main _vv2 = null;
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
 //BA.debugLineNum = 26;BA.debugLine="If rc.haveRoot Then";
if (_v5.haveRoot()) { 
 //BA.debugLineNum = 27;BA.debugLine="File.MakeDir(File.DirInternal, \"bin\")";
anywheresoftware.b4a.keywords.Common.File.MakeDir(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"bin");
 //BA.debugLineNum = 28;BA.debugLine="File.Copy(File.DirAssets,\"busybox\",File.DirInter";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"busybox",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/busybox");
 //BA.debugLineNum = 29;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/busybox\",755)";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox",BA.NumberToString(755));
 //BA.debugLineNum = 30;BA.debugLine="rc.execRootCmdSilent( \"/system/bin/toolbox chmod";
_v5.execRootCmdSilent("/system/bin/toolbox chmod -R 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin");
 //BA.debugLineNum = 31;BA.debugLine="rc.execRootCmdSilent( \"/system/bin/toolbox chmod";
_v5.execRootCmdSilent("/system/bin/toolbox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox");
 //BA.debugLineNum = 33;BA.debugLine="File.Copy(File.DirAssets,\"CB_RunHaveged\",File.Di";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"CB_RunHaveged",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/CB_RunHaveged");
 //BA.debugLineNum = 34;BA.debugLine="File.Copy(File.DirAssets,\"haveged\",File.DirInter";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"haveged",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/haveged");
 //BA.debugLineNum = 35;BA.debugLine="File.Copy(File.DirAssets,\"sqlite3\",File.DirInter";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"sqlite3",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/sqlite3");
 //BA.debugLineNum = 36;BA.debugLine="File.Copy(File.DirAssets,\"cb.sh\",File.DirInterna";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cb.sh",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/cb.sh");
 //BA.debugLineNum = 37;BA.debugLine="File.Copy(File.DirAssets,\"cb_init.sh\",File.DirIn";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cb_init.sh",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/cb_init.sh");
 //BA.debugLineNum = 38;BA.debugLine="File.Copy(File.DirAssets,\"cb_io.sh\",File.DirInte";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cb_io.sh",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/cb_io.sh");
 //BA.debugLineNum = 39;BA.debugLine="File.Copy(File.DirAssets,\"cb_networking.sh\",File";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cb_networking.sh",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/cb_networking.sh");
 //BA.debugLineNum = 40;BA.debugLine="File.Copy(File.DirAssets,\"cb_weekly.sh\",File.Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cb_weekly.sh",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/cb_weekly.sh");
 //BA.debugLineNum = 42;BA.debugLine="File.Copy(File.DirAssets,\"cb_weekly.sh\",File.Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cb_weekly.sh",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"/bin/cb_sync.sh");
 //BA.debugLineNum = 44;BA.debugLine="rc.execRootCmdSilent(\"/system/bin/toolbox chmod";
_v5.execRootCmdSilent("/system/bin/toolbox chmod -R 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin");
 //BA.debugLineNum = 45;BA.debugLine="rc.execRootCmdSilent(\"/system/bin/toolbox chmod";
_v5.execRootCmdSilent("/system/bin/toolbox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox");
 //BA.debugLineNum = 47;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/CB_RunHaveged\"";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/CB_RunHaveged",BA.NumberToString(755));
 //BA.debugLineNum = 48;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/haveged\",755)";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/haveged",BA.NumberToString(755));
 //BA.debugLineNum = 49;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/sqlite3\",755)";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/sqlite3",BA.NumberToString(755));
 //BA.debugLineNum = 50;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/cb.sh\",755)";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb.sh",BA.NumberToString(755));
 //BA.debugLineNum = 51;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/cb_init.sh\",75";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_init.sh",BA.NumberToString(755));
 //BA.debugLineNum = 52;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/cb_io.sh\",755)";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_io.sh",BA.NumberToString(755));
 //BA.debugLineNum = 53;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/cb_networking.";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_networking.sh",BA.NumberToString(755));
 //BA.debugLineNum = 54;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/cb_weekly.sh\",";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_weekly.sh",BA.NumberToString(755));
 //BA.debugLineNum = 56;BA.debugLine="fs.chmod(File.DirInternal & \"/bin/cb_sync.sh\",75";
_v6.chmod(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_sync.sh",BA.NumberToString(755));
 //BA.debugLineNum = 58;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/CB_RunHaveged");
 //BA.debugLineNum = 59;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/haveged");
 //BA.debugLineNum = 60;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/sqlite3");
 //BA.debugLineNum = 61;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb.sh");
 //BA.debugLineNum = 62;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_init.sh");
 //BA.debugLineNum = 63;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_io.sh");
 //BA.debugLineNum = 64;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_networking.sh");
 //BA.debugLineNum = 65;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_weekly.sh");
 //BA.debugLineNum = 67;BA.debugLine="rc.execRootCmdSilent( File.DirInternal & \"/bin/b";
_v5.execRootCmdSilent(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox chmod 755 "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_sync.sh");
 //BA.debugLineNum = 70;BA.debugLine="rc.execRootCmd( File.DirInternal & \"/bin/busybox";
_v5.execRootCmd(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox setsid "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox sh "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb.sh RUN FORCE");
 //BA.debugLineNum = 71;BA.debugLine="rc.execRootCmd( File.DirInternal & \"/bin/busybox";
_v5.execRootCmd(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox setsid "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox sh "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_io.sh RUN FORCE");
 //BA.debugLineNum = 72;BA.debugLine="rc.execRootCmd( File.DirInternal & \"/bin/busybox";
_v5.execRootCmd(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox setsid "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox sh "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_networking.sh RUN FORCE");
 //BA.debugLineNum = 80;BA.debugLine="rc.execRootCmd( File.DirInternal & \"/bin/busyb";
_v5.execRootCmd(anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox setsid "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/busybox sh "+anywheresoftware.b4a.keywords.Common.File.getDirInternal()+"/bin/cb_init.sh RUN FORCE");
 };
 //BA.debugLineNum = 89;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 98;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 100;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
long _timeofday = 0L;
 //BA.debugLineNum = 91;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 93;BA.debugLine="Dim timeofday As Long";
_timeofday = 0L;
 //BA.debugLineNum = 94;BA.debugLine="timeofday = DateTime.Now + ( ( 29 - DateTime.GetH";
_timeofday = (long) (anywheresoftware.b4a.keywords.Common.DateTime.getNow()+((29-anywheresoftware.b4a.keywords.Common.DateTime.GetHour(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))*3600000));
 //BA.debugLineNum = 95;BA.debugLine="StartServiceAt(\"\", timeofday , True)";
anywheresoftware.b4a.keywords.Common.StartServiceAt(processBA,(Object)(""),_timeofday,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 96;BA.debugLine="End Sub";
return "";
}
}
