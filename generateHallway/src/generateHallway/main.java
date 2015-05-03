package generateHallway;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class main {
	public static void main(String [] args){
		
		int startRoomNum = 1431;
		int endRoomNum = 1411;
		
		ArrayList<Double>distancesB = new ArrayList<Double>();
		ArrayList<Integer>roomNumsB = new ArrayList<Integer>();
		ArrayList<Double>distancesR = new ArrayList<Double>();
		ArrayList<Integer>roomNumsR = new ArrayList<Integer>();
		ArrayList<Double>distancesS = new ArrayList<Double>();
		ArrayList<Integer>roomNumsS = new ArrayList<Integer>();
		ArrayList<Double>curRoomsDistances = new ArrayList<Double>();
		ArrayList<Integer>blankenshipHallway = new ArrayList<Integer>();
		ArrayList<Integer>rennerHallway = new ArrayList<Integer>();
		ArrayList<Integer>sunLabHallway = new ArrayList<Integer>();
		ArrayList<Integer> curHallway;
		ArrayList<String> hallProgression = new ArrayList<String>();
		ArrayList<String> turnProgression = new ArrayList<String>();
		String colorPaperToLook = null;
		String direction = "";
		double curPos = 0; //represents the current position along the current hallway
		// Generate Blankenship Hallway
		//Added 5 at end of array to signify end of hallway, using 9001 to signify this
		double [] blankenshipDistances = {6,13,9,13,8,14,10,10,6,17,10,12,10,5};
		int [] blankenshipRooms = {1409, 1411, 1415,1415,1417,1417,1421,1423,1427,1427,1431,1431,1433,9001};
		double [] rennerDistances = {8,11,9,12,9, 13,11,9,11,13,9,13,9,9,9,11,7,8,19,8,31};
		int [] rennerRooms = {1315,1317,1321,1321,1323,1323,1325,1327,1329,1329,1333,1333,1337,1337,1339,1498,1498,5000,1405,1405,5001};
		double [] sunLabDistances = {36,5,24,5,6,5,28,6,21,5,12};
		int [] sunLabRooms = {1458,1458,1454,1454,1450,1450,1446,1446,1442,1442,9000};
		
		for(int i=0;i<blankenshipDistances.length;++i){
			distancesB.add(blankenshipDistances[i]);
		}
		for(int i=0;i<blankenshipRooms.length;++i){
			roomNumsB.add(blankenshipRooms[i]);
		}
		
		for(int j=0;j<distancesB.size();++j) {
			  Double distance = distancesB.get(j);
			  Integer room = roomNumsB.get(j);

			for(double i=0;i<distance;i+=1){
				blankenshipHallway.add(0);
			}
			blankenshipHallway.add(room);
		}
		blankenshipHallway.set(0, 4000);
		// Generate Renner Hallway
		for(int i=0;i<rennerDistances.length;++i){
			distancesR.add(rennerDistances[i]);
		}
		for(int i=0;i<rennerRooms.length;++i){
			roomNumsR.add(rennerRooms[i]);
		}
		
		for(int j=0;j<distancesR.size();++j) {
			  Double distance = distancesR.get(j);
			  Integer room = roomNumsR.get(j);

			for(double i=0;i<distance;i+=1){
				rennerHallway.add(0);
			}
			rennerHallway.add(room);
		}
		
		// Generate SunLab Hallway
		for(int i=0;i<sunLabDistances.length;++i){
			distancesS.add(sunLabDistances[i]);
		}
		for(int i=0;i<sunLabRooms.length;++i){
			roomNumsS.add(sunLabRooms[i]);
		}
		
		for(int j=0;j<distancesS.size();++j) {
			  Double distance = distancesS.get(j);
			  Integer room = roomNumsS.get(j);

			for(double i=0;i<distance;i+=1){
				sunLabHallway.add(0);
			}
			sunLabHallway.add(room);
		}
		sunLabHallway.set(0, 4001);
		// print out hallway arrays
		/*for(int i=0;i<blankenshipHallway.size();++i){
			System.out.print(blankenshipHallway.get(i)+" ");
		}
		System.out.println();
		for(int i=0;i<rennerHallway.size();++i){
			System.out.print(rennerHallway.get(i)+" ");
		}
		System.out.println();
		for(int i=0;i<sunLabHallway.size();++i){
			System.out.print(sunLabHallway.get(i)+" ");
		}*/
		
		int roomNumsIndex = 0;
		if(blankenshipHallway.contains(startRoomNum)){
			roomNumsIndex =  roomNumsB.indexOf(startRoomNum);
		}
		else if(rennerHallway.contains(startRoomNum)){
			roomNumsIndex =  roomNumsR.indexOf(startRoomNum);
		}
		else if(sunLabHallway.contains(startRoomNum)){
			roomNumsIndex =  roomNumsS.indexOf(startRoomNum);
		}
		int turnAt;
		
		if(blankenshipHallway.contains(startRoomNum) && blankenshipHallway.contains(endRoomNum)){
			hallProgression.add("b");
		}
		else if(blankenshipHallway.contains(startRoomNum) && sunLabHallway.contains(endRoomNum)){
			hallProgression.add("brs");
			turnProgression.add("Left");
			turnProgression.add("Left");
		}
		else if(blankenshipHallway.contains(startRoomNum) && rennerHallway.contains(endRoomNum)){
			hallProgression.add("br");
			turnProgression.add("Left");
		}
		else if(sunLabHallway.contains(startRoomNum) && blankenshipHallway.contains(endRoomNum)){
			hallProgression.add("srb");
			turnProgression.add("Right");
			turnProgression.add("Right");
		}
		else if(sunLabHallway.contains(startRoomNum) && sunLabHallway.contains(endRoomNum)){
			hallProgression.add("s");
		}
		else if(sunLabHallway.contains(startRoomNum) && rennerHallway.contains(endRoomNum)){
			hallProgression.add("sr");
			if(rennerHallway.indexOf(endRoomNum) < rennerHallway.indexOf(5000))
				turnProgression.add("Left");
			else
				turnProgression.add("Right");
		}
		else if(rennerHallway.contains(startRoomNum) && blankenshipHallway.contains(endRoomNum)){
			hallProgression.add("rb");
			turnProgression.add("Right");
		}
		else if(rennerHallway.contains(startRoomNum) && sunLabHallway.contains(endRoomNum)){
			hallProgression.add("rs");
			if(rennerHallway.indexOf(endRoomNum) < rennerHallway.indexOf(5000))
				turnProgression.add("Right");
			else
				turnProgression.add("Left");
		}
		else if(rennerHallway.contains(startRoomNum) && rennerHallway.contains(endRoomNum)){
			hallProgression.add("r");
		}

		if(hallProgression.get(0).equals("b"))
			curHallway = blankenshipHallway;
		else if(hallProgression.get(0).equals("s"))
			curHallway = sunLabHallway;
		else
			curHallway = rennerHallway;
		
		boolean end=false;
		curPos = curHallway.indexOf(startRoomNum);
		//System.out.println(curPos+" "+roomNumsIndex);
		while(hallProgression.size() > 0){
			if(hallProgression.size()>1){
				if(hallProgression.get(0).equals("b") && hallProgression.get(1).equals("r")){
					int indexTurn = 4000;
					direction = "backwards";
				}
				else if(hallProgression.get(0).equals("s") && hallProgression.get(1).equals("r")){
					int indexTurn = 4001; 
					direction = "backwards";
				}
				else if(hallProgression.get(0).equals("r") && hallProgression.get(1).equals("b")){
					int indexTurn = 5001;
					direction = "forward";
				}
				else if(hallProgression.get(0).equals("r") && hallProgression.get(1).equals("s")){
					int indexTurn = 5000;
					if(curHallway.indexOf(curPos) < curHallway.indexOf(5000))
						direction="forward";
					else
						direction="backward";
				}
					
			}
			else{
				if(curPos < curHallway.indexOf(endRoomNum))
					direction="forward";
				else
					direction="backward";
			}
			
			/*if(//update encoder by 1)
				if(direction.equals("forward"))
					curPos += 1;
				else
					curPos -= 1;*/
			if(hallProgression.get(0).equals("b")){
				curHallway = blankenshipHallway;
				curRoomsDistances = distancesB;
			}
			else if(hallProgression.get(0).equals("s")){
				curHallway = sunLabHallway;
				curRoomsDistances = distancesS;
			}
			else{
				curHallway = rennerHallway;
				curRoomsDistances = distancesR;
			}
			//System.out.println(curPos);
			if(true)
				if(direction.equals("forward")){
					roomNumsIndex += 1;
					curPos = -1;
					//System.out.println(roomNumsIndex);
					for(int i=0;i<=roomNumsIndex;++i){
						curPos += curRoomsDistances.get(i);
						curPos += 1;
					}
				}
				else{
					roomNumsIndex -= 1;
					curPos = -1;
					for(int i=0;i<=roomNumsIndex;++i){
						curPos += curRoomsDistances.get(i);
						curPos += 1;
					}
				}
			//System.out.println(curPos+" "+curHallway.get((int)(curPos)));
			if(curHallway.get((int)(curPos)) == endRoomNum){
				end = true;
				hallProgression.clear();
				//System.out.println("Found the room");
			}
			//curPos += getHowMuchFurtherRobotHasGone or curPos = exactDistMoved
		}
	}
}
