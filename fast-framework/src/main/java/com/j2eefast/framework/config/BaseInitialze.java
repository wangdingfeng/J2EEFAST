/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.config;

import cn.hutool.core.comparator.ComparableComparator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.setting.Setting;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.io.PropertiesUtils;
import com.j2eefast.common.core.mutidatasource.DataSourceContextHolder;
import com.j2eefast.common.core.utils.RedisUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.common.db.context.DataSourceContext;
import com.j2eefast.common.db.utils.SqlExe;
import com.j2eefast.framework.quartz.entity.SysJobEntity;
import com.j2eefast.framework.quartz.service.SysJobService;
import com.j2eefast.framework.quartz.utils.ScheduleUtils;
import com.j2eefast.framework.sys.entity.SysModuleEntity;
import com.j2eefast.framework.sys.service.SysModuleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 *
 * @ClassName: BaseInitialze
 * @Package: com.j2eefast.framework.config
 * @author: zhouzhou Emall:loveingowp@163.com
 * @time 2020/2/14 18:32
 * @version V1.0
 */
@Slf4j
@Component
public class BaseInitialze  implements ApplicationRunner {

    @Autowired
    private SysModuleService sysModuleService;
	@Qualifier("schedulerFactoryBean")
	@Autowired
	private Scheduler scheduler;
    @Autowired
    private SysJobService sysJobService;
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 是否检测脚本自动升级
     */
    @Value("${fast.updateDb.auto: false}")
    private boolean auto;

    @SuppressWarnings({ "unchecked"})
	@Override
    public void run(ApplicationArguments args) throws Exception {
    	
    	/**
    	 * 初始化检测模块
    	 */
        List<SysModuleEntity> list = sysModuleService.list();
        for(SysModuleEntity entity: list){
            boolean flag = true;
            try {
                Class.forName(entity.getMainClassName());
            }catch (ClassNotFoundException e){
                flag = false;
            }
            if(!flag){
                if(!entity.getStatus().equals("2")){
                	entity.setStatus("2");
                	sysModuleService.setRoles(entity.getId(), entity.getStatus());
                }
            }else{

                if(auto){
                    //检测升级脚本
                    String path = "classpath:config"+File.separator+"update"+File.separator+"db" + File.separator +
                    		entity.getModuleCode() + File.separator + "version.setting";
                    if(FileUtil.exist(path)){
                        Setting setting = new Setting(path);
                        if(!setting.isEmpty("dbType") && !setting.isEmpty("version")){
                            //指定数据库
                            String dbType = setting.get("dbType","dbName");
                            if(ToolUtil.isNotEmpty(dbType)){
                                //数据库版本
                                String dbV = entity.getCurrentVersion();
                                //获取本身系统对应数据库类型
                                String defaultDbType = DataSourceContext.getDbType(dbType);
                                String version = "";
                                for(int i=1;;i++){
                                    if(setting.containsKey("version","v"+i)){
                                        String tempVersion = setting.get("version","v"+i);
                                        if(ToolUtil.isNotEmpty(tempVersion)){
                                            if(ComparableComparator.INSTANCE.compare(dbV,tempVersion) < 0){
                                                //需要脚本升级 检测脚本数据库对应脚本是否存在
                                                path = "classpath:config"+File.separator+"update"+File.separator+"db"+ File.separator + entity.getModuleCode() +
                                                        File.separator + defaultDbType + File.separator  + defaultDbType+"_" +tempVersion + ".sql";
                                                if(FileUtil.exist(path)){
                                                    //
                                                    log.info("------------------------------------////---<"+entity.getModuleName()+ ">模块 -->[版本:"+dbV+" 升级-->"+tempVersion+"]///------------------------------------");
                                                    SqlExe.runFileSql(dbType,FileUtil.file(path));
                                                    version = tempVersion;
                                                }
                                            }
                                        }
                                    }else{
                                        break;
                                    }
                                }

                                if(ToolUtil.isNotEmpty(version) && !version.equals(dbV)){
                                    sysModuleService.setVersion(entity.getId(),version);
                                }
                            }
                        }
                    }
                }

                if(!entity.getStatus().equals("0")){
                	entity.setStatus("0");
                    sysModuleService.setRoles(entity.getId(), entity.getStatus());
                }
            }
        }

        //清除
		scheduler.clear();

        //设置系统版本号
        redisUtil.set(ConfigConstant.CONFIG_KEY, PropertiesUtils.getInstance().getProperty(ConfigConstant.SYS_VERSION,"1.0.1"));

        /**
         * 检测定时任务
         */
        List<SysJobEntity> sysJobList =  sysJobService.list();
        if(ToolUtil.isNotEmpty(sysJobList)) {
        	for(SysJobEntity sysJob: sysJobList) {
    			ScheduleUtils.createScheduleJob(scheduler, sysJob);
        	}
        }
        
    }
}
