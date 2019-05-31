package com.omv.common.util.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

public class ExportExcelByPoiUtil {

    public static void excel() throws Exception {
        // 创建Excel的工作书册 Workbook,对应到一个excel文档
        HSSFWorkbook wb = new HSSFWorkbook();

        // 创建Excel的工作sheet,对应到一个excel文档的tab
        HSSFSheet sheet = wb.createSheet("sheet1");

        // 设置excel每列宽度
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 3500);

        // 创建字体样式
        HSSFFont font = wb.createFont();
        font.setFontName("Verdana");
        font.setBoldweight((short) 100);
        font.setFontHeight((short) 300);
        font.setColor(HSSFColor.BLUE.index);

        // 创建单元格样式
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        // 设置边框
        style.setBottomBorderColor(HSSFColor.RED.index);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);

        style.setFont(font);// 设置字体

        // 创建Excel的sheet的一行
        HSSFRow row = sheet.createRow(0);
        row.setHeight((short) 500);// 设定行的高度
        // 创建一个Excel的单元格
        HSSFCell cell = row.createCell(0);

        // 合并单元格(startRow，endRow，startColumn，endColumn)
        sheet.addMergedRegion(new CellRangeAddress((short) 0, (short) 0, (short) 0, (short) 2));

        // 给Excel的单元格设置样式和赋值
        cell.setCellStyle(style);
        cell.setCellValue("hello world");

        // 设置单元格内容格式
        HSSFCellStyle style1 = wb.createCellStyle();
        style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("h:mm:ss"));

        style1.setWrapText(true);// 自动换行

        row = sheet.createRow(1);

        // 设置单元格的样式格式

        cell = row.createCell(0);
        cell.setCellStyle(style1);
        cell.setCellValue(new Date());

        // 创建超链接
        HSSFHyperlink link = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
        link.setAddress("http://www.baidu.com");
        cell = row.createCell(1);
        cell.setCellValue("百度");
        cell.setHyperlink(link);// 设定单元格的链接

        FileOutputStream os = new FileOutputStream("D:\\report\\workbook.xls");
        wb.write(os);
        os.close();
    }

    public static <T> HSSFWorkbook exportExcel(List<T> list, String[] column, String[] heads, String[] fields) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();

        if (column != null) {
            for (int i = 0; i < column.length; i++) {
                sheet.setColumnWidth(i, Short.valueOf(column[i]));
            }
        }

        HSSFRow headRow = sheet.createRow(0);
        headRow.setHeight((short) 400);

        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Verdana");
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);

        HSSFCellStyle style2 = wb.createCellStyle();
        HSSFFont font2 = wb.createFont();
        font2.setColor(HSSFFont.COLOR_RED);
        style2.setFont(font2);

        for (int i = 0; i < heads.length; i++) {
            HSSFCell cell = headRow.createCell(i);
            cell.setCellValue(heads[i]);
            cell.setCellStyle(style);
        }
        int i = 1;
        for (T t : list) {
            HSSFRow row = sheet.createRow(i);
            row.setHeight((short) 320);
            Class<? extends Object> c = t.getClass();
            int j = 0;
            for (String f : fields) {
                try {
                    Method m = c.getDeclaredMethod("get" + f.substring(0, 1).toUpperCase() + f.substring(1));
                    Object valueOb = m.invoke(t);
                    HSSFCell cell = row.createCell(j);
                    if (valueOb != null) {
                        if (valueOb instanceof Integer) {
                            cell.setCellValue((Integer) valueOb);
                        } else if (valueOb instanceof Float) {
                            cell.setCellValue((Float) valueOb);
                        } else if (valueOb instanceof Long) {
                            cell.setCellValue((Long) valueOb);
                        } else if (valueOb instanceof Date) {
                            cell.setCellValue((Date) valueOb);
                        } else {
                            cell.setCellValue(valueOb.toString());
                            if (valueOb.toString().equals("总计")) {
                                cell.setCellStyle(style);
                            }
                            if (valueOb.toString().contains("预算结果")) cell.setCellStyle(style2);
                        }
                    } else {
                        cell.setCellValue("");
                    }

                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                j++;
            }
            i++;
        }
        return wb;
    }

    public static <T> HSSFWorkbook exportExcels(List<T> list, String[] column, String[] heads, String[] fields) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();

        if (column != null) {
            for (int i = 0; i < column.length; i++) {
                sheet.setColumnWidth(i, Short.valueOf(column[i]));
            }
        }

        HSSFRow headRow = sheet.createRow(0);
        headRow.setHeight((short) 400);

        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Verdana");
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);

        HSSFCellStyle style2 = wb.createCellStyle();
        HSSFFont font2 = wb.createFont();
        font2.setColor(HSSFFont.COLOR_RED);
        style2.setFont(font2);

        for (int i = 0; i < heads.length; i++) {
            HSSFCell cell = headRow.createCell(i);
            cell.setCellValue(heads[i]);
            cell.setCellStyle(style);
        }
        int i = 1;
        for (T t : list) {
            HSSFRow row = sheet.createRow(i);
            row.setHeight((short) 320);
            Class<? extends Object> c = t.getClass();
            int j = 0;
            for (String f : fields) {
                try {
                    if (f.contains("+")) {

                        String[] flist = f.split("\\+");
                        String tempResult = "";
                        for (int n = 0; n < flist.length; n++) {
                            Method m = c.getDeclaredMethod("get" + flist[n].substring(0, 1).toUpperCase() + flist[n].substring(1));
                            String valueOb = (String) m.invoke(t);
                            tempResult += valueOb + "-";
                        }
                        HSSFCell cell = row.createCell(j);
                        tempResult = tempResult.substring(0, tempResult.length() - 1);
                        cell.setCellValue(tempResult);
                    } else {
                        Method m = c.getDeclaredMethod("get" + f.substring(0, 1).toUpperCase() + f.substring(1));
                        Object valueOb = m.invoke(t);
                        HSSFCell cell = row.createCell(j);
                        if (valueOb != null) {
                            if (valueOb instanceof Integer) {
                                cell.setCellValue((Integer) valueOb);
                            } else if (valueOb instanceof Float) {
                                cell.setCellValue((Float) valueOb);
                            } else if (valueOb instanceof Long) {
                                cell.setCellValue((Long) valueOb);
                            } else if (valueOb instanceof Date) {
                                cell.setCellValue((Date) valueOb);
                            } else {
                                cell.setCellValue(valueOb.toString());
                                if (valueOb.toString().equals("总计")) {
                                    cell.setCellStyle(style);
                                }
                                if (valueOb.toString().contains("预算结果")) cell.setCellStyle(style2);
                            }
                        } else {
                            cell.setCellValue("");
                        }
                    }

                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                j++;
            }
            i++;
        }
        return wb;
    }

    public static <T> HSSFWorkbook exportExcelByXmlTemplate(List<T> list, String filePath) {
        // 创建Excel的工作书册 Workbook,对应到一个excel文档
        HSSFWorkbook wb = new HSSFWorkbook();

        return wb;
    }


