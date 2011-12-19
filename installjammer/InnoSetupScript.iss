[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{9A16F867-DE78-4859-85D7-B993361B255E}
AppName=leJOS NXJ
AppVersion=0.9.0
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

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[CustomMessages]
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

[Run]
; Filename: "{app}\MyProg.exe"; Description: "{cm:LaunchProgram,LeJOS NXJ}"; Flags: nowait postinstall skipifsilent

[Code]
var
  JDKSelectLabel: TLabel;
  JDKSelectButton: TButton;
  JDKSelectTree: TFolderTreeView;
  
  function GetJDKPath(const Version: String; var Path: String): Boolean;
  var
    Tmp: String;
  begin
    RegQueryStringValue(HKEY_LOCAL_MACHINE, 'SOFTWARE\JavaSoft\Java Development Kit\'
      + Version, 'JavaHome', Tmp);
    
    Result := (Length(Tmp) > 0) and DirExists(Tmp);
    if Result then Path := Tmp;
  end;
  
  function DetectJDK(var Path: String): Boolean;
  var
    Tmp : String;
  begin
    Result := GetJDKPath('1.7', Path);
    if Result then Exit;
    Result := GetJDKPath('1.6', Path);
    if Result then Exit;
    Result := GetJDKPath('1.5', Path);
    if Result then Exit;
    
    // if everything else fails
    RegQueryStringValue(HKEY_LOCAL_MACHINE, 'SOFTWARE\JavaSoft\Java Development Kit', 'CurrentVersion', Tmp);
    if Length(Tmp) > 0 then Result := GetJDKPath(Tmp, Path)
    else Result := false;
  end;
  
  procedure JDKSelect_Activate(Page: TWizardPage);
  var
    Tmp: String;
  begin
    if DetectJDK(Tmp) then JDKSelectTree.Directory := Tmp
    else MsgBox('The installer was uanble to detect a 32 Bit Java Development Kit.'
      + #10 + 'By default, such a JDK is installed in' + ExpandConstant('{pf32}'),
      mbInformation, MB_OK);
  end;
  
  function JDKSelect_ShouldSkipPage(Page: TWizardPage): Boolean;
  begin
    //nothing to do yet
    Result := False;
  end;
  
  function JDKSelect_BackButtonClick(Page: TWizardPage): Boolean;
  begin
    //nothing to do yet
    Result := True;
  end;
  
  procedure JDKSelect_OpenDownloadPage(Sender: TObject);
  var
    Tmp: String;
    ErrorCode: Integer;
  begin
    Tmp := 'http://www.oracle.com/technetwork/java/javase/downloads/';
    if not ShellExecAsOriginalUser('', Tmp, '', '', SW_SHOW, ewNoWait, ErrorCode) then
      MsgBox('Error: was unable to open webpage '+Tmp+' with error code '+IntToStr(ErrorCode),
        mbError, MB_OK);
  end;
  
  function JDKSelect_NextButtonClick(Page: TWizardPage): Boolean;
  var
    Tmp, Error: String;
  begin
    Tmp := JDKSelectTree.Directory + '\bin\java.exe';
    if not FileExists(Tmp) then Error := Error + Tmp + ' does not exist.' + #10;
    Tmp := JDKSelectTree.Directory + '\bin\javac.exe';
    if not FileExists(Tmp) then Error := Error + Tmp + ' does not exist.' + #10;
    Result := Length(Error) <= 0;
    if (not Result) then
      MsgBox(Error + 'Please select the root directory of a valid JDK.'
        + #10 + #10 + 'To download a JDK for manual install click the ''Download JDK'' Button.',
        mbError, MB_OK);
  end;
  
  procedure JDKSelect_CancelButtonClick(Page: TWizardPage; var Cancel, Confirm: Boolean);
  begin
    //nothing to do yet
  end;
  
  function JDKSelect_CreatePage(PreviousPageId: Integer): Integer;
  var
    Page: TWizardPage;
  begin
    Page := CreateCustomPage(
      PreviousPageId,
      ExpandConstant('{cm:JDKSelectCaption}'),
      ExpandConstant('{cm:JDKSelectDescription}')
    );
  
    { JDKSelectLabel }
    JDKSelectLabel := TLabel.Create(Page);
    with JDKSelectLabel do
    begin
      Parent := Page.Surface;
      Left := ScaleX(0);
      Top := ScaleY(0);
      Width := ScaleX(297);
      Height := ScaleY(25);
      Caption := 'Select the root directory of a 32-Bit Java Development Kit'
        + #10 + 'for use with leJOS NXJ:';
    end;
    
    { JDKSelectButton }
    JDKSelectButton := TButton.Create(Page);
    with JDKSelectButton do
    begin
      Parent := Page.Surface;
      Left := ScaleX(304);
      Top := ScaleY(0);
      Width := ScaleX(105);
      Height := ScaleY(25);
      Caption := 'Download JDK';
      TabOrder := 1;
      OnClick := @JDKSelect_OpenDownloadPage;
    end;

    { JDKSelectTree }
    JDKSelectTree := TFolderTreeView.Create(Page);
    with JDKSelectTree do
    begin
      Parent := Page.Surface;
      Left := ScaleX(0);
      Top := ScaleY(32);
      Width := ScaleX(409);
      Height := ScaleY(193);
      Cursor := crArrow;
      TabOrder := 0;
    end;
      
    with Page do
    begin
      OnActivate := @JDKSelect_Activate;
      OnShouldSkipPage := @JDKSelect_ShouldSkipPage;
      OnBackButtonClick := @JDKSelect_BackButtonClick;
      OnNextButtonClick := @JDKSelect_NextButtonClick;
      OnCancelButtonClick := @JDKSelect_CancelButtonClick;
    end;
  
    Result := Page.ID;
  end;
  
  procedure InitializeWizard();
  begin
    JDKSelect_CreatePage(wpWelcome);
  end;


