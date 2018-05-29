Type=Service
Version=7.3
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: True	
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

	Dim rc As RootCmd
	Dim fs As MLfiles
	
	Dim PE As PhoneEvents

	Dim BatteryLevel As Int
	Dim BatteryCharging As Boolean
	
End Sub

Sub PE_BatteryChanged(Level As Int, Scale As Int, Plugged As Boolean, Intent As Intent)
	BatteryLevel = Level
	BatteryCharging = Plugged
	'Log("Level: " & Level & "%, Scale: " & Scale & ", Plugged: " & Plugged)
End Sub

Sub Service_Create
	If rc.haveRoot Then
		File.MakeDir(File.DirInternal, "bin")
		File.Copy(File.DirAssets,"busybox",File.DirInternal,"/bin/busybox")
		fs.chmod(File.DirInternal & "/bin/busybox",755)
		rc.execRootCmdSilent( "/system/bin/toolbox chmod -R 755 " & File.DirInternal & "/bin" )
		rc.execRootCmdSilent( "/system/bin/toolbox chmod 755 " & File.DirInternal & "/bin/busybox" )
		
		File.Copy(File.DirAssets,"CB_RunHaveged",File.DirInternal,"/bin/CB_RunHaveged")
		File.Copy(File.DirAssets,"haveged",File.DirInternal,"/bin/haveged")
		File.Copy(File.DirAssets,"sqlite3",File.DirInternal,"/bin/sqlite3")
		File.Copy(File.DirAssets,"cb.sh",File.DirInternal,"/bin/cb.sh")
		File.Copy(File.DirAssets,"cb_init.sh",File.DirInternal,"/bin/cb_init.sh")
		File.Copy(File.DirAssets,"cb_io.sh",File.DirInternal,"/bin/cb_io.sh")
		File.Copy(File.DirAssets,"cb_networking.sh",File.DirInternal,"/bin/cb_networking.sh")
		File.Copy(File.DirAssets,"cb_weekly.sh",File.DirInternal,"/bin/cb_weekly.sh")
		File.Copy(File.DirAssets,"cb_weekly.sh",File.DirInternal,"/bin/cb_reboot.sh")
		File.Copy(File.DirAssets,"cb_weekly.sh",File.DirInternal,"/bin/cb_sync.sh")
		
		rc.execRootCmdSilent("/system/bin/toolbox chmod -R 755 " & File.DirInternal & "/bin")
		rc.execRootCmdSilent("/system/bin/toolbox chmod 755 " & File.DirInternal & "/bin/busybox")
		
		fs.chmod(File.DirInternal & "/bin/CB_RunHaveged",755)
		fs.chmod(File.DirInternal & "/bin/haveged",755)
		fs.chmod(File.DirInternal & "/bin/sqlite3",755)
		fs.chmod(File.DirInternal & "/bin/cb.sh",755)
		fs.chmod(File.DirInternal & "/bin/cb_init.sh",755)
		fs.chmod(File.DirInternal & "/bin/cb_io.sh",755)
		fs.chmod(File.DirInternal & "/bin/cb_networking.sh",755)
		fs.chmod(File.DirInternal & "/bin/cb_weekly.sh",755)
		fs.chmod(File.DirInternal & "/bin/cb_reboot.sh",755)
		fs.chmod(File.DirInternal & "/bin/cb_sync.sh",755)
		
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/CB_RunHaveged" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/haveged" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/sqlite3" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb.sh" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb_init.sh" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb_io.sh" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb_networking.sh" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb_weekly.sh" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb_reboot.sh" )
		rc.execRootCmdSilent( File.DirInternal & "/bin/busybox chmod 755 " & File.DirInternal & "/bin/cb_sync.sh" )

'fs.RootCmd
		rc.execRootCmd( File.DirInternal & "/bin/busybox setsid " & File.DirInternal & "/bin/busybox sh " & File.DirInternal & "/bin/cb.sh RUN FORCE" )
		rc.execRootCmd( File.DirInternal & "/bin/busybox setsid " & File.DirInternal & "/bin/busybox sh " & File.DirInternal & "/bin/cb_io.sh RUN FORCE" )
		rc.execRootCmd( File.DirInternal & "/bin/busybox setsid " & File.DirInternal & "/bin/busybox sh " & File.DirInternal & "/bin/cb_networking.sh RUN FORCE" )
'#If MAINTENANCE and Not(ONETIME)		
'		rc.execRootCmd( File.DirInternal & "/bin/busybox setsid " & File.DirInternal & "/bin/busybox sh " & File.DirInternal & "/bin/cb_init.sh RUN FORCE" )
'#Else
'		rc.execRootCmd( File.DirInternal & "/bin/busybox setsid " & File.DirInternal & "/bin/busybox sh " & File.DirInternal & "/bin/cb_init.sh RUN RUN" )
		'#End If
		If BatteryCharging = True And BatteryLevel > 70 Then
'		ToastMessageShow( "Maintenance start!" , True )
		  rc.execRootCmd( File.DirInternal & "/bin/busybox setsid " & File.DirInternal & "/bin/busybox sh " & File.DirInternal & "/bin/cb_init.sh RUN FORCE" )
		Else 
			If BatteryLevel > 30 Then
			  rc.execRootCmd( File.DirInternal & "/bin/busybox setsid " & File.DirInternal & "/bin/busybox sh " & File.DirInternal & "/bin/cb_init.sh RUN RUN" )
		    End If
		End If
		
' Put weekly scheduler		
	End If
End Sub

Sub Service_Start (StartingIntent As Intent)
'	StartServiceAt("", DateTime.Add(DateTime.Now,0,0,1), True)
	Dim timeofday As Long
	timeofday = DateTime.Now + ( ( 29 - DateTime.GetHour(DateTime.Now) ) * 3600000 )
	StartServiceAt("", timeofday , True)
End Sub

Sub Service_Destroy

End Sub
