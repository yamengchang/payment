package com.omv.common.util.basic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
*@Author:Gavin
*@Email:gavinsjq@sina.com
*@Date:  2017/12/12
*@Param
*@Description:
*/
public class OperateLogger {
    public static void doLog(String username, String operation) {
//        createFile(Dictionary.LOG_PATH + Constants.OPERTION_FILE);
//        String time = DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss");
//        String content = "时间：" + time + " 用户：" + username + "    操作：" + operation;
//        content += "\r\n";
//        appandOperation(content);
    }

    private static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            return false;
        }
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            System.out.println("创建目录" + destDirName + "成功！");
            return true;
        } else {
            System.out.println("创建目录" + destDirName + "失败！");
            return false;
        }
    }

    public static void appandOperation(String content) {
//        String file = Dictionary.LOG_PATH + Constants.OPERTION_FILE;
        String file = "";
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(file, true);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
