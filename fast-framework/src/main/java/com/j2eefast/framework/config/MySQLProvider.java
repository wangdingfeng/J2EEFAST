package com.j2eefast.framework.config;

import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import com.j2eefast.framework.sys.entity.SysUreportFileEntity;
import com.j2eefast.framework.sys.service.SysUreportFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "fast.ureport.mysql.provider")
public class MySQLProvider implements ReportProvider {

    private static final String NAME = "Msyql数据库存储";

    // 特定前缀
    private String prefix = "fast-";

    // 是否禁用
    private boolean enabled;


    @Autowired
    private SysUreportFileService sysUreportFileService;


    /**
     * 加载
     * @param file
     * @return
     */
    @Override
    public InputStream loadReport(String file) {
        SysUreportFileEntity ureportFile = sysUreportFileService
                .getUreportFileByFileName(getCorrectName(file));
        byte[] content = ureportFile.getContent();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
        return inputStream;
    }

    /**
     * 删除
     * @param file
     */
    @Override
    public void deleteReport(String file) {
        sysUreportFileService.delUreportFileByFileName(getCorrectName(file));
    }

    /**
     * 获取列表
     * @return
     */
    @Override
    public List<ReportFile> getReportFiles() {
        List<SysUreportFileEntity> list = sysUreportFileService.list();
        List<ReportFile> reportList = new ArrayList<>();
        for (SysUreportFileEntity ureportFileEntity : list) {
            reportList.add(new ReportFile(ureportFileEntity.getName(), ureportFileEntity.getUpdateTime()));
        }
        return reportList;
    }

    /**
     * 保存
     * @param file
     * @param content
     */
    @Override
    public void saveReport(String file, String content) {
        file = getCorrectName(file);
        SysUreportFileEntity ureportFileEntity = sysUreportFileService
                .getUreportFileByFileName(file);
        if(ureportFileEntity == null){
            ureportFileEntity = new SysUreportFileEntity();
            ureportFileEntity.setName(file);
            ureportFileEntity.setContent(content.getBytes());
            ureportFileEntity.setUpdateTime(new Date());
        }else{
            ureportFileEntity.setContent(content.getBytes());
        }
        sysUreportFileService.saveOrUpdate(ureportFileEntity);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean disabled() {
        return enabled;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * 获取没有前缀的文件名
     * @param name
     * @return
     */
    private String getCorrectName(String name){
        if(name.startsWith(prefix)){
            name = name.substring(prefix.length(), name.length());
        }
        return name;
    }
}
