package com.zb.data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.zb.kits.EncryDigestUtil;
import com.zb.kits.HttpUtilManager;
import com.zb.kits.MapSort;

public class TradeInfoData {
	
	

		private static Logger log = Logger.getLogger(TradeInfoData.class);

		// 正式
		public final String ACCESS_KEY = "";
		public final String SECRET_KEY = "";
		public final String URL_PREFIX = "https://trade.zb.com/api/";
		public static String API_DOMAIN = "http://api.zb.com";
		
		
		public final String PAY_PASS = "xxxx";
		
		
		
/*		
 * 		
		GET-http://api.zb.com/data/v1/markets
		}*/
			
		
		public static void main(String[] args) {
			testMarketInfo();
			testTicker();
			testDepth();
			testTrades();
			testKline();
			
		}
		
		
		/**
		 * 获取已开启的市场信息，包括价格，数量	
		 */
		public static void testMarketInfo() {
			try {
				//String currency = "btc_qc";
				// 请求地址
				String url = API_DOMAIN + "/data/v1/markets";
				log.info("testMarketInfo url: " + url);
				// 请求测试
				String callback = get(url, "UTF-8");
				log.info( "testMarketInfo 结果: " + callback);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		
		/**
		 * 测试获取行情
		 */
		public static void testTicker() {
			try {
				String currency = "btc_qc";
				// 请求地址
				String url = API_DOMAIN + "/data/v1/ticker?market=" + currency;
				log.info(currency + "-testTicker url: " + url);
				// 请求测试
				String callback = get(url, "UTF-8");
				log.info(currency + "-testTicker 结果: " + callback);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		/**
		 * 测试获取深度
		 * market=币种类
		 * size=显示的数量
		 */
		public static void testDepth() {
			try {
				String currency = "btc_qc";
				String merge = "0.1";
				// 请求地址
				//String url = API_DOMAIN + "/data/v1/depth?market=" + currency;
				// String url = API_DOMAIN+"/data/v1/depth?currency=" + currency +
				//"&size=3&merge=" + merge;
				// String url = API_DOMAIN+"/data/v1/depth?currency=" + currency +
				// "&size=3";
				 String url = API_DOMAIN+"/data/v1/depth?market=" + currency +
				 "&size=10";
				log.info(currency + "-testDepth url: " + url);
				// 请求测试
				String callback = get(url, "UTF-8");
				log.info(currency + "-testDepth 结果: " + callback);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		/**
		 * 测试获取最近交易历史记录
		 */
		public static void testTrades() {
			try {
				String currency = "btc_qc";
				// 请求地址
				String url = API_DOMAIN + "/data/v1/trades?market=" + currency;
				log.info(currency + "-testTrades url: " + url);
				// 请求测试
				String callback = get(url, "UTF-8");
				log.info(currency + "-testTrades 结果: " + callback);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		/**
		 * 测试获取K线数据
		 */
		public static void testKline() {
			try {
				String currency = "btc_qc";
				// 请求地址
				String url = API_DOMAIN + "/data/v1/kline?market=" + currency + "&times=1min";
				log.info(currency + "-testKline url: " + url);
				// 请求测试
				String callback = get(url, "UTF-8");
				JSONObject json = JSONObject.parseObject(callback);
				log.info(currency + "-testKline 结果: " + json.toJSONString());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
				
		
		/**
		 * 
		 * @param urlAll
		 *            :请求接口
		 * @param charset
		 *            :字符编码
		 * @return 返回json结果
		 */
		public static String get(String urlAll, String charset) {
			BufferedReader reader = null;
			String result = null;
			StringBuffer sbf = new StringBuffer();
			String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";// 模拟浏览器
			try {
				URL url = new URL(urlAll);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setReadTimeout(30000);
				connection.setConnectTimeout(30000);
				connection.setRequestProperty("User-agent", userAgent);
				connection.connect();
				InputStream is = connection.getInputStream();
				reader = new BufferedReader(new InputStreamReader(is, charset));
				String strRead = null;
				while ((strRead = reader.readLine()) != null) {
					sbf.append(strRead);
					sbf.append("\r\n");
				}
				reader.close();
				result = sbf.toString();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		
		
		
		/**
		 * 获取json内容(统一加密)
		 * 
		 * @param params
		 * @return
		 */
		private String getJsonPost(Map<String, String> params) {
			params.put("accesskey", ACCESS_KEY);// 这个需要加入签名,放前面
			String digest = EncryDigestUtil.digest(SECRET_KEY);

			String sign = EncryDigestUtil.hmacSign(MapSort.toStringMap(params), digest);// 参数执行加密
			String method = params.get("method");

			// 加入验证
			params.put("sign", sign);
			params.put("reqTime", System.currentTimeMillis() + "");
			String url = "请求地址:" + URL_PREFIX + method + " 参数:" + params;
			System.out.println(url);
			String json = "";
			try {
				json = HttpUtilManager.getInstance().requestHttpPost(URL_PREFIX, method, params);
			} catch (HttpException  e) {
				log.error("获取交易json异常", e);
			}catch ( IOException e) {
				log.error("获取交易json异常", e);
			}
			return json;
		}
		

}
