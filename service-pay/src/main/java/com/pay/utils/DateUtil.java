package com.pay.utils;

import org.apache.commons.lang3.*;

import java.text.*;
import java.util.*;

/**
 * 日期时间工具类
 * @author tangdongfeng
 */
public class DateUtil {

	public static final String DAY = "DAY";
	public static final String WEEK = "WEEK";
	public static final String MONTH = "MONTH";

	/**
	 * 一天的秒数
	 */
	public static final int SECOND_OF_DAY = 60 * 60 * 24;
	/**
	 * 一天的毫秒数
	 */
	public static final int MILLISECOND_OF_DAY = 1000 * 60 * 60 * 24;

	/**
	 * 偏移类型：天
	 */
	public static final int OFFSET_DAY = 1;
	/**
	 * 偏移类型：周
	 */
	public static final int OFFSET_WEEK = 2;
	/**
	 * 偏移类型：月
	 */
	public static final int OFFSET_MONTH = 3;
	/**
	 * 偏移类型：年
	 */
	public static final int OFFSET_YEAR = 4;

	/**
	 * SimpleDateFormat不是线程安全的,所以需要放在ThreadLocal中来保证安全性
	 */
	private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			return simpleDateFormat;
		}
	};

	private static final ThreadLocal<SimpleDateFormat> TIMESTAMP_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			return simpleDateFormat;
		}
	};

	private static final ThreadLocal<SimpleDateFormat> TIME_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			return simpleDateFormat;
		}
	};

	/**
	 * 获取当前unix time
	 * @return
	 */
	public static Integer unixTime() {
		return Integer.valueOf((int)(System.currentTimeMillis() / 1000L));
	}

	/**
	 * 将日期格式字符串转化成日期，比如：2015-12-02
	 * @param date
	 * @return
	 */
	public static Date parseDate(String date) {
		if (StringUtils.isBlank(date)) {
			return null;
		}

		try {
			return DATE_FORMAT.get().parse(date);
		}catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 将时间戳格式字符串转化成日期，比如：2015-12-02 15:56:23
	 * @param timeStamp
	 * @return
	 */
	public static Date parseTimeStamp(String timeStamp) {
		if (StringUtils.isBlank(timeStamp)) {
			return null;
		}

		try {
			return TIMESTAMP_FORMAT.get().parse(timeStamp);
		}catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/*
	 * 将时间转换为时间戳
	 */
	public static String dateToStamp(String s) {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = simpleDateFormat.parse(s);
		}catch (ParseException e) {
			e.printStackTrace();
		}
		long ts = date.getTime();
		res = String.valueOf(ts);
		return res;
	}

	/**
	 * 将时间格式字符串转化成日期，比如：15:56:23
	 * @param time
	 * @return
	 */
	public static Date parseTime(String time) {
		if (StringUtils.isBlank(time)) {
			return null;
		}

		try {
			return TIME_FORMAT.get().parse(time);
		}catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 格式化日期
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		if (null == date) {
			throw new NullPointerException();
		}
		return DATE_FORMAT.get().format(date);
	}

	/**
	 * 格式化时间戳
	 * @param timeStamp
	 * @return
	 */
	public static String formatTimeStamp(Date timeStamp) {
		if (null == timeStamp) {
			throw new NullPointerException();
		}
		return TIMESTAMP_FORMAT.get().format(timeStamp);
	}

	/**
	 * 格式化时间
	 * @param time
	 * @return
	 */
	public static String formatTime(Date time) {
		if (null == time) {
			throw new NullPointerException();
		}
		return TIME_FORMAT.get().format(time);
	}

	/**
	 * @param date 格式：yyyy-MM-dd 如 2014-03-18
	 * @return 0点-1点#1395072000#1395075600 1点-2点#1395075600#1395079200 .........
	 * 22点-23点#1395151200#1395154800 23点-24点#1395154800#1395158400
	 */
	public static List<String> get24Time(String date) {
		if (StringUtils.isEmpty(date)) {
			return null;
		}

		String[] ymd = date.split("-");
		int splitCount = 3;
		if (ymd.length != splitCount) {
			return null;
		}

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(ymd[0]));
		c.set(Calendar.MONTH, Integer.parseInt(ymd[1]) - 1);
		c.set(Calendar.DATE, Integer.parseInt(ymd[2]));
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		int hoursOf24 = 24;
		List<Long> sec = new ArrayList<Long>(hoursOf24);
		for (int i = 0 ; i <= hoursOf24 ; i++) {
			c.set(Calendar.HOUR_OF_DAY, i);
			sec.add(c.getTimeInMillis() / 1000);
		}

		List<String> result = new ArrayList<String>();
		StringBuilder str = new StringBuilder();
		for (int i = 0 ; i < sec.size() - 1 ; i++) {
			str.append(i).append("点-").append(i + 1).append("点#").append(sec.get(i)).append("#").append(sec.get(i + 1));
			result.add(str.toString());
			str.setLength(0);
		}

		return result;
	}

	/**
	 * 获取当前日期
	 * @return
	 */
	public static String currentDate() {
		return DATE_FORMAT.get().format(Calendar.getInstance().getTime());
	}

	public static String beforeNumDate(int num) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - (num - 1));

		return DATE_FORMAT.get().format(c.getTime());
	}


	/**
	 * 返回两个日期之前的,包括两个日期的集合,如果begin和end在同一天,将只返回改天
	 * @param begin
	 * @param end
	 * @return
	 */
	public static List<Date> findBetweenDates(Date begin, Date end) {
		if (begin.after(end)) {
			Date temp = begin;
			begin = end;
			end = temp;
		}

		List<Date> dates = new ArrayList<Date>();
		if (formatDate(begin).equals(formatDate(end))) {
			dates.add(end);
			return dates;
		}

		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.setTime(begin);

		while (end.after(beginCalendar.getTime())) {
			dates.add(beginCalendar.getTime());

			beginCalendar.add(Calendar.DAY_OF_MONTH, 1);

			if (formatDate(end).equals(formatDate(beginCalendar.getTime()))) {
				break;
			}
		}
		dates.add(end);

		return dates;
	}

	public static List<Date> findBetweenDates(String begin, String end) {
		if (StringUtils.isEmpty(begin) || StringUtils.isEmpty(end)) {
			return null;
		}

		return findBetweenDates(parseDate(begin), parseDate(end));
	}

	public static List<Date> findBetweenDates(Long begin, Long end) {
		if (null == begin || null == end) {
			return null;
		}

		return findBetweenDates(new Date(begin), new Date(end));
	}

	public static List<String> getBetweenDates(Date begin, Date end) {
		if (begin.after(end)) {
			Date temp = begin;
			begin = end;
			end = temp;
		}

		List<String> dates = new ArrayList<String>();
		if (formatDate(begin).equals(formatDate(end))) {
			dates.add(formatDate(end));
			return dates;
		}

		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.setTime(begin);

		while (end.after(beginCalendar.getTime())) {
			dates.add(formatDate(beginCalendar.getTime()));
			beginCalendar.add(Calendar.DAY_OF_MONTH, 1);
			if (formatDate(end).equals(formatDate(beginCalendar.getTime()))) {
				dates.add(formatDate(end));
				break;
			}
		}

		return dates;
	}

	/**
	 * 00:01:00 - 00:11:00
	 * @param begin
	 * @param end
	 * @return 00:01:00
	 * 00:02:00
	 * 00:03:00
	 * 00:04:00
	 * 00:05:00
	 * 00:06:00
	 * 00:07:00
	 * 00:08:00
	 * 00:09:00
	 * 00:10:00
	 * 00:11:00
	 */
	public static List<String> getBetweenMinutes(String begin, String end) {
		List<String> result = new ArrayList<String>();
		if (StringUtils.isBlank(begin) || StringUtils.isBlank(end)) {
			return result;
		}

		if (begin.compareTo(end) > 0) {
			String temp = begin;
			begin = end;
			end = temp;
		}

		String timeOfStart = "00:00:00";
		if (begin.compareTo(timeOfStart) < 0) {
			begin = timeOfStart;
		}

		String timeOfEnd = "23:59:00";
		if (end.compareTo(timeOfEnd) > 0) {
			end = timeOfEnd;
		}

		result.add(begin);
		if (begin.compareTo(end) == 0) {
			return result;
		}

		String[] begins = begin.split(":");
		String[] ends = end.split(":");
		int beginHour = Integer.valueOf(begins[0]);
		int beginMinute = Integer.valueOf(begins[1]);
		int endHour = Integer.valueOf(ends[0]);
		int endMinute = Integer.valueOf(ends[1]);
		while (true) {
			beginMinute = beginMinute + 1;
			if (beginMinute == 60) {
				beginMinute = 0;
				beginHour = beginHour + 1;
			}

			if (beginHour == endHour && beginMinute == endMinute) {
				result.add(end);
				break;
			}

			result.add(getTime(beginHour, beginMinute, begins[2]));
		}

		return result;
	}

	private static String getTime(int hour, int minute, String second) {
		return String.valueOf(hour < 10 ?"0" + hour : hour) + ":" + (minute < 10 ?"0" + minute : minute) + ":" + second;
	}

	public static List<String> getBetweenDates(String begin, String end) {
		if (StringUtils.isEmpty(begin) || StringUtils.isEmpty(end)) {
			return null;
		}

		return getBetweenDates(parseDate(begin), parseDate(end));
	}

	public static List<String> getBetweenDates(Long begin, Long end) {
		if (null == begin || null == end) {
			return null;
		}

		return getBetweenDates(new Date(begin), new Date(end));
	}

	public static Integer getThisMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * 上周开始时间
	 * @return
	 */
	public static long getLastWeekStartTime() {
		Calendar lastWeek = Calendar.getInstance();
		lastWeek.set(Calendar.WEEK_OF_MONTH, lastWeek.get(Calendar.WEEK_OF_MONTH) - 1);
		if (lastWeek.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			Long time = lastWeek.getTime().getTime() - 1000 * 60 * 60 * 24;
			lastWeek.setTimeInMillis(time);
		}

		lastWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		lastWeek.set(Calendar.HOUR_OF_DAY, 0);
		lastWeek.set(Calendar.MINUTE, 0);
		lastWeek.set(Calendar.SECOND, 0);
		lastWeek.set(Calendar.MILLISECOND, 0);

		return lastWeek.getTime().getTime();
	}

	/**
	 * 获取上月开始时间
	 * @return
	 */
	public static long getLastMonthStartTime() {
		Calendar lastMonth = Calendar.getInstance();
		lastMonth.set(Calendar.MONTH, lastMonth.get(Calendar.MONTH) - 1);
		lastMonth.set(Calendar.DAY_OF_MONTH, 1);
		lastMonth.set(Calendar.HOUR_OF_DAY, 0);
		lastMonth.set(Calendar.MINUTE, 0);
		lastMonth.set(Calendar.SECOND, 0);
		lastMonth.set(Calendar.MILLISECOND, 0);

		return lastMonth.getTime().getTime();
	}

	public static Date getLastMonthBegin() {
		Calendar lastMonth = Calendar.getInstance();
		lastMonth.set(Calendar.MONTH, lastMonth.get(Calendar.MONTH) - 1);
		lastMonth.set(Calendar.DAY_OF_MONTH, 1);
		lastMonth.set(Calendar.HOUR_OF_DAY, 0);
		lastMonth.set(Calendar.MINUTE, 0);
		lastMonth.set(Calendar.SECOND, 0);
		lastMonth.set(Calendar.MILLISECOND, 0);

		return lastMonth.getTime();
	}

	/**
	 * 获取指定日期的开始时刻
	 * @param date
	 * @return
	 */
	public static Date getDayBegin(Date date) {
		Calendar day = Calendar.getInstance();
		day.setTime(date);

		day.set(Calendar.HOUR_OF_DAY, 0);
		day.set(Calendar.MINUTE, 0);
		day.set(Calendar.SECOND, 0);
		day.set(Calendar.MILLISECOND, 0);

		return day.getTime();
	}

	/**
	 * 获取指定日期的下一天
	 * @param date
	 * @return
	 */
	public static Date getNextBegin(Date date) {
		return new Date(date.getTime() + (1000 * 60 * 60 * 24));
	}

	/**
	 * 获取指定日期的开始时刻
	 * @param date
	 * @return
	 */
	public static Date getDayBegin(String date) {
		return getDayBegin(parseDate(date));
	}

	/**
	 * 获取指定日期的开始时刻
	 * @param date
	 * @return
	 */
	public static Date getDayBegin(Long date) {
		return getDayBegin(new Date(date));
	}


	/**
	 * 获取指定date前后num天
	 * @param date 指定日期
	 * @param num 前后几天
	 * @return YYYY-MM-dd
	 */
	public static String getBeforeAnyDay(Date date, int num) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, num);
		return formatDate(cal.getTime());
	}

	/**
	 * 获取今天的开始时刻
	 * @return
	 */
	public static Date getDayBegin() {
		return getDayBegin(new Date());
	}

	/**
	 * 获取指定日期的结束时刻
	 * @param date
	 * @return
	 */
	public static Date getDayEnd(Date date) {
		Calendar day = Calendar.getInstance();
		day.setTime(date);

		day.set(Calendar.HOUR_OF_DAY, 23);
		day.set(Calendar.MINUTE, 59);
		day.set(Calendar.SECOND, 59);
		day.set(Calendar.MILLISECOND, 999);

		return day.getTime();
	}

	/**
	 * 获取指定日期的结束时刻
	 * @param date
	 * @return
	 */
	public static Date getDayEnd(String date) {
		return getDayEnd(parseDate(date));
	}

	/**
	 * 获取指定日期的结束时刻
	 * @param date
	 * @return
	 */
	public static Date getDayEnd(Long date) {
		return getDayEnd(new Date(date));
	}

	/**
	 * 获取今天的结束时刻
	 * @return
	 */
	public static Date getDayEnd() {
		return getDayEnd(new Date());
	}

	/**
	 * 获取昨天开始毫秒数
	 * @return
	 */
	public static long getYesterdayStartTime() {
		Calendar yesterday = Calendar.getInstance();
		yesterday.set(Calendar.DATE, yesterday.get(Calendar.DATE) - 1);
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);
		yesterday.set(Calendar.MILLISECOND, 0);

		return yesterday.getTime().getTime();
	}

	/**
	 * 获取上一天的当前时间
	 * @return
	 */
	public static Date getDay(String date) {
		return getDayBegin(date + "00:00:00");
	}

	/**
	 * 获取指定日期所在周的周一开始时刻
	 * @param date
	 * @return
	 */
	public static Date getWeekBegin(Date date) {
		Calendar week = Calendar.getInstance();
		week.setTime(date);

		//周日是一周的开始,所以要将周日的时间设置成周六的,然后再计算周一
		if (week.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			Long time = week.getTime().getTime() - 1000 * 60 * 60 * 24;
			week.setTimeInMillis(time);
		}

		week.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		week.set(Calendar.HOUR_OF_DAY, 0);
		week.set(Calendar.MINUTE, 0);
		week.set(Calendar.SECOND, 0);
		week.set(Calendar.MILLISECOND, 0);

		return week.getTime();
	}

	/**
	 * 获取指定日期所在周的周一开始时刻
	 * @param date
	 * @return
	 */
	public static Date getWeekBegin(String date) {
		return getWeekBegin(parseDate(date));
	}

	/**
	 * 获取指定日期所在周的周一开始时刻
	 * @param date
	 * @return
	 */
	public static Date getWeekBegin(Long date) {
		return getWeekBegin(new Date(date));
	}

	/**
	 * 获取今天所在周的周一开始时刻
	 * @return
	 */
	public static Date getWeekBegin() {
		return getWeekBegin(new Date());
	}

	/**
	 * 获取指定日期所在周的周日结束时刻
	 * @param date
	 * @return
	 */
	public static Date getWeekEnd(Date date) {
		Calendar week = Calendar.getInstance();
		week.setTime(date);

		if (week.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return getDayEnd(date);
		}

		week.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		week.set(Calendar.HOUR_OF_DAY, 23);
		week.set(Calendar.MINUTE, 59);
		week.set(Calendar.SECOND, 59);
		week.set(Calendar.MILLISECOND, 999);
		week.setTimeInMillis(week.getTimeInMillis() + 1000 * 60 * 60 * 24);

		return week.getTime();
	}

	/**
	 * 获取指定日期所在周的周日结束时刻
	 * @param date
	 * @return
	 */
	public static Date getWeekEnd(String date) {
		return getWeekEnd(parseDate(date));
	}

	/**
	 * 获取指定日期所在周的周日结束时刻
	 * @param date
	 * @return
	 */
	public static Date getWeekEnd(Long date) {
		return getWeekEnd(new Date(date));
	}

	/**
	 * 获取今天所在周的周日结束时刻
	 * @return
	 */
	public static Date getWeekEnd() {
		return getWeekEnd(new Date());
	}

	/**
	 * 获取指定日期所在月的开始时刻
	 * @param date
	 * @return
	 */
	public static Date getMonthBegin(Date date) {
		Calendar month = Calendar.getInstance();
		month.setTime(date);

		month.set(Calendar.DAY_OF_MONTH, 1);
		month.set(Calendar.HOUR_OF_DAY, 0);
		month.set(Calendar.MINUTE, 0);
		month.set(Calendar.SECOND, 0);
		month.set(Calendar.MILLISECOND, 0);

		return month.getTime();
	}

	/**
	 * 获取指定日期所在月的开始时刻
	 * @param date
	 * @return
	 */
	public static Date getMonthBegin(String date) {
		return getMonthBegin(parseDate(date));
	}

	/**
	 * 获取指定日期所在月的开始时刻
	 * @param date
	 * @return
	 */
	public static Date getMonthBegin(Long date) {
		return getMonthBegin(new Date(date));
	}

	/**
	 * 获取今天所在月的开始时刻
	 * @return
	 */
	public static Date getMonthBegin() {
		return getMonthBegin(new Date());
	}

	/**
	 * 获取指定日期所在月的最后时刻
	 * @param date
	 * @return
	 */
	public static Date getMonthEnd(Date date) {
		Calendar month = Calendar.getInstance();
		month.setTime(date);
		month.add(Calendar.MONTH, 1);
		month.set(Calendar.DAY_OF_MONTH, 1);
		month.set(Calendar.HOUR_OF_DAY, 0);
		month.set(Calendar.MINUTE, 0);
		month.set(Calendar.SECOND, 0);
		month.set(Calendar.MILLISECOND, 0);
		month.setTimeInMillis(month.getTimeInMillis() - 1);

		return month.getTime();
	}

	/**
	 * 获取指定日期所在月的最后时刻
	 * @param date
	 * @return
	 */
	public static Date getMonthEnd(String date) {
		return getMonthEnd(parseDate(date));
	}

	/**
	 * 获取指定日期所在月的最后时刻
	 * @param date
	 * @return
	 */
	public static Date getMonthEnd(Long date) {
		return getMonthEnd(new Date(date));
	}

	/**
	 * 获取今天所在月的最后时刻
	 * @return
	 */
	public static Date getMonthEnd() {
		return getMonthEnd(new Date());
	}

	/**
	 * 获取相对于今天的偏移一定量的时间后的日期
	 * @param amount
	 * @param type
	 * @return
	 */
	public static Date getOffsetDate(int amount, int type) {
		Calendar today = Calendar.getInstance();

		if (OFFSET_DAY == type) {
			today.add(Calendar.DATE, amount);
		}else if (OFFSET_WEEK == type) {
			today.add(Calendar.WEEK_OF_MONTH, amount);
		}else if (OFFSET_MONTH == type) {
			today.add(Calendar.MONTH, amount);
		}else if (OFFSET_YEAR == type) {
			today.add(Calendar.YEAR, amount);
		}

		return today.getTime();
	}

	public static List<String> getBetweenDates(Long begin, Long end, String type) {
		if (null == begin || null == end) {
			return null;
		}

		return getBetweenDates(new Date(begin), new Date(end), type);
	}


	public static List<String> getBetweenDates(Date begin, Date end, String type) {
		if (begin.after(end)) {
			return null;
		}

		List<String> dates = new ArrayList<String>();
		if (formatDate(begin).equals(formatDate(end))) {
			dates.add(formatDate(end));
			return dates;
		}

		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.setTime(begin);
		Integer increment = null;
		if (DateUtil.DAY.equals(type)) {
			increment = Calendar.DAY_OF_MONTH;
		}else if (DateUtil.WEEK.equals(type)) {
			increment = Calendar.WEEK_OF_YEAR;
		}else {
			increment = Calendar.MONTH;
		}

		while (end.after(beginCalendar.getTime())) {
			dates.add(formatDate(beginCalendar.getTime()));
			beginCalendar.add(increment, 1);
			if (formatDate(end).equals(formatDate(beginCalendar.getTime()))) {
				break;
			}
		}
		return dates;
	}

	public static List<String> getBetweenDates(String begin, String end, String type) {
		Date b = parseDate(begin);
		Date e = parseDate(end);
		return getBetweenDates(b, e, type);
	}


	/**
	 * 根据类型获取时间段
	 * @param start unix 时间戳  例如：1450931510
	 * @param end unix 时间戳  列如：1450931510
	 * @param type
	 * @return
	 */
	public static List<String> getBetweenUnixTimes(Integer start, Integer end, Integer type) {
		if (start == null || end == null) {
			return null;
		}
		if (start > end) {
			return null;
		}

		List<String> dates = new ArrayList<String>();
		if (start.equals(end)) {
			dates.add(start + "_" + end);
			return dates;
		}

		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.setTime(new Date(start * 1000L));
		int time = (int)(beginCalendar.getTimeInMillis() / 1000L);
		while (end > time) {
			int before = (int)(beginCalendar.getTimeInMillis() / 1000L);
			beginCalendar.add(type, 1);
			time = (int)(beginCalendar.getTimeInMillis() / 1000L);
			if (end <= time) {
				dates.add(before + "_" + end);
				break;
			}
			dates.add(before + "_" + time);
		}
		return dates;
	}

	public static List<String> getBetweenDatesUnixTimes(String start, String end) {
		if (start == null || end == null) {
			return null;
		}

		List<String> dates = new ArrayList<String>();
		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.setTime(DateUtil.getDayBegin(start));
		while (end.compareTo(start) >= 0) {
			beginCalendar.add(Calendar.DAY_OF_MONTH, 1);
			dates.add(getDayBegin(start).getTime() / 1000 + "_" + getDayEnd(start).getTime() / 1000);
			start = formatDate(beginCalendar.getTime());
		}
		return dates;
	}

	public static List<String> getBetweenWeeksUnixTimes(String start, String end) {
		if (start == null || end == null) {
			return null;
		}

		List<String> dates = new ArrayList<String>();
		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.setTime(DateUtil.getDayBegin(start));
		while (end.compareTo(start) >= 0) {
			beginCalendar.add(Calendar.WEEK_OF_YEAR, 1);
			if (getWeekEnd(start).after(new Date())) {
				dates.add(getWeekBegin(start).getTime() / 1000 + "_" + getOffsetDate(-1, OFFSET_DAY).getTime() / 1000);
			}else {
				dates.add(getWeekBegin(start).getTime() / 1000 + "_" + getWeekEnd(start).getTime() / 1000);
			}
			start = formatDate(beginCalendar.getTime());
		}
		return dates;
	}

	public static List<String> getBetweenMonthsUnixTimes(String start, String end) {
		if (start == null || end == null) {
			return null;
		}

		List<String> dates = new ArrayList<String>();
		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.setTime(DateUtil.getDayBegin(start));
		while (end.compareTo(start) >= 0) {
			beginCalendar.add(Calendar.MONTH, 1);
			if (getMonthEnd(start).after(new Date())) {
				dates.add(getMonthBegin(start).getTime() / 1000 + "_" + getOffsetDate(-1, OFFSET_DAY).getTime() / 1000);
			}else {
				dates.add(getMonthBegin(start).getTime() / 1000 + "_" + getMonthEnd(start).getTime() / 1000);
			}
			start = formatDate(beginCalendar.getTime());
		}
		return dates;
	}

	public static Integer getYearBeginUnixTime(Integer year) {
		return (int)(parseTimeStamp(year + "-01-01 00:00:00").getTime() / 1000L);
	}

	public static Integer getYearEndUnixTime(Integer year) {
		return (int)(parseTimeStamp(year + "-12-31 23:59:59").getTime() / 1000L);
	}

	/**
	 * 通过指定增加的年月日获取新的时间
	 * @param date
	 * @param years
	 * @param months
	 * @param days
	 * @return
	 */
	public static Date getDate(Date date, Integer years, Integer months, Integer days) {
		Calendar oldDate = Calendar.getInstance();
		oldDate.setTime(date);
		if (years != null) {
			oldDate.add(Calendar.YEAR, years);
		}
		if (months != null) {
			oldDate.add(Calendar.MONTH, months);
		}
		if (days != null) {
			oldDate.add(Calendar.DAY_OF_YEAR, days);
		}
		return oldDate.getTime();
	}

	/**
	 * 获取微秒级的时间戳：20180702143229001
	 * @param currentTimeMillis
	 * @return
	 */
	public static String getCurrentTimeStamp(Long currentTimeMillis) {
		return new SimpleDateFormat("yyyyMMddHHmmssFFF").format(currentTimeMillis);
	}

	/**
	 * 获取两个日期之间相差得天数
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int differentDaysByMillisecond(Date date1, Date date2) {
		int days = (int)((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
		return days;
	}

	/**
	 * 获取上一个月
	 * @return
	 */
	public static String getLastMonth(Integer num) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -num);
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM");
		String lastMonth = dft.format(cal.getTime());
		return lastMonth + "-01";
	}

	public static String getPreMonthLastDay(Integer integer) {
		return formatDate(getMonthEnd(getLastMonth(integer)));
	}

	/**
	 * 获取今天的开始时间
	 * @param date
	 * @return
	 */
	public static String getDayStartString(String date) {
		return date + " 00:00:00";
	}

	/**
	 * 获取今天的结束时间
	 * @param date
	 * @return
	 */
	public static String getDayEndString(String date) {
		return date + " 23:59:59";
	}

	public static String preDateStart(String date) {
		Date data = DateUtil.parseDate(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date today = calendar.getTime();
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastMonth = dft.format(calendar.getTime());
		return lastMonth;
	}

	public static String preDateEnd(String date) {
		Date data = DateUtil.parseDate(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date today = calendar.getTime();
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		String lastMonth = dft.format(calendar.getTime());
		return lastMonth + " 23:59:59";
	}

	public static String preDate(String date) {
		Date data = DateUtil.parseDate(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date today = calendar.getTime();
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		String lastMonth = dft.format(calendar.getTime());
		return lastMonth;
	}

	public static void main(String args[]) {
		System.out.println(preDate("2018-11-12"));
		;
	}

}