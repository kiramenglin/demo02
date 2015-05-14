
$(document).ready(function(){	
	var ua = navigator.userAgent.toLowerCase();
	var binfo =
	{
		ve : ua.match(/.+(?:rv|it|ra|ie|me)[\/: ]([\d.]+)/)[1],
		ie : /msie/.test(ua) && !/opera/.test(ua),
		op : /opera/.test(ua),
		sa : /version.*safari/.test(ua),
		ch : /chrome/.test(ua),
		ff : /gecko/.test(ua) && !/webkit/.test(ua)
	};
	var xmlurl="data/questions.xml";
	var page="data/result.html";					   
	$.ajax({
		   type:"GET",
		   url:xmlurl,
		   dataType:'xml',
		   timeout:1500,
		   error:function(){alert("Error loading xml!")},   
		   success:function(xml){
			   $("#loader").fadeOut(200);
			    var i=0;
				var sld=0;
				var res=0;	
				var prog=100;  //$("#prog").css("width");
				var ht=420;
				var speed=500;					   
				var len=12
			    var len=$(xml).find("quesiton").length;
			 
			  $(xml).find("quesiton").each(function(){			
				var h3="<h3>"+$(this).find("title").text()+"</h3>";				
				var li="";				
				$(this).find("opt").each(function(){
					li+="<li><span>&nbsp;</span><label><input type='radio' value='"+$(this).attr("valume")+"'\/>"+$(this).text()+"</label></li>";					
					})				
				var ul="<ul>"+li+"</ul>";				
				$("<div class='cnt'></div>").html(h3+ul).appendTo($("#issue"));				
				})		  
			  
			  function setporogress(j){			
				i+=j;
				i=(i<0)? 0:i;
				checkbtn();
				if(!$("#info").is(":animated")){			
					$("#tips span").html((i+1>len?len:i+1)+"\/"+len+"题");		
					var wh=$("#tips").get(0).offsetWidth;		
					var ress=Math.round(i*prog/len);		
					$("#ress").css({"width": ress+"px"});		
					$("#tips").css({"left":ress-Math.round(wh/2)+"px"});
					$("#issue").animate({"top": -i*ht + "px"},500);
					}
				}
		
			function selec(ele){			
				sld++;				
				ele= $(ele)? $(ele):ele;
				ele.parents("div.cnt").addClass("selected");
				ele.addClass("sel");
				res+=parseInt (ele.find("input").val());
				
				if(sld<len){
					$("#temp").html("您目前得分是 "+res);		
					}else if(sld==len){
						$("#temp").html("您最终得分是 "+res);		
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
						result(res*25/12);
						alert("a");
						$.ajax({
							   type:"POST",
							   url:"HttpChannel?action=SAVE_ACTION&BUSINESS_TYPE=patient.question&RES="+(res*25/12),
							   dataType:'json',
							   timeout:1500,
							   error:function(){alert("Error loading xml!")},   
							   success:function(){
								   alert("success");
							   }
						});
						alert(res*25/12);
						}	
					return false;	
				})
	
			function result(k){
				var toload;
				var sult=$("<div class='result' id='result'></div>");
				if(k>=0 && k<=12) toload=page+" #re0";
				if(k>12 && k<=22) toload=page+" #re1";
				if(k>24 && k<=32) toload=page+" #re2";	
				if(k>32) toload=page+" #re3";	
				sult.load(toload).appendTo($("#issue"));		
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
	}	
		   
	})
})