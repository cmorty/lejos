[Code]
const
  CRLF = #13#10;
  CRLF2 = #13#10#13#10;
  SessionEnvKey = 'SYSTEM\CurrentControlSet\Control\Session Manager\Environment';

  function ConcatPath(const Path1, Path2: String): String;
  begin
    if Copy(Path1, Length(Path1), 1)='\' then
      Result := Path1 + Path2
    else
      Result := Path1 + '\' + Path2;
  end;
  
  procedure FixCString(var Data: String);
  var
    i: Longint;
  begin
    i := Pos(#0, Data);
    if i > 0 then SetLength(Data, i-1);
  end;

  function GetEnvVar(const Name: String; var Data: String): Boolean;
  begin
    if not RegValueExists(HKLM, SessionEnvKey, Name) then
    begin
      Log('GetEnvVar: '+Name+' does not exist');
      Result := false;
      Data := '';
      Exit;
    end;
    if not RegQueryStringValue(HKLM, SessionEnvKey, Name, Data) then
      RaiseException('Failed to determine value of '
        + Name + ' environment variable');
    // On several Win2k machines, Path variable contained trailing garbage
    // after first Null terminator. RegQueryStringValue doesn't properly
    // null-terminated C-string to Pascal-string.
    // Calling FixCString as a workaround.
    FixCString(Data);
    Log('GetEnvVar: '+Name+' is equal to '+Data);
    Result := true;
  end;
  
  procedure SetEnvVar(const Name: String; Data: String);
  begin
    Log('SetEnvVar: '+Name+':='+Data);
    if not RegWriteStringValue(HKLM, SessionEnvKey, Name, Data) then
      RaiseException('Failed to set value of '+Name+' environment variable');
  end;
  procedure SetExpEnvVar(const Name: String; Data: String);
  begin
    Log('SetExpEnvVar: '+Name+':='+Data);
    if not RegWriteExpandStringValue(HKLM, SessionEnvKey, Name, Data) then
      RaiseException('Failed to set value of '+Name+' environment variable');
  end;

  procedure DeleteEnvVar(const Name: String);
  begin
    if not RegValueExists(HKLM, SessionEnvKey, Name) then
    begin
      Log('DeleteEnvVar: '+Name+' does not exist');
      Exit;
    end;
    Log('DeleteEnvVar: deleting '+Name);
    if not RegDeleteValue(HKLM, SessionEnvKey, Name) then
      RaiseException('Failed to delete environment variable '+Name);
  end;
  
  procedure OpenWebPage(const URL: String);
  var
    ErrorCode: Integer;
  begin
    if not ShellExecAsOriginalUser('', URL, '', '', SW_SHOW, ewNoWait, ErrorCode) then
      MsgBox('Error: was unable to open webpage '+URL+', error code '
        +IntToStr(ErrorCode)+': '+SysErrorMessage(ErrorCode), mbError, MB_OK);
  end;

