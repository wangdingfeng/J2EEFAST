/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.j2eefast.common.core.base.entity.TableEntity;
import com.j2eefast.common.core.controller.BaseController;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <p>组件控制类</p>
 *
 * @author: zhouzhou
 * @date: 2020-05-22 09:14
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Controller
@RequestMapping("/sys/component")
public class SysComponentController extends BaseController {

	private String                  urlPrefix                   = "modules/sys/macro";

	/**
	 * 跳转选择树页面
	 * @return
	 */
	@RequestMapping(value = "/treeselect", method = RequestMethod.POST)
	public String treeselect(ModelMap mmap) {
		// 树结构数据URL
		mmap.put("url",super.getPara("url"));
		//树节点ID
		mmap.put("treeId",super.getPara("treeId"));
		//节点名称
		mmap.put("treeName",super.getPara("treeName"));
		//关联ID
		mmap.put("correlationId",super.getPara("correlationId"));
		//是否展开树
		mmap.put("expandLevel",super.getPara("expandLevel"));
		//是否可以选中父节点
		mmap.put("isSelectParent",super.getPara("isSelectParent"));
		//是否可复选 单选还是多选
		mmap.put("checked", super.getPara("checked"));
		// 单选分组范围 同级互斥 - 或者  整个树互斥
		mmap.put("radioType",super.getPara("radioType"));
		//复选框级联选择规则 默认：{'Y':'ps','N':'ps'}
		mmap.put("chkboxType", super.getPara("chkboxType","{'Y':'ps','N':'ps'}"));
		//编辑回传此节点所有父节点
		mmap.put("parentIds",super.getPara("parentIds",""));
		return urlPrefix + "/treeselect";
	}
	
	/**
	 * 跳转表格选择页面
	 * @param mmap
	 * @return
	 */
	@RequestMapping(value = "/tableselect", method = RequestMethod.POST)
	public String tableselect(ModelMap mmap) {
		
		
		// 结构数据URL
		mmap.put("url",super.getPara("url"));
		// 选中ID
		mmap.put("selectId",super.getPara("selectId"));
		//节点名称
		mmap.put("selectName",super.getPara("selectName"));
		
		//表格展示信息
		String talbeInfo = super.getPara("tableInfo");
		JSONArray json = JSONUtil.parseArray(talbeInfo);
		List<TableEntity> tablelsit = new ArrayList<TableEntity>(json.size());
		boolean query = false;
		for(int i=0; i<json.size(); i++) {
			JSONObject o =  (JSONObject) json.get(i);
			TableEntity t = new TableEntity();
			t.setField(o.getStr("field",""));
			t.setDict(o.getStr("dict",""));
			t.setQuery(o.getStr("query","").equals("true"));
			t.setTitle(o.getStr("title",""));
			if(t.isQuery()) {
				query = true;
			}
			tablelsit.add(t);
		}
		mmap.put("tableInfo",tablelsit);
		
		//是否可复选 单选还是多选
		mmap.put("checked", super.getPara("checked").equals("true"));
		//是否有机构查询
		mmap.put("isorga",super.getPara("isorga").equals("true"));

		mmap.put("keyId",super.getPara("keyId"));

		mmap.put("keyName",super.getPara("keyName"));

		mmap.put("separator",super.getPara("separator"));
		//是否需要查询
		mmap.put("query", query);
		
		return urlPrefix + "/tableselect";
	}

	/**
	 * 跳转图标选择
	 * @param mmap
	 * @return
	 */
	@RequestMapping(value = "/iconselect", method = RequestMethod.POST)
	public String iconselect(ModelMap mmap) {
		mmap.put("iconValue", super.getPara("iconValue"));
		return urlPrefix + "/iconselect";
	}

	@GetMapping("/fileViwe")
	public String fileViwe(ModelMap mmap){
		String fileName = super.getPara("fileName");
		String fileUrl = super.getPara("fileUrl");
		String extName = FileUtil.extName(fileName);
		mmap.put("fileUrl",fileUrl);
		mmap.put("extName",extName);
		return urlPrefix + "/fileView";
	}

	/**
	 * 图片裁剪跳转
	 * @param mmap
	 * @return
	 */
	@RequestMapping(value = "/cropperImg", method = RequestMethod.POST)
	public String cropperImg(ModelMap mmap) {
		mmap.put("imgValue", super.getPara("imgValue"));
		mmap.put("imageDefault", super.getPara("imageDefault"));
		mmap.put("ratio", super.getPara("ratio"));
		mmap.put("viewMode", super.getPara("viewMode"));
		return urlPrefix + "/cropperImg";
	}

}
