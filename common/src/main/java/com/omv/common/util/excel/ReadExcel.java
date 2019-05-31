package com.omv.common.util.excel;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by by on 2017/10/9.
 */
public class ReadExcel {
//    public static void main(String[] args) {
//        // 获取Excel文件的sheet列表
//        try {
//            InputStream InputStream = new InputStream("C:/Users/by/Desktop/常见问题数据导入样表.xlsx");
//            String suffix = "xlsx";
//            getSheetList(InputStream, suffix);
//
//            // 获取Excel文件的第1个sheet的内容
//            readExcel(InputStream, 0, suffix);
//
//            // 获取Excel文件的第2个sheet的第2、4-7行和第10行及以后的内容
//            readExcelByRows(InputStream, 1, "2,4-7,10-", suffix);
//
//            // 获取Excel文件的第3个sheet中a,b,g,h,i,j等列的所有内容
//            readExcelByCell(InputStream, 2, new String[]{"a", "b", "g", "h", "i", "j"}, suffix);
//
//            // 获取Excel文件的第4个sheet的第2、4-7行和第10行及以后，a,b,g,h,i,j等列的内容
//            readExcelByAssign(InputStream, 3, "2,4-7,10-", new String[]{"a", "b", "g", "h", "i", "j"}, suffix);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    // 测试获取sheet列表
    public static ArrayList<String> getSheetList(InputStream inStream, String suffix) {
        PoiExcelHelper helper = getPoiExcelHelper(suffix);

        // 获取Sheet列表
        ArrayList<String> arrayList = helper.getSheetList(inStream);
//        printList(arrayList);
        return arrayList;
    }

    // 测试Excel读取
    public static ArrayList<ArrayList<String>> readExcel(InputStream inStream, int sheetIndex, String suffix) {
        PoiExcelHelper helper = getPoiExcelHelper(suffix);

        // 读取excel文件数据
        ArrayList<ArrayList<String>> dataList = helper.readExcel(inStream, sheetIndex);

        // 打印单元格数据
//        printBody(dataList);
        return dataList;
    }

    /*
   *@Author:Gavin
   *@Email:gavinsjq@sina.com
   *@Date:  2017/10/9
   *@Param filePath文件   sheetIndex-页码  rows-行（eg:"2,4-7,10-",其中“10-”标识10行以后的所有数据）
   *@Description:读取指定行的内容
   */
    public static ArrayList<ArrayList<String>> readExcelByRows(InputStream inStream, int sheetIndex, String rows, String suffix) {
        PoiExcelHelper helper = getPoiExcelHelper(suffix);

        // 读取excel文件数据
        ArrayList<ArrayList<String>> dataList = helper.readExcel(inStream, sheetIndex, rows);

        // 打印单元格数据
//        printBody(dataList);
        return dataList;
    }

    /*
   *@Author:Gavin
   *@Email:gavinsjq@sina.com
   *@Date:  2017/10/9
   *@Param filePath文件   sheetIndex-页码   columns-列（eg:new String[] {"a","b","g","h","i","j"}）
   *@Description:读取指定列的内容
   */
    public static ArrayList<ArrayList<String>> readExcelByCell(InputStream inStream, int sheetIndex, String[] columns, String suffix) {
        PoiExcelHelper helper = getPoiExcelHelper(suffix);

        // 读取excel文件数据
        ArrayList<ArrayList<String>> dataList = helper.readExcel(inStream, sheetIndex, columns);

        // 打印列标题
//        printHeader(columns);

        // 打印单元格数据
//        printBody(dataList);
        return dataList;
    }

    /*
    *@Author:Gavin
    *@Email:gavinsjq@sina.com
    *@Date:  2017/10/9
    *@Param filePath文件   sheetIndex-页码  rows-行（eg:"2,4-7,10-"）  columns-列（eg:new String[] {"a","b","g","h","i","j"}）
    *@Description:通过指定行和列读取内容
    */
    public static ArrayList<ArrayList<String>> readExcelByAssign(InputStream inStream, int sheetIndex, String rows, String[] columns, String suffix) {
        PoiExcelHelper helper = getPoiExcelHelper(suffix);

        // 读取excel文件数据
        ArrayList<ArrayList<String>> dataList = helper.readExcel(inStream, sheetIndex, rows, columns);

        // 打印列标题
//        printHeader(columns);

        // 打印单元格数据
//        printBody(dataList);
        return dataList;
    }

    // 获取Excel处理类
    private static PoiExcelHelper getPoiExcelHelper(String suffix) {
        PoiExcelHelper helper;
        if (suffix.indexOf(".xlsx") != -1) {
            helper = new PoiExcel2k7Helper();
        } else {
            helper = new PoiExcel2k3Helper();
        }
        return helper;
    }


    // 打印Excel的Sheet列表
//    private static void printList(ArrayList<String> sheets) {
//        for (String sheet : sheets) {
//            System.out.println(" ==> " + sheet);
//        }
//    }

//    // 打印列标题
//    private static void printHeader(String[] columns) {
//        System.out.println();
//        for (String column : columns) {
//            System.out.print("\t\t" + column.toUpperCase());
//        }
//    }

    // 打印单元格数据
//    private static void printBody(ArrayList<ArrayList<String>> dataList) {
//        int index = 0;
//        for (ArrayList<String> data : dataList) {
//            index++;
//            System.out.println();
//            System.out.print(index);
//            for (String v : data) {
//                System.out.print("\t\t" + v);
//            }
//        }
//    }
}
