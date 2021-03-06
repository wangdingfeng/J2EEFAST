/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.flowable.bpm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.flowable.bpm.cmd.processinstance.DeleteFlowableProcessInstanceCmd;
import com.j2eefast.flowable.bpm.entity.ProcessInstanceEntity;
import com.j2eefast.flowable.bpm.entity.RevokeProcessEntity;
import com.j2eefast.flowable.bpm.entity.StartProcessInstanceEntity;
import com.j2eefast.flowable.bpm.enums.CommentTypeEnum;
import com.j2eefast.flowable.bpm.service.FlowableProcessInstanceService;
import com.j2eefast.flowable.bpm.service.IFlowableBpmnModelService;
import com.j2eefast.framework.utils.Constant;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.ui.common.tenant.TenantProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.flowable.engine.runtime.Execution;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author: zhouzhou
 * @date: 2020-04-22 16:10
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Service
public class FlowableProcessInstanceServiceImpl extends  BaseProcessService implements FlowableProcessInstanceService {

	@Autowired
	protected TenantProvider tenantProvider;
//	@Autowired
//	private IFlowableBpmnModelService flowableBpmnModelService;

	@Override
	public ResponseData startProcessInstanceByKey(StartProcessInstanceEntity params) {
		if (ToolUtil.isNotEmpty(params.getProcessDefinitionKey())
				&& ToolUtil.isNotEmpty(params.getBusinessKey())) {
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(params.getProcessDefinitionKey())
					.latestVersion().singleResult();
			if (processDefinition != null && processDefinition.isSuspended()) {
				return ResponseData.error("70001","?????????????????????,????????????????????????!");
			}
			/**
			 * 1???????????????
			 * 1.1?????????????????????????????????????????????????????????
			 * 1.2???????????????????????????
			 * 1.3???????????????????????????
			 */
			//1.1?????????????????????????????????????????????????????????
			params.getVariables().put("initiator", "");
			//1.2???????????????????????????
			params.getVariables().put("_FLOWABLE_SKIP_EXPRESSION_ENABLED", true);
			// TODO 1.3???????????????????????????
			//2???????????????????????????????????????
			String creator = params.getCreator();
			if (ToolUtil.isEmpty(creator)) {
				creator = params.getCurrentUserCode();
				params.setCreator(creator);
			}
			//3.????????????
			identityService.setAuthenticatedUserId(creator);

			ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
					.processDefinitionKey(params.getProcessDefinitionKey().trim())
					.name(params.getFormName().trim())
					.businessKey(params.getBusinessKey().trim())
					.variables(params.getVariables())
					.tenantId(tenantProvider.getTenantId())
					.start();
			//4.??????????????????


			String taskId = flowableCommentService.findTaskInstId(processInstance.getProcessInstanceId());

			this.addComment(taskId,params.getCurrentUserCode(), processInstance.getProcessInstanceId(),
					CommentTypeEnum.TJ.toString(), params.getFormName() + "??????");
			//5.TODO ??????????????????
			return ResponseData.success(processInstance);
		} else {
			return  ResponseData.error("70001","????????? ??????????????? ProcessDefinitionKey,BusinessKey,SystemSn");
		}
	}


	@Override
	public PageUtil findPage(Map<String, Object> params) {
		String formName = (String) params.get("formName");
		String starterId = (String) params.get("starterId");
		String starter = (String) params.get("starter");
		Page<ProcessInstanceEntity> page = processInstanceMapper.findPage(	new Query<ProcessInstanceEntity>(params).getPage(),
				StrUtil.nullToDefault(starterId,""),
				StrUtil.nullToDefault(formName,""),
				StrUtil.nullToDefault(starter,""),
				StrUtil.nullToDefault(tenantProvider.getTenantId(),""),
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	@Override
	public ResponseData deleteProcessInstanceById(String processInstanceId) {
		long count = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).count();
		if (count > 0) {
			DeleteFlowableProcessInstanceCmd cmd = new DeleteFlowableProcessInstanceCmd(processInstanceId, "??????????????????", true);
			managementService.executeCommand(cmd);
			return ResponseData.success("????????????");
		} else {
			historyService.deleteHistoricProcessInstance(processInstanceId);
			return ResponseData.success("????????????");
		}
	}

	@Override
	public ResponseData suspendOrActivateProcessInstanceById(String processInstanceId, Integer suspensionState) {
		if (suspensionState == 1) {
			runtimeService.suspendProcessInstanceById(processInstanceId);
			return ResponseData.success("????????????");
		} else {
			runtimeService.activateProcessInstanceById(processInstanceId);
			return ResponseData.success("????????????");
		}
	}

	/**
	 * ?????????????????????
	 * @param revoke
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public ResponseData revokeProcess(RevokeProcessEntity revoke) {
		if (ToolUtil.isNotEmpty(revoke.getProcessInstanceId())) {
			ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
					.processInstanceId(revoke.getProcessInstanceId()).singleResult();
			if (processInstance != null) {
				//1.??????????????????
				this.addComment(revoke.getUserId(), revoke.getProcessInstanceId(), CommentTypeEnum.CH.toString(), revoke.getMessage());
				//2.???????????????
				runtimeService.setVariable(revoke.getProcessInstanceId(), "initiator", processInstance.getStartUserId());
				//3.????????????
//				Activity disActivity = flowableBpmnModelService.findActivityByName(processInstance.getProcessDefinitionId(), "?????????");
				String activityId = flowableActinstService.getBpmActivityId(processInstance.getStartUserId(),revoke.getProcessInstanceId());
				//4.????????????????????????????????????
				this.deleteActivity(activityId, revoke.getProcessInstanceId());
				//5.????????????
				List<Execution> executions = runtimeService.createExecutionQuery().parentId(revoke.getProcessInstanceId()).list();
				List<String> executionIds = new ArrayList<>();
				executions.forEach(execution -> executionIds.add(execution.getId()));
				this.moveExecutionsToSingleActivityId(executionIds,activityId);
				return ResponseData.success("????????????!");
			}else{
				return ResponseData.error("????????????!");
			}
		} else {
			return ResponseData.error("????????????id????????????!");
		}
	}

}
