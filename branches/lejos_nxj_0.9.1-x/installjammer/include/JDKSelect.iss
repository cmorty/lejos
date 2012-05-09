[Code]
var
  JDKSelectLabel: TLabel;
  JDKSelectButton: TButton;
  JDKSelectTree: TFolderTreeView;
  JDKSelectDetection: Boolean;
  
  function Is32BitJDK(const Path: String; var Error: String): Boolean;
  var
    Tmp: String;
  begin
    Error := '';
    Tmp := ConcatPath(Path, 'bin\java.exe');
    if not FileExists(Tmp) then Error := Error + Tmp + ' does not exist.' + CRLF;
    Tmp := ConcatPath(Path, 'bin\javac.exe');
    if not FileExists(Tmp) then Error := Error + Tmp + ' does not exist.' + CRLF;
    if Length(Error) <= 0 then
    begin
      Tmp := ConcatPath(Path, 'jre\lib\i386');
      if not DirExists(Tmp) then
        Error := Error + 'Selected JDK is not a 32 Bit version.' + CRLF;   
    end;
    Result := Length(Error) <= 0;
  end;

  function GetJDKPath(const Version: String; var Path: String): Boolean;
  var
    Tmp: String;
  begin
    Result := RegQueryStringValue(HKEY_LOCAL_MACHINE,
      'SOFTWARE\JavaSoft\Java Development Kit\' + Version,
      'JavaHome', Tmp) and (Length(Tmp) > 0) and DirExists(Tmp);
    if Result then Path := Tmp;
  end;
  
  function DetectJDK(var Path: String): Boolean;
  var
    Tmp, Dummy: String;
  begin
    GetEnvVar('LEJOS_NXT_JAVA_HOME', Tmp);
    if Length(Tmp) <= 0 then
      Tmp := GetEnv('LEJOS_NXT_JAVA_HOME');
    if (Length(Tmp) > 0) and Is32BitJDK(Tmp, Dummy) then
    begin
      Result := true;
      Path := Tmp;
      Exit;
    end;
  
    Result := GetJDKPath('1.7', Path);
    if Result then Exit;
    Result := GetJDKPath('1.6', Path);
    if Result then Exit;
    Result := GetJDKPath('1.5', Path);
    if Result then Exit;
    
    // if everything else fails
    if RegQueryStringValue(HKEY_LOCAL_MACHINE, 'SOFTWARE\JavaSoft\Java Development Kit',
      'CurrentVersion', Tmp) and (Length(Tmp) > 0) then Result := GetJDKPath(Tmp, Path)
    else Result := false;
  end;
  
  function JDKSelect_GetSelection(Param: String) : String;
  begin
    Result := JDKSelectTree.Directory;
  end;
  
  procedure JDKSelect_Activate(Page: TWizardPage);
  var
    Tmp: String;
  begin
    if JDKSelectDetection then
    begin
      JDKSelectDetection := false;
    
      if DetectJDK(Tmp) then JDKSelectTree.Directory := Tmp
      else
      begin
        Tmp := ExpandConstant('{pf32}\Java');
        MsgBox('The installer was uanble to detect a 32 Bit Java Development Kit.'
          + CRLF + 'By default, such a JDK is installed in ' + Tmp,
          mbInformation, MB_OK);
          
        if DirExists(Tmp) then JDKSelectTree.Directory := Tmp
        else 
        begin
          Tmp := ExpandConstant('{pf32}');
          if DirExists(Tmp) then JDKSelectTree.Directory := Tmp
          else JDKSelectTree.Directory := ExpandConstant('{sd}\');
        end;
      end;     
    end;
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
  
  function JDKSelect_NextButtonClick(Page: TWizardPage): Boolean;
  var
    Error: String;
  begin
    Result := Is32BitJDK(JDKSelectTree.Directory, Error);
    if (not Result) then
      MsgBox(Error + 'Please select the root directory of a valid JDK. '
        + 'This is required to continue.' + CRLF2
        + 'To download a JDK for manual install click the ''Download JDK'' Button.',
        mbError, MB_OK);
  end;
  
  procedure JDKSelect_CancelButtonClick(Page: TWizardPage; var Cancel, Confirm: Boolean);
  begin
    //nothing to do yet
  end;
  
  procedure JDKSelect_OpenDownloadPage(Sender: TObject);
  begin
    OpenWebPage('http://www.oracle.com/technetwork/java/javase/downloads/');
  end;
  
  function JDKSelect_CreatePage(PreviousPageId: Integer): Integer;
  var
    Page: TWizardPage;
  begin
    JDKSelectDetection := true;
  
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
        + CRLF + 'for use with leJOS NXJ:';
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
