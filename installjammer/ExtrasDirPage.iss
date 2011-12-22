[Code]
var
  ExtrasDirPage: TInputDirWizardPage;

  function ExtrasDirPage_GetSamplesFolder(Param: String): String;
  begin
    Result := ExtrasDirPage.Values[0];
  end;
  
  function ExtrasDirPage_GetSourcesFolder(Param: String): String;
  begin
    Result := ExtrasDirPage.Values[1];
  end;
 
  procedure ExtrasDirPage_Activate(Page: TWizardPage);
  var
    Tmp : Boolean;
  begin
    Tmp := IsComponentSelected('extras\samples');
    ExtrasDirPage.Edits[0].Enabled := Tmp;
    ExtrasDirPage.Buttons[0].Enabled := Tmp;
    ExtrasDirPage.PromptLabels[0].Enabled := Tmp;
    Tmp := IsComponentSelected('extras\sources');
    ExtrasDirPage.Edits[1].Enabled := Tmp;
    ExtrasDirPage.Buttons[1].Enabled := Tmp;
    ExtrasDirPage.PromptLabels[1].Enabled := Tmp;
  end;
  
  function ExtrasDirPage_ShouldSkipPage(Page: TWizardPage): Boolean;
  begin
    Result := not (IsComponentSelected('extras\samples') or IsComponentSelected('extras\sources'));
  end;
  
  function ExtrasDirPage_CreatePage(PreviousPageId: Integer): Integer;
  begin
    ExtrasDirPage := CreateInputDirPage(PreviousPageId,
      'Select the Folders for the Additional Sources',
      'Where should the additional sources be stored?',
      'Select the Folders for the Additional Sources:',
      false, '');
    
    with ExtrasDirPage do
    begin
      Add('Sample and Example Projects');
      Add('Sources of leJOS Development Kit');
      Values[0] := ExpandConstant('{userdocs}\LeJOS NXJ Samples');
      Values[1] := ExpandConstant('{userdocs}\LeJOS NXJ Development Kit Sources');
      
      OnActivate := @ExtrasDirPage_Activate;
      OnShouldSkipPage := @ExtrasDirPage_ShouldSkipPage;      
    end;

    Result := ExtrasDirPage.ID;
  end;
  
