(function(){

	var user_id ='1111';
	var user_fullname ='Anna Smith';
	var lon = -122.08;
	var lat = 37.78;
	
	/*main function(entrance)*/
	init();
	
	/*step 1: define init function*/
	function init(){
		//register event listeners
		//$() is a function we defined, simplified the getting process
		$('nearby-btn').addEventListener('click', loadNearbyItems);
		$('fav-btn').addEventListener('click', loadFavoriteItems);
		$('recommend-btn').addEventListener('click', loadRecommendedItems);
		
		var welcomeMsg = $('welcome-msg');
		welcomeMsg.innerHTML = 'Welcome, ' + user_fullname;
		//innerHTML: update the content
		
		/**step7: 根据自己的geo loc显示信息**/
		//if can get web browser location, get that; if not, use default IP location
		initGeoLocation();
			
	}
	/*step 5: create $ function, simulate selector*/
	function $(tag, options){
		if (!options){
			return document.getElementById(tag);
		}
		var element = document.createElement(tag);
		
		for (var option in options) {
			if (options.hasOwnProperty(option)) {
				element[option] = options[option];
			}
		}
		return element;
	}
	
	/*create ajax helper function*/
	/**
	 * @param method - GET|POST|PUT|DELETE
	 * @param url : API end point
	 * @param callback : successful callback
	 * @param errorHandler: failed callback
	 */
	function ajax(method, url, data, callback, errorHandler) {
		//create an ajax object
		var xhr = new XMLHttpRequest();
		// tell the method(get, post) and tell url address
		xhr.open(method, url, true);
		
		//onload check response error, and onerror check request error
		//check xhr response code: ok for 200; get response after xhr.send(); 
		xhr.onload = function(){
			// process response
			if (xhr.status === 200){
				//callback function is the parameter of ajax
				callback(xhr.responseText);
			} else if (xhr.status === 403){
				onSessionInvalid();
			} else {
				errorHandler();
			}
		};
		
		//xhr request error
		xhr.onerror = function() {
			console.error("The request couldn't be completed");
			errorHandler();
		};
		
		//check if we need to send data to server
		if (data === null){
			xhr.send();
		} else {
			xhr.setRequestHeader("Content-Type",
					"application/json;charset=utf-8");
			xhr.send(data);
				
		}	
	}
	
	/**step 7.initGeoLocation **/
	function initGeoLocation() {
		if (navigator.geolocation){
			// web browser's function
			//step 8
			navigator.geolocation.getCurrentPosition(onPositionUpdated,
					onLoadPositionFailed, {
						maximumAge : 60000
					});
			showLoadingMessage('Retrieving you location...')
		} else {
			//step 9
			onLoadPositionFailed();
		}
	}
	
	/** step 8 onPositionUpdated**/
	function onPositionUpdated(position) {
		lat = position.coords.latitude;
		lon = position.coords.longitude;
		
		//step 11
		loadNearbyItems();
	}
	/**step 9: onLoadPositionFailed **/
	function onLoadPositionFailed(){
		console.warn('navigator.geolocation is not available');
		//step 10
		getLocationFromIP();
	}
	
	/**step 10: getLocationFromIP **/
	function getLocationFromIP() {
		//get location from http://ipinfo.io/json, this is our default location
		var url = 'http://ipinfo.io/json'
		var req = null;
		// when response ==200, execute the function(res) below
		// function(res)中传入的是server返回的数据：callback(xhr.responseText);
		ajax('GET', url, req, function(res){
			var result = JSON.parse(res);
			if ('loc' in result){
				var loc = result.loc.split(',');
				lat = loc[0];
				lon = loc[1];
			} else {
				console.warn('Getting location by IP failed');
			}
			//step 11
			loadNearbyItems();
		});
	}
	//-------------------------------------------------------------
	//
	//-------------------------------------------------------------
	/**
	 * API #1 load the nearby items API end point: [GET]
	 * /search?user_id=1111&lat=37.388&lon=-122.08
	 */
	/**step 11: loadNearbyItems**/
	function loadNearbyItems() {
		console.log('loadNearbyItems');
		//step 12
		activeBtn('nearby-btn');
		
		// the request parameters， “.”表示当前地址：localhost:8080/Jupiter
		var url ='./search';
		var params = 'user_id=' + user_id + '&lat=' + lat + '&lon='+lon;
		//JSON to an object
		var req = JSON.stringify({});
		
		//display loading message
		showLoadingMessage('Loading nearby items...');
		
		//make AJAX call
		ajax('GET', url + '?' + params, req,
				//successful callback
				function(res){
					var items = JSON.parse(res);
					if (!items || items.length === 0) {
						//step 14
						showWarningMessage('No nearby item');
					} else {
						//step 16
						listItems(items);
					} 
				}, 
				//failed callback
				function(){
					//step 15
					showErrorMessage('cannot load nearby items');
				});
		
	}
	

	
	/**step 12
	 * a helper function that makes a navigation button active
	 * @param btnid - the id of the navigation button
	 */
	function activeBtn(btnId) {
		var btns = document.getElementsByClassName('main-nav-btn');
		
		//deactivate all navigation buttons
		for (var i = 0; i< btns.length; i++) {
			// ure regular expression: /\bactive\b/ to find the class name with 'active', repalce it with ''
			btns[i].className = btns[i].className.replace(/\bactive\b/, '');
		}
		
		//active the one that has id = btnId
		var btn = $(btnId);
		btn.className += ' active';
	}
	/** step 13: showLoadingMessage function **/
	function showLoadingMessage(msg) {
		var itemList = $('item-list');
		itemList.innerHTML = '<p class="notice"><i class= "fa fa-spinner fa-spin"></i>'
			+ msg + '</p>';
	}
	
	/**step 14: showWarningMessage function **/
	function showWarningMessage(msg){
		var itemList = $('item-list');
		itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i>'
			+ msg + '</p>';
	}
	
	/**step 15: showErrorMessage**/
	function showErrorMessage(msg) {
		var itemList = $('item-list');
		itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i>'
			+ msg + '</p>';
	}
	
	/** step 16: listItems function **/
	//
	function listItems(items) {
		//clear the current results
		var itemList = $('item-list');
		itemList.innerHTML ='';
		for (var i = 0; i < items.length; i++) {
			//step 17
			//add new content to itemList tag
			addItem(itemList, items[i]);
		}
	}
	
	/** step 17 addItem function 
	 * add item to the list
	 * @param itemList - the <url id = "item-list"> tag
	 * @param item- the item data(JSON object)
	 * **/
	function addItem (itemList, item) {
		var item_id = item.item_id;
		
		//create the <li> tag and specific the id and class
		var li = $('li', {
			id : 'item-' + item_id,
			className : 'item'
		});
		
		//set the data attribute
		li.dataset.item_id = item_id;
		li.dataset.favorite = item.favorite;
		
		//item image
		if (item.image_url) {
			li.appendChild($('img', {
				src: item.image_url
			}))
		} else {
			li.appendChild($('img', {
				src: 'http://s3-media3.fl.yelpcdn.com/bphoto/EmBj4qlyQaGd9Q4oXEhEeQ/ms.jpg'
			}));
		}
		
		//section
		var section = $('div', {});
		
		//title
		var title = $('a', {
			href: item.url,
			target: '_blank',
			className: 'item-name'
		});
		title.innerHTML = item.name;
		section.appendChild(title);
		
		//category
		var category = $('p', {
			className : 'item-category'
		});
		category.innerHTML = 'Category: ' + item.categories.join(', ');
		section.append(category);
		
		var stars = $('div', {
			className: 'stars'
		});
		
		for (var i = 0; i< item.rating; i++) {
			var star = $('i', {
				className : 'fa fa-star'
			});
			stars.appendChild(star);
		}
		
		if ((''+ item.rating).match(/\.5$/)){
			stars.appendChild($('i', {
				className : 'fa fa-star-half-o'
			}));
		}
		
		section.appendChild(stars);
		
		li.appendChild(section);
		
		//address
		var address = $('p', {
			className : 'item-address'
		});
		
		address.innerHTML = item.address.replace(/,/g, '<br/>').replace(/\"/g,
	            '');
		li.appendChild(address);
		
		//favorite link
		var favLink = $('p', {
			className : 'fav-link'
		});
		
		favLink.onclick = function(){
			changeFavoriteItem(item_id);
		};
		
		favLink.appendChild($('i', {
			id : 'fav-icon-' + item_id,
			className : item.favorite ? 'fa fa-heart' : 'fa fa-heart-o'
		}));
		
		li.appendChild(favLink);
		
		itemList.appendChild(li);
	}
})()