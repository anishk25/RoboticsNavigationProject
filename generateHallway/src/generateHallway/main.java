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
		// Generate Blankenship Hallway
		double [] blankenshipDistances = {8.5,13,8.5,13,8,14,10,10,6,17,10,12,10};
		int [] blankenshipRooms = {1409, 1411, 1415,1415,1417,1417,1421,1423,1427,1427,1431,1431,1433};
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
		double [] rennerDistances = {10.5,11,9,12,9, 13,11,9,11,13,9,13,9,9,9,11,7,27,8,33.5};
		int [] rennerRooms = {1315,1317,1321,1321,1323,1323,1325,1327,1329,1329,1333,1333,1337,1337,1339,1498,1498,1405,1405,9000};
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
		double [] sunLabDistances = {38.5,5,24,5,6,5,28,6,21,5,12};
		int [] sunLabRooms = {1458,1458,1454,1454,1450,1450,1446,1446,1442,1442,5000};
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
		
		for(int i=0;i<blankenshipHallway.size();++i){
			System.out.print(blankenshipHallway.get(i)+" ");
		}
		System.out.println();
		for(int i=0;i<rennerHallway.size();++i){
			System.out.print(rennerHallway.get(i)+" ");
		}
		System.out.println();
		for(int i=0;i<sunLabHallway.size();++i){
			System.out.print(sunLabHallway.get(i)+" ");
		}
	}
}
