package com.omv.database.biz;

import com.omv.database.bean.*;
import com.omv.database.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.repository.*;

import javax.persistence.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by WANG, RUIQING on 12/19/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@NoRepositoryBean
public class BaseBiz<T, ID extends Serializable> {

	@Autowired
	private BaseRepository<T, ID> baseRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public List findAll() {
		List result = null;
		try {
			result = baseRepository.findAll();
			if (null != result && result.size() > 0) {
				Method after = result.get(0).getClass().getMethod("afterFind");
				for (int i = 0 ; i < result.size() ; i++) {
					after.invoke(result.get(i));
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List findAll(Collection ids) {
		return baseRepository.findAllById(ids);
	}

	public List<T> findBySql(String sql, List<Object> param, int pageNo, int pageSize) {
		Query query = entityManager.createQuery(sql);
		for (int i = 0 ; i < param.size() ; i++) {
			query.setParameter(i, param.get(i));
		}
		query.setMaxResults(pageSize);
		query.setFirstResult((pageNo - 1) * pageSize);
		List<T> list = query.getResultList();
		return list;
	}

	public Object findBySqlCount(String sql, List<Object> param) {
		Query query = entityManager.createQuery(sql);
		for (int i = 0 ; i < param.size() ; i++) {
			query.setParameter(i, param.get(i));
		}
		Object count = query.getSingleResult();
		return count;
	}

	@Cacheable(value = "all", key = " #key")
	public PageModel findAll(PageModel pageModel, String key) {
		return findAll(pageModel);
	}

	public PageModel findAll(PageModel pageModel) {
		List result = null;
		try {
			switch (pageModel.getKind()) {
				case 1:
					Page<T> page1 = baseRepository.findAll(pageModel.getWhereClause(), pageModel.getPageable());
					result = page1.getContent();
					pageModel.setTotalPages(page1.getTotalPages());
					pageModel.setTotalRows(page1.getTotalElements());
					break;
				case 2:
					Page<T> page2 = baseRepository.findAll(pageModel.getWhereClause(), pageModel.getPageable());
					result = page2.getContent();
					pageModel.setTotalPages(page2.getTotalPages());
					pageModel.setTotalRows(page2.getTotalElements());
					break;
				case 3:
					result = baseRepository.findAll(pageModel.getWhereClause());
					break;
				case 4:
					result = baseRepository.findAll();
					break;
			}
			if (null != result && result.size() > 0) {
				Method after = result.get(0).getClass().getMethod("afterFind", String.class);
				for (int i = 0 ; i < result.size() ; i++) {
					after.invoke(result.get(i), pageModel.getFields());
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		pageModel.setContent(result);
		return pageModel;
	}


	public T findOne(ID id) {
		T result = null;
		try {
			Optional<T> optional = baseRepository.findById(id);
			result = baseRepository.findById(id).orElse(null);
			if (null != result) {
				Method after = result.getClass().getMethod("afterFind");
				after.invoke(result);
			}
		}catch (NoSuchMethodException e) {
			e.printStackTrace();
		}catch (InvocationTargetException e) {
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	public T save(T entity) {
			return baseRepository.save(entity);
	}

	public Collection<T> save(Collection<T> entities) {
		return baseRepository.saveAll(entities);
	}

	public void deleteById(ID id) {
		baseRepository.deleteById(id);
	}
}
