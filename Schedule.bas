B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Service
Version=8.3
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: True
	
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Dim rc As RootCmd

End Sub

Sub Service_Create

End Sub

Sub myStart()

	Dim hour As Int
	
	hour = DateTime.GetHour(DateTime.Now)

    If hour <> 3 Then		
		If rc.haveRoot Then
			rc.execRootCmdSilent( File.DirInternal & "/bin/busybox setsid " & File.DirInternal & "/bin/busybox sh " & File.DirInternal & "/bin/cb.sh RUN FORCE" )
		End If
	End If

End Sub

Sub Service_Start (StartingIntent As Intent)
	
	StartServiceAt("", DateTime.Now + 3300 * 1000, False) 'will start after 55 minutes.
	
	myStart
	
	Service.StopAutomaticForeground 'Call this when the background task completes (if there is one)
End Sub

Sub Service_Destroy

End Sub
