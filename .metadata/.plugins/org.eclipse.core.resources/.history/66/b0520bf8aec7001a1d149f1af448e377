/**
 * localhost:8080/Jupiter/
 */

(function(){

	var user_id ='1111';
	var user_fullname =' ';
	var lon = -122.08;
	var lat = 37.78;
	
	init();
	
	function init(){
		//register event listeners
		$('nearby-btn').addEventListener('click', loadNearbyItems);
		$('fav-btn').addEventListener('click', loadFavoriteItems);
		$('recommend-btn').addEventListener('click', loadRecommendedItems);
		$('recommend-btn').addEventListener('click', changeFavoriteItem);
		
		var welcomeMsg = $('welcome-msg');
		welcomeMsg.innerHTML = 'Welcome, ' + user_fullname;
		
		//if can get web browser location, get that; if not, use default IP location
		initGeoLocation();
			
	}
	
	function initGeoLocation() {
		if (navigator.geolocation){
			// web browser's function
			navigator.geolocation.getCurrentPosition(onPositionUpdated,
					onLoadPositionFailed, {
						maximumAge : 60000
					});
			showLoadingMessage('Retrieving you location...')
		} else {
			onLoadPositionFailed();
		}
	}
	
	function onPositionUpdated(position) {
		lat = position.coords.latitude;
		lon = position.coords.longitude;
		
		loadNearbyItems();
	}
	
	function onLoadPositionFailed(){
		console.warn('navigator.geolocation is not available');
		getLocationFromIP();
	}
	
	function getLocationFromIP() {
		//get location from http://ipinfo.io/json
		var url = 'http://ipinfo.io/json'
		var req = null;
		// when response ==200, execute the function(res) below
		//function(res)中传入的是server返回的数据：callback(xhr.responseText);
		ajax('GET', url, req, function(res){
			var result = JSON.parse(res);
			if ('loc' in result){
				var loc = result.loc.split(',');
				lat = loc[0];
				lon = loc[1];
			} else {
				console.warn('Getting location by IP failed');
			}

			loadNearbyItems();
		});
	}

	
//--------------------------------------------------------------------------
//			helper functions
//-----------------------------------------------------------------------------
	
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
		
		//xhr request: ok
		xhr.onload = function(){
			// process response
			if (xhr.status === 200){
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
	
	function activeBtn(btnId) {
		var btns = document.getElementsByClassName('main-nav-btn');
		
		//deactivate all navigation buttons
		for (var i = 0; i< btns.length; i++) {
			btns[i].className = btns[i].className.replace(/\bactive\b/, '');
		}
		
		//active the one that has id = btnId
		var btn = $(btnId);
		btn.className += ' active';
	}

	
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


//-----------------------------------------------------------
//   AJAX call server-side APIs
//-------------------------------------------------------------
	/**
	 * API #1 load the nearby items API end point: [GET]
	 * /search?user_id=1111&lat=37.388&lon=-122.08
	 */
	function loadNearbyItems() {
		console.log('loadNearbyItems');
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

	 /**
     * API #2 Load favorite (or visited) items API end point: [GET]
     * ./history?user_id=1111
     */
	function loadFavoriteItems() {
		console.log('loadFavoriteItems');
		activeBtn('fav-btn');
		
		var url = './history';
		var params = 'user_id=' + user_id;
		var req = JSON.stringify({});
		
		showLoadingMessage('Loading favorite items...');
		
		ajax('GET', url + '?' + params, req, 
				function(res){
					var items = JSON.parse(res);
					if (!items || items.length === 0) {
						showWarningMessage('No favorite item.');
					} else {
						listItems(items);
					}
				}, 
				function(){
					showErrorMessage('Cannot load favorite items.');
				});
	}
	
	
    /**
     * API #3 Load recommended items API end point: [GET]
     * ./recommendation?user_id=1111
     */
	function loadRecommendedItems(){
		console.log('loadFavoriteItems');
		activeBtn('recommend-btn');
		
		var url = './recommendation';
		var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lon;
		var req = JSON.stringify({});
		
		showLoadingMessage('Loading recommended items...');
		
		ajax('GET', url + '?' + params, req,
				function(res) {
					var items = JSON.parse(res);
	                if (!items || items.length === 0) {
	                    showWarningMessage('No recommended item. Make sure you have favorites.');
	                } else {
	                    listItems(items);
	                }
	            },
	            function() {
	                showErrorMessage('Cannot load recommended items.');
	            });
	}
	
	/**
     * API #4 Toggle favorite (or visited) items
     * 
     * @param item_id -
     *            The item business id
     * 
     * API end point: [POST]/[DELETE] ./history request json data: {
     * user_id: 1111, visited: [a_list_of_business_ids] }
     */
	function changeFavoriteItem(item_id){
		var li = $('item-' + item_id);
		var favIcon = $('fav-icon-' + item_id);
		var favorite = li.dataset.favorite !== 'true';
		
		var url = './history';
		var req = JSON.stringify({
			user_id: user_id,
			favorite: [item_id]
		});
		
		var method = favorite ? 'POST' : 'DELETE';
		
		ajax(method, url, req,
			function(res){
				var result = JSON.parse(res);
				if (result.result === 'SUCCESS') {
					li.dataset.favorite = favorite;
					favIcon.className = favorite ? 'fa fa-heart' : 'fa fa-heart-o';
				}
		});
	}
	
	
// ------------------------------------------------------
// Create item list
// ----------------------------------------------------------

    /**
     * List items
     * 
     * @param items -An array of item JSON objects
     */
	function listItems(items) {
		//clear the current results
		var itemList = $('item-list');
		itemList.innerHTML ='';
		for (var i = 0; i < items.length; i++) {
			addItem(itemList, items[i]);
		}
	}
	
	/**
     * add item to the list
	 * @param itemList - the <url id = "item-list"> tag
	 * @param item- the item data(JSON object)
	 */

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
		section.appendChild(category);
		
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