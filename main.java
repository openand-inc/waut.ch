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
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
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
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
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
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        if (mostCurrent != null)
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
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
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
public anywheresoftware.b4a.agraham.clocks.ChronometerWrapper.AnalogClockWrapper _v7 = null;
public ice.rootcmd.RootCmd _rc_main = null;
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
public ch.waut.schedule _vv1 = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _aclock_click() throws Exception{
 //BA.debugLineNum = 204;BA.debugLine="Sub Aclock_Click";
 //BA.debugLineNum = 205;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 206;BA.debugLine="ToastMessageShow( DateTime.Time(DateTime.Now), Fa";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow())),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 207;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 208;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _isfirst) throws Exception{
 //BA.debugLineNum = 114;BA.debugLine="Sub Activity_Create(isFirst As Boolean)";
 //BA.debugLineNum = 118;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 120;BA.debugLine="Activity.Title = \"waut.ch! running... ? for help,";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("waut.ch! running... ? for help, space to clear page"));
 //BA.debugLineNum = 122;BA.debugLine="Aclock.Initialize(\"Aclock\")";
mostCurrent._v7.Initialize(mostCurrent.activityBA,"Aclock");
 //BA.debugLineNum = 124;BA.debugLine="Activity.AddView(Aclock, 0dip, 0dip, 100dip, 100d";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._v7.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)));
 //BA.debugLineNum = 127;BA.debugLine="Aclock.Color = Colors.Transparent";
mostCurrent._v7.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 129;BA.debugLine="Aclock.Visible = True";
mostCurrent._v7.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 130;BA.debugLine="Panel1.SendToBack";
mostCurrent._panel1.SendToBack();
 //BA.debugLineNum = 132;BA.debugLine="Label1.Top = Activity.Height / 2";
mostCurrent._label1.setTop((int) (mostCurrent._activity.getHeight()/(double)2));
 //BA.debugLineNum = 133;BA.debugLine="Label1.Left = Activity.Width / 2 - 40";
mostCurrent._label1.setLeft((int) (mostCurrent._activity.getWidth()/(double)2-40));
 //BA.debugLineNum = 135;BA.debugLine="Label1.BringToFront";
mostCurrent._label1.BringToFront();
 //BA.debugLineNum = 137;BA.debugLine="rc_main.haveRoot";
mostCurrent._rc_main.haveRoot();
 //BA.debugLineNum = 141;BA.debugLine="Label1.TextSize = 18 - random13";
mostCurrent._label1.setTextSize((float) (18-_v0()));
 //BA.debugLineNum = 142;BA.debugLine="Button2.TextSize = 14";
mostCurrent._button2.setTextSize((float) (14));
 //BA.debugLineNum = 144;BA.debugLine="If rc_main.haveRoot = False Then";
if (mostCurrent._rc_main.haveRoot()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 146;BA.debugLine="ToastMessageShow(\"root not found...\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("root not found..."),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 149;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 151;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 153;BA.debugLine="StartServiceAt(wautch_service,DateTime.Now, True)";
anywheresoftware.b4a.keywords.Common.StartServiceAt(processBA,(Object)(mostCurrent._wautch_service.getObject()),anywheresoftware.b4a.keywords.Common.DateTime.getNow(),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 155;BA.debugLine="StartServiceAt(Schedule,DateTime.Now + 1 * 1000,";
anywheresoftware.b4a.keywords.Common.StartServiceAt(processBA,(Object)(mostCurrent._vv1.getObject()),(long) (anywheresoftware.b4a.keywords.Common.DateTime.getNow()+1*1000),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 157;BA.debugLine="time_I = DateTime.Now";
_time_i = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 158;BA.debugLine="time_II = 0";
_time_ii = (long) (0);
 //BA.debugLineNum = 159;BA.debugLine="time_III = DateTime.Now";
_time_iii = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 160;BA.debugLine="time_IV = 0";
_time_iv = (long) (0);
 //BA.debugLineNum = 170;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 176;BA.debugLine="Sub Activity_Pause(UserClosed As Boolean)";
 //BA.debugLineNum = 179;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 181;BA.debugLine="ToastMessageShow( Rnd(1,64) , True )";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 182;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 193;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 195;BA.debugLine="Sub Activity_Resume()";
 //BA.debugLineNum = 197;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 198;BA.debugLine="Label1.TextSize = 18 - random13";
mostCurrent._label1.setTextSize((float) (18-_v0()));
 //BA.debugLineNum = 199;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 202;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 351;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 352;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 354;BA.debugLine="CheckBox1.Enabled = True";
mostCurrent._checkbox1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 355;BA.debugLine="CheckBox1.Visible = True";
mostCurrent._checkbox1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 357;BA.debugLine="CheckBox1.Checked = False";
mostCurrent._checkbox1.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 359;BA.debugLine="Aclock.Visible = True";
mostCurrent._v7.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 360;BA.debugLine="Button2.Visible = True";
mostCurrent._button2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 362;BA.debugLine="Button1.Visible = False";
mostCurrent._button1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 363;BA.debugLine="Button1.SendToBack";
mostCurrent._button1.SendToBack();
 //BA.debugLineNum = 364;BA.debugLine="CheckBox1.BringToFront";
mostCurrent._checkbox1.BringToFront();
 //BA.debugLineNum = 366;BA.debugLine="Activity.Title = \"waut.ch! running... ? for help,";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("waut.ch! running... ? for help, space to clear page"));
 //BA.debugLineNum = 367;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 369;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
 //BA.debugLineNum = 210;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 212;BA.debugLine="Button2.Enabled = False";
mostCurrent._button2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 214;BA.debugLine="time_I = DateTime.Now";
_time_i = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 216;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 218;BA.debugLine="If ( time_I > ( time_II + 1500 ) ) Then";
if ((_time_i>(_time_ii+1500))) { 
 //BA.debugLineNum = 219;BA.debugLine="ToastMessageShow( \"utility for background calibr";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("utility for background calibration, curation and tuning of the device towards an intuitive interface. subsystems being battery, entropy, encryption, disk, cpu, memory, filesystem, ui, scheduler, and network, all safe and open source technology. presented in this gaming metric format with infinite feedback and an interestingly assymetric chance. scribble anywhere, check in some stress, or find the 8!"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 220;BA.debugLine="time_II = DateTime.Now";
_time_ii = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 };
 //BA.debugLineNum = 223;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 225;BA.debugLine="Button2.Enabled = True";
mostCurrent._button2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 227;BA.debugLine="End Sub";
return "";
}
public static String  _checkbox1_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 240;BA.debugLine="Sub CheckBox1_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 243;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 245;BA.debugLine="CheckBox1.Enabled = False";
mostCurrent._checkbox1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 246;BA.debugLine="CheckBox1.Visible = False";
mostCurrent._checkbox1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 248;BA.debugLine="CheckBox1.Checked = False";
mostCurrent._checkbox1.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 250;BA.debugLine="Aclock.Visible = False";
mostCurrent._v7.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 251;BA.debugLine="Button2.Visible = False";
mostCurrent._button2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 254;BA.debugLine="CheckBox1.Enabled = True";
mostCurrent._checkbox1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 256;BA.debugLine="Button1.Visible = True";
mostCurrent._button1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 257;BA.debugLine="Button1.BringToFront";
mostCurrent._button1.BringToFront();
 //BA.debugLineNum = 258;BA.debugLine="CheckBox1.SendToBack";
mostCurrent._checkbox1.SendToBack();
 //BA.debugLineNum = 259;BA.debugLine="Activity.Title = \"waut.ch! running... ! to redraw";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("waut.ch! running... ! to redraw elements"));
 //BA.debugLineNum = 260;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 262;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 24;BA.debugLine="Dim Aclock As AnalogClock";
mostCurrent._v7 = new anywheresoftware.b4a.agraham.clocks.ChronometerWrapper.AnalogClockWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim rc_main As RootCmd";
mostCurrent._rc_main = new ice.rootcmd.RootCmd();
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
 //BA.debugLineNum = 229;BA.debugLine="Sub Label1_Click";
 //BA.debugLineNum = 230;BA.debugLine="Dim dummy As Boolean";
_dummy = false;
 //BA.debugLineNum = 231;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 232;BA.debugLine="ToastMessageShow( Rnd(1,64), True )";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 233;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 234;BA.debugLine="End Sub";
return "";
}
public static String  _panel1_click() throws Exception{
 //BA.debugLineNum = 236;BA.debugLine="Sub Panel1_Click";
 //BA.debugLineNum = 237;BA.debugLine="SetRandom_Local";
_setrandom_local();
 //BA.debugLineNum = 238;BA.debugLine="End Sub";
return "";
}
public static void  _panel1_touch(int _action,float _x,float _y) throws Exception{
ResumableSub_Panel1_Touch rsub = new ResumableSub_Panel1_Touch(null,_action,_x,_y);
rsub.resume(processBA, null);
}
public static class ResumableSub_Panel1_Touch extends BA.ResumableSub {
public ResumableSub_Panel1_Touch(ch.waut.main parent,int _action,float _x,float _y) {
this.parent = parent;
this._action = _action;
this._x = _x;
this._y = _y;
}
ch.waut.main parent;
int _action;
float _x;
float _y;
long _random_long = 0L;
int _i = 0;
int _j = 0;
int _k = 0;
int step9;
int limit9;
int step10;
int limit10;
int step19;
int limit19;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 270;BA.debugLine="Dim random_long As Long";
_random_long = 0L;
 //BA.debugLineNum = 272;BA.debugLine="Dim i,j,k As Int";
_i = 0;
_j = 0;
_k = 0;
 //BA.debugLineNum = 276;BA.debugLine="If Aclock.Visible = False Then";
if (true) break;

case 1:
//if
this.state = 38;
if (parent.mostCurrent._v7.getVisible()==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 278;BA.debugLine="time_IV = DateTime.Now";
parent._time_iv = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 286;BA.debugLine="If ( time_IV > ( time_III + 15000 ) ) Then";
if (true) break;

case 4:
//if
this.state = 7;
if ((parent._time_iv>(parent._time_iii+15000))) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 287;BA.debugLine="ToastMessageShow( Rnd(1,64) , True )";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 290;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;
;
 //BA.debugLineNum = 294;BA.debugLine="For j = 1 To 64";

case 7:
//for
this.state = 25;
step9 = 1;
limit9 = (int) (64);
_j = (int) (1) ;
this.state = 39;
if (true) break;

case 39:
//C
this.state = 25;
if ((step9 > 0 && _j <= limit9) || (step9 < 0 && _j >= limit9)) this.state = 9;
if (true) break;

case 40:
//C
this.state = 39;
_j = ((int)(0 + _j + step9)) ;
if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 296;BA.debugLine="For i = 1 To 64";
if (true) break;

case 10:
//for
this.state = 17;
step10 = 1;
limit10 = (int) (64);
_i = (int) (1) ;
this.state = 41;
if (true) break;

case 41:
//C
this.state = 17;
if ((step10 > 0 && _i <= limit10) || (step10 < 0 && _i >= limit10)) this.state = 12;
if (true) break;

case 42:
//C
this.state = 41;
_i = ((int)(0 + _i + step10)) ;
if (true) break;

case 12:
//C
this.state = 13;
 //BA.debugLineNum = 297;BA.debugLine="random_long = Rnd(1,2+Abs(Action)+(Abs(X)*Abs(";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (2+anywheresoftware.b4a.keywords.Common.Abs(_action)+(anywheresoftware.b4a.keywords.Common.Abs(_x)*anywheresoftware.b4a.keywords.Common.Abs(_y)))));
 //BA.debugLineNum = 298;BA.debugLine="If(Rnd(1,64) > 30 ) Then";
if (true) break;

case 13:
//if
this.state = 16;
if ((anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))>30)) { 
this.state = 15;
}if (true) break;

case 15:
//C
this.state = 16;
 //BA.debugLineNum = 299;BA.debugLine="random_long = Rnd(1,1+Rnd(1,922337203685477))";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (1+anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (922337203685477L)))));
 //BA.debugLineNum = 300;BA.debugLine="random_long = Rnd(1+random12,9-random12)";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv2()),(int) (9-_vv2())));
 //BA.debugLineNum = 301;BA.debugLine="random_long = Rnd(1+random12,9-random12)";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv2()),(int) (9-_vv2())));
 //BA.debugLineNum = 302;BA.debugLine="random_long = random13";
_random_long = (long) (_v0());
 if (true) break;

case 16:
//C
this.state = 42;
;
 if (true) break;
if (true) break;
;
 //BA.debugLineNum = 306;BA.debugLine="For i = 1 To 64";

case 17:
//for
this.state = 24;
step19 = 1;
limit19 = (int) (64);
_i = (int) (1) ;
this.state = 43;
if (true) break;

case 43:
//C
this.state = 24;
if ((step19 > 0 && _i <= limit19) || (step19 < 0 && _i >= limit19)) this.state = 19;
if (true) break;

case 44:
//C
this.state = 43;
_i = ((int)(0 + _i + step19)) ;
if (true) break;

case 19:
//C
this.state = 20;
 //BA.debugLineNum = 307;BA.debugLine="random_long = Rnd(1,2+Abs(Action)+(Abs(X)*Abs(";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (2+anywheresoftware.b4a.keywords.Common.Abs(_action)+(anywheresoftware.b4a.keywords.Common.Abs(_x)*anywheresoftware.b4a.keywords.Common.Abs(_y)))));
 //BA.debugLineNum = 308;BA.debugLine="If(Rnd(1,64) < 30 ) Then";
if (true) break;

case 20:
//if
this.state = 23;
if ((anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))<30)) { 
this.state = 22;
}if (true) break;

