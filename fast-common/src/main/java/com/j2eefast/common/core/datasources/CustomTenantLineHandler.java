package com.j2eefast.common.core.datasources;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.google.common.collect.Lists;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.io.PropertiesUtils;
import com.j2eefast.common.core.utils.ToolUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huanzhou
 */
public class CustomTenantLineHandler implements TenantLineHandler {


    @Override
    public Expression getTenantId() {
        return new StringValue(getSysTenantId());
    }

    @Override
    public String getTenantIdColumn() {
        return getSysTenantIdColumn();
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return ignoreSysTable(tableName);
    }

    protected String getSysTenantId(){
        return "";
    }

    protected String getSysTenantIdColumn(){
        return "tenant_id";
    }

    protected boolean ignoreSysTable(String tableName) {
        //获取配置数据 合并得到所有忽略表
        String ignore_table = PropertiesUtils.getInstance().getProperty(ConfigConstant.IGNORETABLE);
        List<String> compare = new ArrayList<>();
        if(ToolUtil.isNotEmpty(ignore_table)){
            String[] sp = ignore_table.split(StrUtil.COMMA);
            for(String s: sp){
                compare.add(StrUtil.trim(s));
            }
            compare.removeAll(ConfigConstant.IGNORE_TABLE_NAMES);
            compare.addAll(ConfigConstant.IGNORE_TABLE_NAMES);
        }else{
            compare = ConfigConstant.IGNORE_TABLE_NAMES;
        }
//        return compare.contains(tableName);
        return verifyList(compare,tableName);
    }

    /**
     * 判断prefix数据 是否存在List中 List中数据支持* 通配, 忽略大小写
     * 例如 List:['aa','bb',"cs_*"] prefix: cs_tiss 返回 true
     * @param str
     * @param prefix
     * @return 匹配到返回 true
     */
    private boolean verifyList(List<String> str, String prefix){
        boolean mark = false;
        for(String s: str){
            if(s.indexOf("*") > 0){
                s = StrUtil.sub(s,0,s.indexOf("*"));
                if(StrUtil.startWith(prefix,s,true)){
                    mark = true;
                    break;
                }
            }else{
                if(StrUtil.equalsIgnoreCase(s,prefix)){
                    mark = true;
                    break;
                }
            }
        }
        return  mark;
    }

}
