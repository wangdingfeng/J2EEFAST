/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.service;

import com.j2eefast.common.core.base.entity.CommentTree;
import com.j2eefast.framework.sys.entity.SysMsgCommentsEntity;
import com.j2eefast.framework.sys.mapper.SysMsgCommentsMapper;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.framework.utils.UserUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 评论Service接口
 * @author: ZhouZhou
 * @date 2021-02-28 11:00:00
 */
@Service
public class SysMsgCommentsService extends ServiceImpl<SysMsgCommentsMapper,SysMsgCommentsEntity> {

	@Resource
	private SysMsgCommentsMapper sysMsgCommentsMapper;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<SysMsgCommentsEntity> queryWrapper = new QueryWrapper<SysMsgCommentsEntity>();
		String msgId = (String) params.get("msgId");
        queryWrapper.eq(ToolUtil.isNotEmpty(msgId), "msg_id", msgId);
		String replyId = (String) params.get("replyId");
        queryWrapper.eq(ToolUtil.isNotEmpty(replyId), "reply_id", replyId);
		String content = (String) params.get("content");
        queryWrapper.eq(ToolUtil.isNotEmpty(content), "content", content);
		String sendUserCode = (String) params.get("sendUserCode");
        queryWrapper.eq(ToolUtil.isNotEmpty(sendUserCode), "send_user_code", sendUserCode);
		String sendUserName = (String) params.get("sendUserName");
        queryWrapper.like(ToolUtil.isNotEmpty(sendUserName), "send_user_name", sendUserName);
		String commentsDate = (String) params.get("commentsDate");
        queryWrapper.eq(ToolUtil.isNotEmpty(commentsDate), "comments_date", commentsDate);
		String isReply = (String) params.get("isReply");
        queryWrapper.eq(ToolUtil.isNotEmpty(isReply), "is_reply", isReply);
		Page<SysMsgCommentsEntity> page = sysMsgCommentsMapper.selectPage(new Query<SysMsgCommentsEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,SysMsgCommentsEntity sysMsgCommentsEntity) {

		Page<SysMsgCommentsEntity> page = sysMsgCommentsMapper.findPage(new Query<SysMsgCommentsEntity>(params).getPage(),
					sysMsgCommentsEntity,
				(String) params.get(Constant.SQL_FILTER));

		//数据转换
		List<CommentTree> list = new ArrayList<>(page.getOrders().size());
		for(SysMsgCommentsEntity comments :page.getRecords()){
			CommentTree cTree = new CommentTree();
			cTree.setId(comments.getId());
			cTree.setUserId(comments.getUserId());
			cTree.setCommentsDate(comments.getCommentsDate());
			cTree.setContent(comments.getContent());
			cTree.setReply(comments.getIsReply().equals("1"));
			cTree.setUserName(comments.getUserName());
			cTree.setReplyId(comments.getReplyId());
			cTree.setAvatar(comments.getAvatar());
			if(cTree.isReply()){
				cTree.setChildren(new ArrayList<>());
				getCommentsTree(cTree.getId(),cTree.getChildren());
			}
			list.add(cTree);
		}

		return new PageUtil(list,page.getTotal(),page.getSize(),page.getCurrent());
	}

	private void getCommentsTree(Long replyId,List<CommentTree> children){
		SysMsgCommentsEntity comment = new SysMsgCommentsEntity();
		comment.setReplyId(replyId);
		List<SysMsgCommentsEntity> list = findList(comment);
		for(SysMsgCommentsEntity comments: list){
			CommentTree cTree = new CommentTree();
			cTree.setId(comments.getId());
			cTree.setUserId(comments.getUserId());
			cTree.setCommentsDate(comments.getCommentsDate());
			cTree.setContent(comments.getContent());
			cTree.setReply(comments.getIsReply().equals("1"));
			cTree.setReplyId(comments.getReplyId());
			cTree.setUserName(comments.getUserName());
			cTree.setAvatar(comments.getAvatar());
			children.add(cTree);
			if(cTree.isReply()){
				cTree.setChildren(new ArrayList<>());
				getCommentsTree(cTree.getId(),cTree.getChildren());
			}
		}
	}


	/**
	* 查列表
	*/
	public List<SysMsgCommentsEntity> findList(SysMsgCommentsEntity sysMsgCommentsEntity){
		return sysMsgCommentsMapper.findList(sysMsgCommentsEntity);
	}

	/**
     * 批量删除
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteBatchByIds(Long[] ids) {
		return this.removeByIds(Arrays.asList(ids));
	}

	/**
     * 删除
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean delSysMsgCommentsById(Long id) {
		SysMsgCommentsEntity commentsEntity = this.getById(id);
		if(!commentsEntity.getUserId().equals(UserUtils.getUserId())){
			return false;
		}
		return this.delMsgCommentsTree(commentsEntity);
	}

	/**
	 * 删除评论树
	 * @param commentsEntity
	 * @return
	 */
	public boolean delMsgCommentsTree(SysMsgCommentsEntity commentsEntity){
		// 判断是否有子节点
		if(commentsEntity.getIsReply().equals("1")){
			 List<SysMsgCommentsEntity> list = this.list(new QueryWrapper<SysMsgCommentsEntity>()
					 .eq("reply_id",commentsEntity.getId()).eq("msg_id",commentsEntity.getMsgId()));
			 for(SysMsgCommentsEntity comments: list){
			 	this.delMsgCommentsTree(comments);
			 }
		}
		return this.removeById(commentsEntity.getId());
	}

	public void setIsReply(Long replyId, String isReple){
		this.baseMapper.setIsReply(replyId,isReple);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addSysMsgComments(SysMsgCommentsEntity sysMsgComments){
		sysMsgComments.setUserId(UserUtils.getUserId());
		sysMsgComments.setUserName(UserUtils.getName());
		if(sysMsgComments.getReplyId() != 0){
			//修改父为有子
			this.baseMapper.setIsReply(sysMsgComments.getReplyId(),"1");
		}
		sysMsgComments.setCommentsDate(new Date());
		sysMsgComments.setIsReply("0");
		if(this.save(sysMsgComments)){
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	public boolean updateSysMsgCommentsById(SysMsgCommentsEntity sysMsgComments) {
		if(this.updateById(sysMsgComments)){
			return true;
		}
		return false;
	}

	/**
     * 根居ID获取对象
     */
	public SysMsgCommentsEntity findSysMsgCommentsById(Long id){
		return sysMsgCommentsMapper.findSysMsgCommentsById(id);
	}


}
