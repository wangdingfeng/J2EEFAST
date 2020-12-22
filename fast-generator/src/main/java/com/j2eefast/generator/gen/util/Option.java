package com.j2eefast.generator.gen.util;

/**
 * @ClassName Options
 * @Description GenTableEntity 扩展 ---对应options 在数据库中以json存储
 * @author mfksn001@163.com
 * @date 2020年6月10日
 */
public class Option {

    /** 树编码字段 */
    private String treeCode;

    /** 树父编码字段 */
    private String treeParentCode;

    /** 树名称字段 */
    private String treeName;

    /** 是否异步加载 1 异步 0 同步*/
    private String async;

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public String getAsync() {
		return async;
	}

	public void setAsync(String async) {
		this.async = async;
	}

	public String getTreeParentCode() {
		return treeParentCode;
	}

	public void setTreeParentCode(String treeParentCode) {
		this.treeParentCode = treeParentCode;
	}

	public String getTreeName() {
		return treeName;
	}

	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}
    
}
