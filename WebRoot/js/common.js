﻿﻿﻿/**
 * 弹出框宽度
 */
var globalPopWidth = 600;
var globalPopWidthLong = 800;
﻿/**
 * 弹出框高度
 */
var globalPopHeight = 450;
var globalPopHeightShort = 400;
var globalPopHeightLong = 500;
var globalPopHeightTinny = 300;
﻿/**
 * action字符串
 */
var COMMON_ACTION = "HttpChannel?action=COMMON_ACTION&BUSINESS_TYPE=";
var QUERY_ACTION = "HttpChannel?action=QUERY_ACTION&BUSINESS_TYPE=";
var GOTO_ACTION = "HttpChannel?action=GOTO_ACTION&BUSINESS_TYPE=";
var DEL_ACTION = "HttpChannel?action=DEL_ACTION&BUSINESS_TYPE=";
var SAVE_ACTION = "HttpChannel?action=SAVE_ACTION&BUSINESS_TYPE=";
/**
 * ckeditor配置
 */
var ckeditor_config = {
		height:"120px",
		extraPlugins : 'uicolor',
		removePlugins:'elementspath',
		resize_enabled:false,
		uiColor: "#F7F7F7",
		filebrowserUploadUrl : 'ckeditor/uploader?Type=File',
		filebrowserImageUploadUrl : 'ckeditor/uploader?Type=Image',
		filebrowserFlashUploadUrl : 'ckeditor/uploader?Type=Flash',
		toolbar:
		[
			//[ 'Source' , '-' , 'Save' , 'NewPage' , 'Preview' , '-' ,
			// 'Templates' ],
			//[ 'Cut' , 'Copy' , 'Paste' , 'PasteText' , 'PasteFromWord' , '-'
			// , 'Print' , 'SpellChecker' , 'Scayt' ],
			[ 'Undo' , 'Redo' , '-' , 'Find' , 'Replace' , '-' , 'SelectAll' , 'RemoveFormat' ],
			//[ 'Form' , 'Checkbox' , 'Radio' , 'TextField' , 'Textarea' ,
			// 'Select' , 'Button' , 'ImageButton' , 'HiddenField' ],
			[ 'Bold' , 'Italic' , 'Underline' , 'Strike' , '-' , 'Subscript' , 'Superscript' ],
			//[ 'NumberedList' , 'BulletedList' , '-' , 'Outdent' , 'Indent' ,
			// 'Blockquote' ],
			[ 'JustifyLeft' , 'JustifyCenter' , 'JustifyRight' ],
			[ 'Link' , 'Unlink' ],
			[ 'Image' , 'Flash' , 'Table' , 'HorizontalRule' , 'SpecialChar' , 'PageBreak' ],
			[ 'TextColor' , 'BGColor' ],
			[ 'Maximize' ]
			//['Styles','Format','Font','FontSize']
		]
	};


String.prototype.replaceAll  = function(s1,s2){   
	return this.replace(new RegExp(s1,"gm"),s2);   
}  


//取得顶层topIframe对象
function getPopWindow() {
	return window.top.$("#topIframe")[0].contentWindow;
}
function getCurrTabId() {
	try{
		var currTabId = $(window.top.document).find(".ui-tabs-selected > a").attr("href");
		currTabId = currTabId.substring(1);
		currTabId = currTabId + "-frame";
		return currTabId;
	}catch(e){
		return "";
	}
}
// 取得当前显示的工作区Iframe对象
function getCurrentFrame() {
	// return parent.$("#"+selTabID)[0].contentWindow
	var currTabId = getCurrTabId();
	return window.top.$("#" + currTabId)[0].contentWindow;
}

