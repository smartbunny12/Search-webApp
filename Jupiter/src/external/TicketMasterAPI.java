package external;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class TicketMasterAPI {
	private static final String URL="https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = "";  //no keyword by default
	private static final String API_KEY = "GJyaun1rwjUeiAcs5MJPq1gsSQ1GSGCK"; // use my API_KEY
	// {"name: "XXX"
	// 		"id": "12345"
	// 		"url":"www.annaxue...."
	// 	"_embedded": {
	//      "venues":[
	//				"address":{
	// 					"line1": "666 Mesina ln,"
	// 					"line2": build 1
	// 				}
	// 				"city": {
	// 					"name": San Jose}
	//]}
	//"}
	// 666 Mesina ln 
	// https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				// if have more than one address, we only care about the 1st one
				for (int i = 0; i < venues.length(); i++) {
					JSONObject venue = venues.getJSONObject(i);
					
					StringBuilder sb = new StringBuilder();
					
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						
						if (!address.isNull("line1")) {
							sb.append(address.getString("line1"));
						}
						if (!address.isNull("line2")) {
							sb.append(" ");
							sb.append(address.getString("line2"));
						}
						if (!address.isNull("line3")) {
							sb.append(" ");
							sb.append(address.getString("line3"));
						}
					}
					
					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						
						if (!city.isNull("name")) {
							sb.append(" ");
							sb.append(city.getString("name"));
						}
					}
					if (!sb.toString().equals("")) {
						return sb.toString();
					}
					
				}
			}
		}
		return "";
	}
	
	//{"image":[{"url": "www....jpg"}, ....]}
	private String getImageUrl(JSONObject event) throws JSONException{
		if (!event.isNull("images")) {
			JSONArray images = event.getJSONArray("images");
			
			for (int i = 0; i< images.length(); i++) {
				JSONObject image = images.getJSONObject(i);
				
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return "";
	}
	
	// {"classifications": [{"segment": {"name": music}},...]}
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			
			for (int i = 0; i < classifications.length(); i++) {
				JSONObject classification = classifications.getJSONObject(i);
				
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					
					if (!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		return categories;
	}
	
	// convert JSONArray to a list of item objects
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		
		for (int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			
			ItemBuilder builder = new ItemBuilder();
			
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if (!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if (!event.isNull("rating")) {
				builder.setRating(event.getDouble("rating"));
			}
			if (!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			builder.setCategories(getCategories(event));
			builder.setAddress(getAddress(event));
			builder.setImageUrl(getImageUrl(event));
			
			itemList.add(builder.build());
		}
		return itemList;
	}
	
	public List<Item> search(double lat, double lon, String keyword) {
		JSONArray ret = new JSONArray();
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try{
			// if the keyword is not use English, need to encode it
			// urlencoder: 因为数据是通过http请求，发送给ticketMaster，所以要encode成http的url支持的格式
			keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String geoHash = GeoHash.encodeGeohash(lat,lon, 8);
		// need to build a url like this: https://app.ticketmaster.com/discovery/v2/events.json?apikey=**********&geoPoint=....&keyword=music&radius=50
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 50);
		
		try {
			HttpURLConnection connection =(HttpURLConnection) new URL(URL + "?" + query).openConnection(); // make url to connection =>返回urlconnection的类型
			// 只有http connection才支持getResponseCode； 所以上面要类型转换
			//connection作用：链接我们的程序和ticketMaster的service
			int responseCode = connection.getResponseCode(); //return 200 means success
			//上面openConnection的时候没有发送，只有getResponseCode的时候发送请求并返回code
			//just debug
			System.out.println("\nSending 'GET' request to URL: " + URL + "?" + query);
			System.out.println("Response Code: " + responseCode);
			
			if (responseCode != 200) {
				
			}
			//read ticketMaster response
			//in相当于一个handler，一个指针；String是java之间传输数据最常见的格式
			//BufferedReader: 一次读一行，不需要一次性全部读到buffer里面； InputStreamReader相当于获取一个读取设备
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputline;
			StringBuilder response = new StringBuilder();
			while ((inputline = in.readLine()) != null) {
				response.append(inputline);
			}
			in.close();
			connection.disconnect();
			
			JSONObject obj = new JSONObject(response.toString());
			if (obj.isNull("_embedded")) { //所有JSON数据都在_embedded中的，API中JSON信息知道的
				throw new Exception();
			}
			
			JSONObject embedded = obj.getJSONObject("_embedded");
			JSONArray events = embedded.getJSONArray("events");
			
			return getItemList(events);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ret is raw data; all we need is the 8 items in the Item class
		return new ArrayList<>();
	}
	
	//helper, use to debug: the result from search is correct
	private void queryAPI(double lat, double lon) {
		List<Item> events = search(lat, lon, null);
		try {
			for (Item event: events) {
				System.out.println(event.toJSONObject());
			} 
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		tmApi.queryAPI(29.682684, -95.295410);
	}

}
