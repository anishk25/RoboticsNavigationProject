package hallway_nav;

public class Driver {

	public static void main(String [] args){
		HallwayNavigator hn = new HallwayNavigator(13231,1339);
		while(!hn.getDestReached()){
			System.out.println("Current point is " + hn.getCurrentPointPaper());
			hn.updateBasedOnPaperSeen();
		}
		System.out.println("Current point is " + hn.getCurrentPointPaper());
	}
}
