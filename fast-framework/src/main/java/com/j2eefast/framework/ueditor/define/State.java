package com.j2eefast.framework.ueditor.define;

/**
 * 处理状态接口
 * @author hancong03@baidu.com
 *
 */
public interface State {
	
	public boolean isSuccess();
	
	public void putInfo(String name, String val);
	
	public void putInfo(String name, long val);

	public String getStr(String name);

	public long getLong(String name);
	
	public String toJSONString();

}
