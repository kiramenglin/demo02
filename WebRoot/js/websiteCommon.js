
//处理键盘事件 禁止后退键（Backspace）密码或单行、多行文本框除外
function banBackSpace(e){   
    var ev = e || window.event;//获取event对象   
    var obj = ev.target || ev.srcElement;//获取事件源   
    
    var t = obj.type || obj.getAttribute('type');//获取事件源类型  
    
    //获取作为判断条件的事件类型
    var vReadOnly = obj.getAttribute('readonly');
    var vEnabled = obj.getAttribute('enabled');
    //处理null值情况
    vReadOnly = (vReadOnly == null) ? false : vReadOnly;
    vEnabled = (vEnabled == null) ? true : vEnabled;
    
    //当敲Backspace键时，事件源类型为密码或单行、多行文本的，
    //并且readonly属性为true或enabled属性为false的，则退格键失效
    var flag1=(ev.keyCode == 8 && (t=="password" || t=="text" || t=="textarea") 
                && (vReadOnly==true || vEnabled!=true))?true:false;
   
    //当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效
    var flag2=(ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea")
                ?true:false;        
    
    //判断
    if(flag2){
        return false;
    }
    if(flag1){   
        return false;   
    }   
}
//禁止后退键 作用于Firefox、Opera
document.onkeypress=banBackSpace;
//禁止后退键  作用于IE、Chrome
document.onkeydown=banBackSpace;
//window.onload = banBackSpace;

//知识点弹出框的size
var popKnowledgeWidth = 953;
var popKnowledgeHeight = 610;
var mypageSize = 8;
/**
 * 前台的ajax调用，类似后台的 hkajax
 */
jQuery.extend(jQuery, {
	webAjax: function(url,data, options) {
		$.ajax($.extend({  
	        async : false,  
	        cache:false,  
	        type: 'POST',  
	        dataType : "json",  
	        url: url,// 请求的action路径
	        data: data,
	        error:  function(xhr,status,error) {// 请求失败处理函数
	        	$.alert('友情提示', '亲，您可能太久没在页面活动了，或者网络出现了异常，请刷新页面后再继续吧', "", "", true);
	        } 
	    }, options));  
	}
});

/**
 * 弹出框
 * autoClose: 是否自动关闭， true：是；false：否
 */
jQuery.extend(jQuery, {
	alert: function(title, content, afterSuccess, options, autoClose) {
		try {
			var $modalDiv = window.top.$("#alertMoal");
			if($modalDiv.length > 0){
				$modalDiv.remove();
			}
		} catch (e) {
		}
		var html = '<div class="modal fade bs-example-modal-sm" id="alertMoal" tabindex="-1" role="dialog" aria-labelledby="alertModalLabel" aria-hidden="true">';
		html += ' <div class="modal-dialog"><div class="modal-content"><div class="modal-header">';
		html += '  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>';
		html += '  <h4 class="modal-title" id="alertModalLabel">' + title + '</h4>';
		html += ' </div><div class="modal-body">' + content + '</div><div class="modal-footer">';
		html += '  <button type="button" class="btn btn-default" data-dismiss="modal" >关闭</button>';
		html += '</div></div></div></div>';
		
		//隐藏父页面的滚动条，防止打开modal后父页面还可以一直滚动
		var $body = $("body");
		var overflow = $body.css("overflow-y");
		$body.css("overflow-y", "hidden");
		var $dia = window.top.$(html).on('hide.bs.modal', function (e) {
			//恢复原来顶层页面的滚动条
			$body.css("overflow-y", overflow);
			//如果有回调函数，那么在模态关闭时，执行回调函数
			if(afterSuccess){
				afterSuccess.call();
			}
		}).on('shown.bs.modal', function (e) {
			var $obj = $(this);
			var second = 1000;
			if(options && options.second){
				second = options.second;
			}
			
			if(autoClose){
				setTimeout(function(){
					$dia.modal('hide');
				}, second);
			}
		}).modal($.extend({  
			//该方法必须放在最后，否则在IE8 下，当打开页面时，并不会调用方法 shown.bs.modal，原因：在调用某个事件之前，你必须得给这个事件注册
			keyboard: true
	    }, options));
	},
	/**
	 * 确认框 提示 "是、否、取消"
	 * @param title
	 * @param content
	 * @param fn1
	 * @param fn2
	 * @param fn3
	 * @param options
	 */
	yesorno: function(title, content, fn1, fn2, fn3, options) {
		var id = "yesorno";
		var modalId = id + "Moal";
		var labelId = modalId + "Label";
		try {
			var $modalDiv = window.top.$("#" + modalId);
			if($modalDiv.length > 0){
				$modalDiv.remove();
			}
		} catch (e) {
		}
		var html = '<div class="modal fade bs-example-modal-sm" id="'+ modalId +'" tabindex="-1" role="dialog" aria-labelledby="'+ labelId +'" aria-hidden="true">';
		html += ' <div class="modal-dialog"><div class="modal-content"><div class="modal-header">';
		html += '  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>';
		html += '  <h4 class="modal-title" id="'+ labelId +'">' + title + '</h4>';
		html += ' </div><div class="modal-body">' + content + '</div><div class="modal-footer">';
		html += '  <button type="button" class="btn btn-primary yes-btn" data-dismiss="modal" style="width:53px;" onclick="javascript:this.value=0">是</button>';
		html += '  <button type="button" class="btn btn-default no-btn" data-dismiss="modal" style="width:53px;" onclick="javascript:this.value=1">否</button>';
		html += '  <button type="button" class="btn btn-default cancel-btn" data-dismiss="modal" onclick="javascript:this.value=2">取消</button>';
		html += '</div></div></div></div>';
		
		//隐藏父页面的滚动条，防止打开modal后父页面还可以一直滚动
		var $body = $("body");
		var overflow = $body.css("overflow");
		$body.css("overflow", "hidden");
		var $dia = window.top.$(html).modal($.extend({  
			keyboard: true
	    }, options)).on('hide.bs.modal', function (e) {
	    	//恢复原来顶层页面的滚动条
	    	$body.css("overflow", overflow);
			//如果有回调函数，那么在模态关闭时，执行回调函数
	    	var value = $(this).find("button[value]").val();
			if(fn1 && value == "0"){
				fn1.call();
			}else if(fn2 && value == "1"){
				fn2.call();
			}else if(fn3 && value == "2"){
				fn3.call();
			}
			
			try{
				//删除原来有的层
				$(this).remove();
			}catch(err){}
		});
	},
	/**
	 * 确认框 提示 "确认、取消"
	 * @param title
	 * @param content
	 * @param fn1
	 * @param fn2
	 * @param options
	 */
	comfirm: function(title, content, fn1, fn2, options) {
		var id = "confirm";
		var modalId = id + "Moal";
		var labelId = modalId + "Label";
		try {
			var $modalDiv = window.top.$("#" + modalId);
			if($modalDiv.length > 0){
				$modalDiv.remove();
			}
		} catch (e) {
		}
		var html = '<div class="modal fade bs-example-modal-sm" id="'+ modalId +'" tabindex="-1" role="dialog" aria-labelledby="'+ labelId +'" aria-hidden="true">';
		html += ' <div class="modal-dialog"><div class="modal-content"><div class="modal-header">';
		html += '  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>';
		html += '  <h4 class="modal-title" id="'+ labelId +'">' + title + '</h4>';
		html += ' </div><div class="modal-body">' + content + '</div><div class="modal-footer">';
		html += '  <button type="button" class="btn btn-primary yes-btn" data-dismiss="modal" onclick="javascript:this.value=0">确认</button>';
		html += '  <button type="button" class="btn btn-default cancel-btn" data-dismiss="modal" onclick="javascript:this.value=2">取消</button>';
		html += '</div></div></div></div>';
		
		//隐藏父页面的滚动条，防止打开modal后父页面还可以一直滚动
		var $body = $("body");
		var overflow = $body.css("overflow");
		$body.css("overflow", "hidden");
		var $dia = window.top.$(html).modal($.extend({  
			keyboard: true
	    }, options)).on('hide.bs.modal', function (e) {
	    	//恢复原来顶层页面的滚动条
	    	$body.css("overflow", overflow);
			//如果有回调函数，那么在模态关闭时，执行回调函数
	    	var value = $(this).find("button[value]").val();
			if(fn1 && $.isFunction(fn1) && value == "0"){
				fn1.apply();
			}else if(fn2 && $.isFunction(fn2) && value == "2"){
				fn2.apply();
			}
			
			try{
				//删除原来有的层
				$(this).remove();
			}catch(err){}
		});
	},
	/**
	 * 确认框
	 */
	confirmProduct: function(title, content,url, afterSuccess, options) {
		try {
			var $modalDiv = window.top.$("#confirmMoal");
			if($modalDiv.length > 0){
				$modalDiv.remove();
			}
		} catch (e) {
		}
		var html = '<div class="modal fade bs-example-modal-sm" id="alertMoal" tabindex="-1" role="dialog" aria-labelledby="confirmModalLabel" aria-hidden="true">';
		html += ' <div class="modal-dialog"><div class="modal-content"><div class="modal-header">';
		html += '  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>';
		html += '  <input type="hidden" id="inpconfirm"/>';
		html += '  <h4 class="modal-title" id="confirmModalLabel">' + title + '</h4>';
		html += ' </div><div class="modal-body">' + content + '</div><div class="modal-footer">';
		html += '  <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="$(\'#inpconfirm\').val(1)">确认</button>';
		html += '  <button type="button" class="btn btn-default" data-dismiss="modal" onclick="$(\'#inpconfirm\').val(0)">关闭</button>';
		html += '</div></div></div></div>';

		//隐藏父页面的滚动条，防止打开modal后父页面还可以一直滚动
		var $body = $("body");
		var overflow = $body.css("overflow");
		$body.css("overflow", "hidden");
		window.top.$(html).modal($.extend({  
			keyboard: true
		}, options)).on('hide.bs.modal', function (e) {
			//恢复原来顶层页面的滚动条
			$body.css("overflow", overflow);
			var inpvalue = window.top.$('#inpconfirm').val();
			//确认的回调函数
			if(inpvalue == 1){
				if(url){
					$.modelOpen("order",url,"课程产品","no","800", "580");
				}
			}
			//如果有回调函数，那么在模态关闭时，执行回调函数
			if(afterSuccess){
				afterSuccess.call();
			}
			try{
				//删除原来有的层
				$(this).remove();
			}catch(err){}
		});
		
		
	},
	/**
	 * 
	 * @param url 			请求路径
	 * @param title 		标题
	 * @param isscroll 		是否留滚动条
	 * @param mwidth 		modal宽
	 * @param mheight 		modal高
	 * @param istwo         是否第二次弹出
	 * @param afterSuccess	关闭按钮的回调函数
	 * @param options		modal参数
	 */
	modelOpen: function(id,url, title,isscroll,mwidth,mheight,istwo,afterSuccess,maxCallback,minCallback, options) {
		
		var divid = id || 'defualtName';
		
		var divId = divid + "Modal";
		var iframeId = divid + "Iframe";
		var titleLabelId = divid + "ModalLabel";
		
		try{
			//删除原来有的层
			window.top.$("#" + iframeId).contents().find("body").empty();
			window.top.$("#" + iframeId).attr("src","about:blank");
			window.top.$("#" + iframeId).remove();
		}catch(err){}
		
		var top;
		 if(istwo){
			  top= parseInt($(window).height()/3);//屏幕居中  
		 }else{
			 top = 20;  
		 }
	     
		mwidth = 'undefined' != mwidth && '' != mwidth && !isNaN(mwidth) ? mwidth :undefined;
		mheight = 'undefined' != mheight && '' != mheight && !isNaN(mheight) ? mheight :undefined;
		var sizeObj = $.extend({},{width:500,height:500},{width:mwidth,height:mheight});
		var html = '<div class="modal fade" id="'+divId+'" tabindex="-1" role="dialog" aria-labelledby="' + titleLabelId + '" aria-hidden="true">' +
		' 	<div class="modal-dialog" id="modal_dialog_customId" style="margin:0 auto;width: ' + sizeObj.width  + 'px;height:' + sizeObj.height + 'px;top:'+top+'px;">' +
		' 		<div class="modal-content" style="border-radius: 0 !important">' +
		' 			<div class="modal-header" style="background:#2184df;color:#fff;height:30px;line-height:40px;">' +
		'  				<button class="close" type="button"style="color:#fff;margin-top:-10px;" data-dismiss="modal" aria-hidden="true"><i class="window-tools close-btn"></i></button>' ;
		if(maxCallback && $.isFunction(maxCallback) && minCallback && $.isFunction(minCallback)){
			html += '  				<button class="close" id="bigBtn" type="button" style="color:#fff;margin-top:-10px;margin-right:10px;" onclick="bigWindow(\''+divId+'\');"><i class="window-tools max-btn"></i></button>' +
					'  				<button class="close hide" id="recBtn" type="button" style="color:#fff;margin-top:-10px;margin-right:10px;" onclick="recWindow(\''+divId+'\',\''+sizeObj.width+'\',\'20\',\''+sizeObj.height+'\')"><i class="window-tools restore-btn"></i></button>' ;
		}
		html +='  				<h4 class="modal-title" id="' + titleLabelId + '" style="margin-top:-10px;font-size:16px !important">' + title + '</h4>' +
		' 			</div>' +
		' 			<div class="modal-body" style="padding:0px;overflow:hidden;">' +
		' 				<iframe id="' + iframeId + '" scrolling="' + isscroll + '" frameborder="0" src="' + url + '" style="width: ' + (sizeObj.width-2)  + 'px;height:' + sizeObj.height + 'px;border: none;"></iframe>' +
		'  			</div>' +
		'		</div>' +
		'	</div>' +
		'</div>';
			
		//隐藏父页面的滚动条，防止打开modal后父页面还可以一直滚动
		var $body = $("body");
		var overflow = $body.css("overflow");
		$body.css("overflow", "hidden");
		window.top.$(html).modal($.extend({  
			keyboard: false,
			backdrop: false
	    }, options)).on('hide.bs.modal', function (e) {
	    	//恢复原来顶层页面的滚动条
	    	$body.css("overflow", overflow);
			//如果有回调函数，那么在模态关闭时，执行回调函数
			if(afterSuccess){
				afterSuccess.call();
			}
		});
	},
	/**
	 * 弹出框
	 * @param id 弹出框ID
	 * @param url 
	 * @param title
	 * @param width
	 * @param height
	 * @param afterSuccess 当打开页面关闭时调用回调函数
	 * @param options 弹出框参数
	 */
	webPopWindow: function(id, url, title, width, height, afterSuccess, options) {
		var divId = id + "Modal";
		var iframeId = id + "Iframe";
		var titleLabelId = id + "ModalLabel";
		try{
			//删除原来有的层
			window.top.$("#" + iframeId).contents().find("body").empty();
			window.top.$("#" + iframeId).attr("src","about:blank");
			window.top.$("#" + iframeId).remove();
			window.top.$("#" + divId).remove();
		}catch(err){}
		
		//删除原来有的层
		//window.top.document.getElementById(divId).remove();
		width = 'undefined' != width && '' != width && !isNaN(width) ? width :undefined;
		height = 'undefined' != height && '' != height && !isNaN(height) ? height :undefined;
		var sizeObj = $.extend({},{width:500,height:500},{width:width,height:height});
		var html = '<div class="modal fade" id="' + divId + '" tabindex="-1" role="dialog" aria-labelledby="' + titleLabelId + '" aria-hidden="true">';
		html += ' <div class="modal-dialog" style="width: ' + sizeObj.width  + 'px;height: ' + sizeObj.height + 'px;">';
		html += '  <div class="modal-content" >';
		html += '   <div class="modal-header pop-window-head">';
		html += '  	 <button type="button" class="close modal-close-btn" data-dismiss="modal" aria-hidden="true">&times;</button>';
		html += '  	 <h5 class="modal-title" id="' + titleLabelId + '">' + title + '</h5>';
		html += ' 	</div>';
		html += ' 	<div class="modal-body pop-window-body" style="width: ' + sizeObj.width  + 'px;height: ' + (sizeObj.height - 50) + 'px;">';
		html += ' 	 <iframe id="' + iframeId + '" scrolling="auto" frameborder="0" src="' + url + '"  style="width: ' + (sizeObj.width-10)  + 'px;height:' + (sizeObj.height- 50) + 'px;border: 0;"></iframe>';
		html += ' 	</div>';
		html += '  </div>';
		html += ' </div>';
		html += '</div>';
		//隐藏父页面的滚动条，防止打开modal后父页面还可以一直滚动
		var $body = $(window.parent.document.body);
		var overflow = $body.css("overflow");
		$body.css("overflow", "hidden");
		window.top.$(html).modal($.extend({  
			keyboard: false,
			backdrop: 'static'
	    }, options)).on('hide.bs.modal', function (e) {
	    	//恢复原来顶层页面的滚动条
	    	$body.css("overflow", overflow);
			//如果有回调函数，那么在模态关闭时，执行回调函数
			if(afterSuccess){
				afterSuccess.call();
			}
		});
	}
});

/**
 * 最大化
 * @param divId
 */
function bigWindow(divId){ 
	//获取屏幕高度
	var clientHeight = document.documentElement.clientHeight;
	var $modall = window.top.$("#" + divId);
	$modall.find(".modal-dialog").css({
		"width": screen.availWidth - 30,// 减去 滚动条的宽度
		"top": "0.5px",
		"height": clientHeight
	});
	
	$modall.find(".modal-body").css({
		"height": clientHeight - 45      // 减去 标题行高度
	});
	
	$modall.find("#bigBtn").addClass("hide");
	$modall.find("#recBtn").removeClass("hide");
}

/**
 * 还原
 * @param divId
 * @param width
 * @param top
 * @param height
 */
function recWindow(divId, width, top, height){ 
	//var clientHeight = document.documentElement.clientHeight;
	var $modall = window.top.$("#" + divId);
	$modall.find(".modal-dialog").css({
		"width": width,// 减去 
		"top": "20px",
		"height": height    // 减去 
	});
	
	$modall.find(".modal-body").css({
		"height": height - 3   // 减去 
	});
	
	$modall.find("#bigBtn").removeClass("hide");
	$modall.find("#recBtn").addClass("hide");
}


/**
 * 删除弹出框
 * @param dialogId
 */
function delPopWindow(dialogId){
	var $modal = window.top.$("#" + dialogId + "Modal");
	if($modal.length > 0){
		$modal.modal("hide");
	}
}

/**
 * 初始化分页栏
 * @param paperId 分页栏ID
 * @param data 后台返回值
 * @param afterSuccess 回调函数
 * @param options 参数设置
 */
function initPager(paperId, data, afterSuccess, options,isnonedata){
	initPagerWithData(paperId, data.CurrentPage, data.TotalPage, data.TotalRows, afterSuccess, options,isnonedata);
}

/**
 * 初始化分页栏
 * @param paperId 分页栏ID
 * @param currPage 当前页
 * @param totalPage 总页数
 * @param totalRows 总记录数
 * @param afterSuccess 回调函数
 * @param options 参数设置
 */
function initPagerWithData(paperId, currPage, totalPage, totalRows, afterSuccess, options,isnonedata){
	//分页栏起始页码的判定：1、总页数 <= pageBtnNum，那么下标从 1 到 总页数； 2、总页数 > pageBtnNum && 当前页 <= pageBtnNum/2，那么下标从 1 到 pageBtnNum
	//3、否则 从当前页开始，昨天显示 pageBtnNum/2 个按钮，余下的算右边
	//属性含义：每页记录数，第几页，分页栏中共显示多少个按钮，是否显示首尾、上下页的按钮('1':显示)，是否显示“益玖为您找到 X 条记录”('1':显示)，当只有一页时是否显示分页栏
	var pagerJson = $.extend({},{
		"pageSize" : 10,
		"pageNumber" : 1,
		"pageBtnNum" : 8,
		"showHeadAndTail": "1",
		"showTotalRecord" : "1",
		"hidePaperOnOnePage": "1"
	}, options);
	
	var html = '';
	var pageBtnNum = parseInt(pagerJson.pageBtnNum);
	var pageNumber = parseInt(pagerJson.pageNumber);
	
	//循环起始下标、截止下标； 当前页按钮左边有几个按钮，右边有几个按钮
	var startIndex = 0;
	var lastIndex = 0;
	var currLeftNum = (pageBtnNum / 2).toFixed(0);
	var currRightNum = ((pageBtnNum / 2) - 1).toFixed(0);
	if(parseInt(totalPage) <= pageBtnNum) {
		startIndex = 1;
		lastIndex = totalPage;
	}else if(parseInt(currPage) <= currLeftNum){
		startIndex = 1;
		lastIndex = pageBtnNum;
	}else{
		if(parseInt(currPage) + parseInt(currRightNum) <= parseInt(totalPage)){
			startIndex = parseInt(currPage) - parseInt(currLeftNum);
			lastIndex = parseInt(currPage) + parseInt(currRightNum);
		}else{
			lastIndex = totalPage;
			startIndex = parseInt(totalPage) - parseInt(pageBtnNum) + 1;
		}
	}
	
	for ( var index = startIndex; index <= lastIndex; index++) {
		if(parseInt(currPage) != parseInt(index)){
			html += '<li data-index="' + index + '"><a href="javascript:void(0)">' + index + '</a></li>';
		}else{
			html += '<li class="active" data-index="' + index + '"><a href="javascript:void(0)">' + index + '</a></li>';
		}
	}
	
	//当前页等于1：首页、上一页不可点击；等于最后一页：尾页、下一页不可点击
	if(parseInt(totalPage) != 0 && ( parseInt(totalPage) > parseInt(pageBtnNum) || pagerJson.showHeadAndTail == "1")) {
		var cls = '';
		if(parseInt(currPage) == 1){
			cls = ' class="disabled" ';
		}
		var headHtml = '<li' + cls + ' data-index="1"><a href="javascript:void(0)">首页</a></li>';
		headHtml += '<li' + cls + ' data-index="' + (parseInt(currPage) - 1) + '"><a href="javascript:void(0)">上一页</a></li>';	
		
		cls = '';
		if(parseInt(currPage) == parseInt(totalPage)){
			cls = ' class="disabled" ';
		}
		var tailHtml = '<li' + cls + ' data-index="' + (parseInt(currPage) + 1) + '"><a href="javascript:void(0)">下一页</a></li>';
		tailHtml += '<li' + cls + ' data-index="' + totalPage + '"><a class="paper-last-page" href="javascript:void(0)">尾页</a></li>';
	
		html = headHtml + html + tailHtml;
	}
	
	if(parseInt(totalPage) == 0){
		if(!isnonedata){
			html = '<span class="text-muted" style="font-size:14px;">暂无数据</span>';
		}
	}else if(pagerJson.showTotalRecord == "1"){
		html = '<ul class="pagination">' + html + '<li><span>找到 ' + totalRows + ' 条记录</span></li></ul>';
	}else{
		html = '<ul class="pagination">' + html + '</ul>';
	}
	
	if(pagerJson.hidePaperOnOnePage == "1" && parseInt(totalPage) == 1){
		//清空内容
		$("#" + paperId).html("");
		return;
	}

	$("#" + paperId).html( html).find("li").not(".active,.disabled").click(function(){
		var dataIndex = $(this).attr("data-index");
		if(afterSuccess && dataIndex){
			afterSuccess.call(this, $(this).attr("data-index"), pagerJson.pageSize);
		}
	});
}

/**
 * 知识点选择
 * @param courseId
 * @param selectValue
 * @param codeInput
 * @param nameInput
 * @param maxSelectd 最多可以选择的数量
 * @param iframeId 方法afterSuccess所在的iframe页面
 * @param afterSuccess
 */
function popKnowledgeSelect(courseId, selectValue, codeInput, nameInput, maxSelectd, iframeId, afterSuccess){
	var url = "HttpChannel?action=GOTO_ACTION&BUSINESS_TYPE=w.learn.knowledge&GOTO_TYPE=SELECT";
	url += "&COURSE_ID=" + courseId;
	url += "&SELECT_VALUE=" + selectValue;
	url += "&CODE_INPUT=" + codeInput;
	url += "&NAME_INPUT=" + nameInput;
	url += "&MAX_SELECTED=" + maxSelectd; 
	if(iframeId){
		url += "&IFRAME_ID=" + iframeId; 
	}
	if(afterSuccess){
		url += "&CALLBACK=" + afterSuccess;
	}
	$.webPopWindow("knowledgeDialog", url, "知识点选择", 700, 500, "", "");
}

/**
 * 习题解析
 */
function qsAnalyse(qsId, subSiteOrg){
	var url = "HttpChannel?action=GOTO_ACTION&BUSINESS_TYPE=w.question.question&CMD=ANALYSE&QUESTION_ID=" + qsId;
	if(subSiteOrg && subSiteOrg != undefined){
		url += "&sub_site_org=" + subSiteOrg;
		url += "&ORG_ID=" + subSiteOrg;
	}
	$.webPopWindow("questionAnalyse", url, "习题解析", 800, 600, "", {keyboard: true, backdrop: true});
}

//动态加载脚本
function loadScript(url, callback){
    var script = document.createElement ("script");
    script.type = "text/javascript";
    if (script.readyState){ //IE
        script.onreadystatechange = function(){
            if (script.readyState == "loaded" || script.readyState == "complete"){
                script.onreadystatechange = null;
                if(callback){
                	callback();
                }
            }
        };
    } else { //Others
        script.onload = function(){
            if(callback){
            	callback();
            }
        };
    }
    script.src = url;
    document.getElementsByTagName("head")[0].appendChild(script);
}

/**
 * 设置按钮在点击后立马变成disabled，并且在1秒后恢复成可点击状态
 * 
 * @param selector jquery选择器 如 #id, .cls
 */
function enabledBtn( selector){
	var sel = "button.enabled-btn";
	if(selector && selector != undefined){
		sel = selector; 
	}
	$(sel).on('keydown', function(){
		var $btn = $(this);
		$(this).attr("disabled", "disabled");
		setTimeout(function(){
			$btn.removeAttr("disabled");
		}, 1000);
	});
}

//验证表单
function commonValidateWeb(formid, tabid){
	validateForm = false;
	jQuery("#"+formid).validationEngine(
		'attach', {
			tabid: tabid,
			scroll: false,
			validationEventTriggers:"keyup blur",  // will validate on keyup and blur
			relative: true,
			promptPosition: "bottomLeft"
		});
	$("#"+formid).bind("jqv.form.result", function(event , errorFound){
		if(errorFound) {
			validateForm = false;
		}else{
			if(allErr!=""){
				validateForm = false;
				$.alert(allErr);
				allErr="";
			}else{
				validateForm = true;
			}
		}
	});
}
//提交表单
function submitInfoFormWeb(formid, url, afterSuccess){
	$('#'+formid).submit(function(e){
        jQuery.ajax({
            url: url,   // 提交的页面
            data: $('#'+formid).serialize(), // 从表单中获取数据
            type: "POST",                   // 设置请求类型为"POST"，默认为"GET"
            dataType:"json",
            beforeSend: function()          // 设置表单提交前方法
            {
            	//验证
            	if(!validateForm){
            		//验证不通过
            	}else{
            		//验证通过
            	}
                return validateForm;
            },
            error: function(request) {      // 设置表单提交出错
                $.alert("表单提交出错，请稍候再试");
            },
            success: function(data) {
            	if(afterSuccess){
            		afterSuccess(data);
            	}
            }
        });
        return false;
    });
}

String.prototype.replaceAll  = function(s1,s2){   
	return this.replace(new RegExp(s1,"gm"),s2);   
};

/**
 * 获取window.top下的iframe里面的元素，返回html对象，返回null表示找不到
 * @param iframeId
 * @param elementId
 */
function getEleInIframe(id, elementId){
	var iframeId = id + "Iframe";
	if( window.top.frames[iframeId] != null && window.top.frames[iframeId].document.getElementById(elementId) != null){
		return window.top.frames[iframeId].document.getElementById(elementId);
	}
	return null;
}

