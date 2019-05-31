package com.sup.controller;

import com.alibaba.fastjson.*;
import com.omv.common.util.basic.*;
import com.omv.rabbit.service.*;
import com.sup.pojo.*;
import com.sup.service.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author tdf
 * @date 2018-12-19 15:53
 **/
public class BaseController<T extends BaseEntity> {
	@Autowired
	private MQSenderService mqSenderService;
	/**
	 * 通过子类的 默认的构造方法赋值
	 */
	IBaseMongoDbService baseMongoDbService;
	Class aClass;

	/**
	 * 新增
	 * @param t:对象
	 * @return ：{"code":"string","data":{},"msg":"string"}
	 */
	@PostMapping("/")
	@ApiOperation(value = "新增", notes = "新增")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "success", response = RestJson.class)})
	@SuppressWarnings("unchecked")
	public String receive(@RequestBody T t) {
		return ValueUtil.toJson(mqSenderService.sendAndReceive(aClass.getSimpleName() + "-save", JSONObject.toJSONString(t)));
		//		baseMongoDbService.save(t));
	}

	/**
	 * 批量新增
	 * @param t:实体对象
	 * @return :{"code":"string","data":{},"msg":"string"}
	 */
//	@PostMapping("/batch")
//	@ApiOperation(value = "批量新增", notes = "批量新增")
//	@ApiResponses(value = {@ApiResponse(code = 200, message = "success", response = RestJson.class)})
//	@SuppressWarnings("unchecked")
//	public String receive(@RequestBody List<T> t) {
//		return ValueUtil.toJson(mqSenderService.sendAndReceive(aClass.getSimpleName() + "-batchsave", JSONObject.toJSONString(t)));
//	}

	/**
	 * 批量新增
	 * @param
	 * @return :{"code":"string","data":{},"msg":"string"}
	 */
	@PostMapping("/batch")
	@ApiOperation(value = "批量新增", notes = "批量新增")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "success", response = RestJson.class)})
	@SuppressWarnings("unchecked")
	public String receive(@RequestBody ReceiveEntity receiveEntity) {
		mqSenderService.sendMsg(aClass.getSimpleName() + "-batchsave", JSONObject.toJSONString(receiveEntity));
		return ValueUtil.toJson();
	}

	/**
	 * 查询账单信息
	 * @param map :参数
	 * @return ：{"code":"string","data":{},"msg":"string"}
	 */
	@PostMapping("/page")
	@ApiOperation(value = "分页查询账单信息", notes = "分页查询账单信息")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "success", response = RestJson.class)})
	public String receive(@RequestParam(required = false) Map<String, Object> map, Integer pageNo, Integer pageSize) {
		map.put("pageNo", pageNo);
		map.put("pageSize", pageSize);
		return ValueUtil.toJson(baseMongoDbService.findAll(map));
	}

	/**
	 * 根据id查询
	 * @param id ：参数id
	 * @return ：{"code":"string","data":{},"msg":"string"}
	 */
	@GetMapping("/{id}")
	@ApiOperation(value = "根据id查询", notes = "根据id查询")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "success", response = RestJson.class)})
	public String receive(@PathVariable("id") String id) {
		return ValueUtil.toJson(baseMongoDbService.findById(id));
	}

	/**
	 * 根据id删除 物理删除请谨慎使用
	 * @param id :参数id
	 * @return ：{"code":"string","data":{},"msg":"string"}
	 */
	@Deprecated
	@DeleteMapping("/{id}")
	@ApiOperation(value = "根据id删除 物理删除请谨慎使用", notes = "根据id删除 物理删除请谨慎使用")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "success", response = RestJson.class)})
	public String deleteReceive(@PathVariable("id") String id) {
		return ValueUtil.toJson(baseMongoDbService.delete(id));
	}

	/**
	 * 根据id修改
	 * @param t ：实体对象
	 * @return ：{"code":"string","data":{},"msg":"string"}
	 */
	@PutMapping("/")
	@ApiOperation(value = "根据id修改", notes = "根据id修改")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "success", response = RestJson.class)})
	@SuppressWarnings("unchecked")
	public String updateReceive(@RequestBody T t) {
		System.out.println(aClass.getSimpleName());
		return ValueUtil.toJson(mqSenderService.sendAndReceive(aClass.getSimpleName() + "-update", JSONObject.toJSONString(t)));
	}
}
