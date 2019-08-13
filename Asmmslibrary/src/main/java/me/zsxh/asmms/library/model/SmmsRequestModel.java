package me.zsxh.asmms.library.model;
import org.json.*;


/**
*上传结果解析类
*created by zs-river
*/

public class SmmsRequestModel
{
	private JSONObject requestjson;
	
	private String requeststr;

	private JSONObject datajson;
	
	public SmmsRequestModel(String request) throws JSONException{
		requeststr=request;
		requestjson=new JSONObject(requeststr);
	}
	
	public boolean issuccess() throws JSONException{
		String code=requestjson.getString("code");
		if(code.equals("success")){
			datajson=requestjson.getJSONObject("data");
			return true;
		}
		else{
			return false;
		}
	}
	
	public String geterrormsg() throws JSONException{
		if(!issuccess()){
			return requestjson.getString("msg");
		}
		else{
			return null;
		}
	}
	
	public int getsize() throws JSONException{
		if(issuccess()){
			return datajson.getInt("size");
		}
		else{
			return -1;
		}
	}
	
	public String getfilename() throws JSONException{
		if(issuccess()){
			return datajson.getString("filename");
		}
		else{
			return null;
		}
	}
	
	public int getwidth() throws JSONException{
		if(issuccess()){
			return datajson.getInt("width");
		}
		else{
			return -1;
		}
	}
	
	public int getheight() throws JSONException{
		if(issuccess()){
			return datajson.getInt("height");
		}
		else{
			return -1;
		}
	}
	
	public String gethash() throws JSONException{
		if(issuccess()){
			return datajson.getString("hash");
		}
		else{
			return null;
		}
	}
	
	public String getpath() throws JSONException{
		if(issuccess()){
			return datajson.getString("path");
		}
		else{
			return null;
		}
	}
	
	public String geturl() throws JSONException{
		if(issuccess()){
			return datajson.getString("url");
		}
		else{
			return null;
		}
	}
	
	public String getdeleteurl() throws JSONException{
		if(issuccess()){
			return datajson.getString("delete");
		}
		else{
			return null;
		}
	}
	public String getname() throws JSONException{
		if(issuccess()){
			return datajson.getString("storename");
		}
		else{
			return null;
		}
	}
	
	public String getstringvalue(String key) throws JSONException{
		if(issuccess()){
			return datajson.getString(key);
		}
		else{
			return null;
		}
	}
}
