package com.omv.mongo.util;

import com.mongodb.BasicDBObject;
import com.omv.common.util.date.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by zwj on 2018/6/13.
 */
public class MongoQueryUtils {
    public static Query getQuery(Map<String, Object> params) {
        Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
        Map<String, Object> orderMap = new HashMap<>();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.indexOf("_asc") > 0 || key.indexOf("_desc") > 0) {
                orderMap.put(key, value);
                it.remove();
            }
        }

        String showFields = (String) params.get("showFields");
        Query query = null;
        if (StringUtils.isNotEmpty(showFields)) {
            query = getBasicQuery(params);
        } else {
            query = getCriteriaQuery(params);
        }

        Query finalQuery = query;
        orderMap.forEach((k, v) -> {
            String[] orderParam = k.split("_");
            if (orderParam[1].toLowerCase().equals("asc")) {
                finalQuery.with(Sort.by(Sort.Direction.ASC, orderParam[0]));
            } else if (orderParam[1].toLowerCase().equals("desc")) {
                finalQuery.with(Sort.by(Sort.Direction.DESC, orderParam[0]));
            }
        });
        return finalQuery;
    }

    private static Query getBasicQuery(Map<String, Object> params) {

        Query basicQuery = basicQuery(params);
        return basicQuery;
    }

    private static Query basicQuery(Map<String, Object> params) {
        String showFields = (String) params.get("showFields");
        params.remove("showFields");
        //指定返回的字段
        String showFieldArr[] = showFields.split(",");
        BasicDBObject fieldsObject = new BasicDBObject(showFieldArr.length);
        for (String showField : showFieldArr) {
            fieldsObject.put(showField, true);
        }

        return new BasicQuery(getBasic(params), fieldsObject.toJson());
    }

    private static String getBasic(Map<String, Object> params) {
        BasicDBObject searchQuery = new BasicDBObject();
        params.forEach((ka, v) -> {
            String[] ks = ka.split("_");
            String k = ks[0];
            String type = "";
            if (ks.length == 2) {
                type = ks[1];
            }
            String clzPath = null;
            if (ks.length == 3) {
                clzPath = ks[2];
            }
            Date date = null;
            Date endDate = null;
            if (k.toLowerCase().indexOf("time") >= 0 || k.toLowerCase().indexOf("date") >= 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = sdf.parse(v.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                v = date;
                endDate = DateUtil.getFinallyDate(date);
            }

            switch (type) {
                case "l":
                    Pattern pattern = Pattern.compile("^.*" + v + ".*$",
                            Pattern.CASE_INSENSITIVE);
                    searchQuery.put(k,pattern);
                    break;
                case "ne":
                    searchQuery.put(k,new BasicDBObject("$ne", v));
                    break;
                case "gt":
                    searchQuery.put(k,new BasicDBObject("$gt", v));
                    break;
                case "gte":
                    searchQuery.put(k,new BasicDBObject("$gte", v));
                    break;
                case "lt":
                    searchQuery.put(k,new BasicDBObject("$lt", v));
                    break;
                case "lte":
                    searchQuery.put(k,new BasicDBObject("$lte", v));
                    break;
                case "in":
                    if (v instanceof Collection) {
                        searchQuery.put(k,new BasicDBObject("$in", v));
                    } else {
                        String[] arr = ((String) v).split(",");
                        searchQuery.put(k,new BasicDBObject("$in", arr));
                    }
                    break;
                default:
                    if (k.toLowerCase().indexOf("time") >= 0 || k.toLowerCase().indexOf("date") >= 0) {
                        searchQuery.put(k,new BasicDBObject("$gte", v).append("$lte", endDate));
                        break;
                    }
                    searchQuery.put(k,v);
                    break;
            }
        });
        return searchQuery.toString();
    }

    private static Query getCriteriaQuery(Map<String, Object> params) {
        return new Query(getCriteria(params));
    }

    public static Criteria getCriteria(Map<String, Object> param) {
        Criteria criteria = new Criteria();
        param.forEach((ka, v) -> {
            String[] ks = ka.split("_");
            String k = ks[0];
            String type = "";
            if (ks.length == 2) {
                type = ks[1];
            }
            String clzPath = null;
            if (ks.length == 3) {
                clzPath = ks[2];
            }
            Date date = null;
            Date endDate = null;
            if (k.toLowerCase().indexOf("time") >= 0 || k.toLowerCase().indexOf("date") >= 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = sdf.parse(v.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                v = date;
                endDate = DateUtil.getFinallyDate(date);
            }

            switch (type) {
                case "l":
                    Pattern pattern = Pattern.compile("^.*" + v + ".*$",
                            Pattern.CASE_INSENSITIVE);
                    criteria.and(k).regex(pattern);
                    break;
                case "ne":
                    criteria.and(k).ne(v);
                    break;
                case "gt":
                    criteria.and(k).gt(v);
                    break;
                case "gte":
                    criteria.and(k).gte(v);
                    break;
                case "lt":
                    criteria.and(k).lt(v);
                    break;
                case "lte":
                    criteria.and(k).lte(v);
                    break;
                case "in":
                    if (v instanceof Collection) {
                        criteria.and(k).is(v);
                    } else {
                        String[] arr = ((String) v).split(",");
                        criteria.and(k).in(arr);
                    }
                    break;
                default:
                    if (k.toLowerCase().indexOf("time") >= 0 || k.toLowerCase().indexOf("date") >= 0) {
                        criteria.and(k).gt(v).lt(endDate);
                        break;
                    }
                    criteria.and(k).is(v);
                    break;
            }
        });
        return criteria;
    }
}
