package com.dabai.markdownq.utils;

import java.util.*;

/**
*错误信息翻译类
*created by zs-river
*/
public class ErrortranslateModel
{
	public static String replace(String errmessage)
	{
		Map<String,String> tran=new HashMap<String,String>();
		tran.put("Access Denied.", "拒绝访问");
		tran.put("Upload file count limit.", "上传文件过多");
		tran.put("Upload file frequency limit.", "上传过频");
		tran.put("Server error. Upload directory isn't writable.", "服务器空间不足");
		tran.put("No files were uploaded.", "没有上传文件");
		tran.put("File is empty.", "文件为空");
		tran.put("File is too large.", "文件过大");
		tran.put("File has an invalid extension.", "文件有无用后缀");
		tran.put("Could not save uploaded file.", "无法储存上传文件");
		tran.put("File has an invalid extension.", "文件有无用扩展");
		if (tran.get(errmessage) == null)
		{
			return errmessage;
		}
		else
		{
			return tran.get(errmessage);
		}
	}
}