function deletePopDiv(id) {
	try{
		var d = parent.document.getElementById(id);
		if (d) {
			var p
			if (p = d.parentNode) {
				$(d).find("iframe").attr("src","about:blank");
				p.removeChild(d);
			}
		}
	}catch(e){}
}
function deleteDiv(id) {
	var d = document.getElementById(id);
	if (d) {
		var p
		if (p = d.parentNode) {
			$(d).find("iframe").attr("src","about:blank");
			p.removeChild(d);
		}
	}
}
//弹出新增或修改窗口
function popSaveInfo(url,title,width,height){
	if(url.indexOf("&TMP=")<0){
		url = url + "&TMP=" + new Date();
	}
	pDialog = $.jqTopOpen(url, 
			{ 
				closeOnEscape: true,
				title: title, 
				width: width, 
				height: height, 
				draggable: true
				/*
				 * buttons: { "保存": function() { getPopWindow().saveInfo(); },
				 * "取消": function() { window.top.$(this).dialog( "close" ); } }
				 */
		});
}
//弹出查看窗口
function popReadInfo(url,title,width,height){
	url = url + "&RESOURCE_AUTH=read";
	if(url.indexOf("&TMP=")<0){
		url = url + "&TMP=" + new Date();
	}
	pDialog = $.jqTopOpen(url, 
		{ 
			closeOnEscape: true,
			title: title, 
			width: width, 
			height: height, 
			draggable: true
			/*
			 * buttons: { "取消": function() { window.top.$(this).dialog( "close" ); } }
			 */
	});
}
//公共删除
function commonDelete(url,afterSuccess){
	jQuery.ajax({
        url: url,   // 提交的页面
        data: "", // 从表单中获取数据
        type: "POST",                   // 设置请求类型为"POST"，默认为"GET"
        dataType:"json",
        error: function(request) {      // 设置表单提交出错
            $.uialert("删除出错，请稍候再试");
        },
        success: function(data) {
            $("#searchBtn").click();
            var result = data.result;
            $.uimessage(result);
            if(afterSuccess){
            	afterSuccess.call();
            }
        }
    });
}

//验证表单
function commonValidate(formid,tabid){
	validateForm = false;
	jQuery("#"+formid).validationEngine(
			'attach', 
				{
				tabid:tabid,
				scroll : false,
				validationEventTriggers:"keyup blur",  // will validate on
														// keyup and blur
				relative: true,
				promptPosition : "bottomLeft"
				});
	$("#"+formid).bind("jqv.form.result", function(event , errorFound){
		if(errorFound) {
			validateForm = false;
		}else{
			if(allErr!=""){
				validateForm = false;
				
				$.uishowvalidate(allErr);
				allErr="";
			}else{
				validateForm = true;
			}
		}
	});
}
//提交表单
function submitInfoForm(formid,url,afterSuccess){
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
                $.uierror("表单提交出错，请稍候再试");
            },
            success: function(data) {
                var result = data.result;
                if(result.indexOf("失败")>0){
                	$.uierror(result);
                	return;
                }
                	
                var currTabId = getCurrTabId();
                var pc = parent.$("#"+currTabId).contents();
                if(currTabId==""){
                	pc = $(window.parent.document);
                	if(pc.find("#searchBtn").size()==0){
                		// 如果还是找不到查询按钮，则需要再处理
                	}
                }
                if(pc!=null){
            		pc.find("#searchBtn").click();// 弹出本窗口的iframe页面中的查询按钮
                	if(window.top.checkRelatePage){
                    	window.top.checkRelatePage(pc.find("#RESOURCE_ITEMNO").text(), pc.find("#RESOURCE_RELATEITEMNO").text());// 顶级窗口的检测相关性
                    }
                }
                $.uimessage(result);
                
            	// window.top.$("#"+currTabId)[0].contentWindow.pDialog.dialog("close");
            	if($("#CMD").val()=="A" && result.indexOf("成功")>0 && $("#notClearAfterSave").val()!="Y"){
            		$("#"+formid)[0].reset();
            		
            	}
            	if(result.indexOf("成功") != -1){
            		if($("#closeChecked").attr("id") && !$("#closeChecked").attr("checked")){
            			
            		}else{
            			cancel();
            		}
            	}
            	if(afterSuccess){
            		afterSuccess(data);
            	}
            }
        });
        return false;
    });
}
// 提交表单
function submitHasExtendInfoForm(formid,url,extendFrameId,extendformid,afterSuccess){
	$('#'+formid).submit(function(){
        jQuery.ajax({
            url: url,   // 提交的页面
            data: $('#'+formid).serialize()+"&"+$("#"+extendformid, window.frames[0].document).serialize(), // 从表单中获取数据
            type: "POST",                   // 设置请求类型为"POST"，默认为"GET"
            dataType:"json",
            beforeSend: function()          // 设置表单提交前方法
            {
            	// 验证
                return validateForm;
            },
            error: function(request) {      // 设置表单提交出错
                $.uierror("表单提交出错，请稍候再试");
            },
            success: function(data) {
                var result = data.result;
                if(result.indexOf("失败")>0){
                	$.uierror(result);
                	return;
                }
                	
                var currTabId = getCurrTabId();
                var pc = parent.$("#"+currTabId).contents();
                if(pc!=null){
                	pc.find("#searchBtn").click();// 弹出本窗口的iframe页面中的查询按钮
                	
                	if(window.top.checkRelatePage){
                    	window.top.checkRelatePage(pc.find("#RESOURCE_ITEMNO").text(), pc.find("#RESOURCE_RELATEITEMNO").text());// 顶级窗口的检测相关性
                    }
                }
                $.uimessage(result);
                
            	// window.top.$("#"+currTabId)[0].contentWindow.pDialog.dialog("close");
            	if($("#CMD").val()=="A" && result.indexOf("成功")>0 && $("#notClearAfterSave").val()!="Y"){
            		$("#"+formid)[0].reset();
            		
            	}
            	if(result.indexOf("成功")>0){
            		if($("#closeChecked").attr("id") && !$("#closeChecked").attr("checked")){
            			
            		}else{
            			cancel();
            		}
            	}
            	if(afterSuccess){
            		afterSuccess(data);
            	}
           		// window.parent.$("#"+currTabId)[0].contentWindow.pDialog.dialog("close");//关闭窗口
           		// $('#dialog-window', window.parent.document).remove();
                	
                
            }
        });
        return true;
    });
}
// 计算Div的高度，格式是多个div的ID用,隔开
// 返回值为正数
function calDivsHeight(calDivsId) {
  var arr = calDivsId.split(",");
  var s = 0;
  var obj;
  for (var i = 0; i < arr.length; i++) {
	  if(isNaN(parseInt(arr[i]))){
	      obj = $("#"+arr[i]);
	      if (obj != null) {
	          s = s + obj.outerHeight();
	      }
	  }else{
		  s = s +parseInt(arr[i]);// 数字，校准那些padding
	  }
  }
  return s;
}

