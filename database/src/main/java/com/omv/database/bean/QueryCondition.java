
package com.omv.database.bean;


import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by WANG, RUIQING on 12/22/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
public class QueryCondition {

    private ConditionType conditionType;
    private String field;
    private Object value;
    private String className;

    public QueryCondition(ConditionType conditionType, String field, Object value) {
        this.conditionType = conditionType;
        this.field = field;
        this.value = value;
        this.className = null;
    }

    public QueryCondition(ConditionType conditionType, String field, Object value, String clzPath) {
        this.conditionType = conditionType;
        this.field = field;
        this.value = value;
        this.className = clzPath;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public <T> Expression<Boolean> getExpression(Root<T> root, CriteriaBuilder cb) {
        if (null != className) {
            try {
                Class clz = Class.forName(className);
                value = Enum.valueOf(clz, value.toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        switch (conditionType) {
            case equal:
                if(value.equals("null")){
                    return cb.isNull(root.get(field));
                }
                Date date = null;
                if (field.indexOf("Time") >= 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        date = sdf.parse(value.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return cb.equal(root.get(field), date);
                }
                return cb.equal(root.get(field), value);
            case notEqual:
                if(value.equals("null")){
                    return cb.isNotNull(root.get(field));
                }
                return cb.notEqual(root.get(field), value);
            case like:
                return cb.like(root.get(field), "%" + value + "%");
            case notLike:
                return cb.notLike(root.get(field), "%" + value + "%");
            case greaterThan:
                Date gtDate = null;
                if (field.indexOf("Time") >= 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        gtDate = sdf.parse(value.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return cb.greaterThan(root.get(field), gtDate);
                }
                return cb.greaterThan(root.get(field), value.toString());
            case greaterThanEquals:

                Date gteDate = null;
                if (field.indexOf("Time") >= 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        gteDate = sdf.parse(value.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return cb.greaterThanOrEqualTo(root.get(field), gteDate);
                }
                return cb.greaterThanOrEqualTo(root.get(field), value.toString());
            case lessThan:
                Date lessDate = new Date();
                if (field.indexOf("Time") >= 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        lessDate = sdf.parse(value.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return cb.lessThan(root.get(field), lessDate);
                }
                return cb.lessThan(root.get(field), value.toString());
            case lessThanEquals:
                Date lessEqualsDate = new Date();
                if (field.indexOf("Time") >= 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        lessEqualsDate = sdf.parse(value.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return cb.lessThanOrEqualTo(root.get(field), lessEqualsDate);
                }
                return cb.lessThanOrEqualTo(root.get(field), value.toString());
            case in:
                Object[] objs = value.toString().split(",");
                List list = new ArrayList();
                try {
                    for (Object obj : objs) {
                        list.add(obj.toString());
                    }
                } catch (Exception e) {
                    for (Object obj : objs) {
                        list.add(obj);
                    }
                }
                return cb.in(root.get(field)).value(list);
            case notIn:
                Object[] ninObjs = value.toString().split(",");
                List ninList = new ArrayList();
                try {
                    for (Object obj : ninObjs) {
                        ninList.add(obj.toString());
                    }
                } catch (Exception e) {
                    for (Object obj : ninObjs) {
                        ninList.add(obj);
                    }
                }
                return cb.in(root.get(field)).value(ninList).not();
            default:
                break;
        }
//		return cb.equal(root.<Integer>get("difficulty"),Integer.valueOf(value.toString())) ;
        return null;
    }

    public <T> void sortBy(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Order> orderList;
        if (query.getOrderList().isEmpty()) {
            orderList = new ArrayList<>();
        } else {
            orderList = query.getOrderList();
        }

        boolean flag = true;//是否默认时间倒序排序
        if (null != className) {
            try {
                Class clz = Class.forName(className);
                value = Enum.valueOf(clz, value.toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        switch (conditionType) {
            case asc:
                orderList.add(cb.asc(root.get(field)));
                break;
            case desc:
                orderList.add(cb.desc(root.get(field)));
                break;
            default:
                break;
        }
        query.orderBy(orderList);
    }


//    public void sortBy(Root<T> root, CriteriaQuery<?> query,  CriteriaBuilder cb) {
//
//    }

//	private  <T> Expression<Boolean> expString(Root<T> root, CriteriaBuilder cb) {
//		return cb.equal(root.get(field),value) ;
//	}


}
