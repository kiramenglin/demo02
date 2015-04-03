

$(document).ready(function(){	
	var i=0;
	var sld=0;
	var res=0;
	var len=$("#issue").find("div.cnt").length;	
	var prog=100;  //$("#prog").css("width");
	var ht=420;
	var speed=500;
	var page="data/result.html";
	
	function setporogress(j){			
		i+=j;
		i=(i<0)? 0:i;
		checkbtn();
		if(!$("#info").is(":animated")){			
			$("#tips span").html((i+1>len?len:i+1)+"\/"+len+"��");		
			var wh=$("#tips").get(0).offsetWidth;		
			var ress=Math.round(i*prog/len);		
			$("#ress").css({"width": ress+"px"});		
			$("#tips").css({"left":ress-Math.round(wh/2)+"px"});
			$("#issue").animate({"top": -i*ht + "px"},500);
			}
		}
		
	function selec(ele){			
		sld++;
		//alert(sld);
		ele= $(ele)? $(ele):ele;
		ele.parents("div.cnt").addClass("selected");
		ele.addClass("sel");
		res+=parseInt (ele.find("input").val());
		if(sld<len){
			$("#temp").html("��Ŀǰ�÷��� "+res);		
			}else if(sld==len){
				$("#temp").html("�����յ÷��� "+res);		
				}
		}
		
	$("#prev").click(function(){
		checkbtn();					  
		if(i>0) {
			setporogress(-1)
			}
		})	
	$("#next").click(function(){
		checkbtn();					  
		if(sld>i) {
			setporogress(1)
			}
		})	
	
	$("#issue").find("li").click(function(){								  
			if(!$(this).parents("div.cnt").hasClass("selected")){							
				selec(this);				
				}			
			setporogress(1);
			
			if(i==len){
				result(res);
				}	
			return false;	
		})
	.hover(
		function(){$(this).addClass("hover")},
		function(){$(this).removeClass("hover")}
		)
	
	function result(k){
		var toload;
		if(k>=12 && k<=18) toload=page+" #re0";
		if(k>18 && k<=24) toload=page+" #re1";
		if(k>24 && k<=48) toload=page+" #re2";		
		$("#result").load(toload);		
		}
/*	$("div.selected").click(function(){
		$(this).find("li").click();
		})
	*/
	function warn(txt){
		$("#war").html(txt).fadeIn(500,function(){$("#war").html("").fadeOut(500)});		
		}
	
	
	function checkbtn(){
		if(i<=0){
			$("#prev").addClass("noprev");
			}else{
				$("#prev").removeClass("noprev");
				}
		if(sld<=i){
			$("#next").addClass("nonext");
			}else{
				$("#next").removeClass("nonext");
				}	
		}
	
	setporogress(i);
	
	

})