// 从iframe里面的页面中获取iframe的高度，传入document参数
function getOpenerFrameHeight(doc){
  var frames = window.parent.frames, frame;  
  for(var i=0;i<frames.length;i++){
  	frame = frames[i];
	if(frame.document == doc){  
    	 return $(frame).height();
	}  
  }
  return $(window.parent).height();
}

/**
 * 从对象数组中删除属性为objPropery，值为objValue元素的对象
 * 
 * @param Array
 *            arr 数组对象
 * @param String
 *            objPropery 对象的属性
 * @param String
 *            objPropery 对象的值
 * @return Array 过滤后数组
 */
function remove(arr,objPropery,objValue)
{
	return $.grep(arr, function(cur,i){
		return cur[objPropery]!=objValue;
	});
}
/**
 * 从对象数组中获取属性为objPropery，值为objValue元素的对象
 * 
 * @param Array
 *            arr 数组对象
 * @param String
 *            objPropery 对象的属性
 * @param String
 *            objPropery 对象的值
 * @return Array 过滤后的数组
 */
function getArrayItem(arr,objPropery,objValue)
{
	return $.grep(arr, function(cur,i){
		return cur[objPropery]==objValue;
	});
} 

jQuery.extend(jQuery, {
	  hkajax: function(url,data, options) {
		  
		  $.ajax($.extend({  
		        async : false,  
		        cache:false,  
		        type: 'POST',  
		        dataType : "json",  
		        url: url,// 请求的action路径
		        data: data,
		        error:  function(xhr,status,error) {// 请求失败处理函数
		        	if(xhr.responseText.indexOf("<html>")>-1){
		        		document.write(xhr.responseText);
		        	}else{
		        		$.uierror('请求失败');  
		        	}
		        } 
		    }, options));  
	  }
});
// 取消
function cancel(){
	try{
		window.top.closeIframe();
	}catch(e){
		
	}
}
// 关闭弹出窗口
function closePop(id){
	window.top.closePopWindow(id);
}
// 权限过滤
function author(auth){
	if(auth=="admin")return;
	$("button[rightval]").each(function(){
		var rightVal = $(this).attr("rightval");
		if(!((auth&rightVal)==rightVal)){
			$(this).remove();
		}
	});
}
// 通用查询
function commonQuery(url,gridid,searchformid,searchBtnId){
	if($.browser.safari) {
		// 谷歌浏览器特殊处理
		var e = getEvent();
        var actObj =  e.target  == document ? null : e.target;
        if(actObj.id==searchBtnId){
        	$("#"+gridid).jqGrid("setGridParam", {  
        		url: url + "&"+$("#"+searchformid).serialize()+"&TMP="+new Date()
    		}).trigger("reloadGrid");   // (10)重新载入Grid表格
        }else{
        	$("#"+gridid).jqGrid("setGridParam", {  
        		url: url + "&"+$("#"+searchformid).serialize()+"&TMP="+new Date()
    		}).trigger("reloadGrid", [{page:1}]);   // (10)重新载入Grid表格
        }
        actid = actObj.id;
    }else{
    	var actid = document.activeElement.id;
    	if(actid==searchBtnId){
    		$("#"+gridid).jqGrid("setGridParam", {  
        		url: url + "&"+$("#"+searchformid).serialize()+"&TMP="+new Date()
    		}).trigger("reloadGrid", [{page:1}]);   // (10)重新载入Grid表格
    	}else{
    		$("#"+gridid).jqGrid("setGridParam", {  
        		url: url + "&"+$("#"+searchformid).serialize()+"&TMP="+new Date()
    		}).trigger("reloadGrid");   // (10)重新载入Grid表格
    	}
    }
}

