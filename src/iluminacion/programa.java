/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iluminacion;

import com.jogamp.opengl.util.FPSAnimator;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 *
 * @author Familia
 */
public class programa {
    
    public static void main(String[] args) {
        
        programa ol = new programa();
        ol.test("./data/bulldog.obj");
        
    }
    
    public void test(String fileName) {
                  
            // o.printVectors();

            JFrame fr = new JFrame();
            prueba canvas = new prueba(fileName);
            
            final FPSAnimator animator = new FPSAnimator(canvas, prueba.FPS, true);
            
            fr.getContentPane().add(canvas);
            
            fr.addKeyListener(canvas);
            
            fr.addWindowListener(new WindowAdapter() {
               @Override
               public void windowClosing(WindowEvent e) {
                  // Use a dedicate thread to run the stop() to ensure that the
                  // animator stops before program exits.
                  new Thread() {
                     @Override
                     public void run() {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);
                     }
                  }.start();
               }
            });
                        
            fr.setTitle("ESTRELLA");
            fr.pack();
            fr.setVisible(true);
            animator.start(); // start the animation loop

    }
    
}
