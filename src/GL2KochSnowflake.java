import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL.GL_LINE_SMOOTH;
import static com.jogamp.opengl.GL.GL_NICEST;

/**
 * A program that creates and draws a Koch Snowflake. It also calculates the number of segments that make up the
 * snowflake, the total length and the total area all assuming side length is equal to 1.
 *
 */
public class GL2KochSnowflake extends Frame implements GLEventListener{

    static {
        GLProfile.initSingleton( );
    }
    //Instance aux vars
    GLProfile glprofile=null;  //Profile
    GLCapabilities glcapabilities=null;  //Capabilities
    GLCanvas glcanvas=null; //Canvas

    //Constructor
    public GL2KochSnowflake() {
        super("GL2KochSnowflake");
        glprofile = GLProfile.getDefault();
        glcapabilities = new GLCapabilities( glprofile );
        glcanvas = new GLCanvas( glcapabilities );

        glcanvas.addGLEventListener( this);

        add( glcanvas );
        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                remove( glcanvas );
                dispose();
                System.exit( 0 );
            }
        });

        // Resolution must have the same width and height or it skews
        setSize( 2000, 2000 );
        setVisible( true );
    }

    public static void main( String [] args ) {
        new GL2KochSnowflake();
    }

    //Implementing GLEventListener methods

    @Override
    public void init( GLAutoDrawable glautodrawable ) {
        //System.out.println("Entering init();");
        GL2 gl = glautodrawable.getGL().getGL2();
        gl.glClearColor(1f, 1f, 1f, 0f); //set to non-transparent black
    }

    @Override
    public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
        //System.out.println("Entering reshape()");
        //Get the context
        GL2 gl2=glautodrawable.getGL().getGL2();
        //Set up projection
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        gl2.glOrtho(-2,2,-2,2,0,2);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();

    }

    @Override
    public void display( GLAutoDrawable glautodrawable ) {
        //System.out.println("Entering display");
        //We always need a context to draw
        GL2 gl = glautodrawable.getGL().getGL2();
        gl.glClearColor(1.f, 1.f, 1.f, 1.f);
        gl.glEnable(GL_BLEND);
        gl.glEnable(GL_LINE_SMOOTH);
        gl.glHint(GL_LINE_SMOOTH,GL_NICEST);

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        gl.glColor3f(0.0f,0.0f,0.0f);

        //Main loop
        gl.glBegin(GL.GL_LINES);

        // Create the Koch Snowflake using the renderer and depth specified
        Snowflake(gl, 10);

        gl.glEnd();
        gl.glFlush();

    }
    @Override
    public void dispose( GLAutoDrawable glautodrawable ) {
    }

    /*
        This is used to call the Koch Snowflake compute and draw function sending in the depth
        and initial coordinates of each point along with the GL2 object.
        It also calls the calculations function.
        It take in a GL2 object gl and a depth specifying the number of iterations for the fractal.
     */
    public void Snowflake(GL2 gl, int depth){
        createBump(gl, 1f, 0f, -1f, 0f, depth);
        createBump(gl, 0f, 1.732f, 1f, 0f, depth);
        createBump(gl,  -1f, 0f, 0f, 1.732f, depth);

        calculations();
    }

    /*
        This draws the bump and calculates the new points necessary to create the fractal. It takes in the
        GL2 object, the first point, the second point and the depth. It assumes that points will be input from
        left side to right side.
     */
    private void createBump(GL2 gl, float x1, float y1, float x2, float y2, int depth) {
        if (depth <= 0) {
            gl.glBegin(GL.GL_LINES);
            gl.glVertex2f(x1, y1);
            gl.glVertex2f(x2, y2);
            gl.glEnd();
        }
        else if (depth > 0){

            float a_x = x1, a_y = y1;
            float b_x = x1 + (1.0f/3.0f)*(x2 - x1), b_y = y1 + (1.0f/3.0f)*(y2 - y1);
            float c_x = ((1.0f/2.0f) * (x1 + x2)) + ((float)(Math.sqrt(3.0f)/6.0f) * (y1 - y2));
            float c_y = ((1.0f/2.0f) * (y1 + y2)) + ((float)(Math.sqrt(3.0f)/6.0f) * (x2 - x1));
            float d_x = x2 - (1.0f/3.0f)*(x2 - x1), d_y = y2 - (1.0f/3.0f)*(y2 - y1);
            float e_x = x2, e_y = y2;

            createBump(gl, a_x, a_y, b_x, b_y, depth-1);
            createBump(gl, b_x, b_y, c_x, c_y, depth-1);
            createBump(gl, c_x, c_y, d_x, d_y, depth-1);
            createBump(gl, d_x, d_y, e_x, e_y, depth-1);
        }
    }
    /*
        Calculates the number of line segments, perimeter and area of the Koch Curve assuming it is an equilateral
        triangle where each side has length 1.
     */
    public void calculations(){

        for(int depth = 0; depth <= 10; depth++){
            float lineSegs = 3*(int)Math.pow(4, depth);
            System.out.print(lineSegs + " ");

        }
        System.out.println();
        for(int depth = 0; depth <= 10; depth++){
            float length = 3 * (float)Math.pow((4f/3f), depth);
            System.out.print(length + " ");

        }
        System.out.println();

        for(int depth = 0; depth <= 10; depth++){
            float area = (1f/5f) * (8f - 3f * (float)Math.pow(( 4f / 9f), depth));
            System.out.print(area + " ");

        }
//      System.out.println("The number of line segments in a Koch Snowflake with depth " + depth + " is: " + lineSegs);
//      System.out.println("The perimeter of a Koch Snowflake with depth " + depth + " is: " + length);
//      System.out.println("The area of a Koch Snowflake with depth " + depth + " is: " + area);
    }

}