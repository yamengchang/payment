package com.pay.controller.signature;

import com.pay.service.ILeaseTemplateService;
import com.pay.entity.LeaseTemplate;
import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.userutil.UserUtils;
import com.omv.database.bean.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by zwj on 2019/1/3.
 */
@RestController
public class LeaseTemplateController {
	@Autowired
	private ILeaseTemplateService templateService;


	/*
	 *@Author : Gavin
	 *@Email : gavinsjq@sina.com
	 *@Date: 2019/1/9 15:07
	 *@Description : 创建合同模板
	 *@Params :  * @param null
	 */
	@PostMapping("/lease/template")
	public String save(@RequestBody LeaseTemplate template) {
		String userId = UserUtils.getUserId();
		template.setInstanceId(userId);
		templateService.save(template);
		return ValueUtil.toJson();
	}

	/*
	 *@Author : Gavin
	 *@Email : gavinsjq@sina.com
	 *@Date: 2019/1/9 15:07
	 *@Description : 合同模板列表
	 *@Params :  * @param null
	 */
	@GetMapping("/scrt/lease/templates")
	public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize) {
		String userId = UserUtils.getUserId();
		params.put("instanceId", userId);
		params.remove(params.remove("sign").toString());
		PageModel pageModel = PageModel.createPageMould(params, pageNo, pageSize);
		pageModel = templateService.findAll(pageModel);
		List<LeaseTemplate> templates = pageModel.getContent();
		for (LeaseTemplate template : templates) {

		}

		return ValueUtil.toJson(pageModel);
	}

	/*
	 *@Author : Gavin
	 *@Email : gavinsjq@sina.com
	 *@Date: 2019/1/9 15:08
	 *@Description : 删除合同模板
	 *@Params :  * @param null
	 */
	@DeleteMapping("/lease/template/{id}")
	public String index(@PathVariable("id") String id) {
		templateService.deleteById(id);
		return ValueUtil.toJson();
	}
}
