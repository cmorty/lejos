[Code]
  function ConcatPath(const Path1, Path2: String): String;
  begin
    if Copy(Path1, Length(Path1), 1)='\' then
      Result := Path1 + Path2
    else
      Result := Path1 + '\' + Path2;
  end;

  function GetEnvVar(const Name: String) : String;
  begin
    if not RegQueryStringValue(HKLM,
      'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
      Name, Result) then
      RaiseException('Failed to determine old value of Path environment variable');
  end;
  
  procedure SetEnvVar(const Name: String; Data : String);
  begin
    if not RegWriteStringValue(HKLM,
      'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
      Name, Data) then
      RaiseException('Failed to set value of Path environment variable');
  end;
  
  procedure OpenWebPage(const URL: String);
  var
    ErrorCode: Integer;
  begin
    if not ShellExecAsOriginalUser('', URL, '', '', SW_SHOW, ewNoWait, ErrorCode) then
      MsgBox('Error: was unable to open webpage '+URL+', error code '
        +IntToStr(ErrorCode)+'.', mbError, MB_OK);
  end;