case 22:
//C
this.state = 23;
 //BA.debugLineNum = 309;BA.debugLine="random_long = Rnd(1,1+Rnd(1,922337203685477))";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (1+anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (922337203685477L)))));
 //BA.debugLineNum = 310;BA.debugLine="random_long = Rnd(1+random12,9-random12)";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv2()),(int) (9-_vv2())));
 //BA.debugLineNum = 311;BA.debugLine="random_long = Rnd(1+random12,9-random12)";
_random_long = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv2()),(int) (9-_vv2())));
 //BA.debugLineNum = 312;BA.debugLine="random_long = random13";
_random_long = (long) (_v0());
 if (true) break;

case 23:
//C
this.state = 44;
;
 if (true) break;
if (true) break;

case 24:
//C
this.state = 40;
;
 //BA.debugLineNum = 318;BA.debugLine="Label1.TextSize = 18 - random13";
parent.mostCurrent._label1.setTextSize((float) (18-_v0()));
 //BA.debugLineNum = 319;BA.debugLine="S2";
_vv3();
 //BA.debugLineNum = 320;BA.debugLine="Sleep(30)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (30));
this.state = 45;
return;
case 45:
//C
this.state = 40;
;
 //BA.debugLineNum = 321;BA.debugLine="Label1.Text = Rnd(1,64)";
