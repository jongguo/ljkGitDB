

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class ReduceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ReduceTest();
	}
	
	public ReduceTest() {
		String[] links = {
				"https://localhost/viewer.html#unit:default",
				"https://localhost/viewer.html#unit:area/791024",
				"https://localhost/viewer.html#unit:area/780693",
				"https://localhost/viewer.html#unit:area/791024",
				"https://localhost/viewer.html#unit:area/780693",
				"https://localhost/viewer.html#unit:area/791021",
				"https://localhost/viewer.html#unit:area/780692",
				"https://localhost/viewer.html#unit:area/791023",
				"https://localhost/viewer.html#unit:area/780694",
				"https://localhost/viewer.html#unit:area/791025",
				"https://localhost/viewer.html#unit:area/780696",
				"https://localhost/viewer.html#unit:area/791027",
				"https://localhost/viewer.html#unit:area/780698",
				"https://localhost/viewer.html#unit:area/791029",
				"https://localhost/viewer.html#unit:area/780690",
				"https://localhost/viewer.html#unit:area/780693/events/upcoming",
				"https://localhost/viewer.html#leader:area/423",
				"https://localhost/viewer.html#leader:area/423/biography",
				"https://localhost/viewer.html#leader:area/423/family",
				"https://localhost/viewer.html#leader:area/31371/assignments",
				"https://localhost/viewer.html#event:682683",
				"https://localhost/viewer.html#unit:stake/507407/fullsize",
				"https://localhost/viewer.html#unit:area/780693/districts",
				"https://localhost/viewer.html#unit:area/780693/stakes",
				"https://localhost/viewer.html#unit:area/780693/missions",
				"https://localhost/viewer.html#unit:area/780693/temples"
		};

        WeightedGraph<String, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);


		for(String s : links) {

			System.out.println("");
			System.out.println("Working url: " + s);
			
			// clear out the protocol
			s = s.replaceAll("https://", "");
			
			// separate the url from the fragments
			String[] parts = s.split("#");

	        // add the root part of url (everything up to '#')
			String root = parts[0] + "#";
	        if(!g.containsVertex(root)) {
	        	g.addVertex(root);
	        	System.out.println("added root node: " + root);
	        }
	        else {
	        	System.out.println("root node exists: " + root);
	        }

	        String concatenatedFragments = root;
	        String previous = root;
	        
			String[] fragments = parts[1].split("[/:]");
			for(int i = 0; i < fragments.length; i++) {

				concatenatedFragments += "/" + fragments[i];
				
				if(!g.containsVertex(concatenatedFragments)) {
					g.addVertex(concatenatedFragments);
		        	System.out.println("added node: " + concatenatedFragments);
		        	
					g.addEdge(previous, concatenatedFragments, new DefaultWeightedEdge());
		        	System.out.println("     +edge: " + previous + " -> " + concatenatedFragments);
		        	previous = concatenatedFragments;
				}
				else {
		        	System.out.println("node exists: " + concatenatedFragments);
		        	if(!g.containsEdge(previous, concatenatedFragments)) {
						g.addEdge(previous, concatenatedFragments, new DefaultWeightedEdge());
			        	System.out.println("     +edge: " + previous + " -> " + concatenatedFragments);
		        	}
		        	else {
		        		DefaultWeightedEdge dwe = g.getEdge(previous, concatenatedFragments);
		        		g.setEdgeWeight(dwe, g.getEdgeWeight(dwe) + 1);
		        	}
		        	previous = concatenatedFragments;					
				}
			}
			
		}

//		System.out.println("BREADTH-----------");
//		BreadthFirstIterator<String,DefaultWeightedEdge> bfi = new BreadthFirstIterator<String,DefaultWeightedEdge>(g);
//		while(bfi.hasNext()) {
//			System.out.println(bfi.next());
//		}
		
//		System.out.println("DEPTH-----------");
//		DepthFirstIterator<String,DefaultWeightedEdge> dfi = new DepthFirstIterator<String,DefaultWeightedEdge>(g);
//		while(dfi.hasNext()) {
//			System.out.println(dfi.next());
//		}

		DescriptiveStatistics stats = new DescriptiveStatistics();
		for(String s : g.vertexSet()) {
			stats.addValue((g.edgesOf(s).size() - 1));
		}

		double stddev = stats.getStandardDeviation();
		System.out.println(stddev * 2);
		for(String s : g.vertexSet()) {
			if(g.edgesOf(s).size() > (stddev * 2)) {
				System.out.println("Reduction candidate: " + s + ": " + g.edgesOf(s).size());
			}
		}
	}
}
