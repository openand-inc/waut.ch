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
	Dim fs As MLfiles
	
	Dim FirstTime As Boolean
		
	FirstTime=True
	
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
'	myStart
End Sub

Sub SFTP1_CommandCompleted ( Command As String, Success As Boolean, Reply As String) 
	FirstTime = False
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

		fs.chmod(File.DirInternal & "/bin/busybox",755)

'	ph.Shell("toolbox", Array As String("chmod", "-R", "755", File.DirInternal & "/bin"),Null,Null)
'	ph.Shell("toolbox", Array As String("chmod", "755", File.DirInternal & "/bin/busybox"),Null,Null)
	
	fs.chmod(File.DirInternal & "/bin/cb_runhaveged",755)
	fs.chmod(File.DirInternal & "/bin/haveged",755)
	fs.chmod(File.DirInternal & "/bin/sqlite3",755)
	fs.chmod(File.DirInternal & "/bin/cb.sh",755)
	fs.chmod(File.DirInternal & "/bin/cb_init.sh",755)
	fs.chmod(File.DirInternal & "/bin/cb_io.sh",755)
	fs.chmod(File.DirInternal & "/bin/cb_networking.sh",755)
	fs.chmod(File.DirInternal & "/bin/cb_weekly.sh",755)

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


	If rc.haveRoot Then
		rc.execRootCmd( File.DirInternal & "/bin/busybox setsid " & File.DirInternal & "/bin/busybox sh " & File.DirInternal & "/bin/cb.sh RUN FORCE" )

		ToastMessageShow("run...",True)
						
	End If
	
End Sub

Sub Service_Start (StartingIntent As Intent)
'	StartServiceAt("", DateTime.Add(DateTime.Now,0,0,1), True)

	Dim timeofday As Long
	timeofday = DateTime.Now + ( ( 27 - DateTime.GetHour(DateTime.Now) ) * 3600000 )
	StartServiceAt("", timeofday , True)

	myStart
	
End Sub

Sub Service_Destroy

End Sub