parent.mostCurrent._label1.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))));
 if (true) break;
if (true) break;

case 25:
//C
this.state = 26;
;
 //BA.debugLineNum = 325;BA.debugLine="k = 40";
_k = (int) (40);
 //BA.debugLineNum = 327;BA.debugLine="If Activity.Height > 1000 Then k = 75";
if (true) break;

case 26:
//if
this.state = 31;
if (parent.mostCurrent._activity.getHeight()>1000) { 
this.state = 28;
;}if (true) break;

case 28:
//C
this.state = 31;
_k = (int) (75);
if (true) break;

case 31:
//C
this.state = 32;
;
 //BA.debugLineNum = 329;BA.debugLine="If Activity.Width > 1000 Then k = 75";
if (true) break;

case 32:
//if
this.state = 37;
if (parent.mostCurrent._activity.getWidth()>1000) { 
this.state = 34;
;}if (true) break;

case 34:
//C
this.state = 37;
_k = (int) (75);
if (true) break;

case 37:
//C
this.state = 38;
;
 //BA.debugLineNum = 331;BA.debugLine="i = random13";
_i = _v0();
 //BA.debugLineNum = 332;BA.debugLine="j = random13";
_j = _v0();
 //BA.debugLineNum = 335;BA.debugLine="Label1.Top = Activity.Height / 2 + 3*k - (i+j)*k";