// 取事件
function getEvent(){     // 同时兼容ie和ff的写法
    if(document.all)    return window.event;       
    func=getEvent.caller;           
    while(func!=null){   
        var arg0=func.arguments[0];
        if(arg0){
            if((arg0.constructor==Event || arg0.constructor ==MouseEvent)
                || (typeof(arg0)=="object" && arg0.preventDefault && arg0.stopPropagation)){   
                return arg0;
            }
        }
        func=func.caller;
    }
    return null;
}
// 级联下拉框
function changeChildSelect(childSelectedId,url){
	$.hkajax(url,"",{
		success:function(data){
			$("#"+childSelectedId).html("");
			$("#"+childSelectedId).append("<option value=''>--请选择--</option>");
			$.each(data.Rows,function(i,item){
				$("#"+childSelectedId).append("<option value='"+item.CODE+"'>"+item.NAME+"</option>");
			});
			$("#"+childSelectedId).change();
		}
	});
}
/**
 * 级联下拉框
 * 
 * @param childSelectedId
 *            子类的selectId
 * @param url
 *            取子类的url
 * @param defaultcode
 *            代码
 * 
 */
function changeChildSelect(childSelectedId,url,defaultValue){
	$.hkajax(url,"",{
		success:function(data){
			$("#"+childSelectedId).html("");
			$("#"+childSelectedId).append("<option value=''>--请选择--</option>");
			$.each(data.Rows,function(i,item){
				var selected = "";
				if(item.CODE==defaultValue){
					selected = "selected";
				}
				
				var op = "<option value='"+item.CODE+"' "+selected+">"+item.NAME+"</option>";
				$("#"+childSelectedId).append(op);
			});
			$("#"+childSelectedId).change();
		}
	});
}
/**
 * 级联下拉框
 * 
 * @param childSelectedId
 *            子类的selectId
 * @param url
 *            取子类的url
 * @param defaultcode
 *            代码
 * 
 */
function changeChildSelect3(childSelectedId,url,defaultValue){
	$.hkajax(url,"",{
		success:function(data){
			$("#"+childSelectedId).html("");
			$("#"+childSelectedId).append("<option value=''>--请选择--</option>");
			$.each(data.Rows,function(i,item){
				var selected = "";
				if(item.CODE==defaultValue){
					selected = "selected";
				}
				
				var op = "<option value='"+item.CODE+"' "+selected+">"+item.NAME+"</option>";
				$("input[id='"+childSelectedId+"']").append(op);
			});
			$("input[id='"+childSelectedId+"']").change();
		}
	});
}
/**
 * 级联下拉框
 * 
 * @param childSelectedId
 *            子类的selectId
 * @param url
 *            取子类的url
 * @param code
 *            代码
 * @param name
 *            名称
 * @param empty
 *            是否可以为空
 */
