package ch.waut;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "ch.waut", "ch.waut.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "ch.waut", "ch.waut.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "ch.waut.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.agraham.clocks.ChronometerWrapper.AnalogClockWrapper _vv2 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button2 = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkbox1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public static long _time_i = 0L;
public static long _time_ii = 0L;
public static long _time_iii = 0L;
public static long _time_iv = 0L;
public ch.waut.wautch_service _wautch_service = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _aclock_click() throws Exception{
 //BA.debugLineNum = 191;BA.debugLine="Sub Aclock_Click";
 //BA.debugLineNum = 192;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 193;BA.debugLine="ToastMessageShow( DateTime.Time(DateTime.Now), Fa";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow())),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 194;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 195;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _isfirst) throws Exception{
 //BA.debugLineNum = 116;BA.debugLine="Sub Activity_Create(isFirst As Boolean)";
 //BA.debugLineNum = 120;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 122;BA.debugLine="Activity.Title = \"waut.ch! running... ? for help,";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("waut.ch! running... ? for help, space to clear page"));
 //BA.debugLineNum = 124;BA.debugLine="Aclock.Initialize(\"Aclock\")";
mostCurrent._vv2.Initialize(mostCurrent.activityBA,"Aclock");
 //BA.debugLineNum = 126;BA.debugLine="Activity.AddView(Aclock, 0dip, 0dip, 100dip, 100d";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vv2.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)));
 //BA.debugLineNum = 128;BA.debugLine="Aclock.Color = Colors.Transparent";
mostCurrent._vv2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 130;BA.debugLine="Label1.Top = Activity.Height / 2";
mostCurrent._label1.setTop((int) (mostCurrent._activity.getHeight()/(double)2));
 //BA.debugLineNum = 131;BA.debugLine="Label1.Left = Activity.Width / 2 - 40";
mostCurrent._label1.setLeft((int) (mostCurrent._activity.getWidth()/(double)2-40));
 //BA.debugLineNum = 133;BA.debugLine="Label1.BringToFront";
mostCurrent._label1.BringToFront();
 //BA.debugLineNum = 135;BA.debugLine="Label1.TextSize = 18 - random13";
mostCurrent._label1.setTextSize((float) (18-_vv3()));
 //BA.debugLineNum = 136;BA.debugLine="Button2.TextSize = 14";
mostCurrent._button2.setTextSize((float) (14));
 //BA.debugLineNum = 138;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 140;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 142;BA.debugLine="time_I = DateTime.Now";
_time_i = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 143;BA.debugLine="time_II = 0";
_time_ii = (long) (0);
 //BA.debugLineNum = 144;BA.debugLine="time_III = DateTime.Now";
_time_iii = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 145;BA.debugLine="time_IV = 0";
_time_iv = (long) (0);
 //BA.debugLineNum = 147;BA.debugLine="StartServiceAt(wautch_service,DateTime.Now + 1 *";
anywheresoftware.b4a.keywords.Common.StartServiceAt(processBA,(Object)(mostCurrent._wautch_service.getObject()),(long) (anywheresoftware.b4a.keywords.Common.DateTime.getNow()+1*1000),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 149;BA.debugLine="If ( isFirst = True ) Then";
if ((_isfirst==anywheresoftware.b4a.keywords.Common.True)) { 
 //BA.debugLineNum = 151;BA.debugLine="ToastMessageShow(\"reboot once at convenience...\"";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("reboot once at convenience..."),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 157;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 163;BA.debugLine="Sub Activity_Pause(UserClosed As Boolean)";
 //BA.debugLineNum = 166;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 168;BA.debugLine="ToastMessageShow( Rnd(1,64) , True )";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 169;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 180;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 182;BA.debugLine="Sub Activity_Resume()";
 //BA.debugLineNum = 184;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 185;BA.debugLine="Label1.TextSize = 18 - random13";
mostCurrent._label1.setTextSize((float) (18-_vv3()));
 //BA.debugLineNum = 186;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 189;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 327;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 328;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 330;BA.debugLine="CheckBox1.Enabled = True";
mostCurrent._checkbox1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 331;BA.debugLine="CheckBox1.Visible = True";
mostCurrent._checkbox1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 333;BA.debugLine="CheckBox1.Checked = False";
mostCurrent._checkbox1.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 335;BA.debugLine="Aclock.Visible = True";
mostCurrent._vv2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 336;BA.debugLine="Button2.Visible = True";
mostCurrent._button2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 338;BA.debugLine="Button1.Visible = False";
mostCurrent._button1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 339;BA.debugLine="Button1.SendToBack";
mostCurrent._button1.SendToBack();
 //BA.debugLineNum = 340;BA.debugLine="CheckBox1.BringToFront";
mostCurrent._checkbox1.BringToFront();
 //BA.debugLineNum = 342;BA.debugLine="Activity.Title = \"waut.ch! running... ? for help,";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("waut.ch! running... ? for help, space to clear page"));
 //BA.debugLineNum = 343;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 345;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
 //BA.debugLineNum = 197;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 199;BA.debugLine="Button2.Enabled = False";
mostCurrent._button2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 201;BA.debugLine="time_I = DateTime.Now";
_time_i = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 203;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 205;BA.debugLine="If ( time_I > ( time_II + 1500 ) ) Then";
if ((_time_i>(_time_ii+1500))) { 
 //BA.debugLineNum = 206;BA.debugLine="ToastMessageShow( \"utility for background calibr";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("utility for background calibration, curation and tuning of the device towards an intuitive interface. subsystems being battery, entropy, encryption, disk, cpu, memory, filesystem, ui, scheduler, and network, all safe and open source technology. presented in this gaming metric format with infinite feedback and an interestingly assymetric chance. scribble anywhere, check in some stress, or find the 8!"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 207;BA.debugLine="time_II = DateTime.Now";
_time_ii = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 };
 //BA.debugLineNum = 210;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 212;BA.debugLine="Button2.Enabled = True";
mostCurrent._button2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 214;BA.debugLine="End Sub";
return "";
}
public static String  _checkbox1_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 227;BA.debugLine="Sub CheckBox1_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 230;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 232;BA.debugLine="CheckBox1.Enabled = False";
mostCurrent._checkbox1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 233;BA.debugLine="CheckBox1.Visible = False";
mostCurrent._checkbox1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 235;BA.debugLine="CheckBox1.Checked = False";
mostCurrent._checkbox1.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 237;BA.debugLine="Aclock.Visible = False";
mostCurrent._vv2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 238;BA.debugLine="Button2.Visible = False";
mostCurrent._button2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 241;BA.debugLine="CheckBox1.Enabled = True";
mostCurrent._checkbox1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 243;BA.debugLine="Button1.Visible = True";
mostCurrent._button1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 244;BA.debugLine="Button1.BringToFront";
mostCurrent._button1.BringToFront();
 //BA.debugLineNum = 245;BA.debugLine="CheckBox1.SendToBack";
mostCurrent._checkbox1.SendToBack();
 //BA.debugLineNum = 246;BA.debugLine="Activity.Title = \"waut.ch! running... ! to redraw";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("waut.ch! running... ! to redraw elements"));
 //BA.debugLineNum = 247;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 249;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 24;BA.debugLine="Dim Aclock As AnalogClock";
mostCurrent._vv2 = new anywheresoftware.b4a.agraham.clocks.ChronometerWrapper.AnalogClockWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private Button2 As Button";
mostCurrent._button2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private CheckBox1 As CheckBox";
mostCurrent._checkbox1 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Dim time_I, time_II, time_III, time_IV As Long";
_time_i = 0L;
_time_ii = 0L;
_time_iii = 0L;
_time_iv = 0L;
 //BA.debugLineNum = 39;BA.debugLine="End Sub";
return "";
}
public static String  _label1_click() throws Exception{
boolean _dummy = false;
 //BA.debugLineNum = 216;BA.debugLine="Sub Label1_Click";
 //BA.debugLineNum = 217;BA.debugLine="Dim dummy As Boolean";
_dummy = false;
 //BA.debugLineNum = 218;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 219;BA.debugLine="ToastMessageShow( Rnd(1,64), True )";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 220;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 221;BA.debugLine="End Sub";
return "";
}
public static String  _panel1_click() throws Exception{
 //BA.debugLineNum = 223;BA.debugLine="Sub Panel1_Click";
 //BA.debugLineNum = 224;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 225;BA.debugLine="End Sub";
return "";
}
public static String  _panel1_touch(int _action,float _x,float _y) throws Exception{
long _random_long = 0L;
int _i = 0;
int _j = 0;
int _k = 0;
 //BA.debugLineNum = 251;BA.debugLine="Sub Panel1_Touch (Action As Int, X As Float, Y As";
 //BA.debugLineNum = 252;BA.debugLine="Dim random_long As Long";
_random_long = 0L;
 //BA.debugLineNum = 254;BA.debugLine="Dim i,j,k As Int";
_i = 0;
_j = 0;
_k = 0;
 //BA.debugLineNum = 258;BA.debugLine="If Aclock.Visible = False Then";
if (mostCurrent._vv2.getVisible()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 260;BA.debugLine="time_IV = DateTime.Now";
_time_iv = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 268;BA.debugLine="If ( time_IV > ( time_III + 30000 ) ) Then";
if ((_time_iv>(_time_iii+30000))) { 
 //BA.debugLineNum = 269;BA.debugLine="ToastMessageShow( Rnd(1,64) , True )";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 272;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 };
 //BA.debugLineNum = 276;BA.debugLine="For j = 1 To 64";
{
final int step9 = 1;
final int limit9 = (int) (64);
_j = (int) (1) ;
for (;(step9 > 0 && _j <= limit9) || (step9 < 0 && _j >= limit9) ;_j = ((int)(0 + _j + step9))  ) {
 //BA.debugLineNum = 278;BA.debugLine="For i = 1 To 64";
{
final int step10 = 1;
final int limit10 = (int) (64);
_i = (int) (1) ;
for (;(step10 > 0 && _i <= limit10) || (step10 < 0 && _i >= limit10) ;_i = ((int)(0 + _i + step10))  ) {
 //BA.debugLineNum = 279;BA.debugLine="random_long = Rnd(1,2+Abs(Action)+(Abs(X)*Abs(";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (2+anywheresoftware.b4a.keywords.Common.Abs(_action)+(anywheresoftware.b4a.keywords.Common.Abs(_x)*anywheresoftware.b4a.keywords.Common.Abs(_y)))));
 //BA.debugLineNum = 280;BA.debugLine="If(Rnd(1,64) > 30 ) Then";
if ((anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))>30)) { 
 //BA.debugLineNum = 281;BA.debugLine="random_long = Rnd(1,1+Rnd(1,922337203685477))";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (1+anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (922337203685477L)))));
 //BA.debugLineNum = 282;BA.debugLine="random_long = Rnd(1+random12,9-random12)";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv4()),(int) (9-_vv4())));
 //BA.debugLineNum = 283;BA.debugLine="random_long = Rnd(1+random12,9-random12)";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv4()),(int) (9-_vv4())));
 //BA.debugLineNum = 284;BA.debugLine="random_long = random13";
_random_long = (long) (_vv3());
 };
 }
};
 //BA.debugLineNum = 288;BA.debugLine="For i = 1 To 64";
{
final int step19 = 1;
final int limit19 = (int) (64);
_i = (int) (1) ;
for (;(step19 > 0 && _i <= limit19) || (step19 < 0 && _i >= limit19) ;_i = ((int)(0 + _i + step19))  ) {
 //BA.debugLineNum = 289;BA.debugLine="random_long = Rnd(1,2+Abs(Action)+(Abs(X)*Abs(";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (2+anywheresoftware.b4a.keywords.Common.Abs(_action)+(anywheresoftware.b4a.keywords.Common.Abs(_x)*anywheresoftware.b4a.keywords.Common.Abs(_y)))));
 //BA.debugLineNum = 290;BA.debugLine="If(Rnd(1,64) < 30 ) Then";
if ((anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))<30)) { 
 //BA.debugLineNum = 291;BA.debugLine="random_long = Rnd(1,1+Rnd(1,922337203685477))";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (1+anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (922337203685477L)))));
 //BA.debugLineNum = 292;BA.debugLine="random_long = Rnd(1+random12,9-random12)";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv4()),(int) (9-_vv4())));
 //BA.debugLineNum = 293;BA.debugLine="random_long = Rnd(1+random12,9-random12)";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv4()),(int) (9-_vv4())));
 //BA.debugLineNum = 294;BA.debugLine="random_long = random13";
_random_long = (long) (_vv3());
 };
 }
};
 //BA.debugLineNum = 300;BA.debugLine="Label1.TextSize = 18 - random13";
mostCurrent._label1.setTextSize((float) (18-_vv3()));
 //BA.debugLineNum = 301;BA.debugLine="Label1.Text = Rnd(1,64)";
mostCurrent._label1.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))));
 }
};
 //BA.debugLineNum = 305;BA.debugLine="k = 40";
