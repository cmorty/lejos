[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{253252E2-EFAE-4AA8-96B6-0828619E536C}
AppName=leJOS NXJ
AppVersion=0.9.0beta
AppVerName=leJOS NXJ 0.9.0beta
OutputBaseFilename=leJOS_NXJ_0.9.0beta_win32
AppPublisher=The leJOS Team
AppPublisherURL=http://www.lejos.org/
AppSupportURL=http://www.lejos.org/
AppUpdatesURL=http://www.lejos.org/
SetupIconFile=../org.lejos.website/htdocs/lejos.ico
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
;Source: "..\release\build\samples\*"; DestDir: "{userdocs}\LeJOS NXJ Samples"; Flags: ignoreversion recursesubdirs createallsubdirs; Tasks: samples
;Source: "..\release\build\source\*"; DestDir: "{userdocs}\LeJOS NXJ Developer Sources"; Flags: ignoreversion recursesubdirs createallsubdirs; Tasks: sources

[Icons]
Name: "{group}\API Documentation (NXT)"; Filename: "{app}\docs\nxt\index.html"
Name: "{group}\API Documentation (PC)"; Filename: "{app}\docs\pc\index.html"
Name: "{group}\NXJ Flash"; Filename: "{app}\bin\nxjflashg.bat"; Flags: closeonexit
Name: "{group}\NXJ Browse"; Filename: "{app}\bin\nxjbrowse.bat"; Flags: closeonexit
Name: "{group}\NXJ Charting Logger"; Filename: "{app}\bin\nxjchartinglogger.bat"; Flags: closeonexit
Name: "{group}\NXJ Control"; Filename: "{app}\bin\nxjcontrol.bat"; Flags: closeonexit
Name: "{group}\NXJ Console Viewer"; Filename: "{app}\bin\nxjconsoleviewer.bat"; Flags: closeonexit
Name: "{group}\NXJ Data Viewer"; Filename: "{app}\bin\nxjdataviewer.bat"; Flags: closeonexit
Name: "{group}\NXJ Image Convertor"; Filename: "{app}\bin\nxjimage.bat"; Flags: closeonexit
Name: "{group}\NXJ Map Command"; Filename: "{app}\bin\nxjmapcommand.bat"; Flags: closeonexit
Name: "{group}\NXJ Monitor"; Filename: "{app}\bin\nxjmonitor.bat"; Flags: closeonexit
Name: "{group}\Uninstall LeJOS"; Filename: "{uninstallexe}"

[Registry]
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; ValueType: string; ValueName: "LEJOS_NXT_JAVA_HOME"; ValueData: "{code:JDKSelect_GetSelection}"; Flags: uninsdeletevalue

[Run]
; We use explorer.exe for starting nxjflashg, since this makes the updated values
; of the environment variables available to the batch file
Filename: "{win}\explorer.exe"; Parameters: """{app}\bin\nxjflashg.bat"""; Description: "{cm:LaunchProgram}"; Flags: nowait postinstall skipifsilent

[Code]
var
  JDKSelectLabel: TLabel;
  JDKSelectButton: TButton;
  JDKSelectTree: TFolderTreeView;
  
  #include "JDKSelect.iss"
  #include "ModPath.iss"
  
  function GetPath : String;
  begin
    if not RegQueryStringValue(HKLM, 'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
      'Path', Result) then
      RaiseException('Failed to determine old value of Path environment variable');
  end;
  procedure SetPath(Data : String);
  begin
    if not RegWriteStringValue(HKLM, 'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
      'Path', Data) then
      RaiseException('Failed to set value of Path environment variable');
  end;
  
  function CheckInstallJammer : Boolean;
  var
    Tmp, Command: String;
  begin
    // old install jammer appid
    Tmp := 'SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\253252E2-EFAE-4AA8-96B6-0828619E536C';
    Result := RegQueryStringValue(HKLM, Tmp, 'UninstallString', Command) and FileExists(Command);
  end;
  
  function GetUninstallCommand(var Command: String; var Params: String): Boolean;
  var
    Tmp: String;
  begin
    // new InnoSetup appid
    Tmp := 'SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\{253252E2-EFAE-4AA8-96B6-0828619E536C}_is1';
    if RegQueryStringValue(HKLM, Tmp, 'UninstallString', Command) then
    begin
      Command := RemoveQuotes(Command);
      if FileExists(Command) then
      begin
        Params := '/SILENT /NOCANCEL /NORESTART';
        Result := true;
        Exit;
      end;
    end;
    Result := false;
  end;
  
  function NextButtonClick(curPageID: Integer): Boolean;
  var
    UCommand, UParams : String;
    ResultCode : Integer;
  begin
    if (curPageID = wpReady) and CheckInstallJammer() then
    begin
      // presumably, the install jammer uninstaller starts another process
      // and hence terminates immediatly. Hence, this install and the installjammer
      // uninstaller run in parallel. Hence, the user must uninstall manually.
      MsgBox('Old leJOS installation detected. Please uninstall manually via the control panel.',
        mbError, MB_OK);
      Result := false;
      Exit;
    end;
    if (curPageID = wpReady) and GetUninstallCommand(UCommand, UParams) then
    begin
      if MsgBox('A previous was detected and needs to be uninstalled before this setup can proceed.',
        mbInformation, MB_OKCANCEL) = IDCANCEL then
        begin
          Result := false;
          Exit;
        end;
      if not Exec(UCommand, UParams,'', SW_SHOW, ewWaitUntilTerminated, ResultCode) then
        MsgBox('Unable to execute uninstaller '+UCommand, mbError, MB_OK);
    end;
    Result := true;
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
  
