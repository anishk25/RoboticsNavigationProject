package generateHallway;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class main {
	public static void main(String [] args){
		//TreeMap<Double,Integer>distances = new TreeMap<Double,Integer>();
		ArrayList<Double>distances = new ArrayList<Double>();
		ArrayList<Integer>roomNums = new ArrayList<Integer>();
		ArrayList<Integer>hallway = new ArrayList<Integer>();
		distances.add(new Double(8.5));
		distances.add(new Double(13));
		distances.add(new Double(8.5));
		distances.add(new Double(13));
		distances.add(new Double(8));
		distances.add(new Double(14));
		distances.add(new Double(10));
		distances.add(new Double(10));
		distances.add(new Double(6));
		distances.add(new Double(17));
		distances.add(new Double(10));
		distances.add(new Double(12));
		distances.add(new Double(10));
		roomNums.add(new Integer(1409));
		roomNums.add(new Integer(1411));
		roomNums.add(new Integer(1415));
		roomNums.add(new Integer(1415));
		roomNums.add(new Integer(1417));
		roomNums.add(new Integer(1417));
		roomNums.add(new Integer(1421));
		roomNums.add(new Integer(1423));
		roomNums.add(new Integer(1427));
		roomNums.add(new Integer(1427));
		roomNums.add(new Integer(1431));
		roomNums.add(new Integer(1431));
		roomNums.add(new Integer(1433));
		
		for(int j=0;j<distances.size();++j) {
			  Double distance = distances.get(j);
			  Integer room = roomNums.get(j);

			for(double i=0.5;i<distance;i+=0.5){
				hallway.add(0);
			}
			hallway.add(room);
		}
		
		for(int i=0;i<hallway.size();++i){
			System.out.print(hallway.get(i)+" ");
		}
	}
}