function changeChildSelect2(childSelectedId,url,code,name,empty){
	$.hkajax(url,"",{
		success:function(data){
			$("#"+childSelectedId).html("");
			if(empty){
				$("#"+childSelectedId).append("<option value=''>--请选择--</option>");
			}
			$.each(data.Rows,function(i,item){
				$("#"+childSelectedId).append("<option value='"+eval("item."+code)+"'>"+eval("item."+name)+"</option>");
			});
			$("#"+childSelectedId).change();
		}
	});
}
// 选择一条数据并自定义对话框内容
//	2014-07-08 ysp
function selectOneMsg(selectedId,msg){
	if(selectedId==""){
		$.uimessage(msg);
		return false;
	}
	return true;
}
//选择一条数据
function selectOne(selectedId){
	if(selectedId==""){
		$.uimessage("请选择一条数据!");
		return false;
	}else if(selectedId.indexOf(",")>0){
		$.uimessage("只能选择一条数据!");
		return false;
	}
	return true;
}
// 选择一条数据
function selectOneOrMore(selectedId){
	if(selectedId==""){
		$.uimessage("请选择数据!");
		return false;
	}
	return true;
}
// 打印数据
function export2Excel(key){
	url = excelaction+"&EXCEL_KEY="+key+"&"+$("#searchform").serialize();
	location.href=url;
}
// 检查是否是汉字，全部是汉字返回true，否则返回false
function checkIsChinese(s_value){		
	if (s_value.match(/[^\u4e00-\u9fa5]/g)){
		return false;
	}
	return true;
}
// 搜索关键字高亮 obj 要设置的对象，flag 0为取消，1为设置，str 查找的字符串
function setFontHighLight(obj,flag,s_nr){
	var s_info=obj.innerHTML;
	if(flag=='0'){
		// 取消高亮的
		var s_tmp='\\<span style\\=\\"color\\: black\\;background\\-color\\: rgb\\(255\\,0\\,255\\)\\;\\"\\>'+s_nr+'\\<\\/span\\>';
		s_info=s_info.replace(new RegExp(s_tmp,"g"),s_nr);// 这种方法在IE中有问题
	}else{
		// 设置高亮的
		var s_tmp='<span style="color: black;background-color: rgb(255,0,255);">'+s_nr+'</span>';
		s_info=s_info.replace(new RegExp(s_nr,"g"),s_tmp);
	}
	obj.innerHTML=s_info;
} 
// 日期控件居中
function setMiddle(){
	// 日期控件居中
	$(".ui-datepicker-trigger").css("vertical-align","middle");
	$('input[datepiker="DATETIME"]').siblings().css("vertical-align","middle");
}
// 扫描所有readonly的元素
function scanReadonly(){
	$.each($(":input[readonly='readonly']"),function(i,item){
		if($(this).is("select")){
			var idx = $(this).get(0).selectedIndex;
		    $(this).change(function(){
		          $(this).children('option').eq(idx).attr("selected",true);
		    });
			$(this).focus(function(e){
				$(this).blur();
				$(this).attr("disabled",true);
				$.uimessage("不能修改数据");
				window.setTimeout(function(){$(item).attr("disabled",false);},1);
			});
		}else if($(this).attr("type")=="checkbox" || $(this).attr("type")=="radio"){
			$(this).focus(function(e){
				$(this).attr("disabled","disabled");
				window.setTimeout(function(){$(item).removeAttr("disabled");},500);
			});
		}else{
			$(this).focus(function(e){
				$(this).blur();
			});
			if($(this).attr("datepiker")!=null && $(this).attr("datepiker")!=""){
				$( "#"+$(this).attr("id") ).unbind("focus");
				$("#"+$(this).attr("id")).focus(function(e){
					$(this).blur();
				});
				$(".ui-datepicker-trigger").css("vertical-align","middle");
				$( "#"+$(this).attr("id") ).siblings().unbind("click");
			}
		}
	});
}