parent.mostCurrent._label1.setTop((int) (parent.mostCurrent._activity.getHeight()/(double)2+3*_k-(_i+_j)*_k));
 //BA.debugLineNum = 337;BA.debugLine="i = random13";
_i = _v0();
 //BA.debugLineNum = 338;BA.debugLine="j = random13";
_j = _v0();
 //BA.debugLineNum = 339;BA.debugLine="Label1.Left = Activity.Width / 2 + 3*k - (i+j)*k";
parent.mostCurrent._label1.setLeft((int) (parent.mostCurrent._activity.getWidth()/(double)2+3*_k-(_i+_j)*_k));
 //BA.debugLineNum = 341;BA.debugLine="Label1.TextSize = 22 - 2*random13";
parent.mostCurrent._label1.setTextSize((float) (22-2*_v0()));
 //BA.debugLineNum = 343;BA.debugLine="S2";
_vv3();
 //BA.debugLineNum = 344;BA.debugLine="Sleep(30)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (30));
this.state = 46;
return;
case 46:
//C
this.state = 38;
;
 //BA.debugLineNum = 345;BA.debugLine="Label1.Text = Rnd(1,64)";
parent.mostCurrent._label1.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))));
 if (true) break;

case 38:
//C
this.state = -1;
;
 //BA.debugLineNum = 349;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
