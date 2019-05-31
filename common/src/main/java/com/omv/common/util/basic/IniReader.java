package com.omv.common.util.basic;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by by on 2017/10/12.
 */
public class IniReader {
    public static void main(String[] args) {
        Map<String, Object> ini = null;
        try {
            ini = readIniFile("excelModel.ini");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (String k : ini.keySet()) {
            System.out.println(ini.get(k));
//            System.out.println(k + ini.get(k));
        }
        List<String> portArr = (List<String>) ini.get("port");
        System.out.println(((Map<String, String>) ini.get("language")).get("Chinese"));
    }

    /**
     * 去除ini文件中的注释，以";"或"#"开头，顺便去除UTF-8等文件的BOM头
     *
     * @param source
     * @return
     */
    private static String removeIniComments(String source) {
        String result = source;

        if (result.contains(";")) {
            result = result.substring(0, result.indexOf(";"));
        }

        if (result.contains("#")) {
            result = result.substring(0, result.indexOf("#"));
        }

        return result.trim();
    }

    /**
     * 读取 INI 文件，存放到Map中
     * <p>
     * 支持以‘#’或‘;’开头的注释；
     * 支持行连接符（行末的'\'标记）；
     * 支持缺省的global properties；
     * 支持list格式（非name=value格式处理为list格式）；
     * 支持空行、name/value前后的空格；
     * 如果有重名，取最后一个；
     * <p>
     * 格式（例子）如下
     * <p>
     * # 我是注释
     * ; 我也是注释
     * <p>
     * name0=value0  # 我是global properties
     * name10=value10
     * <p>
     * [normal section] # 我是普通的section
     * name1=value1 # 我是name和value
     * <p>
     * [list section] # 我是只有value的section，以第一个是否包含'='为判断标准
     * value1
     * value2
     *
     * @param fileName
     * @return Map<sectionName, object> object是一个Map（存放name=value对）或List（存放只有value的properties）
     */
    public static Map<String, Object> readIniFile(String fileName) throws FileNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(fileName);
        String filePath = url.getPath();
        Map<String, List<String>> listResult = new HashMap<String, List<String>>();
        Map<String, Object> result = new HashMap<String, Object>();

        String globalSection = "global"; //Map中存储的global properties的key

        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            //reader = new BufferedReader(new FileReader(file));

            //使用BOMInputStream自动去除UTF-8中的BOM！！！
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            String str = null;
            String currentSection = globalSection; //处理缺省的section
            List<String> currentProperties = new ArrayList<String>();
            boolean lineContinued = false;
            String tempStr = null;

            //一次读入一行（非空），直到读入null为文件结束
            //先全部放到listResult<String, List>中
            while ((str = reader.readLine()) != null) {
                str = removeIniComments(str).trim(); //去掉尾部的注释、去掉首尾空格

                if ("".equals(str) || str == null) {
                    continue;
                }

                //如果前一行包括了连接符'\'
                if (lineContinued == true) {
                    str = tempStr + str;
                }

                //处理行连接符'\'
                if (str.endsWith("\\")) {
                    lineContinued = true;
                    tempStr = str.substring(0, str.length() - 1);
                    continue;
                } else {
                    lineContinued = false;
                }

                //是否一个新section开始了
                if (str.startsWith("[") && str.endsWith("]")) {
                    String newSection = str.substring(1, str.length() - 1).trim();

                    //如果新section不是现在的section，则把当前section存进listResult中
                    if (!currentSection.equals(newSection)) {
                        listResult.put(currentSection, currentProperties);
                        currentSection = newSection;

                        //新section是否重复的section
                        //如果是，则使用原来的list来存放properties
                        //如果不是，则new一个List来存放properties
                        currentProperties = listResult.get(currentSection);
                        if (currentProperties == null) {
                            currentProperties = new ArrayList<String>();
                        }
                    }
                } else {
                    currentProperties.add(str);
                }
            }

            //把最后一个section存进listResult中
            listResult.put(currentSection, currentProperties);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        //整理拆开name=value对，并存放到MAP中：
        //从listResult<String, List>中，看各个list中的元素是否包含等号“=”，如果包含，则拆开并放到Map中
        //整理后，把结果放进result<String, Object>中
        for (String key : listResult.keySet()) {
            List<String> tempList = listResult.get(key);

            //空section不放到结果里面
            if (tempList == null || tempList.size() == 0) {
                continue;
            }

            if (tempList.get(0).contains("=")) { //name=value对，存放在MAP里面
                Map<String, String> properties = new HashMap<String, String>();
                for (String s : tempList) {
                    int delimiterPos = s.indexOf("=");
                    //处理等号前后的空格
                    properties.put(s.substring(0, delimiterPos).trim(), s.substring(delimiterPos + 1, s.length()).trim());
                }
                result.put(key, properties);
            } else { //只有value，则获取原来的list
                result.put(key, listResult.get(key));
            }
        }

        return result;
    }
}
