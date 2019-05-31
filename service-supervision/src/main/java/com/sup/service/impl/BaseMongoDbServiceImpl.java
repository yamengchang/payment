package com.sup.service.impl;

import com.omv.mongo.util.*;
import com.sup.pojo.*;
import com.sup.service.*;
import org.apache.commons.logging.*;
import org.springframework.beans.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.util.*;

import java.util.*;

/**
 * @author tdf
 * @date 2018-12-18 14:25
 **/
public class BaseMongoDbServiceImpl<T extends BaseEntity> implements IBaseMongoDbService<T> {
	private static final Log logger = LogFactory.getLog(BaseMongoDbServiceImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	private Class<T> aClass;

	BaseMongoDbServiceImpl(Class aClass) {
		logger.info("初始化" + aClass + "...");
		this.aClass = aClass;
	}

	@Override
	public String save(T t) {
		Assert.notNull(t, "保存失败,参数不能为空");
		T temp = findById(t.getId());
		mongoTemplate.save(com.sup.util.BeanUtils.copyProperties(t, temp == null ?com.sup.util.BeanUtils.newTclass(aClass) : temp), aClass.getName());
		return t.getId();
	}

	@Override
	public String saveAll(List<T> list) {
		Assert.notEmpty(list, "参数不能为空");
		mongoTemplate.insertAll(list);
		return "SUCCESS";
	}

	@Override
	@SuppressWarnings("unchecked")
	public Page findAll(Map<String, Object> params) {
		Assert.notEmpty(params, "分页查询失败,参数不能为空");

		Integer pageNo = params.get("pageNo") == null ?1 : Integer.valueOf(params.get("pageNo").toString());
		Integer pageSize = params.get("pageSize") == null ?10 : Integer.valueOf(params.get("pageSize").toString());
		if (params.get("pageNo") != null) {
			params.remove(params.remove("pageNo").toString());
		}
		if (params.get("pageSize") != null) {
			params.remove(params.remove("pageSize").toString());
		}
		Query query = MongoQueryUtils.getQuery(params);
		Long totalCount = mongoTemplate.count(query, aClass.getName());
		query.skip((pageNo - 1) * pageSize).limit(pageSize);

		long totalPages = totalCount / pageSize;
		if (totalCount % pageSize != 0) {
			totalPages = totalPages + 1;
		}
		return new Page(pageNo, pageSize, totalCount, Integer.valueOf(String.valueOf(totalPages)), mongoTemplate.find(query, aClass, aClass.getName()));
	}


	@Override
	@SuppressWarnings("unchecked")
	public T findById(String id) {
		Assert.notNull(id, "根据id查询失败,参数不能为空");
		return (T)mongoTemplate.findById(id, aClass, aClass.getName());
	}

	@Override
	public long delete(String id) {
		Assert.notNull(id, "根据id删除失败,参数不能为空");
		return mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), aClass, aClass.getName()).getDeletedCount();
	}

	/**
	 * 非空字段更新
	 * @param t :实体对象
	 * @return ：long
	 */
	@Override
	public long update(T t) {
		Assert.notNull(t, "根据id更新失败,参数修改参数不能为空");
		Assert.notNull(t.getId(), "根据id更新失败,id不能为空");
		return mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(t.getId())), getUpdatePropertyNames(t), aClass, aClass.getName()).getModifiedCount();
	}

	@Override
	public long batchUpdate(List<T> list) {
		BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, aClass.getName());
		for (T t : list) {
			bulkOperations.upsert(new Query(Criteria.where("_id").is(t.getId())), getUpdatePropertyNames(t));
		}
		return bulkOperations.execute().getModifiedCount();
	}

	@Override
	public List<T> find(Map param) {
		return mongoTemplate.find(MongoQueryUtils.getQuery(param), aClass, aClass.getName());
	}

	@Override
	public T findOne(Map param) {
		List<T> list = mongoTemplate.find(MongoQueryUtils.getQuery(param), aClass, aClass.getName());
		if (list == null || list.size() == 0) {
			return null;
		}
		if(list.size()>1){
			Assert.isTrue(false, "查询数据大于一条");
		}
		return list.get(0);
	}

	private static Update getUpdatePropertyNames(final Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		Update update = new Update();
		for (java.beans.PropertyDescriptor pd : src.getPropertyDescriptors()) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (!"class".equals(pd.getName()) && srcValue != null && !"".equals(srcValue)) {
				update.set(pd.getName(), srcValue);
			}
		}
		return update;
	}
}
