package com.j2eefast.web.legao.vo;

import com.j2eefast.web.legao.entity.LMemberEntity;
import com.j2eefast.web.legao.entity.LTableEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author: wangdingfeng
 * @Date: 2021/12/24 14:05
 * @Description:
 */
@Data
public class LTableVo extends LTableEntity {
    public LMemberEntity member;
    public Date startTime;
}