_k = (int) (40);
 //BA.debugLineNum = 307;BA.debugLine="If Activity.Height > 1000 Then k = 55";
if (mostCurrent._activity.getHeight()>1000) { 
_k = (int) (55);};
 //BA.debugLineNum = 309;BA.debugLine="If Activity.Width > 1000 Then k = 55";
if (mostCurrent._activity.getWidth()>1000) { 
_k = (int) (55);};
 //BA.debugLineNum = 311;BA.debugLine="i = random12";
_i = _vv4();
 //BA.debugLineNum = 312;BA.debugLine="j = random12";
_j = _vv4();
 //BA.debugLineNum = 314;BA.debugLine="Label1.Top = Activity.Height / 2 + 2*k - (i+j)*k";
mostCurrent._label1.setTop((int) (mostCurrent._activity.getHeight()/(double)2+2*_k-(_i+_j)*_k));
 //BA.debugLineNum = 316;BA.debugLine="i = random12";
_i = _vv4();
 //BA.debugLineNum = 317;BA.debugLine="j = random12";
_j = _vv4();
 //BA.debugLineNum = 318;BA.debugLine="Label1.Left = Activity.Width / 2 + 2*k - (i+j)*k";
mostCurrent._label1.setLeft((int) (mostCurrent._activity.getWidth()/(double)2+2*_k-(_i+_j)*_k));
 //BA.debugLineNum = 320;BA.debugLine="Label1.TextSize = 18 - random13";
