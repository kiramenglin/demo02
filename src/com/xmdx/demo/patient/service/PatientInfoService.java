package com.xmdx.demo.patient.service;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.ImgUtil;
import com.e9rj.platform.common.services.BusinessServices;
import com.e9rj.platform.util.SessionUtil;
import com.xmdx.demo.back.dao.ZkgkConstants;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;
@Service(name="patient.info")
public class PatientInfoService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "back.patient";
	//表名
	private static final String tableName = "TS_OP";
	//主键名
	private static final String keyField = "PERSON_ID";
	
	private static final int UPLOAD_SUCCSSS=0;    // "上传文件成功！",   
    private static final int UPLOAD_FAILURE=1;    // "上传文件失败！"),   
    private static final int UPLOAD_TYPE_ERROR=2; // "上传文件类型错误！"),   
    private static final int UPLOAD_OVERSIZE=3;   // "上传文件过大！"),  
    private static final int UPLOAD_ZEROSIZE=4;   // "上传文件为空！"),  
    private static final int UPLOAD_NOTFOUND=5;   // "上传文件路径错误！")  
	@Override
	public int delete(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int goTo(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int init(ActionContext ac) throws Exception {
		String userName=SessionUtil.getOpno(ac);
		System.out.println(userName+"这个是sessionid哦");
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String id =po[0].getString("PERSON_ID");
		
		StringBuilder sqlp = new StringBuilder("SELECT * FROM PATIENT U ");
		if(StringUtils.isNotBlank(id)) {
			sqlp.append(" WHERE U.PATIENT_ID LIKE '%").append(id).append("%' ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), sqlp.toString());
		ac.setObjValue("USER", pop[0]);
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/patient/patient_info.html");		
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int insertExportData(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int query(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int save(ActionContext ac) throws Exception {
		System.out.println("a");
		PrintWriter out=ac.getHttpResponse().getWriter();
		String rootPath = "http://127.0.0.1:8080/demo02";    
        
        
        FileItemFactory factory = new DiskFileItemFactory();    
        ServletFileUpload upload = new ServletFileUpload(factory);    
        upload.setHeaderEncoding("UTF-8");    
        try{    
            List items = upload.parseRequest(ac.getHttpRequest());    
            if(null != items){    
                Iterator itr = items.iterator();    
                while(itr.hasNext()){    
                    FileItem item = (FileItem)itr.next();
                    
                    if(item.isFormField()){    
                       continue;    
                    }else{    
                         //以当前精确到秒的日期为上传的文件的文件名    
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddkkmmss");   
                        String path="/image";  
                        File savedFile = new File(rootPath+path,item.getName());    
                        item.write(savedFile);   
                        ImgUtil.img2Base64Code(rootPath+path);
                        out.print("{status:"+this.UPLOAD_SUCCSSS+",message:'"+path+"/"+item.getName()+"'}");  
                    }    
                }    
            }    
        }catch(Exception e){    
            e.printStackTrace();    
        }  
        return CONST_RESULT_AJAX;
	}

	public int saveImg(ActionContext ac) throws Exception{
		
		PrintWriter out=ac.getHttpResponse().getWriter();
		String rootPath = ac.getHttpRequest().getParameter("rootPath");    
	     
        String param = ac.getHttpRequest().getParameter("param"); 
        if(rootPath == null) rootPath = "";   
        
        rootPath = rootPath.trim();    
        ImgUtil.img2Base64Code(rootPath);
        
        FileItemFactory factory = new DiskFileItemFactory();    
        ServletFileUpload upload = new ServletFileUpload(factory);    
        upload.setHeaderEncoding("UTF-8");    
        try{    
            List items = upload.parseRequest(ac.getHttpRequest());    
            if(null != items){    
                Iterator itr = items.iterator();    
                while(itr.hasNext()){    
                    FileItem item = (FileItem)itr.next();
                    
                    if(item.isFormField()){    
                       continue;    
                    }else{    
                         //以当前精确到秒的日期为上传的文件的文件名    
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddkkmmss");   
                        String path="/image";  
                        File savedFile = new File(rootPath+path,item.getName());    
                        item.write(savedFile);   
                          
                        out.print("{status:"+this.UPLOAD_SUCCSSS+",message:'"+path+"/"+item.getName()+"'}");  
                    }    
                }    
            }    
        }catch(Exception e){    
            e.printStackTrace();    
        }  
		return CONST_RESULT_SUCCESS;
		
	}
}
