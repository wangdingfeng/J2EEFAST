package com.j2eefast.generator.gen.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.j2eefast.common.core.base.entity.Ztree;
import com.j2eefast.common.core.mutidatasource.DataSourceContextHolder;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.common.db.entity.SysDatabaseEntity;
import com.j2eefast.framework.sys.mapper.SysDatabaseMapper;
import com.j2eefast.generator.gen.entity.GenTableColumnEntity;
import com.j2eefast.generator.gen.mapper.GenTableColumnMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhouzhou
 * @time 2020/1/6 15:23
 * @version V1.0
 */
@Service
public class GenTableColumnService extends ServiceImpl<GenTableColumnMapper,GenTableColumnEntity> {

    @Resource
    private GenTableColumnMapper genTableColumnMapper;
    
    @Resource
    private SysDatabaseMapper sysDatabaseMapper;

    public List<GenTableColumnEntity> selectGenTableColumnListByTableId(Map<String, Object> params) {
        Long tableId = Long.parseLong((String) params.get("id"));
        List<GenTableColumnEntity> list = Lists.newArrayList();
        list = findListByTableId(tableId);
        return list;
    }

    public PageUtil findPage(Map<String, Object> params) {
        String pId = (String) params.get("pId");
        String name =(String) params.get("name");
        //初始化上传
        String searchValue = (String) params.get("searchValue");

        Page<GenTableColumnEntity> page = this.baseMapper.selectPage(new Query<GenTableColumnEntity>(params).getPage(),
                new QueryWrapper<GenTableColumnEntity>()
                        .eq(StringUtils.isNotBlank(pId), "table_id", pId)
                        .eq(StringUtils.isNotBlank(searchValue), "id",ToolUtil.isNotEmpty(searchValue)? Long.parseLong(searchValue):null)
                        .like(StringUtils.isNotBlank(name), "column_name", name));
        //数据转换
        List<Ztree> list = new ArrayList<>();
        for(GenTableColumnEntity tableEntity: page.getRecords()){
            Ztree ztree = new Ztree();
            ztree.setId(String.valueOf(tableEntity.getId()));
            ztree.setName(tableEntity.getColumnName() + "("+tableEntity.getColumnComment()+")");
            list.add(ztree);
        }
        //数据输出前端分页
        return new PageUtil(list,page.getTotal(),page.getSize(),page.getCurrent());
    }

    public int updateGenTableColumn(GenTableColumnEntity genTable) {
        return genTableColumnMapper.updateGenTableColumn(genTable);
    }

    /** 
	* @Title: findListByTableId 
	* @Description: 获取columns
	* @return  List<GenTableColumnEntity>
	* @author mfksn001@163.com
	* @Date: 2020年6月2日
	*/
	public List<GenTableColumnEntity> findListByTableId(Long tableId) {
		
		return genTableColumnMapper.findListByTableId(tableId);
	}

	public GenTableColumnEntity findByById(Long id){
        return this.getById(id);
    }
    
    
    public List<GenTableColumnEntity> generateDbTableColumnsByName(String dbType,String schema ,String tableName) {
        return genTableColumnMapper.generateDbTableColumnsByName( dbType , schema , tableName);
    }
    
    
    public List<GenTableColumnEntity> generateDbTableColumnsByName(String dbName,String tableName) {
	    List<GenTableColumnEntity> list = Lists.newArrayList();
		try {
			 SysDatabaseEntity db = sysDatabaseMapper.getByName(dbName);
			 DataSourceContextHolder.setDataSourceType(dbName);
			 list = genTableColumnMapper.generateDbTableColumnsByName(db.getDbType(), db.getSchema() , tableName);
		} catch (Exception e) {
		   e.printStackTrace();
		}finally {
			 DataSourceContextHolder.clearDataSourceType();
		}
        return list ;
    }
    
    public int deleteGenTableColumnByIds(Long[] ids) {
        return genTableColumnMapper.deleteGenTableColumnByIds(ids);
    }

	

}
