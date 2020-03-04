B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Service
Version=7.3
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: True	
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

	Dim rc As RootCmd
'	Dim fs As MLfiles
	
'	Dim PE As PhoneEvents

'	Dim BatteryLevel As Int
'	Dim BatteryCharging As Boolean
	
End Sub

'Sub PE_BatteryChanged(Level As Int, Scale As Int, Plugged As Boolean, Intent As Intent)
'	BatteryLevel = Level
'	BatteryCharging = Plugged
'	'Log("Level: " & Level & "%, Scale: " & Scale & ", Plugged: " & Plugged)
'End Sub

Sub Service_Create
	StartService(Schedule)
End Sub


Sub myStart()

		File.MakeDir(File.DirInternal, "bin")
		File.Copy(File.DirAssets,"busybox",File.DirInternal,"/bin/busybox")
		
		File.Copy(File.DirAssets,"cb_runhaveged",File.DirInternal,"/bin/cb_runhaveged")
		File.Copy(File.DirAssets,"haveged",File.DirInternal,"/bin/haveged")
		File.Copy(File.DirAssets,"sqlite3",File.DirInternal,"/bin/sqlite3")
		File.Copy(File.DirAssets,"cb.sh",File.DirInternal,"/bin/cb.sh")
		File.Copy(File.DirAssets,"cb_init.sh",File.DirInternal,"/bin/cb_init.sh")
		File.Copy(File.DirAssets,"cb_io.sh",File.DirInternal,"/bin/cb_io.sh")
		File.Copy(File.DirAssets,"cb_networking.sh",File.DirInternal,"/bin/cb_networking.sh")
		File.Copy(File.DirAssets,"cb_weekly.sh",File.DirInternal,"/bin/cb_weekly.sh")

'		fs.chmod(File.DirInternal & "/bin/busybox",755)

''	ph.Shell("toolbox", Array As String("chmod", "-R", "755", File.DirInternal & "/bin"),Null,Null)
''	ph.Shell("toolbox", Array As String("chmod", "755", File.DirInternal & "/bin/busybox"),Null,Null)
	
'	fs.chmod(File.DirInternal & "/bin/cb_runhaveged",755)
'	fs.chmod(File.DirInternal & "/bin/haveged",755)
'	fs.chmod(File.DirInternal & "/bin/sqlite3",755)
'	fs.chmod(File.DirInternal & "/bin/cb.sh",755)
'	fs.chmod(File.DirInternal & "/bin/cb_init.sh",755)
'	fs.chmod(File.DirInternal & "/bin/cb_io.sh",755)
'	fs.chmod(File.DirInternal & "/bin/cb_networking.sh",755)
'	fs.chmod(File.DirInternal & "/bin/cb_weekly.sh",755)

	If rc.haveRoot Then		
		rc.execRootCmdSilent( "/system/bin/toolbox chmod -R 755 " & File.DirInternal & "/bin" )
		rc.execRootCmdSilent( "/system/bin/toolbox chmod 755 " & File.DirInternal & "/bin/busybox" )
		
		rc.execRootCmdSilent(File.DirInternal & "/bin/busybox chmod -R 755 " & File.DirInternal & "/bin")
		rc.execRootCmdSilent(File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/busybox")
			
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb_runhaveged" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/haveged" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/sqlite3" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb.sh" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb_init.sh" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb_io.sh" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb_networking.sh" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb_weekly.sh" )
	End If

	Dim hour As Int
	
	hour = DateTime.GetHour(DateTime.Now)

	If hour = 3 Then
		If rc.haveRoot Then
'			ToastMessageShow("run...",True)
'			rc.execRootCmdSilent( File.DirInternal & "/bin/busybox setsid " & File.DirInternal & "/bin/busybox sh " & File.DirInternal & "/bin/cb.sh RUN FORCE" )

			rc.execRootCmdSilent( File.DirInternal & "/bin/busybox setsid " & File.DirInternal & "/bin/busybox sh " & File.DirInternal & "/bin/cb_weekly.sh RUN FORCE" )
							
		End If	
	End If
	
End Sub

Sub Service_Start (StartingIntent As Intent)

	Dim hour As Int
	Dim timeofday As Long
	
	hour = DateTime.GetHour(DateTime.Now)
	
	timeofday = DateTime.Now + ( ( 27 - hour ) * 3600000 )

	StartServiceAt("", timeofday , True)

	myStart

	Service.StopAutomaticForeground 'Call this when the background task completes (if there is one)
	
End Sub

Sub Service_Destroy

End Sub