mostCurrent._label1.setTextSize((float) (18-_vv3()));
 //BA.debugLineNum = 321;BA.debugLine="Label1.Text = Rnd(1,64)";
mostCurrent._label1.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))));
 };
 //BA.debugLineNum = 325;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
wautch_service._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
public static int  _vv4() throws Exception{
 //BA.debugLineNum = 41;BA.debugLine="Sub random12() As Int";
 //BA.debugLineNum = 42;BA.debugLine="If(Rnd(1,64) <= 32 ) Then";
if ((anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))<=32)) { 
 //BA.debugLineNum = 43;BA.debugLine="Return 2";
if (true) return (int) (2);
 }else {
 //BA.debugLineNum = 45;BA.debugLine="Return 1";
if (true) return (int) (1);
 };
 //BA.debugLineNum = 47;BA.debugLine="End Sub";
return 0;
}
public static int  _vv3() throws Exception{
int _i = 0;
 //BA.debugLineNum = 49;BA.debugLine="Sub random13() As Int";
 //BA.debugLineNum = 51;BA.debugLine="Dim i As Int";
_i = 0;
 //BA.debugLineNum = 53;BA.debugLine="i = Rnd(1,64)";
_i = anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64));
 //BA.debugLineNum = 55;BA.debugLine="If(i > 0 ) And (i <= 18 )  Then";
