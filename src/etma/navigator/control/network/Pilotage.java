package etma.navigator.control.network;
import java.util.HashMap;

import etma.navigator.control.Navigator;


public class Pilotage {

	protected Navigator navigator ;
	
	public Pilotage (Navigator navigator) {
		this.navigator = navigator ;
	}
	
	public void navigate (String command, String objectName, HashMap<String, Object> hm) {
		if (command.equals ("translate")) {
			System.out.println ("translation: " + hm) ;
			if (objectName.equals ("viewpoint")) {
				navigator.supportTranslateInHeadFrame ((Double)(hm.get ("x")), (Double)(hm.get ("y")), (Double)(hm.get ("z")));
			}
		} else if (command.equals ("rotate")) {
			System.out.println ("rotation: " + hm) ;
			if (objectName.equals ("viewpoint")) {
				navigator.supportRotateInHeadFrame ((Double)(hm.get ("x")), (Double)(hm.get ("y")), (Double)(hm.get ("z")), (Double)(hm.get ("w")));
			}
		} else if (command.equals ("orientate")) {
			//System.out.println ("orientation: " + hm) ;
			if (objectName.equals ("head")) {
				navigator.setHeadOrientationInSupportFrame ((Double)(hm.get ("x")), (Double)(hm.get ("y")), (Double)(hm.get ("z")), (Double)(hm.get ("w")));
			}
		}		
	}

}

