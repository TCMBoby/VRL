/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.v3d;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Main {

    public static void main(String[] args) {

        CubeOptions smallCube = new CubeOptions();
        smallCube.setCenter(new Vector(0, 0, 0));
        smallCube.setRadius(6,0.5,0.5);
        
        CubeOptions smallCube2 = new CubeOptions();
        smallCube2.setCenter(new Vector(0, 0, 0));
        smallCube2.setRadius(1,1,1);
        
        SphereOptions sphereOptions = new SphereOptions();
        sphereOptions.setRadius(2);
//        sphereOptions.setCenter(new Vector(1, 0, 0));

        CylinderOptions cylinderOptions = new CylinderOptions();
        cylinderOptions.setRadius(0.8);
        cylinderOptions.setStart(new Vector(0, -3, 0));
        cylinderOptions.setEnd(new Vector(0, 3, 0));
        
        CSG a = CSG.sphere(sphereOptions);
        CSG b = CSG.cylinder(cylinderOptions);
        CSG c =  CSG.sphere(new SphereOptions());//CSG.cube(smallCube2);//

        CSG testObject = a.subtract(b).subtract(c);


        String stlString;

        stlString = testObject.toStlString();

//        stlString = CSG.cylinder(cylinderOptions).inverse().toStlString();
        BufferedWriter writer;

        try {
            writer = Files.newBufferedWriter(Paths.get("obj.stl"), Charset.defaultCharset(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            writer.write(stlString, 0, stlString.length());

            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
