package fr.etma.navigator.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.vecmath.Vector3d;

import fr.etma.navigator.DemoNavigation;

public class TrajectoryChooser {
	
	JDialog jd ;
	Map<String, Path> trajectories = new HashMap<String, Path> () ;
	Vector3d[] listePositions ;

	public Vector3d[] getTrajectory (DemoNavigation parent) {
		jd = new JDialog (parent, true) ;
		JComboBox<String> combo = new JComboBox<String> () ;
		
		DirectoryStream<Path> directory;
		try {
			directory = Files.newDirectoryStream (FileSystems.getDefault().getPath ("Trajectoires"), "*.trj") ;
			for (Path path : directory) {
				System.out.println (path.getFileName ()) ;
				trajectories.put (path.getFileName ().toString(), path) ;
				combo.addItem (path.getFileName ().toString()) ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		setTrajectory (combo.getSelectedItem ().toString ()) ;

		combo.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent arg0) {
				setTrajectory (combo.getSelectedItem ().toString ()) ;
			}});
		jd.add (combo) ;
		jd.pack () ;
		jd.setVisible (true) ;

		return listePositions ;
	}
	
	private void setTrajectory (String name) {
		System.out.println (name) ;
		Path trajectoire = trajectories.get (name) ;
		List<String> list;
		try {
			list = Files.readAllLines (trajectoire) ;
			listePositions = new Vector3d [list.size ()] ;
			int i = 0 ;
			for (String s : list) {
				System.out.println (s) ;
				String values [] = s.split (",") ;
				listePositions [i] = new Vector3d (Double.parseDouble(values [0]), Double.parseDouble(values [1]), Double.parseDouble(values [2])) ;
				i = i + 1 ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
