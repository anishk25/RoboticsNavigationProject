package com.robot.processor.map;

public class RoomMappings {
	public static final int INTERSECTION_CODE = 9000;
	public static final int CORNER_CODE = 8000;
	public static final String BLANKENSHIP_HALLWAY_NAME = "Blankenship";
	public static final String RENNER_HALLWAY_NAME = "Renner";
	public static final String SUNLAB_HALLWAY_NAME = "Sunlab";
	
	public static final int [] BLANKENSHIP_HALL_DIST = {6,13,9,13,8,14,10,10,6,17,10,12,10,5};
	public static final int [] BLANKENSHIP_HALL_ROOMS = {CORNER_CODE,1409, 1411, 14151,14152,14171,14172,1421,1423,14271,14272,14311,14312,1433,CORNER_CODE};
	
	public static final int [] RENNER_HALL_DIST = {8,11,9,12,9, 13,11,9,11,13,9,13,9,9,9,11,7,8,19,8,31};
	public static final int [] RENNER_HALL_ROOMS = {CORNER_CODE,1315,1317,13211,13212,13231,13232,1325,1327,13291,13292,13331,13332,13371,13372,1339,14981,14982,INTERSECTION_CODE ,14051,14052,CORNER_CODE};
	
	
	public static final int [] SUNLAB_HALL_DIST = {36,5,24,5,6,5,28,6,21,5,12};
	public static final int [] SUNLAB_HALL_ROOMS = {CORNER_CODE,14581,14582,14541,14542,14501,14502,14461,14462,14421,14422,INTERSECTION_CODE};

}