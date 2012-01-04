[Code]
  function DetectOutdatedFantom(const e0, e1, e2: Integer) : Boolean;
  var
    Tmp, Error: String;
    MS, LS: Cardinal;
    d: Array[0..3] of Cardinal;
  begin
    Tmp := ExpandConstant('{syswow64}\fantom.dll');
    if FileExists(Tmp) and GetVersionNumbers(Tmp, MS, LS) then
    begin
      d[0] := MS shr 16;
      d[1] := MS and $ffff;
      d[2] := LS shr 16;
      d[3] := LS and $ffff;
      if (d[0] < e0) or
        ((d[0] = e0) and (d[1] < e1)) or 
        ((d[0] = e0) and (d[1] = e1) and (d[2] < e2)) then
        Error := 'Currently, version '
          +IntToStr(d[0])+'.'+IntToStr(d[1])+'.'+IntToStr(d[2])+'.'+IntToStr(d[3])
          +' of the LEGO NXT Driver is installed. This version is outdated.'
    end
    else
      Error := Tmp+' was either not found or its version cannot be determined.';
    
    if Length(Error) > 0 then
      if MsgBox(Error + CRLF2 + 'Please make sure, that the latest version of the LEGO '
        + 'NXT Driver (also called Fantom Driver) from mindstorms.lego.com is installed, at least version '
        + IntToStr(e0)+'.'+IntToStr(e1)+'.'+IntToStr(e2)+'.'
        + CRLF2 + 'Click OK to open the download page for the driver '
        + 'or click Cancel to proceed installing leJOS.',
        mbInformation, MB_OKCANCEL) = IDOK then
      begin
        OpenWebPage('http://mindstorms.lego.com/en-us/support/files/Driver.aspx');
        Result := false;
        Exit;
      end;
      
    Result := true;
  end;
  
