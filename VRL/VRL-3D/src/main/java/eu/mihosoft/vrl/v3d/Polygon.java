package eu.mihosoft.vrl.v3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// # class Polygon
// Represents a convex polygon. The vertices used to initialize a polygon must
// be coplanar and form a convex loop. They do not have to be `CSG.Vertex`
// instances but they must behave similarly (duck typing can be used for
// customization).
//
// Each convex polygon has a `shared` property, which is shared between all
// polygons that are clones of each other or were split from the same polygon.
// This can be used to define per-polygon properties (such as surface color).
public class Polygon {

    public final List<Vertex> vertices;
    public final boolean shared;
    public final Plane plane;

    public Polygon(List<Vertex> vertices, boolean shared) {
        this.vertices = vertices;
        this.shared = shared;
        this.plane = Plane.fromPoints(
                vertices.get(0).pos,
                vertices.get(1).pos,
                vertices.get(2).pos);
    }

    @Override
    public Polygon clone() {
        List<Vertex> newVertices = new ArrayList<>();
        this.vertices.forEach((vertex) -> {
            newVertices.add(vertex.clone());
        });
        return new Polygon(newVertices, shared);
    }

    public Polygon flip() {
        vertices.forEach((vertex) -> {
            vertex.flip();
        });
        Collections.reverse(vertices);
        plane.flip();
        return this;
    }
    
    public Polygon flipped() {
       
        return clone().flip();
    }

    public String toStlString() {
        String result = "";
        
        if (this.vertices.size() >= 3) // should be!
        {
	    // STL requires triangular polygons. If our polygon has more vertices, create
            // multiple triangles:
            String firstVertexStl = this.vertices.get(0).toStlString();
            for (int i = 0; i < this.vertices.size() - 2; i++) {
                result += "facet normal " + this.plane.normal.toStlString() + "\nouter loop\n";
                result += firstVertexStl+"\n";
                result += this.vertices.get(i + 1).toStlString() + "\n";
                result += this.vertices.get(i + 2).toStlString() + "\n";
                result += "endloop\nendfacet\n";
            }
        }
        return result;
    }

    public Polygon translate(Vector v) {
        vertices.forEach((vertex) -> {
            vertex.pos = vertex.pos.plus(v);
        });
        return this;
    }
    
        public Polygon translated(Vector v) {
            
        return clone().translate(v);
    }
        
        public static Polygon createFromPoints(List<Vector> points, boolean shared) {
            return createFromPoints(points, shared, null);
        }
        
        // Create a polygon from the given points
        public static Polygon createFromPoints(List<Vector> points, boolean shared, Plane plane) {
	Vector normal = (plane!=null)? plane.normal.clone() : new Vector(0,0,0);
	
	List<Vertex> vertices = new ArrayList<>();
	points.forEach((Vector p)-> {
		Vector vec = p.clone();
		Vertex vertex = new Vertex(vec,normal);
		vertices.add(vertex);
	});

	return new Polygon(vertices, shared);
};
    
        
    // Extrude a polygon into the direction offsetvector
	// Returns a CSG object
        public CSG extrude(Vector offsetvector) {
		List<Polygon> newPolygons = new ArrayList<>();

		Polygon polygon1 = this;
		double direction = polygon1.plane.normal.dot(offsetvector);
		if(direction > 0) {
			polygon1 = polygon1.flipped();
		}
		newPolygons.add(polygon1);
		Polygon polygon2 = polygon1.translated(offsetvector);
		int numvertices = this.vertices.size();
		for(int i = 0; i < numvertices; i++) {
			List<Vector> sidefacepoints = new ArrayList<>();
			int nexti = (i < (numvertices - 1)) ? i + 1 : 0;
			sidefacepoints.add(polygon1.vertices.get(i).pos);
			sidefacepoints.add(polygon2.vertices.get(i).pos);
			sidefacepoints.add(polygon2.vertices.get(nexti).pos);
			sidefacepoints.add(polygon1.vertices.get(nexti).pos);
			Polygon sidefacepolygon = Polygon.createFromPoints(sidefacepoints, this.shared);
			newPolygons.add(sidefacepolygon);
		}
		polygon2 = polygon2.flipped();
		newPolygons.add(polygon2);
		return CSG.fromPolygons(newPolygons);
	}

}
