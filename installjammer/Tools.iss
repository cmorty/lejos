[Code]
  function GetEnvVar(Name: String) : String;
  begin
    if not RegQueryStringValue(HKLM,
      'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
      Name, Result) then
      RaiseException('Failed to determine old value of Path environment variable');
  end;
  procedure SetEnvVar(Name: String; Data : String);
  begin
    if not RegWriteStringValue(HKLM,
      'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
      Name, Data) then
      RaiseException('Failed to set value of Path environment variable');
  end;
