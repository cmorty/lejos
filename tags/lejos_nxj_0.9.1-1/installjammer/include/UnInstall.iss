[Code]
  function UninstallWarn : Integer;
  begin
    Result := MsgBox('A previous installation was detected and needs to be uninstalled '
            + 'before this setup can proceed.' + CRLF2
            + 'WARNING:' + CRLF
            + 'This may delete any previously installed samples or sources. Make sure to '
            + 'make a backup of your changes before you continue.',
            mbInformation, MB_OKCANCEL)
  end;

  function UninstallInstallJammer(const ID: String) : Boolean;
  var
    Tmp, Command: String;
    ResultCode: Integer;
  begin
    // old install jammer appid
    Tmp := 'SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\'+ID;
    if RegQueryStringValue(HKLM, Tmp, 'UninstallString', Command) then
    begin
      // tests show, that install jammer stores the path without adding quotes
      // remove quotes anyway, just to be safe
      Command := RemoveQuotes(Command);
      if FileExists(Command) then
      begin
        if UninstallWarn = IDOK then
          // presumably, the install jammer uninstaller starts another process
          // and terminates immediatly. Instead of waiting for the process to end,
          // the user must manually go through the uninstall and setup does not
          // proceed (Result = false)
          if not Exec(Command, '', '', SW_SHOW, ewWaitUntilTerminated, ResultCode) then
            MsgBox('Unable to execute uninstaller '+Command, mbError, MB_OK);            
        Result := false;
        Exit;
      end;
    end;
    Result := true;
  end;
  
  function UninstallInnoSetup(const ID: String) : Boolean;
  var
    Tmp, Command: String;
    ResultCode: Integer;
  begin
    // new InnoSetup appid
    Tmp := 'SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\{'+ID+'}_is1';
    if RegQueryStringValue(HKLM, Tmp, 'UninstallString', Command) then
    begin
      Command := RemoveQuotes(Command);
      if FileExists(Command) then
      begin
        if UninstallWarn = IDCANCEL then
          begin
            Result := false;
            Exit;
          end;
        if not Exec(Command, '/SILENT /NOCANCEL /NORESTART', '', SW_SHOW,
            ewWaitUntilTerminated, ResultCode) then
          MsgBox('Unable to execute uninstaller '+Command+', error code '
            +IntToStr(ResultCode)+': '+SysErrorMessage(ResultCode), mbError, MB_OK);
      end;
    end;
    Result := true;
  end;
  
