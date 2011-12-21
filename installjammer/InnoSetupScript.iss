;TODO:
; - preserve selection of JDK when going back and froth
; - include LEJOS_NXT_JAVA_HOME in JDK detection
; - initialize folder tree with {pf}\Java or {pf}

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{253252E2-EFAE-4AA8-96B6-0828619E536C}
AppName=leJOS NXJ
AppVersion=0.9.1beta
AppVerName=leJOS NXJ 0.9.1beta
OutputBaseFilename=leJOS_NXJ_0.9.1beta_win32
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
Name: "main"; Description: "leJOS Development Kit"; Types: full compact custom; Flags: fixed disablenouninstallwarning
Name: "main\apinxt"; Description: "API Documentation (NXT)"; Types: full compact; Flags: disablenouninstallwarning
Name: "main\apipc"; Description: "API Documentation (PC)"; Types: full compact; Flags: disablenouninstallwarning
Name: "extras"; Description: "Additional Sources"; Types: full; Flags: disablenouninstallwarning
Name: "extras\samples"; Description: "Sample and Example Projects"; Types: full; Flags: disablenouninstallwarning
Name: "extras\sources"; Description: "Sources of leJOS Development Kit"; Types: full; Flags: disablenouninstallwarning

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
; Delete LEJOS_NXT_JAVA_HOME and NXJ_HOME value for current user and set new value globally
Root: HKCU; Subkey: "Environment"; ValueType: none; ValueName: "NXJ_HOME"; Flags: deletevalue
Root: HKCU; Subkey: "Environment"; ValueType: none; ValueName: "LEJOS_NXT_JAVA_HOME"; Flags: deletevalue
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; ValueType: string; ValueName: "NXJ_HOME"; ValueData: "{app}"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; ValueType: string; ValueName: "LEJOS_NXT_JAVA_HOME"; ValueData: "{code:JDKSelect_GetSelection}"; Flags: uninsdeletevalue

[Run]
; We use explorer.exe for starting nxjflashg, since this makes the updated values
; of the environment variables available to the batch file
Filename: "{win}\explorer.exe"; Parameters: """{app}\bin\nxjflashg.bat"""; Description: "{cm:LaunchProgram}"; Flags: nowait postinstall skipifsilent

#include "Tools.iss"
#include "Fantom.iss"
#include "ModPath.iss"
#include "JDKSelect.iss"
#include "ExtrasDirPage.iss"
#include "UnInstall.iss"

[Code]  
  function NextButtonClick(curPageID: Integer): Boolean;
  var
    ID : String;
  begin
    if curPageID = wpWelcome then
    begin
      Result := DetectOutdatedFantom;
      if not Result then Exit;     
    end;
    
    if curPageID = wpReady then
    begin
      ID := '253252E2-EFAE-4AA8-96B6-0828619E536C' 
      Result := UninstallInstallJammer(ID);
      if not Result then Exit;     
      Result := UninstallInnoSetup(ID);
      if not Result then Exit;     
    end;
    
    Result := true;
  end;
   
  procedure CurStepChanged(CurStep: TSetupStep);
  begin
    if CurStep = ssPostInstall then
      SetEnvVar('Path', ModPath_Append(GetEnvVar('Path'), ExpandConstant('{app}\bin')));   
  end;
  procedure CurUninstallStepChanged(CurUninstallStep: TUninstallStep);
  begin
    if CurUninstallStep = usUninstall then
      SetEnvVar('Path', ModPath_Delete(GetEnvVar('Path'), ExpandConstant('{app}\bin')));   
  end;
  
  procedure InitializeWizard();
  begin
    JDKSelect_CreatePage(wpUserInfo);
    ExtrasDirPage_CreatePage(wpSelectComponents);    
  end;
  
