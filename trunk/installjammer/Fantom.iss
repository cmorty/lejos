[Code]
  function DetectOutdatedFantom : Boolean;
  var
    ErrorCode: Integer;
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
        Error := 'Currently, version '
          +IntToStr(d[0])+'.'+IntToStr(d[1])+'.'+IntToStr(d[2])+'.'+IntToStr(d[3])
          +' of the LEGO NXT Driver is installed. This version is outdated.'
    end
    else
      Error := Tmp+' was either not found or its version cannot be determined.';
    
    if Length(Error) > 0 then
      if MsgBox(Error + #10#10 + 'Please make sure, that the latest version of the LEGO '
        + 'NXT Driver (also called Fantom Driver) from mindstorms.lego.com is installed, at least version '
        + IntToStr(e[0])+'.'+IntToStr(e[1])+'.'+IntToStr(e[2])+'.'
        + #10#10 + 'Click OK to open the download page for the driver '
        + 'or click Cancel to proceed installing leJOS.',
        mbInformation, MB_OKCANCEL) = IDOK then
      begin
        OpenWebPage('http://mindstorms.lego.com/en-us/support/files/Driver.aspx');
        Result := false;
        Exit;
      end;
      
    Result := true;
  end;
  
