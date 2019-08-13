package me.zsxh.asmms.library.model.onupload;

import me.zsxh.asmms.library.model.*;

public abstract class OnsmmsUpload
{
	public abstract void onsuccess(SmmsRequestModel request);
	
	public abstract void onerror(Exception e,int code,String str);
	
	//public abstract void ondebug(String s);
}
