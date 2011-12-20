[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{9A16F867-DE78-4859-85D7-B993361B255E}
AppName=leJOS NXJ
AppVersion=0.9.0beta
AppVerName=leJOS NXJ 0.9.0beta
OutputBaseFilename="leJOS_NXJ_0.9.0beta_win32"
AppPublisher=The leJOS Team
AppPublisherURL=http://www.lejos.org/
AppSupportURL=http://www.lejos.org/
AppUpdatesURL=http://www.lejos.org/
DefaultDirName={pf}\leJOS NXJ
DefaultGroupName=leJOS NXJ
SolidCompression=yes
Compression=lzma
OutputDir=.
ChangesEnvironment=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[CustomMessages]
LaunchProgram=Flash leJOS Firmware to NXT Brick
AdditionalStuff=Additional Stuff
SamplesProjects=Install Sample and Example Projects for leJOS Users
DeveloperSources=Install Sources for leJOS Developers
JDKSelectCaption=Select a Java Development Kit
JDKSelectDescription=Select a Java Development Kit for use with leJOS NXJ
  
[Tasks]
Name: "samples"; Description: "{cm:SamplesProjects}"; GroupDescription: "{cm:AdditionalStuff}"; Flags: unchecked
Name: "sources"; Description: "{cm:DeveloperSources}"; GroupDescription: "{cm:AdditionalStuff}"; Flags: unchecked

[Files]
Source: "..\release\build\bin_windows\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\release\build\samples\*"; DestDir: "{userdocs}\LeJOS NXJ Samples"; Flags: ignoreversion recursesubdirs createallsubdirs; Tasks: samples
Source: "..\release\build\source\*"; DestDir: "{userdocs}\LeJOS NXJ Developer Sources"; Flags: ignoreversion recursesubdirs createallsubdirs; Tasks: sources

[Icons]
Name: "{group}\API Documentation"; Filename: "{app}\docs\nxt\index.html"
Name: "{group}\PC API Documentation"; Filename: "{app}\docs\pc\index.html"
Name: "{group}\NXJ Flash"; Filename: "{app}\bin\nxjflashg"; Flags: closeonexit
Name: "{group}\NXJ Browse"; Filename: "{app}\bin\nxjbrowse"; Flags: closeonexit
Name: "{group}\NXJ Charting Logger"; Filename: "{app}\bin\nxjchartinglogger"; Flags: closeonexit
Name: "{group}\NXJ Control"; Filename: "{app}\bin\nxjcontrol"; Flags: closeonexit
Name: "{group}\NXJ Console Viewer"; Filename: "{app}\bin\nxjconsoleviewer"; Flags: closeonexit
Name: "{group}\NXJ Data Viewer"; Filename: "{app}\bin\nxjdataviewer"; Flags: closeonexit
Name: "{group}\NXJ Image Convertor"; Filename: "{app}\bin\nxjimage"; Flags: closeonexit
Name: "{group}\NXJ Map Command"; Filename: "{app}\bin\nxjmapcommand"; Flags: closeonexit
Name: "{group}\NXJ Monitor"; Filename: "{app}\bin\nxjmonitor"; Flags: closeonexit
Name: "{group}\Uninstall LeJOS"; Filename: "{uninstallexe}"

[Registry]
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; ValueType: string; ValueName: "LEJOS_NXT_JAVA_HOME"; ValueData: "{code:JDKSelect_GetSelection}"; Flags: uninsdeletevalue

[Run]
; We use explorer.exe for starting nxjflashg, since this makes the updated values
; of the environment variables available to the batch file
Filename: "{win}\explorer.exe"; Parameters: "{app}\bin\nxjflashg"; Description: "{cm:LaunchProgram}"; Flags: nowait postinstall skipifsilent

[Code]
var
  JDKSelectLabel: TLabel;
  JDKSelectButton: TButton;
  JDKSelectTree: TFolderTreeView;
  
  #include "JDKSelect.iss"
  #include "ModPath.iss"
  
  function GetPath : String;
  begin
    if not RegQueryStringValue(HKEY_LOCAL_MACHINE, 'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
      'Path', Result) then
      RaiseException('Failed to determine old value of Path environment variable');
  end;
  procedure SetPath(Data : String);
  begin
    if not RegWriteStringValue(HKEY_LOCAL_MACHINE, 'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
      'Path', Data) then
      RaiseException('Failed to set value of Path environment variable');
  end;
   
  procedure CurStepChanged(CurStep: TSetupStep);
  begin
    if CurStep = ssPostInstall then
      SetPath(ModPath_Append(GetPath(), ExpandConstant('{app}\bin')));   
  end;
  procedure CurUninstallStepChanged(CurUninstallStep: TUninstallStep);
  begin
    if CurUninstallStep = usUninstall then
      SetPath(ModPath_Delete(GetPath(), ExpandConstant('{app}\bin')));   
  end;

  procedure InitializeWizard();
  begin
    JDKSelect_CreatePage(wpUserInfo);
  end;
  
