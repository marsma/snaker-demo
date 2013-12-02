/* Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.snaker.modules.flow.actorall.web;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.snaker.engine.SnakerEngine;
import org.snaker.engine.core.ModelContainer;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Process;
import org.snaker.engine.entity.Task;
import org.snaker.engine.model.WorkModel;
import org.snaker.framework.security.entity.User;
import org.snaker.framework.security.shiro.ShiroUtils;
import org.snaker.modules.flow.actorall.entity.AATask1;
import org.snaker.modules.flow.actorall.entity.AATask2;
import org.snaker.modules.flow.actorall.service.ActorAllManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author yuqs
 * @version 1.0
 */
@Controller
@RequestMapping(value = "/flow/actorall")
public class ActorAllController {
	@Autowired
	private SnakerEngine snakerEngine;
	@Autowired
	private ActorAllManager manager;
	@RequestMapping(value = "all", method=RequestMethod.GET)
	public String all(Model model, String processId, String orderId, String taskId) {
		Process process = ModelContainer.getEntity(processId);
		List<WorkModel> models = process.getModel().getWorkModels();
		model.addAttribute("works", models);
		model.addAttribute("process", process);
		if(StringUtils.isNotEmpty(orderId) && StringUtils.isNotEmpty(taskId)) {
			Order order = snakerEngine.query().getOrder(orderId);
			Task task = snakerEngine.query().getTask(taskId);
			model.addAttribute("order", order);
			model.addAttribute("task", task);
			model.addAttribute("task1s", manager.getTask1s(orderId));
			model.addAttribute("task2s", manager.getTask2s(orderId));
		} else {
			User user = ShiroUtils.getUser();
			Order order = snakerEngine.startInstanceById(processId, user.getId());
			List<Task> tasks = snakerEngine.query().getActiveTasks(order.getId());
			model.addAttribute("order", order);
			if(tasks != null && tasks.size() > 0) {
				model.addAttribute("task", tasks.get(0));
			}
		}
		return "flow/actorall/all";
	}
	
	@RequestMapping(value = "task1/save", method=RequestMethod.POST)
	public String task1Save(String orderId, String taskId, AATask1 entity) {
		manager.save(orderId, taskId, entity);
		return "redirect:/snaker/task/active";
	}
	
	@RequestMapping(value = "task2/save", method=RequestMethod.POST)
	public String task2Save(String orderId, String taskId, AATask2 entity) {
		manager.save(orderId, taskId, entity);
		return "redirect:/snaker/task/active";
	}
}