if ((_i>0) && (_i<=18)) { 
 //BA.debugLineNum = 56;BA.debugLine="Return 2";
if (true) return (int) (2);
 };
 //BA.debugLineNum = 59;BA.debugLine="If(i > 18 ) And (i <= 36 )  Then";
if ((_i>18) && (_i<=36)) { 
 //BA.debugLineNum = 60;BA.debugLine="Return 3";
if (true) return (int) (3);
 };
 //BA.debugLineNum = 63;BA.debugLine="If(i > 36 ) And (i <= 64 )  Then";
if ((_i>36) && (_i<=64)) { 
 //BA.debugLineNum = 64;BA.debugLine="Return 1";
if (true) return (int) (1);
 };
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return 0;
}
public static String  _setrandom_local() throws Exception{
int _i = 0;
int _j = 0;
long _random = 0L;
 //BA.debugLineNum = 69;BA.debugLine="Sub SetRandom_Local()";
 //BA.debugLineNum = 74;BA.debugLine="Dim i,j As Int";
_i = 0;
_j = 0;
 //BA.debugLineNum = 75;BA.debugLine="Dim random As Long";
_random = 0L;
 //BA.debugLineNum = 77;BA.debugLine="For j = 1 To 64";
{
final int step3 = 1;
final int limit3 = (int) (64);
_j = (int) (1) ;
for (;(step3 > 0 && _j <= limit3) || (step3 < 0 && _j >= limit3) ;_j = ((int)(0 + _j + step3))  ) {
 //BA.debugLineNum = 79;BA.debugLine="For i = 1 To 64";
{
final int step4 = 1;
final int limit4 = (int) (64);
_i = (int) (1) ;
for (;(step4 > 0 && _i <= limit4) || (step4 < 0 && _i >= limit4) ;_i = ((int)(0 + _i + step4))  ) {
 //BA.debugLineNum = 80;BA.debugLine="If(Rnd(1,64) > 30 ) Then";
if ((anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))>30)) { 
 //BA.debugLineNum = 81;BA.debugLine="random = Rnd(1,1+Rnd(1,922337203685477))";
_random = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (1+anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (922337203685477L)))));
 //BA.debugLineNum = 82;BA.debugLine="random = Rnd(1+random12,9-random12)";
_random = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv4()),(int) (9-_vv4())));
 //BA.debugLineNum = 83;BA.debugLine="random = Rnd(1+random12,9-random12)";