// 计算textarea宽度
function calTextAreaWidth(tableId){
	// 插入一列参考列
	var tab = $("#"+tableId);
	if($(tab).css("display")=="none")return;
	var standtr = "<tr><th></th><td><input/></td><th></th><td><input/></td></tr>";
	var lastTrIndex = $("#"+tableId).find("tr").size();
	$("#"+tableId).append(standtr);
	var width = 0;
	$.each($(".calculateElement"),function(i,item){
		if($(item).parent("td").attr("colspan")=="3"){
			if(width==0){
				width = width + $(tab).find("tr:eq("+lastTrIndex+") th:eq(1)").outerWidth();
				width = width + $(tab).find("tr:eq("+lastTrIndex+") td:eq(0)").outerWidth();
				width = width + $(tab).find("tr:eq("+lastTrIndex+") td:eq(1) input").outerWidth();
			}
			if($(item)[0].tagName=="INPUT"){
				var paddingRight = $(item).css("padding-right");
				if(paddingRight.indexOf("px")>0){
					paddingRight = paddingRight.substring(0,paddingRight.length-2);
				}
				if(paddingRight)
				$(item).css("width",width-5-paddingRight);
			}
			if($(item)[0].tagName=="TEXTAREA"){
				$(item).css("width",width-5);
			}
			if($(item)[0].tagName=="SELECT"){
				$(item).css("width",width-5);
			}
		}
	});
	$(tab).find("tr:eq("+lastTrIndex+")").remove();
}
// 按回车键查询
function registEnterSubmit(formId,searchBtnId){
	$.each($("#"+formId+" :input"),function(i,item){
        	$(item).bind('keydown', function (e) {
            var key = e.which;
            if (key == 13) {
                e.preventDefault();
                $("#"+searchBtnId).click();
                // var nxtIdx = $inp.index(this) + 1;

                // $(":input:text:eq(" + nxtIdx + ")").focus();
            }
        });
	});
}
// 按回车键到下一个输入框
function registEnterNext(formId){
	$.each($("#"+formId+" :input"),function(i,item){
		if(item.nodeName.toUpperCase() == "TEXTAREA"){
			return;
		}
    	$(item).bind('keydown', function (e) {
        var key = e.which;
        if (key == 13) {
            e.preventDefault();
            focusNext(formId, this);
            // var nxtIdx = $("#"+formId+" :input").index(this) + 1;
            // if($("#"+formId+" :input:eq(" + nxtIdx +
			// ")")[0].tagName=="hidden" || $("#"+formId+" :input:eq(" + nxtIdx
			// + ")").css("display")=="none"){
            	
            // }
            // $("#"+formId+" :input:eq(" + nxtIdx + ")").focus();
        }
    });
});
}
// 让下一个元素获得焦点
function focusNext(formId,obj){
	var nxtIdx = $("#"+formId+" :input").index(obj) + 1;
	$("#"+formId+" :input:eq(" + nxtIdx + ")").focus();
    if($("#"+formId+" :input:eq(" + nxtIdx + ")").attr("type")=="hidden" || 
    		$("#"+formId+" :input:eq(" + nxtIdx + ")").css("display")=="none" ||
    		$("#"+formId+" :input:eq(" + nxtIdx + ")").attr("readonly")=="readonly"||
    		$("#"+formId+" :input:eq(" + nxtIdx + ")").attr("disabled")=="disabled"||
    		$("#"+formId+" :input:eq(" + nxtIdx + ")").attr("disabled")==true){
    	focusNext(formId,$("#"+formId+" :input:eq(" + nxtIdx + ")"));
    }
}

/**
 * 添加到SElect中，不能重复
 * 
 * @param selectId
 * @param value
 * @param text
 */
function addSelectOption(selectId,value,text){
	var flag = false;
	$.each($("#"+selectId+" option"),function(i,item){
		if($(item).attr("value")==value){
			flag = true;
		}
	});
	if(!flag){
		var optionStr = "<option value='"+value+"'>"+text+"</option>";
		$("#"+selectId).append(optionStr);
	}
}
/**
 * 修改select数据
 * 
 * @param selectId
 * @param value
 * @param text
 */
function updateSelectOption(selectId,value,text){
	var flag = false;
	$.each($("#"+selectId+" option"),function(i,item){
		if($(item).attr("value")==value){
			flag = true;
		}
	});
	if(flag == true){
		$("#"+selectId+" option[value='"+value+"']").remove();
	}
	var optionStr = "<option value='"+value+"'>"+text+"</option>";
	$("#"+selectId).append(optionStr);
}

// 隐藏验证提示
function hiddenValidatePrompt(isStr){
	try{
		$.each(idStr.spit(","),function(i,item){
			$("#"+item).validationEngine("hide"); 
		});
		
	}catch(e){
		
	}
}
// 判断是否为空
function isEmpty(val){
	if(val.length==0){  
	     return true;
	} 
	return false;
}
// 设置日期区间
function setDateRange(startDateId,endDateId){
	// 日期起始设置
	$("#"+startDateId).mousedown(function(){
		$( "#"+startDateId ).datepicker( "option", "maxDate", $("#"+endDateId).val() );
		setMiddle();
	});
	
	$("#"+endDateId).mousedown(function(){
		$( "#"+endDateId ).datepicker( "option", "minDate", $("#"+startDateId).val() );
		setMiddle();
	});
}

