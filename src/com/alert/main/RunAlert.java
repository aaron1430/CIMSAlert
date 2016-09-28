package com.alert.main;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alert.entity.Alarm;
import com.alert.entity.AlarmLevel;
import com.alert.entity.Contract;
import com.alert.entity.ProjectStage;
import com.alert.entity.ReceiveNode;
import com.alert.entity.Task;
import com.alert.entity.User;
import com.alert.repository.AlarmLevelRepository;
import com.alert.repository.AlarmRepository;
import com.alert.repository.ContractRepository;
import com.alert.repository.ProjectStageRepository;
import com.alert.repository.ReceiveNodeRepository;
import com.alert.repository.TaskRepository;
import com.alert.repository.UserRepository;

/**
 * 逐级报警
 * 
 * @author 包
 * @date 2016年9月23日
 */
@Component
public class RunAlert {
	@Autowired
	UserRepository userRepository;
	@Autowired
	ReceiveNodeRepository receiveNodeRepository;
	@Autowired
	ProjectStageRepository projectStageRepository;
	@Autowired
	TaskRepository taskRepository;
	@Autowired
	AlarmRepository alarmRepository;
	@Autowired
	AlarmLevelRepository alarmLevelRepository;
	@Autowired
	ContractRepository contractRepository;

	/**
	 * 报警主进程
	 */
	public void run() {
		// TODO Auto-generated method stub
		HandleRNAlert();
		HandlePSAlert();
		HandleTaskAlert();
	}

	/**
	 * 报警给用户
	 * 
	 * @param alarCode
	 *            报警编码
	 * @param alarContent
	 *            报警内容
	 * @param user
	 *            报警接收用户
	 * @param contract
	 *            报警所属合同
	 * @param task
	 *            引起报警的任务
	 */
	private void alarmToUser(String alarCode, String alarContent, User user, Contract contract, Task task) {
		// TODO Auto-generated method stub

		Alarm alarm = new Alarm();
		alarm.setAlar_code(alarCode);
		alarm.setAlar_content(alarContent);
		alarm.setUser(user);
		alarm.setContract(contract);
		alarm.setAlar_isremove(0);
		alarm.setAlar_time(new Date());
		alarm.setTask(task);
		alarmRepository.saveAndFlush(alarm);
	}

	/**
	 * 报警给用户
	 * 
	 * @param alarCode
	 *            报警编码
	 * @param alarContent
	 *            报警内容
	 * @param user
	 *            报警接收用户
	 * @param contract
	 *            报警所属合同
	 * @param ps
	 *            引起报警的工程进度
	 */
	private void alarmToUser(String alarCode, String alarContent, User user, Contract contract, ProjectStage ps) {
		// TODO Auto-generated method stub

		Alarm alarm = new Alarm();
		alarm.setAlar_code(alarCode);
		alarm.setAlar_content(alarContent);
		alarm.setUser(user);
		alarm.setContract(contract);
		alarm.setAlar_isremove(0);
		alarm.setAlar_time(new Date());
		alarm.setProjectStage(ps);
		alarmRepository.saveAndFlush(alarm);
	}

	/**
	 * 报警给用户
	 * 
	 * @param alarCode
	 *            报警编码
	 * @param alarContent
	 *            报警内容
	 * @param user
	 *            报警接收用户
	 * @param contract
	 *            报警所属合同
	 * @param rn
	 *            引起报警的收款节点
	 */
	private void alarmToUser(String alarCode, String alarContent, User user, Contract contract, ReceiveNode rn) {
		// TODO Auto-generated method stub

		Alarm alarm = new Alarm();
		alarm.setAlar_code(alarCode);
		alarm.setAlar_content(alarContent);
		alarm.setUser(user);
		alarm.setContract(contract);
		alarm.setAlar_isremove(0);
		alarm.setAlar_time(new Date());
		alarm.setReceiveNode(rn);
		alarmRepository.saveAndFlush(alarm);
	}