_random = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv4()),(int) (9-_vv4())));
 //BA.debugLineNum = 84;BA.debugLine="random = random13";
_random = (long) (_vv3());
 };
 }
};
 //BA.debugLineNum = 89;BA.debugLine="For i = 1 To 64";
{
final int step12 = 1;
final int limit12 = (int) (64);
_i = (int) (1) ;
for (;(step12 > 0 && _i <= limit12) || (step12 < 0 && _i >= limit12) ;_i = ((int)(0 + _i + step12))  ) {
 //BA.debugLineNum = 90;BA.debugLine="If(Rnd(1,64) < 30 ) Then";
if ((anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))<30)) { 
 //BA.debugLineNum = 91;BA.debugLine="random = Rnd(1,1+Rnd(1,922337203685477))";
_random = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (1+anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (922337203685477L)))));
 //BA.debugLineNum = 92;BA.debugLine="random = Rnd(1+random12,9-random12)";
_random = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv4()),(int) (9-_vv4())));
 //BA.debugLineNum = 93;BA.debugLine="random = Rnd(1+random12,9-random12)";
_random = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv4()),(int) (9-_vv4())));
 //BA.debugLineNum = 94;BA.debugLine="random = random13";
_random = (long) (_vv3());
 //BA.debugLineNum = 95;BA.debugLine="Label1.TextSize = 18 - random13";
mostCurrent._label1.setTextSize((float) (18-_vv3()));
 //BA.debugLineNum = 96;BA.debugLine="Label1.Text = Rnd(1,64)";
mostCurrent._label1.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))));
 };
 }
};
 //BA.debugLineNum = 100;BA.debugLine="Label1.TextSize = 18 - random13";
mostCurrent._label1.setTextSize((float) (18-_vv3()));
 //BA.debugLineNum = 101;BA.debugLine="Label1.Text = Rnd(1,64)";
mostCurrent._label1.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))));
 //BA.debugLineNum = 103;BA.debugLine="i = random13";
_i = _vv3();
 //BA.debugLineNum = 104;BA.debugLine="Label1.Top = Activity.Height / 2";
mostCurrent._label1.setTop((int) (mostCurrent._activity.getHeight()/(double)2));
 //BA.debugLineNum = 106;BA.debugLine="i = random13";
_i = _vv3();
 //BA.debugLineNum = 107;BA.debugLine="Label1.Left = Activity.Width / 2 - 40";
mostCurrent._label1.setLeft((int) (mostCurrent._activity.getWidth()/(double)2-40));
 }
};
 //BA.debugLineNum = 111;BA.debugLine="Label1.TextSize = 18 - random13";
mostCurrent._label1.setTextSize((float) (18-_vv3()));
 //BA.debugLineNum = 112;BA.debugLine="Label1.Text = Rnd(1,64)";
mostCurrent._label1.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))));
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static String  _startservice_local() throws Exception{
 //BA.debugLineNum = 159;BA.debugLine="Sub StartService_Local()";
 //BA.debugLineNum = 161;BA.debugLine="End Sub";
return "";
}
}
