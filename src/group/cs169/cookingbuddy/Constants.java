package group.cs169.cookingbuddy;

public class Constants {
	
	//URLS
	public static final String BASE_URL = "http://blooming-cove-7340.herokuapp.com";
	public static final String LOGIN_USER_URL = "/users/login";
	public static final String ADD_USER_URL = "/users/signup";
	public static final String ADD_INGREDIENT_URL = "/ingredients/add";
	public static final String REMOVE_INGREDIENT_URL = "/ingredients/remove";
	public static final String UPDATE_INGREDIENT_URL = "/ingredients/update";
	public static final String SEARCH_URL = "/recipes/search";
	public static final String MAKE_RECIPE_URL = "/recipes/make";
	public static final String INGREDIENT_LIST_URL = "/ingredients/get";
	public static final String HISTORY_URL = "/recipes/history";
	public static final String HISTORY_URL_TEMP = "/recipes/historyTemp";
	public static final String CHANGE_PASSWORD = "/users/changePassword";
	public static final String REMOVE_ALL = "/ingredients/removeAll";
	public static final String RECIPE_DATA = "/recipes/getRecipeData";
	
	//Standard codes
	public static final String JSON_USERNAME = "user";
	public static final String JSON_PASSWORD = "password";
	public static final String JSON_NEW_PASSWORD = "newPassword";
	public static final String JSON_STANDARD_RESPONSE = "errCode";
	public static final String SUCCESS = "SUCCESS";
	public static final int DEFAULT_RATING = 3;
	public static final int DEFAULT_PICTURE = R.drawable.stockimage;
	public static final String ERROR_CODE = "-100";
	public static final String SHARED_PREFS_USERNAME = "blah";
	
	//Ingredient List
	public static final String INGREDIENT_KEY = "items"; 
	public static final String INGREDIENT_NAME = "ingredient_name";
	public static final String EXPIRATION = "expiration_date";
	public static final String QUANTITY = "quantity";
	public static final String UNIT = "unit";
	public static final String NEW_QUANTITY = "new_quantity";
	
	//Search
	public static final String SEARCH_KEYWORD = "q";
	public static final String RECIPE_ID = "recipe_id";

	//History
	public static final String COMPLETED_USER_RECIPES = "history";
	public static final String RECIPE_NAME = "recipe_name";
	public static final String CURRENT_DATE = "current_date";
	public static final String RATING = "rating";
	
	//Activity Names
	public static final String SIGNUP_ACTIVITY = "SignUpActivity";
	public static final String MAIN_ACTIVITY = "MainActivity";
	public static final String SEARCH_ACTIVITY = "SearchResultActivity";
	public static final String RECIPE_CLASS = "recipe";
	public static final String INGREDIENT_ACTIVITY = "IngredientActivity";
	
	public Constants() {
		
	}

}