	/**
	 * 处理超时任务报警
	 */
	private void HandleTaskAlert() {
		// TODO Auto-generated method stub
		Date now = new Date();
		Date etime;
		List<Task> taskList = taskRepository.findAllUnclosed();
		for (int i = 0; i < taskList.size(); i++) {

			etime = taskList.get(i).getTask_etime();
			if (etime.compareTo(now) <= 0) {
				// 超时报警
				System.out.println(taskList.get(i).getTask_id() + "任务超时报警");
				taskTimeOutAlarm(taskList.get(i));
				updateTaskAlarmCount(taskList.get(i));
			}
		}
	}

	/**
	 * 写入报警信息
	 * 
	 * @param task
	 *            超时报警的任务
	 */
	private void taskTimeOutAlarm(Task task) {
		// TODO Auto-generated method stub
		String alarmContent = "任务超时：	负责人：" + task.getReceiver().getUser_name() + "	任务内容：" + task.getTask_content();
		String alarmCode = "1";
		Contract alarmContract = task.getContract();

		User taskReceiver = task.getReceiver();
		User taskCreator = task.getCreator();
		alarmToUser(alarmCode, alarmContent, taskReceiver, alarmContract, task);
		alarmToUser(alarmCode, alarmContent, taskCreator, alarmContract, task);
	}

	/**
	 * 更新任务报警次数
	 * 
	 * @param task
	 *            更新的任务
	 */
	private void updateTaskAlarmCount(Task task) {
		// TODO Auto-generated method stub
		task.setTask_alarmnum(task.getTask_alarmnum() + 1);
		taskRepository.saveAndFlush(task);
	}

	/**
	 * 处理超时收款节点报警
	 */
	private void HandlePSAlert() {
		// TODO Auto-generated method stub
		Date now = new Date();
		Integer prst_state = 0;
		Date etime;
		Date wtime;
		List<ProjectStage> projectStage = projectStageRepository.getNodesByState(prst_state);
		for (int i = 0; i < projectStage.size(); i++) {
			etime = projectStage.get(i).getPrst_etime();
			wtime = projectStage.get(i).getPrst_wtime();
			if (etime.compareTo(now) <= 0) {
				// 超时报警
				System.out.println(projectStage.get(i).getPrst_id() + "工程进度超时报警");
				PSTimeOutAlarm(projectStage.get(i));
				updateContractPSAlarmCount(projectStage.get(i).getContract());
			} else {
				if (wtime.compareTo(now) <= 0) {
					// 提前提醒
					PSPrenotice(projectStage.get(i));
					System.out.println(projectStage.get(i).getPrst_id() + "工程进度提前提醒");
				}
			}
		}
	}

	/**
	 * 更新合同工程进度报警次数
	 * 
	 * @param contract
	 *            要更新的合同
	 */
	private void updateContractPSAlarmCount(Contract contract) {
		// TODO Auto-generated method stub
		if (contract != null) {
			contract.setCont_proalanum(contract.getCont_proalanum() + 1);
			contractRepository.saveAndFlush(contract);
		}
	}

	/**
	 * 工程进度超时报警
	 * 
	 * @param projectStage
	 *            超时报警的工程进度记录
	 */
	private void PSTimeOutAlarm(ProjectStage projectStage) {
		// TODO Auto-generated method stub
		List<AlarmLevel> levelList = alarmLevelRepository
				.getAlarmSettingsByRank(projectStage.getContract().getCont_rank());
		Date etime = projectStage.getPrst_etime();
		Date now = new Date();
		long quot = now.getTime() - etime.getTime();
		int days = 0;
		days = (int) (quot / 1000 / 60 / 60 / 24);
		for (int i = 0; i < levelList.size(); i++) {
			if (days >= levelList.get(i).getAlle_days()) {
				List<User> recList = userRepository.findByRoleID(levelList.get(i).getRole().getRole_id());
				for (int j = 0; j < recList.size(); j++) {
					alarmToUser("5",
							"工程进度超时：	负责人：" + projectStage.getManager().getUser_name() + "	内容："
									+ projectStage.getPrst_content(),
							recList.get(j), projectStage.getContract(), projectStage);
				}

			}
		}
	}

