/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.flowable.bpm.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.flowable.bpm.entity.CommentEntity;
import com.j2eefast.flowable.bpm.entity.CompleteTaskEntity;
import com.j2eefast.flowable.bpm.entity.BpmTaskEntity;
import com.j2eefast.flowable.bpm.entity.FlowNodeEntity;
import com.j2eefast.flowable.bpm.enums.CommentTypeEnum;
import com.j2eefast.flowable.bpm.mapper.BpmTaskMapper;
import com.j2eefast.flowable.bpm.service.IFlowableTaskService;
import com.j2eefast.flowable.bpm.utils.BpmConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.task.api.DelegationState;
import com.j2eefast.framework.utils.Constant;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.ui.common.tenant.TenantProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p></p>
 *
 * @author: zhouzhou
 * @date: 2020-04-25 14:38
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Service
public class FlowableTaskServiceImpl extends BaseProcessService implements IFlowableTaskService {

	@Resource
	private BpmTaskMapper bpmTaskMapper;
	@Autowired
	protected RuntimeService runtimeService;
	@Autowired
	protected TenantProvider tenantProvider;

	@Override
	public PageUtil findApplyingTasksPage(Map<String, Object> params){
		Page<BpmTaskEntity> page = bpmTaskMapper.findApplyingTasksPage(new Query<BpmTaskEntity>(params).getPage(),
				(String)params.get("userId"),
				StrUtil.nullToDefault(tenantProvider.getTenantId(),""),
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	@Override
	public PageUtil findApplyedTasksPage(Map<String, Object> params) {
		Page<BpmTaskEntity> page = bpmTaskMapper.findApplyedTasksPage(new Query<BpmTaskEntity>(params).getPage(),
				(String)params.get("userId"),
				StrUtil.nullToDefault(tenantProvider.getTenantId(),""),
				(String) params.get(Constant.SQL_FILTER));
		page.getRecords().forEach(bpmTask->{
			bpmTask.setConsuming(DateUtil.formatBetween(bpmTask.getDuration()));
		});
		return new PageUtil(page);
	}

	/**
	 * 审批任务
	 * @param params 参数
	 * @return
	 */
	@Override
	public ResponseData complete(CompleteTaskEntity params) {
		//1. 先判断是否有实例ID
		if(ToolUtil.isNotEmpty(params.getProcessInstanceId())
				&& ToolUtil.isNotEmpty(params.getTaskId())){
			String taskId = params.getTaskId();
			//2.查看当前任务是存在 使用flowable 方法查询
			TaskEntity taskEntity =
					(TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
			if(ToolUtil.isNotEmpty(taskEntity)){
				//3.委派处理
				if (DelegationState.PENDING.equals(taskEntity.getDelegationState())) {
					//3.1生成历史记录
					TaskEntity task = this.createSubTask(taskEntity, params.getUserId());
					taskService.complete(task.getId());
					taskId = task.getId();
					//3.2执行委派
					taskService.resolveTask(params.getTaskId(), params.getVariables());
				} else {
					//3.1修改执行人 其实我这里就相当于签收了
					taskService.setAssignee(params.getTaskId(), params.getUserId());
					//3.2执行任务
					taskService.complete(params.getTaskId(), params.getVariables());
					//4.处理加签父任务
					String parentTaskId = taskEntity.getParentTaskId();
					if (ToolUtil.isNotEmpty(parentTaskId)) {
						String tableName = managementService.getTableName(TaskEntity.class);
						String sql = "select count(1) from " + tableName + " where PARENT_TASK_ID_=#{parentTaskId}";
						long subTaskCount = taskService.createNativeTaskQuery().sql(sql).parameter("parentTaskId", parentTaskId).count();
						if (subTaskCount == 0) {
							Task task = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
							//处理前后加签的任务
							taskService.resolveTask(parentTaskId);
							if (BpmConstant.AFTER_ADDSIGN.equals(task.getScopeType())) {
								taskService.complete(parentTaskId);
							}
						}
					}
				}
				String type = params.getType() == null ? CommentTypeEnum.SP.toString() : params.getType();
				//5.生成审批意见
				this.addComment(taskId, params.getUserId(), params.getProcessInstanceId(), type, params.getMessage());
				return ResponseData.success("审批成功");
			}else{
				return ResponseData.error("20001","没有次任务,请确认!");
			}
		}else{
			return ResponseData.error("20001","传入参数错误,请确认参数是否正确!");
		}
	}

	/**
	 * 获取可驳回的节点列表
	 * @param processInstanceId
	 * @param taskId
	 * @return
	 */
	@Override
	public List<FlowNodeEntity> getBackNodesByProcessInstanceId(String processInstanceId, String taskId) {

		List<FlowNodeEntity> backNods = new ArrayList<>();
		//查询当前运行时任务数据 ACT_RU_TASK
		TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
		//当前任务定义的ID值
		String currActId = taskEntity.getTaskDefinitionKey();

		//获取运行节点表中usertask
		String sql = "select t.* from act_ru_actinst t where t.ACT_TYPE_ = 'userTask' " +
				" and t.PROC_INST_ID_=#{processInstanceId} and t.END_TIME_ is not null ";
		List<ActivityInstance> activityInstances = runtimeService.createNativeActivityInstanceQuery().sql(sql)
				.parameter("processInstanceId", processInstanceId)
				.list();

		//获取运行节点表的parallelGateway 并行网关 节点并出重, 去除当前ID值
		sql = "SELECT t.ID_, t.REV_,t.PROC_DEF_ID_,t.PROC_INST_ID_,t.EXECUTION_ID_,t.ACT_ID_, t.TASK_ID_, t.CALL_PROC_INST_ID_, t.ACT_NAME_, t.ACT_TYPE_, " +
				" t.ASSIGNEE_, t.START_TIME_, max(t.END_TIME_) as END_TIME_, t.DURATION_, t.DELETE_REASON_, t.TENANT_ID_" +
				" FROM  act_ru_actinst t WHERE t.ACT_TYPE_ = 'parallelGateway' AND t.PROC_INST_ID_ = #{processInstanceId} and t.END_TIME_ is not null" +
				" and t.ACT_ID_ <> #{actId} GROUP BY t.act_id_";
		List<ActivityInstance> parallelGatewaies = runtimeService.createNativeActivityInstanceQuery().sql(sql)
				.parameter("processInstanceId", processInstanceId)
				.parameter("actId", currActId)
				.list();
		//排序
		if (CollectionUtils.isNotEmpty(parallelGatewaies)) {
			activityInstances.addAll(parallelGatewaies);
			activityInstances.sort(Comparator.comparing(ActivityInstance::getEndTime));
		}

		//分组节点
		int count = 0;
		Map<ActivityInstance, List<ActivityInstance>> parallelGatewayUserTasks = new HashMap<>();
		List<ActivityInstance> userTasks = new ArrayList<>();
		ActivityInstance currActivityInstance = null;
		for (ActivityInstance activityInstance : activityInstances) {
			if (BpmnXMLConstants.ELEMENT_GATEWAY_PARALLEL.equals(activityInstance.getActivityType())) {
				count++;
				if (count % 2 != 0) {
					List<ActivityInstance> datas = new ArrayList<>();
					currActivityInstance = activityInstance;
					parallelGatewayUserTasks.put(currActivityInstance, datas);
				}
			}
			if (BpmnXMLConstants.ELEMENT_TASK_USER.equals(activityInstance.getActivityType())) {
				if (count % 2 == 0) {
					userTasks.add(activityInstance);
				} else {
					if (parallelGatewayUserTasks.containsKey(currActivityInstance)) {
						parallelGatewayUserTasks.get(currActivityInstance).add(activityInstance);
					}
				}
			}
		}

		//通过回复
		List<String> userCodes = new ArrayList<>();
		List<CommentEntity> hisComment = flowableCommentService.getFlowCommentVosByProcessInstanceId(processInstanceId);
		hisComment.forEach(e->{
			userCodes.add(e.getUserId());
		});
		List<String> userIds = userCodes.stream().distinct().collect(Collectors.toList());

		//组装人员名称 ACT_HI_TASKINST 历史任务
		List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId).finished().list();

		Map<String, List<HistoricTaskInstance>> taskInstanceMap = new HashMap<>();

		historicTaskInstances.forEach(historicTaskInstance -> {
			//userCodes.add(historicTaskInstance.getAssignee());
			String taskDefinitionKey = historicTaskInstance.getTaskDefinitionKey();
			if (taskInstanceMap.containsKey(historicTaskInstance.getTaskDefinitionKey())) {
				taskInstanceMap.get(taskDefinitionKey).add(historicTaskInstance);
			} else {
				List<HistoricTaskInstance> tasks = new ArrayList<>();
				tasks.add(historicTaskInstance);
				taskInstanceMap.put(taskDefinitionKey, tasks);
			}
		});

		//组装usertask的数据
		List<User> userList = identityService.createUserQuery().userIds(userIds).list();
		if (CollectionUtils.isNotEmpty(userTasks)) {
			userTasks.forEach(activityInstance -> {
				FlowNodeEntity node = new FlowNodeEntity();
				node.setNodeId(activityInstance.getActivityId());
				node.setNodeName(activityInstance.getActivityName());
				node.setEndTime(activityInstance.getEndTime());
				for(int i=0; i< hisComment.size(); i++){
					if(hisComment.get(i).getTaskId().equals(activityInstance.getTaskId())){
						for(int k=0; k< userList.size(); k++){
							if(userList.get(k).getId().equals(hisComment.get(i).getUserId())){
								node.setUserId(userList.get(k).getId());
								node.setUserName(userList.get(k).getDisplayName());
								break;
							}
						}
						break;
					}
				}
				backNods.add(node);
			});
		}
		//组装会签节点数据
		if (MapUtils.isNotEmpty(taskInstanceMap)) {
			Map<String, String> activityIdUserNames = this.getApplyers(processInstanceId, userList, taskInstanceMap);
			parallelGatewayUserTasks.forEach((activity, activities) -> {
				FlowNodeEntity node = new FlowNodeEntity();
				node.setNodeId(activity.getActivityId());
				node.setEndTime(activity.getEndTime());
				StringBuffer nodeNames = new StringBuffer("会签:");
				StringBuffer userNames = new StringBuffer("审批人员:");
				if (CollectionUtils.isNotEmpty(activities)) {
					activities.forEach(activityInstance -> {
						nodeNames.append(activityInstance.getActivityName()).append(",");
						userNames.append(activityIdUserNames.get(activityInstance.getActivityId())).append(",");
					});
					node.setNodeName(nodeNames.toString());
					node.setUserName(userNames.toString());
					backNods.add(node);
				}
			});
		}

		//去重合并
		List<FlowNodeEntity> datas = backNods.stream().collect(
				Collectors.collectingAndThen(Collectors.toCollection(() ->
						new TreeSet<>(Comparator.comparing(nodeVo -> nodeVo.getNodeId()))), ArrayList::new));

		//排序
		datas.sort(Comparator.comparing(FlowNodeEntity::getEndTime));

		return datas;
	}

	private Map<String, String> getApplyers(String processInstanceId, List<User> userList, Map<String, List<HistoricTaskInstance>> taskInstanceMap) {
		Map<String, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
		Map<String, String> applyMap = new HashMap<>();
		//ACT_RU_EXECUTION
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		taskInstanceMap.forEach((activityId, taskInstances) -> {
			StringBuffer applyers = new StringBuffer();
			StringBuffer finalApplyers = applyers;
			taskInstances.forEach(taskInstance -> {
				if (!taskInstance.getName().equals(BpmConstant.FLOW_SUBMITTER)) {
					User user = userMap.get(taskInstance.getAssignee());
					if (user != null) {
						if (StringUtils.indexOf(finalApplyers.toString(), user.getDisplayName()) == -1) {
							finalApplyers.append(user.getDisplayName()).append(",");
						}
					}
				} else {
					String startUserId = processInstance.getStartUserId();
					User user = identityService.createUserQuery().userId(startUserId).singleResult();
					if (user != null) {
						finalApplyers.append(user.getDisplayName()).append(",");
					}
				}
			});
			if (applyers.length() > 0) {
				applyers = applyers.deleteCharAt(applyers.length() - 1);
			}
			applyMap.put(activityId, applyers.toString());
		});
		return applyMap;
	}

	@Override
	public boolean isSuspended(String processInstanceId) {
		boolean flag = true;
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		if (processInstance != null){
			flag = !processInstance.isSuspended();
		}
		return flag;
	}

}
