[Code]
  procedure ModPath_PathSplit(Path: String; const List: TStrings);
  var
    i: Integer;
  begin
    List.Clear;
    if Length(Path) <= 0 then Exit;
    
    while true do
    begin
      i := Pos(';', Path);
      if (i <= 0) then Break;
      List.add(Copy(Path, 1, i-1));
      Path := Copy(Path, i + 1, Length(Path));
    end;    
    List.add(Path);
  end;
 
  procedure ModPath_ListDelete(const List: TStrings; const Token: String);
  var
    i: Integer;
    Tmp: String;
    TmpList: TStrings;
  begin
    TmpList := TStringList.Create;
    try
      for i := 0 to List.Count-1 do
      begin
        Tmp := List.Strings[i];
        if CompareText(Tmp, Token)<>0 then
          TmpList.Add(Tmp);
      end;
      List.Assign(TmpList);
    finally
      TmpList.Free;
    end;
  end;
 
  function ModPath_ListJoin(const List: TStrings) : String;
  var
    i: Integer;
  begin
    if List.Count <= 0 then Result := ''
    else
    begin
      Result := List.Strings[0];
      for i := 1 to List.Count-1 do
        Result := Result + ';' + List.Strings[i];
    end;
  end;
  
  function ModPath_Append(const Path: String; const Token: String) : String;
  var
    List: TStrings;
  begin
    List := TStringList.Create;
    try
      ModPath_PathSplit(Path, List);
      ModPath_ListDelete(List, Token);
      List.add(Token);
      Result := ModPath_ListJoin(List);
    finally
      List.Free;
    end;
  end;
 
  function ModPath_Prepend(const Path: String; const Token: String) : String;
  var
    List: TStrings;
  begin
    List := TStringList.Create;
    try
      ModPath_PathSplit(Path, List);
      ModPath_ListDelete(List, Token);
      List.Insert(0, Token);
      Result := ModPath_ListJoin(List);
    finally
      List.Free;
    end;
  end;
 
  function ModPath_Delete(const Path: String; const Token: String) : String;
  var
    List: TStrings;
  begin
    List := TStringList.Create;
    try
      ModPath_PathSplit(Path, List);
      ModPath_ListDelete(List, Token);
      Result := ModPath_ListJoin(List);
    finally
      List.Free;
    end;
  end;
