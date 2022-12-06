/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iluminacion;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import loader.ObjReader;
import loader.Object3d;

/**
 *
 * @author Familia
 */
public class prueba extends GLCanvas implements GLEventListener, KeyListener {

    private static final int CANVAS_WIDTH = 840;  // ancho
    private static final int CANVAS_HEIGHT = 680; // alto
    public static final int FPS = 10; // fotogramas
    
    int width = CANVAS_WIDTH;
    int height = CANVAS_HEIGHT;
    float fovy = 45.0f;
    GLU glu;  // for the GL Utility
    GLUT glut;
    
    float rotate = 0.0f;
    float scale = 1.0f;
    
    String fileName = "";
    
    //  Posición de la fuente de luz 1
    float plx = 0.0f;
    float ply = 0.0f;
    float plz = 0.0f;

    //  Posición de la cámara
    float pcx = 0.0f;
    float pcy = 2.0f;
    float pcz = 10.0f;    

    //  Dirección de la camara
    float pvx = 0.0f;
    float pvy = 0.0f;
    float pvz = 0.0f;   
        
    //  Posición de los objetos
    float pox = 0.0f;
    float poy = 0.0f;
    float poz = 0.0f;    
    
    int objSel = 0;
    private Object3d obj3;
    
    public prueba(String fileName) {
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        this.addGLEventListener(this);
        this.addKeyListener(this);
        this.fileName = fileName;
    }
    
    /* Circulo */
    
    // Referencias de rotacion
    float rotX = 90.0f;
    float rotY = 0.0f;
    float rotZ = 0.0f;
    
    // Posicion de la camara
    float camX = 2.0f;
    float camY = 2.0f;
    float camZ = 8.0f;
    
    // Posicion de la luz.
    float lightX = 1f;
    float lightY = 1f;
    float lightZ = 1f;
    float dLight = 0.05f;
    
    // Material y luces.       R        G       B      A
    final float ambient[] = {0.282f, 0.427f, 0.694f, 1.0f};
    final float position[] = {lightX, lightY, lightZ, 1.0f};

    //                                R    G    B    A
    final float[] colorBlack = {0.0f, 0.0f, 0.0f, 1.0f};
    final float[] colorWhite = {1.0f, 1.0f, 1.0f, 1.0f};
    final float[] colorGray = {0.4f, 0.4f, 0.4f, 1.0f};
    final float[] colorDarkGray = {0.2f, 0.2f, 0.2f, 1.0f};
    final float[] colorRed = {1.0f, 0.0f, 0.0f, 1.0f};
    final float[] colorGreen = {0.0f, 1.0f, 0.0f, 1.0f};
    final float[] colorBlue = {0.0f, 0.0f, 0.6f, 1.0f};
    final float[] colorYellow = {1.0f, 1.0f, 0.0f, 1.0f};
    final float[] colorLightYellow = {.5f, .5f, 0.0f, 1.0f};

    //       R     G     B     A          
    final float mat_diffuse[] = {0.6f, 0.6f, 0.6f, 1.0f};
    final float mat_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
    final float mat_shininess[] = {50.0f};
    private float aspect;
    
    ///////////////// Funciones /////////////////////////
    public prueba() {
        this.addGLEventListener(this);
        this.addKeyListener(this);
    }
    
    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();

        glu = new GLU();
        glut = new GLUT();

        // ----- Your OpenGL initialization code here -----
        gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f); // set background (clear) color
        gl.glClearDepth(1.0f);      // set clear depth value to farthest               

        gl.glEnable(GL2.GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL2.GL_LEQUAL);  // the type of depth test to do
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST); // best perspective correction
        gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting        

        /* prueba */
        
        // Alguna luz de ambiente global.
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, this.ambient, 0);

        // First Switch the lights on.
        gl.glEnable(GL2.GL_LIGHTING);

        gl.glEnable(GL2.GL_LIGHT0);
        
        // Light 0.
        
        // gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0 );
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, colorWhite, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, colorWhite, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);

        this.initPosition(gl);
        
//        this.setLight(gl); //esto es un metodo

        gl.glEnable(GL2.GL_NORMALIZE);

        ObjReader or = new ObjReader();   
        
        try {
            obj3 = or.Load("./data/18763_Cushion_Star_starfish_v2.obj");
            System.out.println("Estrella :" + this.fileName);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    
    public void initPosition(GL2 gl) {
        float posLight1[] = {lightX, lightY, lightZ, 1.0f};
        float spotDirection1[] = {0.0f, -1.f, 0.f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posLight1, 0);
    }
    
    /////////////// Move light ////////////////////////////
    // Move light 0.
    public void moveLightX(boolean positivDirection) {
        lightX += positivDirection ? dLight : -dLight;
    }

    public void moveLightY(boolean positivDirection) {
        lightY += positivDirection ? dLight : -dLight;
    }

    public void moveLightZ(boolean positivDirection) {
        lightZ += positivDirection ? dLight : -dLight;
    }

    public void animate(GL2 gl, GLU glu, GLUT glut) {
        float posLight0[] = {lightX, lightY, lightZ, 1.f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posLight0, 0);
        drawLight(gl, glu, glut); //metodo
//        lightX += 0.003f;
//        lightY += 0.003f;
    }
    
    /////////////// Define Material /////////////////////
    public void setLightSphereMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorYellow, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorYellow, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorYellow, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 100);
//        gl.glMaterialfv(face, GL2.GL_EMISSION, colorBlue, 0);
        gl.glMaterialfv( face, GL2.GL_EMISSION, colorLightYellow , 0 );
        //gl.glMaterialfv( face, GL.GL_EMISSION, colorBlack , 0 );
    }
    
    public void setSomeYellowMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorBlack, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorLightYellow, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorYellow, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 5);