//    public static <T> HSSFWorkbook exportExcelTwo(ShipmentSummaryVO vo, List<Numbers> numbersExecl, String lastUploadTime, String[] headsBefore, String[] fieldsBefore, List<T> newReportResult1Execl, String[] column, String[] heads, String[] fields) {
//        HSSFWorkbook wb = new HSSFWorkbook();
//        HSSFSheet sheet = wb.createSheet();
//        HSSFCellStyle styleTitleMain = wb.createCellStyle();
//        HSSFCellStyle styleTitle = wb.createCellStyle();
//        HSSFCellStyle style = wb.createCellStyle();
//        HSSFCellStyle dateCellStyle = wb.createCellStyle();
//        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
//        short df = wb.createDataFormat().getFormat("MM/dd/yyyy hh:mm");
//        dateCellStyle.setDataFormat(df);
//        HSSFFont fontTitle = wb.createFont();
//        fontTitle.setFontHeightInPoints((short) 15);
//        fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        fontTitle.setFontName("Verdana");
//        styleTitleMain.setFont(fontTitle);
//        styleTitleMain.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//
//        HSSFFont fontTitle2 = wb.createFont();
//        fontTitle2.setFontHeightInPoints((short) 10);
//        fontTitle2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        fontTitle2.setFontName("Verdana");
//        styleTitle.setFont(fontTitle2);
//        styleTitle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//
//        dateCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        dateCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//        dateCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
//        dateCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
//        dateCellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
//
//        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
//        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
//        style.setBottomBorderColor(HSSFColor.BLACK.index);
//        //设置单元格宽度 ，改为宽度自适应
///*	    if(column!=null){
//            for(int i = 0;i<column.length;i++){
//	    		sheet.setColumnWidth(i, Short.valueOf(column[i]));
//	    	}
//	    }
//*/
//        int rangeLen = heads.length;
//        CellRangeAddress range = new CellRangeAddress(0, 0, 0, rangeLen);
//        sheet.addMergedRegion(range);
//        HSSFRow hssfRow = sheet.createRow(0);
//        hssfRow.setHeight((short) 500);
//        HSSFCell hssfCell = hssfRow.createCell(0);
//        hssfCell.setCellStyle(styleTitleMain);
//        hssfCell.setCellValue("Shipment Report");
//
//        CellRangeAddress range1 = new CellRangeAddress(1, 1, 0, rangeLen);
//        sheet.addMergedRegion(range1);
//        hssfRow = sheet.createRow(1);
//        hssfRow.setHeight((short) 300);
//        hssfCell = hssfRow.createCell(0);
//        hssfCell.setCellStyle(styleTitle);
//        hssfCell.setCellValue(lastUploadTime.substring(0, 16));
//
//        CellRangeAddress range2 = new CellRangeAddress(2, 2, 0, 4);
//        sheet.addMergedRegion(range2);
//        hssfRow = sheet.createRow(2);
//        hssfRow.setHeight((short) 300);
//        hssfCell = hssfRow.createCell(0);
//        hssfCell.setCellStyle(styleTitle);
//        hssfCell.setCellValue(vo.getReportDesc());
//
//
//        HSSFRow headRow = sheet.createRow(3);
//        headRow.setHeight((short) 400);
//        for (int i = 0; i < headsBefore.length; i++) {
//            if (i == 4) {
//                sheet.setColumnWidth(i, 4800);
//            }
//            HSSFCell cell = headRow.createCell(i);
//            cell.setCellValue(headsBefore[i]);
//            cell.setCellStyle(style);
//        }
//        int i1 = 4;
//        for (Numbers t : numbersExecl) {
//            HSSFRow row = sheet.createRow(i1);
//            row.setHeight((short) 320);
//            Class<? extends Object> c = t.getClass();
//            int j = 0;
//            for (String f : fieldsBefore) {
//                try {
//                    Method m = c.getDeclaredMethod("get" + f.substring(0, 1).toUpperCase() + f.substring(1));
//                    Object valueOb = m.invoke(t);
//                    HSSFCell cell = row.createCell(j);
//                    cell.setCellStyle(style);
//                    if (valueOb != null) {
//                        if (valueOb instanceof Integer) {
//                            cell.setCellValue((Integer) valueOb);
//                        } else if (valueOb instanceof Float) {
//                            cell.setCellValue((Float) valueOb);
//                        } else if (valueOb instanceof Long) {
//                            cell.setCellValue((Long) valueOb);
//                        } else if (valueOb instanceof Date) {
//                            cell.setCellValue((Date) valueOb);
//                        } else {
//                            cell.setCellValue(valueOb.toString());
//                        }
//                    } else {
//                        cell.setCellValue("");
//                    }
//
//                } catch (SecurityException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//                j++;
//            }
//
//            i1++;
//        }
//        CellRangeAddress range3 = new CellRangeAddress(i1, i1, 0, rangeLen);
//        sheet.addMergedRegion(range3);
//        hssfRow = sheet.createRow(i1);
//        hssfRow.setHeight((short) 900);
//        hssfCell = hssfRow.createCell(0);
//        hssfCell.setCellStyle(styleTitle);
//        hssfCell.setCellValue("Detail Report");
//        headRow = sheet.createRow(++i1);
//        for (int i = 0; i < heads.length; i++) {
//            HSSFCell cell = headRow.createCell(i);
//            cell.setCellValue(heads[i]);
//            cell.setCellStyle(style);
//        }
//        int i = ++i1;
//        for (T t : newReportResult1Execl) {
//            HSSFRow row = sheet.createRow(i);
//            row.setHeight((short) 320);
//            Class<? extends Object> c = t.getClass();
//            int j = 0;
//            for (String f : fields) {
//                try {
//                    Method m = c.getDeclaredMethod("get" + f.substring(0, 1).toUpperCase() + f.substring(1));
//                    Object valueOb = m.invoke(t);
//                    HSSFCell cell = row.createCell(j);
//                    cell.setCellStyle(style);
//                    if (valueOb != null) {
//                        if (valueOb instanceof Integer) {
//                            cell.setCellValue((Integer) valueOb);
//                        } else if (valueOb instanceof Float) {
//                            cell.setCellValue((Float) valueOb);
//                        } else if (valueOb instanceof Long) {
//                            cell.setCellValue((Long) valueOb);
//                        } else if (valueOb instanceof Date) {
//                            //cell.setCellValue((Date)valueOb);
//                            Date temp = (Date) valueOb;
//                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
//                            cell.setCellValue(temp);
//                            cell.setCellStyle(dateCellStyle);
//                        } else {
//                            cell.setCellValue(valueOb.toString());
//                        }
//                    } else {
//                        cell.setCellValue("");
//                    }
//
//                } catch (SecurityException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//                j++;
//            }
//
//            i++;
//        }
//        //宽度自适应
//        if (column != null) {
//            for (int j = 0; j < column.length; j++) {
//                sheet.autoSizeColumn(j);
//            }
//        }
//        return wb;
//    }


}
