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
JDKSelectCaption=Select a Java Development Kit
JDKSelectDescription=Select a Java Development Kit for use with leJOS NXJ

[Types]
Name: "compact"; Description: "Compact installation"
Name: "full"; Description: "Full installation"
Name: "custom"; Description: "Custom installation"; Flags: iscustom
  
[Components]
Name: "main"; Description: "leJOS Development Kit"; Types: full compact custom; Flags: fixed
Name: "main\apinxt"; Description: "API Documentation (NXT)"; Types: full compact;
Name: "main\apipc"; Description: "API Documentation (PC)"; Types: full compact;
Name: "extras"; Description: "Additional Sources"; Types: full;
Name: "extras\samples"; Description: "Sample and Example Projects"; Types: full;
Name: "extras\sources"; Description: "Sources of leJOS Development Kit"; Types: full;

[Files]
Source: "..\release\build\bin_windows\*"; DestDir: "{app}"; Excludes: "docs"; Flags: ignoreversion recursesubdirs createallsubdirs; Components: main
Source: "..\release\build\bin_windows\docs\pc\*"; DestDir: "{app}\docs\pc"; Flags: ignoreversion recursesubdirs createallsubdirs; Components: main\apipc
Source: "..\release\build\bin_windows\docs\nxt\*"; DestDir: "{app}\docs\nxt"; Flags: ignoreversion recursesubdirs createallsubdirs; Components: main\apinxt
Source: "..\release\build\samples\*"; DestDir: "{code:ExtrasDirPage_GetSamplesFolder}"; Flags: ignoreversion recursesubdirs createallsubdirs; Components: extras\samples
Source: "..\release\build\source\*"; DestDir: "{code:ExtrasDirPage_GetSourcesFolder}"; Flags: ignoreversion recursesubdirs createallsubdirs; Components: extras\sources

[Icons]
Name: "{group}\API Documentation (PC)"; Filename: "{app}\docs\pc\index.html"; Components: main\apipc
Name: "{group}\API Documentation (NXT)"; Filename: "{app}\docs\nxt\index.html"; Components: main\apinxt
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
  ExtrasDirPage: TInputDirWizardPage;

  
  #include "ModPath.iss"
  #include "JDKSelect.iss"
  #include "ExtrasDirPage.iss"
  
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
  
  procedure DetectOutdatedFantom;
  var
    Tmp, Error: String;
    MS, LS: Cardinal;
    d: Array[0..3] of Cardinal;
    e: Array[0..2] of Cardinal;
  begin
    // we expect at least version 1.1.3
    e[0] := 1;
    e[1] := 1;
    e[2] := 3;
  
    Tmp := ExpandConstant('{syswow64}\fantom.dll');
    if FileExists(Tmp) and GetVersionNumbers(Tmp, MS, LS) then
    begin
      d[0] := MS shr 16;
      d[1] := MS and $ffff;
      d[2] := LS shr 16;
      d[3] := LS and $ffff;
      if (d[0] < e[0]) or
        ((d[0] = e[0]) and (d[1] < e[1])) or 
        ((d[0] = e[0]) and (d[1] = e[1]) and (d[2] < e[2])) then
        Error := 'The current version of you LEGO NXT Driver is '
          +IntToStr(d[0])+'.'+IntToStr(d[1])+'.'+IntToStr(d[2])+'.'+IntToStr(d[3])
          +' which is outdated.'
    end
    else
      Error := Tmp+' was either not found or its version cannot be determined.';
      
    if Length(Error) > 0 then
        MsgBox(Error + #10#10 + 'Please make sure, that you install the latest LEGO '
          + 'NXT Driver from www.mindstorms.com (at least Version '
          + IntToStr(e[0])+'.'+IntToStr(e[1])+'.'+IntToStr(e[2])+').',
          mbInformation, MB_OK);      
  end;
  
  function NextButtonClick(curPageID: Integer): Boolean;
  var
    UCommand, UParams : String;
    ResultCode : Integer;
  begin
    if (curPageID = wpWelcome) then
      DetectOutdatedFantom;
      
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
    
    ExtrasDirPage := CreateInputDirPage(wpSelectComponents,
      'Select the Folders for the Additional Sources', 'Where should the additional sources be stored?',
      'Select the Folders for the Additional Sources:',
      false, '');
    
    with ExtrasDirPage do
    begin
      Add('Sample and Example Projects');
      Add('Sources of leJOS Development Kit');
      Values[0] := ExpandConstant('{userdocs}\LeJOS NXJ Samples');
      Values[1] := ExpandConstant('{userdocs}\LeJOS NXJ Development Kit Sources');
      
      OnActivate := @ExtrasDirPage_Activate;
      OnShouldSkipPage := @ExtrasDirPage_ShouldSkipPage;      
    end;
  end;
  
