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
  
  function JDKSelect_GetSelection(Param: String) : String;
  begin
    Result := JDKSelectTree.Directory;
  end;
  
  procedure JDKSelect_Activate(Page: TWizardPage);
  var
    Tmp: String;
  begin
    if DetectJDK(Tmp) then JDKSelectTree.Directory := Tmp
    else MsgBox('The installer was uanble to detect a 32 Bit Java Development Kit.'
      + #10 + 'By default, such a JDK is installed in ' + ExpandConstant('{pf32}\Java'),
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
    Tmp := JDKSelectTree.Directory + '\jre\lib\i386';
    if (Length(Error) <= 0) and not DirExists(Tmp) then
      Error := 'Directory ' + Tmp + ' not found. JDK is not a 32 Bit Version.' + #10;
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
