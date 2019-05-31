package com.omv.common.util.addr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zwj on 2018/8/13.
 */
public class PositionUtil {
    /*
    *@Author : Gavin
    *@Email : gavinsjq@sina.com
    *@Date: 2018/8/16 14:19
    *@Description : 根据地址查看经纬度
    *@Params :  * @param null
    */
    public static Map<String, Double> getCoordinate(String addr) throws IOException {
        String lng = null;//经度
        String lat = null;//纬度
        String address = null;
        try {
            address = java.net.URLEncoder.encode(addr, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String ak = "uu4XFftwlHUuluBdOd9mlunBuIFOr4aZ";
        String url = String.format("http://api.map.baidu.com/geocoder/v2/?address=%s&output=json&ak=%s", address, ak);
        URL myURL = null;
        URLConnection httpsConn = null;
        try {
            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader insr = null;
        BufferedReader br = null;
        Map<String, Double> addrMap = new HashMap<>();
        try {
            httpsConn = (URLConnection) myURL.openConnection();// 不使用代理
            if (httpsConn != null) {
                insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
                br = new BufferedReader(insr);
                String data = null;
                int count = 1;
                String result = "";
                while ((data = br.readLine()) != null) {
                    result += data;

                }
                JSONObject obj = JSON.parseObject(result);
                if (obj.getInteger("status") == 0) {
                    JSONObject location = obj.getJSONObject("result").getJSONObject("location");
                    addrMap.put("lng", location.getDouble("lng"));//经度
                    addrMap.put("lat", location.getDouble("lat"));//纬度
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (insr != null) {
                insr.close();
            }
            if (br != null) {
                br.close();
            }
        }
        return addrMap;
    }

    /*
    *@Author : Gavin
    *@Email : gavinsjq@sina.com
    *@Date: 2018/8/16 14:18
    *@Description : 根据经纬度查地址
    *@Params :  * @param null
    */
    public static String getAddress(Double[] coordinate) throws IOException {
        String lng = null;//经度
        String lat = null;//纬度
        String coordinateStr = coordinate[0] + "," + coordinate[1];
        String ak = "uu4XFftwlHUuluBdOd9mlunBuIFOr4aZ";
        String url = String.format("http://api.map.baidu.com/geocoder/v2/?location=%s&output=json&pois=1&ak=%s", coordinateStr, ak);
        URL myURL = null;
        URLConnection httpsConn = null;
        try {
            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader insr = null;
        BufferedReader br = null;
        String address = "";
        try {
            httpsConn = (URLConnection) myURL.openConnection();// 不使用代理
            if (httpsConn != null) {
                insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
                br = new BufferedReader(insr);
                String data = null;
                int count = 1;
                String result = "";
                while ((data = br.readLine()) != null) {
                    result += data;

                }
                JSONObject obj = JSON.parseObject(result);
                if (obj.getInteger("status") == 0) {
                    JSONObject addressComponent = obj.getJSONObject("result").getJSONObject("addressComponent");
                    String province = addressComponent.getString("province");
                    String city = addressComponent.getString("city");
                    String district = addressComponent.getString("district");
                    address = province + "-" + city + "-" + district;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (insr != null) {
                insr.close();
            }
            if (br != null) {
                br.close();
            }
        }
        return address;
    }


    /*
    *@Author : Gavin
    *@Email : gavinsjq@sina.com
    *@Date: 2018/8/16 14:18
    *@Description : 在百度数据管理平台创建数据
    *@Params :  * @param null
    */
    public static boolean createData(String title, String addr, Map<String, Double> coordinate, Map<String, Object> customColumn) throws IOException {
        Double lng = coordinate.get("lng");//经度
        Double lat = coordinate.get("lat");//纬度
        String ak = "XMT83OGb1GCRZUk0mqqT91PM8wPLMdyf";
        String url = String.format("http://api.map.baidu.com/geodata/v4/poi/create?" +
                "title=%s&addr=%s&coord_type=3&geotable_id=1000005314&&ak=%s&latitude=%s&longitude=%s", title, addr, ak, lat, lng);
        if (customColumn != null) {
            for (Map.Entry<String, Object> entry : customColumn.entrySet()) {
                String key = entry.getKey();
                Object val = entry.getValue();
                url = url + "&" + key + "=" + val;
            }
        }
        URL myURL = null;
        URLConnection httpsConn = null;
        try {
            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader insr = null;
        BufferedReader br = null;
        String address = "";
        try {
            httpsConn = (URLConnection) myURL.openConnection();// 不使用代理
            if (httpsConn != null) {
                insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
                br = new BufferedReader(insr);
                String data = null;
                int count = 1;
                String result = "";
                while ((data = br.readLine()) != null) {
                    result += data;

                }
                JSONObject obj = JSON.parseObject(result);
                String status = obj.getString("status");
                if (status.equals("0")) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (insr != null) {
                insr.close();
            }
            if (br != null) {
                br.close();
            }
        }
        return false;
    }

    /*
    *@Author : Gavin
    *@Email : gavinsjq@sina.com
    *@Date: 2018/8/16 14:18
    *@Description : 根据地区和关键词检索详细地址
    *@Params :  * @param null
    */
    public static String getAddressByKeyword(String city, String keyword) throws IOException {
        String lng = null;//经度
        String lat = null;//纬度
        String ak = "uu4XFftwlHUuluBdOd9mlunBuIFOr4aZ";
        String url = String.format("http://api.map.baidu.com/place/v2/search?query=%s&region=%s&output=json&ak=" + ak, keyword, city);
        URL myURL = null;
        URLConnection httpsConn = null;
        try {
            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader insr = null;
        BufferedReader br = null;
        String address = "";
        try {
            httpsConn = (URLConnection) myURL.openConnection();// 不使用代理
            if (httpsConn != null) {
                insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
                br = new BufferedReader(insr);
                String data = null;
                int count = 1;
                String result = "";
                while ((data = br.readLine()) != null) {
                    result += data;

                }
                JSONObject obj = JSON.parseObject(result);
                obj.remove("status");
                obj.put("code", 200);
                address = obj.toJSONString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (insr != null) {
                insr.close();
            }
            if (br != null) {
                br.close();
            }
        }
        return address;
    }

    public static void main(String[] args) throws IOException {
        Map o = getCoordinate("上海市长宁区晶尚坊");
        Map<String, Object> column = new HashMap<>();
        column.put("rent", "3");
        column.put("deposit", "4");
        createData("test2", "addr2", o, column);
//        String aa = getAddressByKeyword("上海市","晶尚坊");
//        System.out.println(aa);
//        PositionUtil getLatAndLngByBaidu = new PositionUtil();
//        Map o = getLatAndLngByBaidu.getCoordinate("上海市长宁区");
//        System.out.println(o.get("lng"));//经度
//        System.out.println(o.get("lat"));//纬度
//        Double[] addr = new Double[2];
//        addr[0] = (Double) o.get("lat");
//        addr[1] = (Double) o.get("lng");
//        String address = getAddress(addr);
//        System.out.println(address);
    }
}