// 设置时间区间
function setDateTimeRange(startDateTimeId, endDateTimeId, startDateTimeText, endDateTimeText){
	if(!startDateTimeText)
		startDateTimeText = "起始时间";
	if(!endDateTimeText)
		endDateTimeText = "结束时间";
	
	// 日期起始设置
	$( "#"+startDateTimeId ).blur(function(){
		if ($.datepicker._datepickerShowing && $.datepicker._lastInput == this){
			return;
		}else{
			var startTime = $( "#"+startDateTimeId ).val();
			var endTime = $("#"+endDateTimeId).val();
			var start = null, end = null;
			if(startTime!="")
				start = new Date(Date.parse(startTime.replace("-","/")));
			if(endTime!="")
				end = new Date(Date.parse(endTime.replace("-","/")));
			if(startTime!="" && endTime!=""){
				if(start>=end){
					showMessage(startDateTimeText + "应小于" + endDateTimeText,function(yes){
						$( "#"+startDateTimeId ).val("").focus();
					});
					return;
				};
			};
		};
	});
	$( "#"+endDateTimeId ).blur(function(){
		if ($.datepicker._datepickerShowing && $.datepicker._lastInput == this){
			return;
		}else{
			var startTime = $( "#"+startDateTimeId ).val();
			var endTime = $("#"+endDateTimeId).val();
			var start = null, end = null;
			if(startTime!="")
				start = new Date(Date.parse(startTime.replace("-","/")));
			if(endTime!="")
				end = new Date(Date.parse(endTime.replace("-","/")));
			if(startTime!="" && endTime!=""){
				if(start>=end){
					showMessage(endDateTimeText + "应大于" + startDateTimeText,function(yes){
						$( "#"+endDateTimeId ).val("").focus();
					});
					return;
				};
			};
		};
	});	
};

function loadTip(formname,color){
	$("body").click(function(){
		$.each($("[title]"),function(i,item){
			try{$(item).tooltip("close");}catch(e){}
		});
	});
	var url = "HttpChannel?action=COMMON_ACTION&BUSINESS_TYPE=platform.tipsetting&BIZMETHODNAME=queryTipSettingDetailByUrl&URL="+formname;
	$.hkajax(url,"",{
		success:function(data){
			if(data!=null){
				$.each(data.Rows,function(i,item){
					if($("input[name='"+item.ELEMENTNAME+"'],select[name='"+item.ELEMENTNAME+"'],textarea[name='"+item.ELEMENTNAME+"'],button[id='"+item.ELEMENTNAME+"']").parent().get(0).tagName=="TD" ){
						$("input[name='"+item.ELEMENTNAME+"'],select[name='"+item.ELEMENTNAME+"'],textarea[name='"+item.ELEMENTNAME+"']").parent().attr("title",item.TIPCONTENT).attr("showtitle","true");
						$("input[name='"+item.ELEMENTNAME+"'],select[name='"+item.ELEMENTNAME+"'],textarea[name='"+item.ELEMENTNAME+"']").parent().css("background",color);
					}else{
						$("button[id='"+item.ELEMENTNAME+"']").attr("title",item.TIPCONTENT).attr("showtitle","true");
					}
				});
				$("[title][showtitle]").bind("mouseleave", function( event ) {
					event.stopImmediatePropagation(); 
					var fixed = setTimeout('try{$("[title]").tooltip("close");}catch(e){}', 100); 
					$(".ui-tooltip").hover(
							function(){clearTimeout(fixed);}, 
							function(){
								$.each($("[title]"),function(i,item){
									try{
										$(item).tooltip("close");
									}catch(e){
										
									}
								});
							}); 
					}).tooltip({
					    open: function (event, ui) {
					        ui.tooltip.css("max-width", "300px").css("z-index","9999");
					    }
					});
			}
		}
	});
}

// 根据表名，取表字段列表
function getFields(tableSelectId,fieldSelectId,defaultFieldName){
	var tableName = $("#"+tableSelectId).find("option:selected").val();
	var selectedText = $("#"+tableSelectId).find("option:selected").text();
	var connId = selectedText.split("-")[2];
	var schema = selectedText.split("-")[3];
	if(connId == "请选择"){
		connId = "";
	}
	if(tableName!=""){
    	changeChildSelect(fieldSelectId,'HttpChannel?action=COMMON_ACTION&BUSINESS_TYPE=form.metadata!queryFieldsByTable&TABLE_NAME='+tableName+"&CONN_ID="+connId+"&SCHEMA="+schema,defaultFieldName);
	}
}

/**
 * 取下拉框选中的文本值
 * 
 * @param selectedId
 */
function getSelectedText(selectedId){
	try{
		if($(selectedId).find("option:selected")
				&& $(selectedId).find("option:selected").text()!=""){
			return $(selectedId).find("option:selected").text();
		}else{
			return $(selectedId).val();
		}
	}catch(e){
		return $(selectedId).val();
	}
	
}

/**
 * 取选中的radio的值
 * 
 * @param radioname
 *            radio的name属性
 * @returns
 */
