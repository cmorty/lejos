//Setup
var MinFontSize = 10;
var MaxFontSize = 30;

function getSize(fontSize){
	var init = fontSize.indexOf('px');
	var size = fontSize.substr(0,init);
	return parseInt(size);
}

		function increaseFontSize(){
   			var obj = document.getElementById('content');		
			var fontSize = obj.style.fontSize;
			if(fontSize.length == 0){//Default Size
				CurrentfontSize = MinFontSize+1;
				obj.style.fontSize = CurrentfontSize.toString() + "px";
			}else{
				CurrentfontSize = getSize(fontSize);
				if(CurrentfontSize <= MaxFontSize){
					CurrentfontSize++;
					obj.style.fontSize = CurrentfontSize.toString() + "px";
				}
			}
		}
		
		function decreaseFontSize(){
   			var obj = document.getElementById('content');		
			var fontSize = obj.style.fontSize;
			if(fontSize.length == 0){//Default Size
				//Do nothing
			}else{
				CurrentfontSize = getSize(fontSize);
				if(CurrentfontSize > MinFontSize){
					CurrentfontSize--;
					obj.style.fontSize = CurrentfontSize.toString() + "px";
				}
			}	
		}