package group.cs169.cookingbuddy;

public class Constants {
	
	//URLS
	public static final String BASE_URL = "http://blooming-cove-7340.herokuapp.com";
	public static final String LOGIN_USER_URL = "/users/login";
	public static final String ADD_USER_URL = "/users/signup";
	public static final String ADD_INGREDIENT_URL = "/ingredients/add";
	public static final String REMOVE_INGREDIENT_URL = "/ingredients/remove";
	public static final String SEARCH_URL = "/recipes/search";
	public static final String MAKE_RECIPE_URL = "/recipes/make";
	public static final String INGREDIENT_LIST_URL = "/ingredients/get";
	public static final String HISTORY_URL = "/history";
	
	//Standard codes
	public static final String JSON_USERNAME = "user";
	public static final String JSON_PASSWORD = "password";
	public static final String JSON_STANDARD_RESPONSE = "errCode";
	public static final String SUCCESS = "SUCCESS";
	public static final int DEFAULT_RATING = 3;
	public static final int DEFAULT_PICTURE = R.drawable.stockimage;
	
	//Ingredient List
	public static final String INGREDIENT_KEY = "items"; 
	public static final String INGREDIENT_NAME = "ingredient_name";
	public static final String EXPIRATION = "expiration_date";
	public static final String QUANTITY = "quantity";
	public static final String UNIT = "unit";
	public static final String NEW_QUANTITY = "new_quantity";
	
	//Search
	public static final String SEARCH_KEYWORD = "q";

	//History
	public static final String COMPLETED_USER_RECIPES = "userHistory";
	
	public Constants() {
		
	}

}
