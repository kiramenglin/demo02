//提交表单
function submitRegisterForm(formid,url,afterSuccess){
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
                           
            	if(afterSuccess){
            		afterSuccess(data);
            	}
            }
        });
        return false;
    });
}

//验证表单
function registerValidate(formid,tabid){
	validateForm = false;
	jQuery("#"+formid).validationEngine(
			'attach', 
				{
				tabid:tabid,
				scroll : false,
				validationEventTriggers:"keyup blur",  // will validate on
														// keyup and blur
				relative: true,
				promptPosition : "bottomRight",
				show : false
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