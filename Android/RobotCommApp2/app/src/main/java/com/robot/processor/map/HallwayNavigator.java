package com.robot.processor.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class HallwayNavigator {
	private int startRoom,endRoom;
	public enum Direction{
		FORWARD,BACKWARD
	}
	private Direction currDirection;
	private int currRoomIndex;
	private ArrayList<Integer> currHallway;
	private String currHallwayName;
	private ArrayList<Integer> blankenshipHallway,rennerHallway,sunLabHallway;
	private HashMap<String, ArrayList<Integer>> hallways;
	private boolean destReached;
    private boolean needToTurn = false;
	
	
	public HallwayNavigator(int startRoom, int endRoom){
		this.startRoom = startRoom;
		this.endRoom = endRoom;
		this.blankenshipHallway = new ArrayList<Integer>();
		this.rennerHallway = new ArrayList<Integer>();
		this.sunLabHallway = new ArrayList<Integer>();
		this.hallways = new HashMap<String, ArrayList<Integer>>();
		this.destReached = false;
		initHallways();
		selectCurrentHallway();
		calculateDirection();
	}
	
	public void setStartEnd(int startRoom, int endRoom) throws IllegalArgumentException {
		if(startRoom != endRoom){
			this.startRoom = startRoom;
			this.endRoom = endRoom;
			this.destReached = false;
			selectCurrentHallway();
			calculateDirection();
		}else{
			throw new IllegalArgumentException("Start and end room must be different");
		}
	}
	
	
	private void initHallways(){
		fillInHallway(RoomMappings.BLANKENSHIP_HALL_ROOMS, RoomMappings.BLANKENSHIP_HALL_DIST, blankenshipHallway);
		fillInHallway(RoomMappings.RENNER_HALL_ROOMS, RoomMappings.RENNER_HALL_DIST, rennerHallway);
		fillInHallway(RoomMappings.SUNLAB_HALL_ROOMS, RoomMappings.SUNLAB_HALL_DIST, sunLabHallway);
		
		hallways.put(RoomMappings.BLANKENSHIP_HALLWAY_NAME, blankenshipHallway);
		hallways.put(RoomMappings.RENNER_HALLWAY_NAME, rennerHallway);
		hallways.put(RoomMappings.SUNLAB_HALLWAY_NAME, sunLabHallway);
	}
	
	
	private void fillInHallway(int [] rooms, int [] distances, ArrayList<Integer> hallway){
		for(int i = 0; i < rooms.length; i++){
			int roomNum = rooms[i];
			hallway.add(roomNum);
		}
	}
	
	private void selectCurrentHallway(){
		for(Entry<String, ArrayList<Integer>> hall : hallways.entrySet()){
			if(hall.getValue().contains(startRoom)){
				currHallway = hall.getValue();
				currHallwayName = hall.getKey();
				break;
			}
		}	
	}
	
	private void calculateDirection(){
		// only works for blankenship and renner hallway for now
		// code will break if any other hallway is used!
		int index_start = currHallway.indexOf(startRoom);
		int index_end = currHallway.indexOf(endRoom);
		this.currRoomIndex = index_start;
		if(index_end == -1){
			for(Entry<String, ArrayList<Integer>> hall : hallways.entrySet()){
				if(hall.getValue().contains(endRoom)){
					if(currHallwayName.equals(RoomMappings.BLANKENSHIP_HALLWAY_NAME)){
						currDirection = Direction.BACKWARD;
					}else if(currHallwayName.equals(RoomMappings.RENNER_HALLWAY_NAME)){
						currDirection = Direction.FORWARD;
					}
				}
			}	
		}else{
			if(index_start < index_end){
				currDirection = Direction.FORWARD;
			}else{
				currDirection = Direction.BACKWARD;
			}
		}
	}
	
	public void updatePosition(){
		// only works for blankenship and renner hallway for now
		// code will break if any other hallway is used!
		
		if(!destReached){
            needToTurn = false;
			if(currDirection == Direction.FORWARD){
				currRoomIndex += 1;
			}else{
				currRoomIndex -= 1;
			}
			
			if(currHallway.get(currRoomIndex) == RoomMappings.CORNER_CODE){
				// reached a corner so need to turn
                needToTurn = true;
				if(currHallwayName.equals(RoomMappings.BLANKENSHIP_HALLWAY_NAME)){
					currHallwayName = RoomMappings.RENNER_HALLWAY_NAME;
					currHallway = hallways.get(RoomMappings.RENNER_HALLWAY_NAME);
					currRoomIndex = currHallway.size()-1;
				}else if(currHallwayName.equals(RoomMappings.RENNER_HALLWAY_NAME)){
					currHallwayName = RoomMappings.BLANKENSHIP_HALLWAY_NAME;
					currHallway = hallways.get(RoomMappings.BLANKENSHIP_HALLWAY_NAME);
					currRoomIndex = 0;
				}

			}
			
			if(currHallway.get(currRoomIndex) == endRoom){
				destReached = true;
			}
		}
	}
	
	
	
	public boolean getDestReached(){
		return destReached;
	}
	
	public int getCurrentPoint(){
		return currHallway.get(currRoomIndex);
	}

    public Direction getCurrDirection(){
        return currDirection;
    }

    public boolean isNeedToTurn(){
        return needToTurn;
    }

	
	
	

}
