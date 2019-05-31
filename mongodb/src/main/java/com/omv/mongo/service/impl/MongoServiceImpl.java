package com.omv.mongo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.omv.mongo.service.MongoService;
import com.omv.mongo.util.MongoQueryUtils;
import com.omv.mongo.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by zwj on 2018/6/13.
 */
@Service
public class MongoServiceImpl implements MongoService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Object t, Object... collectionName) {
        if (t instanceof Collection) {
            saveAll((Collection) t, collectionName);
        } else {
            String collection = null;
            if (collectionName.length > 0) {
                collection = String.valueOf(collectionName[0]);
                mongoTemplate.save(t, collection);
            } else {
                mongoTemplate.save(t);
            }
        }
    }

    public void saveAll(Collection collections, Object... collectionName) {
        String collection = null;
        if (collectionName.length > 0) {
            collection = String.valueOf(collectionName[0]);
            for (Object t : collections) {
                mongoTemplate.save(t, collection);
            }
        } else {
            for (Object t : collections) {
                mongoTemplate.save(t);
            }
        }
    }

    @Override
    public void delete(Object t, Object... collectionName) {
        if (t instanceof Collection) {
            deleteAll((Collection) t, collectionName);
        } else {
            String collection = null;
            if (collectionName.length > 0) {
                collection = String.valueOf(collectionName[0]);
                mongoTemplate.remove(t, collection);
            } else {
                mongoTemplate.remove(t);
            }
        }
    }

    public void deleteAll(Collection<Object> collections, Object... collectionName) {
        String collection = null;
        if (collectionName.length > 0) {
            collection = String.valueOf(collectionName[0]);
            for (Object t : collections) {
                mongoTemplate.remove(t, collection);
            }
        } else {
            for (Object t : collections) {
                mongoTemplate.remove(t);
            }
        }
    }

    @Override
    public Page findAll(Map<String, Object> params, Class clazz, Object... collectionName) {
        String collection = null;
        if (collectionName.length > 0) {
            collection = String.valueOf(collectionName[0]);
        }
        Integer pageNo = params.get("pageNo") == null ? 1 : Integer.valueOf(params.get("pageNo").toString());
        Integer pageSize = params.get("pageSize") == null ? 10 : Integer.valueOf(params.get("pageSize").toString());
        if (params.get("pageNo") != null) {
            params.remove(params.remove("pageNo").toString());
        }
        if (params.get("pageSize") != null) {
            params.remove(params.remove("pageSize").toString());
        }
        Query query = MongoQueryUtils.getQuery(params);
        Page page = null;
        Long totalCount = null;
        List<JSONObject> resources = null;
        if (collectionName.length > 0) {
            totalCount = mongoTemplate.count(query, collection);
            long start = (pageNo - 1) * pageSize;
            query.skip(start).limit(pageSize);
            resources = mongoTemplate.find(query, clazz, collection);
        } else {
            totalCount = mongoTemplate.count(query, clazz);
            long start = (pageNo - 1) * pageSize;
            query.skip(start).limit(pageSize);
            resources = mongoTemplate.find(query, clazz);
        }
        long totalPages = totalCount / pageSize;
        long remainder = totalCount % pageSize;
        if (remainder != 0) {
            totalPages = totalPages + 1;
        }
        page = new Page(pageNo, pageSize, totalCount, Integer.valueOf(String.valueOf(totalPages)),resources);
        return page;
    }

    @Override
//    public Object findOne(Map<String, Object> params, Class clazz, String collectionName) {
    public Object findOne(Map<String, Object> params, Class clazz, Object... collectionName) {
        if (params.get("pageNo") != null) {
            params.remove(params.remove("pageNo").toString());
        }
        if (params.get("pageSize") != null) {
            params.remove(params.remove("pageSize").toString());
        }
        Query query = MongoQueryUtils.getQuery(params);
        Object returnObj;
        if (collectionName.length > 0) {
            String collection = null;
            if (collectionName.length > 0) {
                collection = String.valueOf(collectionName[0]);
            }
            returnObj = mongoTemplate.findOne(query, clazz, collection);
        }else{
            returnObj = mongoTemplate.findOne(query, clazz);
        }


        return returnObj;
    }


}
