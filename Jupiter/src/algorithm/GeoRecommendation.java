package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon) {
		
		List<Item> recommendItems = new ArrayList<>();
		//Item it = new Item.ItemBuilder().setAddress("abcdsfsa").setName("1234").build();
		//recommendItems.add(it);
		//return recommendItems;
		
		
		DBConnection conn = DBConnectionFactory.getConnection();
		
		//1. get all favorite items
		Set<String> favoriteItemIds = conn.getFavoriteItemIds(userId);
		
		//2. get all categories of favorite items, sort by count
		Map<String, Integer> allCategories = new HashMap<>();
		for (String itemId : favoriteItemIds) {
			Set<String> categories = conn.getCategories(itemId);
			for (String category : categories) {
				if (allCategories.containsKey(category)) {
					allCategories.put(category, allCategories.get(category) + 1);
				} else {
					allCategories.put(category, 1);
				}
			}
		}
		
		// sort counter map: list<Entry<>>
		List<Entry<String, Integer>> categoryList = 
				new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList, new Comparator<Entry<String, Integer>>(){
			@Override
			public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
				return Integer.compare(e2.getValue(), e1.getValue());
			}
		});
		
		//3. do search based on category, filter out favorited events, sort by distance
		Set<Item> visitedItems = new HashSet<>();
		for (Entry<String, Integer> category : categoryList) {
			List<Item> items = conn.searchItems(lat, lon, category.getKey());
			List<Item> filteredItems = new ArrayList<>();
			
			for (Item item : items) {
				if (!favoriteItemIds.contains(item.getItemId())
						&& !visitedItems.contains(item)) {
					filteredItems.add(item);
				}
			}
			//Collections.sort(filteredItems, 
			//		(x, y) -> Double.compare(x.getDistance(), y.getDistance()));
			
			visitedItems.addAll(items);
			recommendItems.addAll(filteredItems);
		}
	
		return recommendItems;
		
	}
}