//        gl.glMaterialfv(face, GL2.GL_EMISSION, colorBlack, 0);
    }
    
    public void setSomeGreenMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorDarkGray, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorGreen, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorWhite, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 128);
        gl.glMaterialfv(face, GL2.GL_EMISSION, colorDarkGray, 0);
    }
    public void setSomeBlueMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorBlue, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorBlue, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorBlue, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 4);
        gl.glMaterialfv(face, GL2.GL_EMISSION, colorBlack, 0);
    }

    public void setSomeRedMaterial(GL2 gl, int face) {
        gl.glMaterialfv(face, GL2.GL_AMBIENT, colorRed, 0);
        gl.glMaterialfv(face, GL2.GL_DIFFUSE, colorRed, 0);
        gl.glMaterialfv(face, GL2.GL_SPECULAR, colorWhite, 0);
        gl.glMateriali(face, GL2.GL_SHININESS, 100);
        gl.glMaterialfv(face, GL2.GL_EMISSION, colorBlack, 0);
    }
    
    
    /////////////////// dibujos /////////////////////////
    ///////////////// Dibuja una Esfera con Luz ///////////////
    public void drawLight(GL2 gl, GLU glu, GLUT glut) {
        setLightSphereMaterial(gl, GL.GL_FRONT);
        gl.glPushMatrix();
        {
            gl.glTranslatef(lightX, lightY, lightZ);
            glut.glutSolidSphere(0.1f, 20, 20);
        }
        gl.glPopMatrix();
    }
    
    private void setLight(GL2 gl) {
        float SHINE_ALL_DIRECTIONS = 1;
        float lightPos[] = {plx, ply, plz, SHINE_ALL_DIRECTIONS};
        float lightAmbient[] = {0.6f, 0.6f, 0.6f, 1.0f};
        float lightDiffuse[] = {0.8f, 0.8f, 0.8f, 1.0f};
        float lightSpecular[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float lmodel_ambient[] = {0.0f, 0.0f, 0.0f, 1.0f};
        float local_view[] = {0.0f};

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        // Set light parameters.
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightSpecular, 0);

        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, local_view, 0);

        // Enable lighting in GL.
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHTING);
        
//        gl.glTranslatef(plx, ply, plz);
//        glut.glutSolidSphere(0.2f, 30, 30);
                
        gl.glLoadIdentity();
        
    }

    @Override
    public void dispose(GLAutoDrawable glad) {

    }
    
    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();  // get the OpenGL 2 graphics context
        
        setCamera(gl);

//        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
//        gl.glMatrixMode(GL2.GL_MODELVIEW);
//        gl.glLoadIdentity();
//
//        gl.glTranslatef(pox, poy, poz);        
//        gl.glRotatef(rotate, 0.0f, 1.0f, 0.0f);
//        gl.glScalef(scale, scale, scale);
//        
//        // ----- Your OpenGL rendering code here (Render a white triangle for testing) -----
//        float material[] = {1.0f, 1.0f, 0.0f, 1.0f};
//
//        gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT);
//        gl.glColor3f(0.8f, 1.0f, 0.4f);
//        
//        gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_AMBIENT_AND_DIFFUSE, material,0);
//        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 100.0f);
//        
//        gl.glEnable(GL2.GL_COLOR_MATERIAL);  
        
        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();
        glu.gluPerspective(fovy, aspect, 0.1, 20.0);
        glu.gluLookAt(this.camX, this.camY, this.camZ, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();  // reset the model-view matrix

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
             
        if (obj3 != null) {
            setSomeYellowMaterial(gl, GL.GL_FRONT);
            obj3.draw(gl);
        }
        
        this.animate(gl, this.glu, this.glut);
        
        gl.glFlush();

    }
    
    private void setCamera(GL2 gl) {
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(pcx, pcy, pcz, pvx, pvy, pvz, 0, 1, 0);

        // Change back to model view matrix.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
        GL2 gl = glad.getGL().getGL2();  // get the OpenGL 2 graphics context

        if (height == 0) {
            height = 1;   // prevent divide by zero
        }
        aspect = (float) width / height;

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // reset projection matrix
        glu.gluPerspective(fovy, aspect, 0.1, 50.0); // fovy, aspect, zNear, zFar

        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity(); // reset
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        int codigo = e.getKeyCode();
        
        //  lightX, lightY, lightZ
        System.out.println("codigo presionado = " + codigo);

        switch (codigo) {
            case KeyEvent.VK_DOWN:
                this.moveLightY(false);
                break;
            case KeyEvent.VK_UP:
                this.moveLightY(true);
                break;
            case KeyEvent.VK_RIGHT:
                this.moveLightX(true);
                break;
            case KeyEvent.VK_LEFT:
                this.moveLightX(false);
                break;
            case KeyEvent.VK_PAGE_UP:
                this.moveLightZ(false);
                break;
            case KeyEvent.VK_PAGE_DOWN:
                this.moveLightZ(true);
                break;

            case KeyEvent.VK_NUMPAD8:
                this.camY += 0.2f;
                break;
            case KeyEvent.VK_NUMPAD2:
                this.camY -= 0.2f;
                break;
            case KeyEvent.VK_NUMPAD6:
                this.camX += 0.2f;
                break;
            case KeyEvent.VK_NUMPAD4:
                this.camX -= 0.2f;
                break;
            case KeyEvent.VK_Z:
                this.camZ += 0.2f;
                break;
            case KeyEvent.VK_A:
                this.camZ -= 0.2f;
                break;
        }
        System.out.println("rotX = " + rotX);
    }
    

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    
}
