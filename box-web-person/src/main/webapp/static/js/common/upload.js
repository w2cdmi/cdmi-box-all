$(function(){
	var topTotalWidth = $('#top').width();
	var topUnit = topTotalWidth/100;
	var isStart=true;
	
	var totalWidth = $('.bottom_r').width();
	var unit = totalWidth/100
	
	$('#kaishi').click(function(){
		if(isStart){
			bartimer = window.setInterval(function(){setProgress()},10);
			isStart = false;
		}else{
			clearInterval(bartimer);
			isStart = true;
		}
	});
	
	function setProgress(){
		var topCurrentWidth = $('.midd').width()+topUnit;
		if((parseInt($('.midd').width()+topUnit)) > topTotalWidth){
			$(".pre").html("完成");
			clearInterval(bartimer);
			return;
		}
		$('.midd').width(topCurrentWidth);
		$(".pre").html(parseInt(topCurrentWidth/topTotalWidth*100) + "%");
		var currentWidth = $('#xjdt').width()+unit;
		$('#xjdt').width(currentWidth);
	}
});