function getRadioValue(radioname){
	return $("input[name='"+radioname+"']:checked").val();
}
/**
 * 设置radio选中
 * 
 * @param radioname
 *            radio的name属性
 * @param value
 *            被选中的radio的value属性
 */
function setRadioValue(radioname,value){
	$("input[name='"+radioname+"'][value='"+value+"']").attr("checked",true);
}

/**
 * 取checkbox选中的值
 * 
 * @param checkboxname
 *            checkbox的name属性
 * @returns
 */
function getCheckboxValue(checkboxname){
	var val = "";
	$.each($("input[name='"+checkboxname+"']:checked"),function(i,item){
		val += "," + $(item).val();
	});
	if(val!=""){
		return val.substring(1);
	}else{
		return val;
	}
}
String.prototype.trim= function(){  
    // 用正则表达式将前后空格
    // 用空字符串替代。
    return this.replace(/(^\s*)|(\s*$)/g, "");  
}
/**
 * 判断对象是否是空
 * 
 * @param obj
 * @returns
 */
function isNotBlank(obj){
	if(obj==null || obj=="" || obj=="undefined" || obj.trim()==""){
		return false;
	}
	return true;
}
/**
 * 判断对象是否是空
 * 
 * @param obj
 * @returns
 */
function isBlank(obj){
	return !isNotBlank(obj);
}

function put2Json(data,key,value){
	
	return data;
}
$.fn.selectReadOnly=function(){
    var tem=$(this).children('option').index($("option:selected"));
    $(this).change(function(){
          $(this).children('option').eq(tem).attr("selected",true); 
    });
}
/**
 * 弹出职员选择框
 * 
 * @param selectEmpId
 *            已经选中的职员ID
 * @param empCodeInput
 *            code输入框ID
 * @param empNameInput
 *            name输入框ID
 * @param popSource
 *            弹出源POP:弹出窗口，TAB:tab页
 * @param maxSelectedEmp
 *            最多可以选择的人数
 */
function popEmployee(selectedEmpId,empCodeInput,empNameInput,popSource,maxSelectedEmp,callBack){
	var url = COMMON_ACTION+"sso.employee!popEmp";
	url += "&SELECTED_EMP_ID="+selectedEmpId;
	url += "&EMP_CODE_INPUT="+empCodeInput;
	url += "&EMP_NAME_INPUT="+empNameInput;
	url += "&POP_SOURCE="+popSource;// 弹出源：POP:弹出窗口，TAB:tab窗口
	url += "&MAX_SELECTED_EMP="+maxSelectedEmp;// 是否只能选择一个学校
	
	if(callBack){
		url += "&CALLBACK="+callBack;// 回调
	}
	$.uiPopWindow(
			'empDialog',
			url,
			{
				closeOnEscape: true,
				title: "选择窗口", 
				width: 600, 
				height: 450, 
				draggable: true
			});
}
// json对象转字符串
var special={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},escape=function(a){return special[a]||"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4);};jQuery.stringifyJSON=function(b){if(window.JSON&&window.JSON.stringify){return window.JSON.stringify(b);}switch(jQuery.type(b)){case"string":return'"'+b.replace(/[\x00-\x1f\\"]/g,escape)+'"';case"array":return"["+jQuery.map(b,jQuery.stringifyJSON)+"]";case"object":var a=[];jQuery.each(b,function(d,e){var c=jQuery.stringifyJSON(e);if(c){a.push(jQuery.stringifyJSON(d)+":"+c);}});return"{"+a+"}";case"number":case"boolean":return""+b;case"undefined":case"null":return"null";}return b;};


// 检查相关联页面是否更新提示。
function checkRelatePage() {
	
	var currTabId = getCurrTabId();
    var pc = parent.$("#"+currTabId).contents();	
    
	if(pc && window.top.checkRelatePage){
    	window.top.checkRelatePage(pc.find("#RESOURCE_ITEMNO").text(), pc.find("#RESOURCE_RELATEITEMNO").text());// 顶级窗口的检测相关性
    }
}
//动态加载脚本
function loadScript(url, callback){
    var script = document.createElement ("script")
    script.type = "text/javascript";
    if (script.readyState){ //IE
        script.onreadystatechange = function(){
            if (script.readyState == "loaded" || script.readyState == "complete"){
                script.onreadystatechange = null;
                callback();
            }
        };
    } else { //Others
        script.onload = function(){
            callback();
        };
    }
    script.src = url;
    document.getElementsByTagName("head")[0].appendChild(script);
}
