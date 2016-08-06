
function log(str){
	//document.getElementById("logger").value = document.getElementById("logger").value + str;
	android.toast(str);
}

(function(){
	
	var lastTime = new Date().getTime();
	window.onscroll = function(){
		var top = document.body.scrollTop;
		var id = getUrlParams(window.location.href).id;
		var curTime = new Date().getTime();
		if((curTime - lastTime) > 1000){
			android.logScrollTop(parseInt(id),parseInt(top));
			//log(parseInt(top));
			lastTime = curTime;
		}
	}

	window.onload = function(){
		var lastTop = getUrlParams(window.location.href).lastTop;
		//log("onload:" + lastTop);
		window.scrollTo(0,parseInt(lastTop));
	}
	
	window.onerror = function(){
		log("error");
	}
	
})()

function getUrlParams(url) {
  var params = {};

  url.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(str, key, value) {
    params[key] = value;
  });

  return params;
}