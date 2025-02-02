package com.iflytek.speech.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Json结果解析类
 */
public class JsonParser {

	public static String parseIatResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				// 转写结果词，默认使用第一个结果
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				JSONObject obj = items.getJSONObject(0);
				if(obj.getString("w").contains("nomatch"))
				{
					return null;
				}
				ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ret.toString();
	}

	public static String parseGrammarResult(String json, String engType) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			// 云端和本地结果分情况解析
			if ("cloud".equals(engType)) {
				for (int i = 0; i < words.length(); i++) {
					JSONArray items = words.getJSONObject(i).getJSONArray("cw");
					for(int j = 0; j < items.length(); j++)
					{
						JSONObject obj = items.getJSONObject(j);
						if(obj.getString("w").contains("nomatch"))
						{
							ret.append("亲没有听明白，您再说 一次！");
							return ret.toString();
						}
						ret.append("【结果】" + obj.getString("w"));
						ret.append("【置信度】" + obj.getInt("sc"));
						ret.append("\n");
					}
				}
			} else if ("local".equals(engType)) {
				ret.append("【结果】");
				for (int i = 0; i < words.length(); i++) {
					JSONObject wsItem = words.getJSONObject(i);
					JSONArray items = wsItem.getJSONArray("cw");
					if ("<contact>".equals(wsItem.getString("slot"))) {
						// 可能会有多个联系人供选择，用中括号括起来，这些候选项具有相同的置信度
						ret.append("【");
						for(int j = 0; j < items.length(); j++)
						{
							JSONObject obj = items.getJSONObject(j);
							if(obj.getString("w").contains("nomatch"))
							{
								ret.append("亲没有听明白，您再说 一次！");
								return ret.toString();
							}
							ret.append(obj.getString("w")).append("|");
						}
						ret.setCharAt(ret.length() - 1, '】');
					} else {
						//本地多候选按照置信度高低排序，一般选取第一个结果即可
						JSONObject obj = items.getJSONObject(0);
						if(obj.getString("w").contains("nomatch"))
						{
							ret.append("没有匹配结果.");
							return ret.toString();
						}
						ret.append(obj.getString("w"));
					}
				}
				ret.append("【置信度】" + joResult.getInt("sc"));
				ret.append("\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
			ret.append("没有匹配结果.");
		}
		return ret.toString();
	}

	public static String parseGrammarResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				for(int j = 0; j < items.length(); j++)
				{
					JSONObject obj = items.getJSONObject(j);
					if(obj.getString("w").contains("nomatch"))
					{
						ret.append("没有匹配结果.");
						return ret.toString();
					}
					ret.append("【结果】" + obj.getString("w"));
					ret.append("【置信度】" + obj.getInt("sc"));
					ret.append("\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret.append("没有匹配结果.");
		}
		return ret.toString();
	}

	public static List<Map<String,Object>>  parseGrammarResultForMap(String json) {
		List<Map<String,Object>> ret = new ArrayList<Map<String,Object>>();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				for(int j = 0; j < items.length(); j++)
				{
					JSONObject obj = items.getJSONObject(j);
					if(obj.getString("w").contains("nomatch"))
					{

						return null;
					}
					Map<String,Object> map=new HashMap<String,Object>();
					map.put("result", obj.getString("w"));
					map.put("reliability", obj.getInt("sc"));
					ret.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}
}