	/**
	 * 工程进度提前提醒
	 * 
	 * @param projectStage
	 *            已到提醒时间的工程进度记录
	 */
	private void PSPrenotice(ProjectStage projectStage) {
		// TODO Auto-generated method stub
		String alarmContent = "工程进度提醒：" + projectStage.getPrst_content();
		String alarmCode = "4";
		User alarmUser = projectStage.getUser();
		Contract alarmContract = projectStage.getContract();
		alarmToUser(alarmCode, alarmContent, alarmUser, alarmContract, projectStage);

	}

	/**
	 * 处理收款节点超时
	 */
	private void HandleRNAlert() {
		// TODO Auto-generated method stub
		Date now = new Date();
		Integer nodeState = 0;
		Date renoTime;
		Date renoWTime;
		List<ReceiveNode> receiveNodeList = receiveNodeRepository.getNodesByState(nodeState);
		for (int i = 0; i < receiveNodeList.size(); i++) {
			renoTime = receiveNodeList.get(i).getReno_time();
			renoWTime = receiveNodeList.get(i).getReno_wtime();
			if (renoTime.compareTo(now) <= 0) {
				// 超时报警
				RNTimeOutAlarm(receiveNodeList.get(i));
				updateContractRNAlarmCount(receiveNodeList.get(i).getContract());
				System.out.println(receiveNodeList.get(i).getReno_id() + "收款超时报警");
			} else {
				if (renoWTime.compareTo(now) <= 0) {
					// 提前提醒
					RNPreNotice(receiveNodeList.get(i));
					System.out.println(receiveNodeList.get(i).getReno_id() + "收款提前提醒");
				}
			}
		}
	}

	private void updateContractRNAlarmCount(Contract contract) {
		// TODO Auto-generated method stub
		if (contract != null) {
			contract.setCont_payalanum(contract.getCont_payalanum() + 1);
			contractRepository.saveAndFlush(contract);
		}
	}

	/**
	 * 收款节点超时报警
	 * 
	 * @param receiveNode
	 *            超时报警的收款节点
	 */
	private void RNTimeOutAlarm(ReceiveNode receiveNode) {
		// TODO Auto-generated method stub
		List<AlarmLevel> levelList = alarmLevelRepository
				.getAlarmSettingsByRank(receiveNode.getContract().getCont_rank());
		Date etime = receiveNode.getReno_time();
		Date now = new Date();
		long quot = now.getTime() - etime.getTime();
		int days = 0;
		days = (int) (quot / 1000 / 60 / 60 / 24);
		for (int i = 0; i < levelList.size(); i++) {
			if (days >= levelList.get(i).getAlle_days()) {
				List<User> recList = userRepository.findByRoleID(levelList.get(i).getRole().getRole_id());
				for (int j = 0; j < recList.size(); j++) {
					alarmToUser("3",
							"收款超时：	负责人：" + receiveNode.getUser().getUser_name() + "	内容："
									+ receiveNode.getReno_content(),
							recList.get(j), receiveNode.getContract(), receiveNode);
				}

			}
		}
	}

	/**
	 * 收款节点提前提醒
	 * 
	 * @param receiveNode
	 *            要提醒的收款节点记录
	 */
	private void RNPreNotice(ReceiveNode receiveNode) {
		// TODO Auto-generated method stub
		String alarmContent = "收款提醒：" + receiveNode.getReno_content();
		String alarmCode = "2";
		User alarmUser = receiveNode.getUser();
		Contract alarmContract = receiveNode.getContract();
		alarmToUser(alarmCode, alarmContent, alarmUser, alarmContract, receiveNode);
	}
}
