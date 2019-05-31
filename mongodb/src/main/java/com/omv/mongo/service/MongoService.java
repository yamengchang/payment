package com.omv.mongo.service;

import com.omv.mongo.util.Page;

import java.util.Collection;
import java.util.Map;

/**
 * Created by zwj on 2018/6/13.
 */
public interface MongoService {
    void save(Object obj, Object... collectionName);

    void delete(Object obj, Object... collectionName);

    Page findAll(Map<String, Object> params, Class clazz, Object... collectionName);

    Object findOne(Map<String, Object> params, Class clazz, Object... collectionName);

}