wautch_service._process_globals();
schedule._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
public static int  _vv2() throws Exception{
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
public static int  _v0() throws Exception{
 //BA.debugLineNum = 49;BA.debugLine="Sub random13() As Int";
 //BA.debugLineNum = 51;BA.debugLine="If(Rnd(1,64) > 0 ) And (Rnd(1,64) <= 18 )  Then";
if ((anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))>0) && (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))<=18)) { 
 //BA.debugLineNum = 52;BA.debugLine="Return 2";
if (true) return (int) (2);
 };
 //BA.debugLineNum = 55;BA.debugLine="If(Rnd(1,64) > 18 ) And (Rnd(1,64) <= 36 )  Then";
if ((anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))>18) && (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))<=36)) { 
 //BA.debugLineNum = 56;BA.debugLine="Return 3";
if (true) return (int) (3);
 };
 //BA.debugLineNum = 59;BA.debugLine="If(Rnd(1,64) > 36 ) And (Rnd(1,64) <= 64 )  Then";
if ((anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))>36) && (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))<=64)) { 
 //BA.debugLineNum = 60;BA.debugLine="Return 1";
if (true) return (int) (1);
 };
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return 0;
}
public static void  _vv3() throws Exception{
ResumableSub_S2 rsub = new ResumableSub_S2(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_S2 extends BA.ResumableSub {
public ResumableSub_S2(ch.waut.main parent) {
this.parent = parent;
}
ch.waut.main parent;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = -1;
 //BA.debugLineNum = 265;BA.debugLine="Sleep(30)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (30));
this.state = 1;
return;
case 1:
//C
this.state = -1;
;
 //BA.debugLineNum = 266;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _setrandom_local() throws Exception{
int _i = 0;
int _j = 0;
long _random = 0L;
 //BA.debugLineNum = 65;BA.debugLine="Sub SetRandom_Local()";
 //BA.debugLineNum = 70;BA.debugLine="Dim i,j As Int";
_i = 0;
_j = 0;
 //BA.debugLineNum = 71;BA.debugLine="Dim random As Long";
_random = 0L;
 //BA.debugLineNum = 73;BA.debugLine="For j = 1 To 64";
{
final int step3 = 1;
final int limit3 = (int) (64);
_j = (int) (1) ;
for (;_j <= limit3 ;_j = _j + step3 ) {
 //BA.debugLineNum = 75;BA.debugLine="For i = 1 To 64";
{
final int step4 = 1;
final int limit4 = (int) (64);
_i = (int) (1) ;
for (;_i <= limit4 ;_i = _i + step4 ) {
 //BA.debugLineNum = 76;BA.debugLine="If(Rnd(1,64) > 30 ) Then";
if ((anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))>30)) { 
 //BA.debugLineNum = 77;BA.debugLine="random = Rnd(1,1+Rnd(1,922337203685477))";
_random = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (1+anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (922337203685477L)))));
 //BA.debugLineNum = 78;BA.debugLine="random = Rnd(1+random12,9-random12)";
_random = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv2()),(int) (9-_vv2())));
 //BA.debugLineNum = 79;BA.debugLine="random = Rnd(1+random12,9-random12)";
