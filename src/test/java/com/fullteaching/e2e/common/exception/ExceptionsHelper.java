package com.fullteaching.e2e.common.exception;

import java.util.HashMap;
import java.util.Map;

public class ExceptionsHelper {

	public static Map<String, Integer> stackFilesAndLines(StackTraceElement[] stack){
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		
		for (StackTraceElement ele : stack) {
			map.put(ele.getFileName(), ele.getLineNumber());
		}		
		return map;		
	}
	
	public static Integer getFileLineInfo(StackTraceElement[] stack, String file){
		Map<String,Integer> stackMap = stackFilesAndLines(stack);
		
		return stackMap.get(file);
	}
}

