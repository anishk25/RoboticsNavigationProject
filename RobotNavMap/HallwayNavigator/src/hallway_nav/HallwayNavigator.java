package hallway_nav;

import java.util.ArrayList;

public class HallwayNavigator {
	private int startRoom,endRoom;
	private enum Direction{
		FORWARD,BACKWARD
	}
	private Direction currDirection;
	private int currIndex,currRoomColorIndex;
	private ArrayList<Integer> currHallwayFull,currHallwayRooms;
	private ArrayList<Integer> blankenshipHallway,rennerHallway,sunLabHallway;
	private ArrayList<ArrayList<Integer>> hallways;
	private boolean destReached;
	
	
	public HallwayNavigator(int startRoom, int endRoom){
		this.startRoom = startRoom;
		this.endRoom = endRoom;
		this.blankenshipHallway = new ArrayList<Integer>();
		this.rennerHallway = new ArrayList<Integer>();
		this.sunLabHallway = new ArrayList<Integer>();
		this.hallways = new ArrayList<ArrayList<Integer>>();
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
		fillInHallway(RoomMappings.RENNER_HALL_ROOMS, RoomMappings.RENNER_HALL_DIST, blankenshipHallway);
		fillInHallway(RoomMappings.SUNLAB_HALL_ROOMS, RoomMappings.SUNLAB_HALL_DIST, blankenshipHallway);
		
		hallways.add(blankenshipHallway);
		hallways.add(rennerHallway);
		hallways.add(sunLabHallway);
		}
	
	private void fillInHallway(int [] rooms, int [] distances, ArrayList<Integer> hallway){
		for(int i = 0; i < rooms.length; i++){
			int dist = distances[i];
			int roomNum = rooms[i];
			for(int d = 0; d < dist; d++){
				hallway.add(0);
			}
			hallway.add(roomNum);
		}
	}
	
	private void selectCurrentHallway(){
		for(ArrayList<Integer> hall : hallways){
			if(hall.contains(startRoom)){
				currHallwayFull = hall;
				currHallwayRooms = new ArrayList<Integer>(hall);
				break;
			}
		}
		
		Integer z = new Integer(0);
		while(currHallwayRooms.contains(0)){
			currHallwayRooms.remove(z);
		}
		
	}
	
	private void calculateDirection(){
		int index_start = currHallwayFull.indexOf(startRoom);
		int index_end = currHallwayFull.indexOf(endRoom);
		this.currIndex = index_start;
		this.currRoomColorIndex = currHallwayRooms.indexOf(startRoom);
		
		if(index_start < index_end){
			currDirection = Direction.FORWARD;
		}else{
			currDirection = Direction.BACKWARD;
		}
	}
	
	public void updatePosition(){
		if(!destReached){
			if(currDirection == Direction.FORWARD){
				currIndex += 1;
			}else{
				currIndex -= 1;
			}
			if(currHallwayFull.get(currIndex) == endRoom){
				destReached = true;
			}
		}
	}
	
	public void updateBasedOnPaperSeen(){
		if(!destReached){
			if(currDirection == Direction.FORWARD){
				currRoomColorIndex += 1;
			}else{
				currRoomColorIndex -= 1;
			}
			if(currHallwayRooms.get(currRoomColorIndex) == endRoom){
				destReached = true;
			}
		}
		
	}
	
	public boolean getDestReached(){
		return destReached;
	}
	
	public int getCurrentPoint(){
		return currHallwayFull.get(currIndex);
	}
	
	public int getCurrentPointPaper(){
		return currHallwayRooms.get(currRoomColorIndex);
	}
	
	
	
	

}