_random = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv2()),(int) (9-_vv2())));
 //BA.debugLineNum = 80;BA.debugLine="random = random13";
_random = (long) (_v0());
 };
 }
};
 //BA.debugLineNum = 85;BA.debugLine="For i = 1 To 64";
{
final int step12 = 1;
final int limit12 = (int) (64);
_i = (int) (1) ;
for (;_i <= limit12 ;_i = _i + step12 ) {
 //BA.debugLineNum = 86;BA.debugLine="If(Rnd(1,64) < 30 ) Then";
if ((anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))<30)) { 
 //BA.debugLineNum = 87;BA.debugLine="random = Rnd(1,1+Rnd(1,922337203685477))";
_random = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (1+anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (922337203685477L)))));
 //BA.debugLineNum = 88;BA.debugLine="random = Rnd(1+random12,9-random12)";
_random = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv2()),(int) (9-_vv2())));
 //BA.debugLineNum = 89;BA.debugLine="random = Rnd(1+random12,9-random12)";
_random = (long) (anywheresoftware.b4a.keywords.Common.Rnd((int) (1+_vv2()),(int) (9-_vv2())));
 //BA.debugLineNum = 90;BA.debugLine="random = random13";
_random = (long) (_v0());
 //BA.debugLineNum = 91;BA.debugLine="Label1.TextSize = 18 - random13";
mostCurrent._label1.setTextSize((float) (18-_v0()));
 //BA.debugLineNum = 93;BA.debugLine="Label1.Text = Rnd(1,64)";
mostCurrent._label1.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))));
 };
 }
};
 //BA.debugLineNum = 97;BA.debugLine="Label1.TextSize = 18 - random13";
mostCurrent._label1.setTextSize((float) (18-_v0()));
 //BA.debugLineNum = 98;BA.debugLine="Label1.Text = Rnd(1,64)";
mostCurrent._label1.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))));
 //BA.debugLineNum = 100;BA.debugLine="i = random13";
_i = _v0();
 //BA.debugLineNum = 101;BA.debugLine="Label1.Top = Activity.Height / 2";
mostCurrent._label1.setTop((int) (mostCurrent._activity.getHeight()/(double)2));
 //BA.debugLineNum = 103;BA.debugLine="i = random13";
_i = _v0();
 //BA.debugLineNum = 104;BA.debugLine="Label1.Left = Activity.Width / 2 - 40";
mostCurrent._label1.setLeft((int) (mostCurrent._activity.getWidth()/(double)2-40));
 }
};
 //BA.debugLineNum = 108;BA.debugLine="Label1.TextSize = 18 - random13";
mostCurrent._label1.setTextSize((float) (18-_v0()));
 //BA.debugLineNum = 110;BA.debugLine="Label1.Text = Rnd(1,64)";
mostCurrent._label1.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (64))));
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _startservice_local() throws Exception{
 //BA.debugLineNum = 172;BA.debugLine="Sub StartService_Local()";
 //BA.debugLineNum = 174;BA.debugLine="End Sub";
return "";
}
}
