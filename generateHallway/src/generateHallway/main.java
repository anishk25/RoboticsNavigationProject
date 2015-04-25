package generateHallway;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class main {
	public static void main(String [] args){
		
		ArrayList<Double>distances = new ArrayList<Double>();
		ArrayList<Integer>roomNums = new ArrayList<Integer>();
		ArrayList<Integer>blankenshipHallway = new ArrayList<Integer>();
		ArrayList<Integer>rennerHallway = new ArrayList<Integer>();
		ArrayList<Integer>sunLabHallway = new ArrayList<Integer>();
		ArrayList<Integer> curHallway;
		double curPos = 0; //represents the current position along the current hallway
		// Generate Blankenship Hallway
		//Added 5 at end of array to signify end of hallway, using 9001 to signify this
		double [] blankenshipDistances = {6,13,8.5,13,8,14,10,10,6,17,10,12,10,5};
		int [] blankenshipRooms = {1409, 1411, 1415,1415,1417,1417,1421,1423,1427,1427,1431,1431,1433,9001};
		for(int i=0;i<blankenshipDistances.length;++i){
			distances.add(blankenshipDistances[i]);
		}
		for(int i=0;i<blankenshipRooms.length;++i){
			roomNums.add(blankenshipRooms[i]);
		}
		
		for(int j=0;j<distances.size();++j) {
			  Double distance = distances.get(j);
			  Integer room = roomNums.get(j);

			for(double i=0.5;i<distance;i+=0.5){
				blankenshipHallway.add(0);
			}
			blankenshipHallway.add(room);
		}
		
		// Generate Renner Hallway
		distances.clear();
		roomNums.clear();
		double [] rennerDistances = {8,11,9,12,9, 13,11,9,11,13,9,13,9,9,9,11,7,8,19,8,31};
		int [] rennerRooms = {1315,1317,1321,1321,1323,1323,1325,1327,1329,1329,1333,1333,1337,1337,1339,1498,1498,5000,1405,1405,5001};
		for(int i=0;i<rennerDistances.length;++i){
			distances.add(rennerDistances[i]);
		}
		for(int i=0;i<rennerRooms.length;++i){
			roomNums.add(rennerRooms[i]);
		}
		
		for(int j=0;j<distances.size();++j) {
			  Double distance = distances.get(j);
			  Integer room = roomNums.get(j);

			for(double i=0.5;i<distance;i+=0.5){
				rennerHallway.add(0);
			}
			rennerHallway.add(room);
		}
		
		// Generate SunLab Hallway
		distances.clear();
		roomNums.clear();
		double [] sunLabDistances = {36,5,24,5,6,5,28,6,21,5,12};
		int [] sunLabRooms = {1458,1458,1454,1454,1450,1450,1446,1446,1442,1442,9000};
		for(int i=0;i<sunLabDistances.length;++i){
			distances.add(sunLabDistances[i]);
		}
		for(int i=0;i<sunLabRooms.length;++i){
			roomNums.add(sunLabRooms[i]);
		}
		
		for(int j=0;j<distances.size();++j) {
			  Double distance = distances.get(j);
			  Integer room = roomNums.get(j);

			for(double i=0.5;i<distance;i+=0.5){
				sunLabHallway.add(0);
			}
			sunLabHallway.add(room);
		}
		
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
		
		int turnAt;
		// Will be populated later (HTTP Request?)
		int roomNumToFind = 1417;
		if(blankenshipHallway.contains(roomNumToFind))
			turnAt = 5001;
		if(sunLabHallway.contains(roomNumToFind))
			turnAt = 5000;
		
		boolean end=false;
		//Used to find where robot is on map as well as if it needs to do a map change
		while(end==false){
			curHallway = rennerHallway;
			if(curHallway.get((int)(curPos/0.5)) == 9000){
				System.out.println("End of Sun lab");
				end = true;
			}
			if(curHallway.get((int)(curPos/0.5)) == 9001){
				System.out.println("End of Blankenship hallway");
				end = true;
			}
			if(curHallway.get((int)(curPos/0.5)) == 5000){
				//Turn into sun lab hallway
				curHallway = sunLabHallway;
				curPos = 0;
			}
			if(curHallway.get((int)(curPos/0.5)) == 5001){
				//Turn into Blankenship hallway
				curHallway = blankenshipHallway;
				curPos = 0;
			}
			if(curHallway.get((int)(curPos/0.5)) == roomNumToFind){
				end = true;
				System.out.println("Found the room");
			}
			//curPos += getHowMuchFurtherRobotHasGone or curPos = exactDistMoved
		}
		// Used for me to see and check map
		/*int numZeros = 0;
		for(int i=0;i<blankenshipHallway.size();++i){
			if(blankenshipHallway.get(i)==0)
				numZeros++;
			else{
				System.out.print(numZeros+" "+blankenshipHallway.get(i)+" ");
				numZeros = 0;
			}
		}
		System.out.println();
		numZeros = 0;
		for(int i=0;i<rennerHallway.size();++i){
			if(rennerHallway.get(i)==0)
				numZeros++;
			else{
				System.out.print(numZeros+" "+rennerHallway.get(i)+" ");
				numZeros = 0;
			}
		}
		numZeros = 0;
		System.out.println();
		for(int i=0;i<sunLabHallway.size();++i){
			if(sunLabHallway.get(i)==0)
				numZeros++;
			else{
				System.out.print(numZeros+" "+sunLabHallway.get(i)+" ");
				numZeros = 0;
			}
		}*/
	}
}
