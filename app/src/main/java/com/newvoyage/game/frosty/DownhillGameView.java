package com.newvoyage.game.frosty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Phil on 12/18/2015.
 */
public class DownhillGameView extends SurfaceView implements Runnable, SensorEventListener{

    Context context;

    private SharedPreferences prefs;

    // This is our thread
    private Thread gameThread = null;

    // Our SurfaceHolder to lock the surface before we draw our graphics
    private SurfaceHolder ourHolder;

    // A boolean which we will set and unset
    // when the game is running- or not.
    private volatile boolean playing;

    // Game is paused at the start
    private boolean paused = true;

    //vairable that stops players playing exiting the popup dialouge at game over and unpausing the game
    private boolean ableToUnpause;

    //the difficulty
    private int difficulty;
    private String difficultyString;

    //trail that the ship leaves behind, and floats that contain the previous coordinates to draw to
    private Path shipPath1;
    private Path shipPath2;
    private float previousPathY;
    private int pathSwitchCounter;
    private Matrix translateMatrix;

    // A Canvas and a Paint object
    private Canvas canvas;
    private Paint paint;
   private Background backOne;
    private Background backTwo;

    private Tile [][] tileMap;
    //private ArrayList<Tile> tilearray = new ArrayList<Tile>();
    private int rows;
    private int columns;
    private int tileWidth;
    private float tileHeight;

    private int timesPlayed;

    // This variable tracks the game frame rate
    private long fps;

    // This is used to help calculate the fps
    private long timeThisFrame;

    //the speed the background goes past at
    private int speed;
    private int savedSpeed;

    //the distance travelled
    private float distanceTravelled;

    //the state of the background/obstacles being generated
    private int backgroundState;
    private int NONE = 0;
    private int SPARSE=1;
    private int WARNINGSIGNS=2;
    private int DENSE=3;
    private int TUNNEL=4;
    private int[] backgroundStateOrder;//the lineup of the background states for the particular level
    private int levelNo;
    private int screenLengthsTravelled;
   private int activeObs;
    private boolean makeEnemies;
    private int gradient;
    private boolean endGame;


    //the x coordinate of the tunnel obstacle, and width of the tunnel
    private int tunnelX;
    int tunnelWidth;

    //which direction is the tunnel moving?
    private boolean tunnelMovingRight;

    //a sign to tell the user when a danger is approaching
    private WarningSign warningSign;

    //a variable to help calculate the change in direction, and thus the turning speed
    float previousTiltX;

    // The size of the screen in pixels
    private int screenX;
    private int screenY;

    private int pauseButtonHeight;
    private int pauseButtonWidth;

    // The score, and how often it is updated
    int score = 0;
    int scoreInterval=100;
    long lastScoreTime;
    boolean speedBonus;
    boolean playSpeedAnimation;
    boolean turnOnAnimation1;
    private String levelDescription;

    //the denstiy unit, used to standardise speed across devices
    private  int densityUnit;

    // The obstacles, an array with a max size of 70 as we don't want any more than this on screen at any one time, and this is the height of all of them combined
    private Obstacle[] obstacles = new Obstacle[70];

    //obstacle size, not used in obstacle generation, but used in tunnel generation, so need to make sure it's the same
    private int obstacleWidth;

    //int and boolean to check if too many obstacles are still being created, to make sure obstacle generation is not too dense
    private int creatingObstacles;
    private boolean noMoreObstacles;

    //the enemies
    private Enemy[] enemies;
    private boolean startEnemyLevel;

    //enemies bullets
    private Bullet[] enemyBullets = new Bullet[10];
    private int bulletInterval = 3000;
    private int bulletNumber;
    private  long lastBulletTime;
    private boolean fireEnemyBullet;

    // The player's bullet
    private Bullet bullet;

    //A bbbbonus gate that appears sometimes
    private Gate gate;
    private boolean isInvincible;
    private  boolean justBecomeInvincible;
    private long invincibleStartTime;
    private boolean flashingSolid;
    private int flashingTime;
    private long flashingStartTime;

    //the player ship
    private Ship ship;


    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    //to tell various methods if is their first time running this game
    private boolean justStarted;

    //variable for the values from tilting the device
    float tiltx;

    //tells instructional dialog if game is yet to begin
    private  boolean beginGame;
    private boolean display_dialog_on_touch = false;

    //variables for smoothing out the turning
    private static float alpha = 0.5f;
    private static final float SENSITIVITY = 1f/2;

    Handler gameHandler;

  //the animation objects
    private Text textAnimation1;
    private Text textAnimation2;
    private Text textAnimation3;
    private SpritesheetAnimation explosionAnimation1;
    private SpritesheetAnimation explosionAnimation2;
    private SpritesheetAnimation snowboardAnimation;

    //the images!!!
    private Bitmap snowmanPic;
    private Bitmap snowboardPicMaster;
    private Bitmap treePic1;
    private Bitmap treePic2;
    private Bitmap snowball;
    private Bitmap enemyPic;
    private Bitmap signPic;
    private Bitmap backgroundpic1;
    private Bitmap gatePic;
    private  Bitmap explosionPic;
    private Bitmap scorePic;
    private Bitmap speedPic;
    private Bitmap pausePic;


    // When the we initialize (call new()) on gameView
// This special constructor method runs
    public DownhillGameView(Context context, int x, int y, Handler handler) {


        // The next line of code asks the
        // SurfaceView class to set up our object.
        // How kind.
        super(context);

        // Make a globally available copy of the context so we can use it in another method
        this.context = context;

        prefs = context.getSharedPreferences("samoanewzealand", Context.MODE_PRIVATE);

        gameHandler=handler;

        if( prefs.contains("sensitivity")) {
            float tempsens = 0;
            tempsens = prefs.getInt("sensitivity", 0);
            if (tempsens<1){tempsens=1;
            }
            tempsens=5-tempsens;
            tempsens=-tempsens;
            alpha=0.5f +(tempsens/30);

        }else {
           // Log.d("sensitivity", "= normal");
        }

        if( prefs.contains("difficulty")) {
            difficulty = prefs.getInt("difficulty", 0);
        }else {
            difficulty = 1;

        }
        display_dialog_on_touch=false;
        //set the dificulty

        if (difficulty==0){
            difficultyString="Easy";
        }else if (difficulty==1){
            difficultyString="Medium";
        }else if (difficulty==2){
            difficultyString="Hard";
        }
       // Log.d("difficulty", ""+difficultyString);

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;

        senSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);

        //load the tree pictures

        treePic1 = BitmapFactory.decodeResource(getResources(),R.drawable.tree1);
        treePic2 = BitmapFactory.decodeResource(getResources(),R.drawable.tree1);

        //populate the obstacles array
        for(int i = 0; i < obstacles.length; i++){
            if ((i%2)==0) {
                obstacles[i] = new Obstacle(screenX, screenY, i, treePic1);
            }else{
                obstacles[i] = new Obstacle(screenX, screenY, i, treePic2);
            }
        }


        //load the ship picture
        snowmanPic = BitmapFactory.decodeResource(getResources(),R.drawable.snowmanbody);
        snowboardPicMaster = BitmapFactory.decodeResource(getResources(), R.drawable.snowboardspritesheet);


        //load the background pictures
        backgroundpic1 = BitmapFactory.decodeResource(getResources(), R.drawable.tilebackgroundcolours);
        //rezise background picture

        backgroundpic1 = Bitmap.createScaledBitmap(backgroundpic1,
                (int) (screenX),
                (int) (screenY),
                false);

        //set up the two background images so they are on top of each other
        backOne = new Background(0,0,screenX,screenY,context, backgroundpic1);
        backTwo = new Background(0, backOne.getImageHeight(), screenX, screenY, context, backgroundpic1);

       // backgroundTile1= BitmapFactory.decodeResource(getResources(), R.drawable.testtile);

        //load the snowball pic
        snowball = BitmapFactory.decodeResource(getResources(),R.drawable.snowball);

        //load the enemy pic
        enemyPic = BitmapFactory.decodeResource(getResources(),R.drawable.enemy);

        //load the gate pic
        gatePic = BitmapFactory.decodeResource(getResources(),R.drawable.gate);

        //load the sign pic
        signPic = BitmapFactory.decodeResource(getResources(),R.drawable.tunnel);

       explosionPic = BitmapFactory.decodeResource(getResources(),R.drawable.explosionspritesheet);

        pauseButtonHeight=screenY/32;
        pauseButtonWidth=screenY/32;
        scorePic =  BitmapFactory.decodeResource(getResources(),R.drawable.trophyiconsmall);
        scorePic= Bitmap.createScaledBitmap(scorePic, screenY / 32, screenY / 32, true);
        speedPic =  BitmapFactory.decodeResource(getResources(), R.drawable.speediconsmall);
        speedPic= Bitmap.createScaledBitmap(speedPic, screenY / 32, screenY / 32, true);
        pausePic =  BitmapFactory.decodeResource(getResources(), R.drawable.pause);
        pausePic= Bitmap.createScaledBitmap(pausePic, pauseButtonWidth, pauseButtonHeight, true);

        if (difficultyString=="Easy"){
            tunnelWidth=(screenX/2)+(screenX/6);
            enemies=new Enemy[1];
        } else if (difficultyString=="Medium"){
            tunnelWidth=(screenX/2);
            enemies=new Enemy[2];
        } else if (difficultyString=="Hard"){
            tunnelWidth=(screenX/2);
            enemies=new Enemy[3];
        }

        prepareLevel();

    }



    public void prepareLevel(){

         speed = 100;
        distanceTravelled=0;
        backgroundState=NONE;
        backgroundStateOrder = new int[12];
        levelNo = 1;
        screenLengthsTravelled=0;
        creatingObstacles=0;
        noMoreObstacles=false;
        makeEnemies=false;
        startEnemyLevel = false;
        gradient = 10;
        savedSpeed=0;
        //start the first tunnel at a random coordinate
        tunnelX=0;
        tunnelMovingRight=true;
        obstacleWidth=screenX/20;
        activeObs=0;
        bulletNumber=0;
        score=0;
        speedBonus = false;
        playSpeedAnimation = true;
        invincibleStartTime=0;
        shipPath1 = new Path();
        shipPath2 = new Path();
        densityUnit=screenY/100;
        textAnimation1 = new Text(screenX, screenY, 1);
        textAnimation2 = new Text(screenX, screenY, 2);
        textAnimation3 = new Text(screenX, screenY, 4);
        turnOnAnimation1=true;
        levelDescription=new String();
        ableToUnpause=true;
        isInvincible=false;
        justBecomeInvincible=false;
        flashingSolid = false;
        flashingTime=300;
        flashingStartTime=0;
        endGame=false;
        display_dialog_on_touch=false;


        // Make a new player ship
        ship = new Ship(context, screenX, screenY, speed, snowmanPic,snowboardPicMaster, difficulty);


        rows=30;
        columns=90;
        tileWidth=screenX/columns;
        //divide by 5 as only need tilemap to cover around a thrid of screen
        tileHeight=((ship.getCentre().y)/rows);
       // backgroundTile1 = Bitmap.createScaledBitmap(backgroundTile1, tileWidth, tileHeight, true);

        // Initialize Tiles
        tileMap= new Tile[columns][rows];

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {

                Tile t = new Tile(i, j, tileWidth,tileHeight,0, densityUnit,rows);
                tileMap[i][j]=t;


            }
        }

        // Here we will initialize all the game objects

        for (int i = 0; i<obstacles.length; i++){
            obstacles[i].clearObs();
        }

        for(int i = 0; i < enemies.length; i++){
            enemies[i] = new Enemy(screenX, screenY, i, enemyPic);
        }

        // Prepare the players bullet
        bullet = new Bullet(screenY, screenX, snowball,0);

        // Initialize the enemies bullets array
        for(int i = 0; i < enemyBullets.length; i++){
            enemyBullets[i] = new Bullet(screenY, screenX, snowball, difficulty);
        }

        //make a warning sign
        warningSign = new WarningSign(screenX,screenY,signPic);

        //make a gate
        gate = new Gate(screenX,screenY,gatePic);

        //make new animations
        explosionAnimation1=new SpritesheetAnimation(explosionPic,4,200,screenX/6,screenX/6);
        explosionAnimation2=new SpritesheetAnimation(explosionPic,4,200,screenX/6,screenX/6);



        previousTiltX=0;
        justStarted=true;
        beginGame = true;
        setLevel();

        if (prefs.contains("timesPlayedGame")) {
            timesPlayed = prefs.getInt("timesPlayedGame", 0);
            if ((timesPlayed ==5 || timesPlayed == 15 || timesPlayed == 25 || timesPlayed == 35)&& (prefs.getBoolean("reviewed", false)==false)) {
                //post it to the main activity class for downhill game, in order for the alertdialog to be able to run
                gameHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setCancelable(false);

                        TextView textView = new TextView(context);
                        textView.setText(R.string.review_text);
                        textView.setTypeface(Typeface.DEFAULT_BOLD);
                        textView.setPadding(20, 10, 5, 5);
                        textView.setTextSize(1, 20);
                        alert.setCustomTitle(textView);


                        alert.setPositiveButton(R.string.yes_review, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final String appPackageName = context.getPackageName();
                                try {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                                dialog.dismiss();
                                SharedPreferences.Editor gameEdit = prefs.edit();
                                gameEdit.putBoolean("reviewed",true);
                                gameEdit.commit();
                            }
                        });

                        alert.setNegativeButton(R.string.no_review, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();

                                SharedPreferences.Editor gameEdit = prefs.edit();
                                gameEdit.putBoolean("reviewed",false);
                                gameEdit.commit();
                            }
                        });

                        alert.show();
                    }
                });
            }
        } else{
            timesPlayed=0;
        }
    }

    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if(!paused){
                update();
            }

            // Draw the frame
            draw();

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

            if(!paused) {
                if (isInvincible) {
                    if(justBecomeInvincible){
                        invincibleStartTime = System.currentTimeMillis();
                        justBecomeInvincible=false;
                    }
                    if ((startFrameTime-invincibleStartTime)>3000){
                        //make the snowman flash if it's nearly time up
                        if (startFrameTime-flashingStartTime>flashingTime){
                            flashingSolid=!flashingSolid;
                            flashingStartTime=System.currentTimeMillis();
                        }
                    }
                    if ((startFrameTime - invincibleStartTime) > 5000) {
                        // stop being invincible
                        isInvincible = false;
                        flashingSolid=false;
                        textAnimation3.setInactive();
                    }
                }

                if ((startFrameTime - lastBulletTime)> bulletInterval) {
                    // The enemy can fire a bullet again
                    lastBulletTime = System.currentTimeMillis();
                    fireEnemyBullet=true;
                }

                if ((startFrameTime - lastScoreTime)> scoreInterval) {
                    if (speed>600) {
                        score = score + 1;
                    }
                    if (difficulty==0){
                        if (speed > 1900) {
                            //rewarded for going faster!
                            score = score + 5;
                            speedBonus = true;
                        } else {
                            speedBonus = false;
                            //can play the speed bonus animation again once speed has dipped below the bonus speed
                            playSpeedAnimation = true;
                        }

                    }else {
                        if (speed >= 2500) {
                            //rewarded for going faster!
                            score = score + 5;
                            speedBonus = true;
                        } else {
                            speedBonus = false;
                            //can play the speed bonus animation again once speed has dipped below the bonus speed
                            playSpeedAnimation = true;
                        }
                    }

                    lastScoreTime=startFrameTime;
                }
            }

        }
    }

   private void deleteIntersectingObject(Obstacle obs){
        //if it intersects antoher object that is still being generated, delete it
        for (int i = 0; i < obstacles.length; i++){

            if (obs.getClearedBottomY()==false){
                if(obstacles[i].getChecked()==false && obstacles[i].getStatus()==true) {
                    if (RectF.intersects(obs.getRect(), obstacles[i].getRect()) && obstacles[i].getID()-obs.getID()!=0) {   //if it intersects and is not itself
                        obs.setInactive();
                    }
                }
            }
        }
        obs.setChecked(true);
    }

    private void setNoMoreObstacles(){  //makes sure that there are not too many obstacles being generated at one time
        if(creatingObstacles>2){
            noMoreObstacles=true;
        } else{
            noMoreObstacles=false;
        }
    }

   private void generateTunnelX(){
        //set how much the tunnel turns.
       int tunnelTurning;
       if (levelNo<5){
           tunnelTurning=5-difficulty;
       }else if(levelNo>=5 && levelNo<7){
          if(difficulty==0){
              tunnelTurning=4;
          }else {tunnelTurning=3;}
       }else{
           if(difficulty==2){
               tunnelTurning=2;
             }else {
               tunnelTurning = 3;
              }
       }

        if (tunnelMovingRight==true){
            tunnelX=tunnelX+(obstacleWidth/tunnelTurning);
        }else{
            tunnelX=tunnelX-(obstacleWidth/tunnelTurning);
        }
       if((tunnelX+obstacleWidth+tunnelWidth)>screenX){ //if at the right hand side of the screen
           tunnelMovingRight=false;
       }
       if (tunnelX<0){
           tunnelMovingRight=true;
       }
    }

    private void setLevel(){
        if (levelNo==1){
            int[] newOrder = {0,1,1,0,1,1,1,1,1,1,1,1};
            System.arraycopy(newOrder,0,backgroundStateOrder,0,10);
            ship.setGradient(gradient);
             }else if (levelNo==2){  //introduce higher gradient
            int[] newOrder = {0,0,1,1,1,1,1,1,1,1,1,1};
            System.arraycopy(newOrder,0,backgroundStateOrder,0,10);
            gradient = 20;
            ship.setGradient(gradient);
            levelDescription = "Steeper slope!";
        }else if (levelNo==3){  //introduce tunnels
            int[] newOrder = {0,0,2,4,4,2,4,4,4,4,4,4};
            System.arraycopy(newOrder,0,backgroundStateOrder,0,10);
            levelDescription = "Tunnels!";
        }else if (levelNo==4){  //introduce enemies
            int[] newOrder = {0,0,2,4,4,4,1,1,1,1,1,1};
            System.arraycopy(newOrder,0,backgroundStateOrder,0,10);
            makeEnemies = true;
            startEnemyLevel = true;
            levelDescription = "Spaceships!";
        }else if (levelNo==5){  //increase gradient
            int[] newOrder = {0, 0, 2, 4, 4, 4, 4, 4, 4, 4,4,1};
            System.arraycopy(newOrder,0,backgroundStateOrder,0,10);
            levelDescription = "More tunnels!";
        }else if (levelNo==6){  //harder backgrounds on gradient
            int[] newOrder = {0,0,1,3,3,3,3,1,3,3,1,1};
            System.arraycopy(newOrder,0,backgroundStateOrder,0,10);
            levelDescription = "Denser trees!";
        } else if (levelNo==7){  //increase gradient
            int[] newOrder = {0,0,1,1,1,1,2,4,4,1,1,1};
            System.arraycopy(newOrder,0,backgroundStateOrder,0,10);
            gradient = 30;
            ship.setGradient(gradient);
            levelDescription = "Steeper gradient!";
        } else if (levelNo==8){  //harder backgrounds on gradient
            int[] newOrder = {0,0,3,3,3,2,4,4,1,3,3,1};
            System.arraycopy(newOrder, 0, backgroundStateOrder, 0, 10);
            levelDescription = "More trees again!";
        } else if (levelNo==9){  //increase gradient
            int[] newOrder = {0,0,2,4,4,4,4,4,3,3,3,1};
            System.arraycopy(newOrder, 0, backgroundStateOrder, 0, 10);
            gradient=40;
            ship.setGradient(gradient);
            levelDescription = "Steeper gradient!";
        }  else if (levelNo==10){  //harder backgrounds on gradient
            int[] newOrder = {0,0,2,4,4,4,4,3,3,3,3,3};
            System.arraycopy(newOrder, 0, backgroundStateOrder, 0, 10);
            levelDescription = "More trees again!";
        }


    }


    private void update(){

        //calculate when the ship has done a full screen length, and update the background state
        distanceTravelled = distanceTravelled + (densityUnit*(speed/20)) / fps;
        if(screenY-distanceTravelled<0){
            distanceTravelled=0;
            screenLengthsTravelled++;
            if(screenLengthsTravelled>=backgroundStateOrder.length){
                levelNo++;
                setLevel();
                screenLengthsTravelled=0;
            }
            //set the background state to be the int in the corresponding pos in the order of background states for this level

            backgroundState = backgroundStateOrder[screenLengthsTravelled];

           /* Log.d("background","backgroundstate"+backgroundState);
            Log.d("screen","screenlengths"+screenLengthsTravelled);
            Log.d("level","levelNo"+levelNo);*/
        }


        //generate or update obstacles

         int createdSecondTunnelObs = 0;
        creatingObstacles=0;


        for(int i = 0; i < obstacles.length; i++){
            if (obstacles[i].getClearedBottomY()==false && obstacles[i].getStatus()){
                creatingObstacles++;
            }
        }

        boolean createdObs = false;

        for(int i = 0; i < obstacles.length; i++){
            if (obstacles[i].getStatus() == false &&noMoreObstacles==false) {
             //if the obstacle is not active, and not too many obstacles currently being generated...

                //generate obstacles
                if (backgroundState==SPARSE) {  //if sparse...
                    if(activeObs<4) {                                                //if not already dense enough...
                        if (activeObs==0 && !createdObs){       //if no obstacles, then create at least one
                            obstacles[i].create(screenX,screenY);
                            createdObs=true;
                        }else {
                            obstacles[i].createSparse(screenX, screenY);
                        }
                    }
                } else if (backgroundState==WARNINGSIGNS) { //if pre-tunnel...
                    if (activeObs < 4) {
                        if (activeObs==0 && !createdObs){       //if no obstacles, then create at least one
                            obstacles[i].create(screenX,screenY);
                            createdObs=true;
                        }else {
                            obstacles[i].createWarning(screenX, screenY);
                        }
                    }
                } else if (backgroundState==NONE){
                    //do nothing
                }else if (backgroundState==DENSE) {    //if dense...
                        if (activeObs < 8) {
                            if (activeObs==0 && !createdObs){       //if no obstacles, then create at least one
                                obstacles[i].create(screenX,screenY);
                                createdObs=true;
                            }else {
                                obstacles[i].createDense(screenX, screenY);
                            }
                        }
                    } else if (backgroundState==TUNNEL) {                                                       //if tunnel...
                        if (creatingObstacles < 1 && createdSecondTunnelObs < 2) {     //if no obstacles being generated and haven't created a second obstacle
                            generateTunnelX();

                            //if it's the second time, move the other obstacle to the other side of the tunnel
                            if (createdSecondTunnelObs == 0) {
                                obstacles[i].createTunnel(tunnelX, screenY);
                            } else {
                                obstacles[i].createTunnel(tunnelX + tunnelWidth, screenY);
                            }
                            createdSecondTunnelObs++;
                        }
                    }
                //remove obstacles from the mouth of the tunnel if the next background is tunnel and not in a tunnel
                if (screenLengthsTravelled+1<backgroundStateOrder.length) { //prevents an array out of bounds
                    if (backgroundStateOrder[screenLengthsTravelled+1] == TUNNEL && backgroundState != TUNNEL &&
                            obstacles[i].getStatus() == true) {
                        //if the obstacle just created falls within the boundries of the tunnel mouth, set inactive
                        if (obstacles[i].getX() > tunnelX && obstacles[i].getX() < (tunnelX + tunnelWidth)) {
                            obstacles[i].setInactive();
                        }
                    }
                }

            } else {    //code for updating existing obstacles, and checking how many are currently being created.
                obstacles[i].update(fps, speed, screenY);
            }

            deleteIntersectingObject(obstacles[i]);

        }

        //update the warning sign
        if (warningSign.getStatus()==false){
            if (backgroundState==WARNINGSIGNS){
                if (distanceTravelled>screenY/2){
                    warningSign.createWarningSign(tunnelX+(tunnelWidth/2));
                }

            }
        } else {
            warningSign.update(fps,speed);
            if (warningSign.getImpactPointY()<0){
                warningSign.setInactive();
            }
        }

        //update the gate
        if (gate.getStatus()==false && gate.createGate(screenX, gate.getStatus())){
            for (int i = 0; i < obstacles.length; i++){
                if (RectF.intersects(gate.getRect(), obstacles[i].getRect()) && obstacles[i].getStatus()==true) {   //if it intersects and is not itself
                    gate.setInactive();
                }
            }
        } else if (gate.getStatus()){
            gate.update(fps,speed);
            if(gate.getImpactPointY()<0){
                gate.setInactive();
            }
        }


        //update the enemies

        for(int i = 0; i < enemies.length; i++){
            if (enemies[i].getStatus() == false ) {
                //create an enemy if it's not active and are at the right level
                if (makeEnemies==true && backgroundState!=TUNNEL) {
                    enemies[i].create(screenX, screenY, startEnemyLevel);
                }
            } else {    //code for updating existing enemies, and firing bullets
                enemies[i].update(fps, speed, screenY);
                //can it fire a bullet?
                if (fireEnemyBullet){
                    if (enemyBullets[bulletNumber].shoot(enemies[i].getX() + enemies[i].getWidth() / 2, enemies[i].getY(), bullet.UP)) {

                        bulletNumber++;
                        //reset to the first bullet if looped through all the bullets in the array
                        if (bulletNumber >= enemyBullets.length) {
                            bulletNumber = 0;
                        }
                    }
                }
            }
        }
        fireEnemyBullet=false;
        startEnemyLevel=false;

        setNoMoreObstacles();


        activeObs=0;
        // Has the obstacle hit the top of the screen, and is it active?
        for(int i = 0; i < obstacles.length; i++){
            if(obstacles[i].getImpactPointY()<0){
                obstacles[i].setInactive();
            }
            if(obstacles[i].getStatus()){
                activeObs++;
            }
            obstacles[i].setChecked(false);
        }

        // Has the enemy hit the top of the screen, and is it active?
        for(int i = 0; i < enemies.length; i++){
            if(enemies[i].getImpactPointY()<0){
                enemies[i].setInactive();
            }
            if(enemies[i].getStatus()){
                activeObs++;
            }
        }

        // Update the players bullet
        if(bullet.getStatus()){
            bullet.update(fps, screenY);
        }

        // Update all the enemy bullets if active
        for(int i = 0; i < enemyBullets.length; i++){
            if(enemyBullets[i].getStatus()) {
                enemyBullets[i].update(fps, screenY);
            }
        }

        // Has the player's bullet hit the bottom of the screen
        if(bullet.getImpactPointY() >screenY){
            bullet.setInactive();
        }

        //update the ship and speed
        if (!endGame) {
            speed = ship.update(fps, speed);
        }

        //update the background
        backOne.update(speed,fps);
        backTwo.update(speed,fps);

        //check if the ship intersects a gate, and make it temporarily invincible
        if (gate.getRect().contains(ship.getCentre().x,ship.getCentre().y)
                || gate.getRect().contains(ship.getA().x,ship.getA().y)){
            isInvincible=true;
            justBecomeInvincible=true;
        }

        // Has an enemies bullet hit the top or bottom of the screen
        for(int i = 0; i < enemyBullets.length; i++){

            if(enemyBullets[i].getImpactPointY() > screenY || enemyBullets[i].getImpactPointY() <0){
                enemyBullets[i].setInactive();
            }
        }

        // Has the player's bullet hit an enemy
        if(bullet.getStatus()) {
            for (int i = 0; i < enemies.length; i++) {
                if (enemies[i].getStatus()) {
                    if (RectF.intersects(bullet.getRect(), enemies[i].getRect())) {
                        explosionAnimation1.setX(enemies[i].getRect().left);
                        explosionAnimation1.setY(enemies[i].getRect().top);
                        enemies[i].setInactive();
                        bullet.setInactive();
                        score = score + 50;
                        explosionAnimation1.setStopAnimation(false);
                    }
                }
            }
        }


        if (!isInvincible) {
            // Has an enemy bullet hit the ship
            for (int i = 0; i < enemyBullets.length; i++) {
                if (enemyBullets[i].getStatus()) {

                    if (enemyBullets[i].getRect().contains(ship.getA().x, ship.getA().y)) {
                        enemyBullets[i].setInactive();
                        shipExplodes();
                    } else if (enemyBullets[i].getRect().contains(ship.getB().x, ship.getB().y)) {
                        enemyBullets[i].setInactive();
                        shipExplodes();
                    } else if (enemyBullets[i].getRect().contains(ship.getC().x, ship.getC().y)) {
                        enemyBullets[i].setInactive();
                        shipExplodes();
                    } else if (enemyBullets[i].getRect().contains(ship.getD().x, ship.getD().y)) {
                        enemyBullets[i].setInactive();
                        shipExplodes();
                    }
                }
            }


            //check if the ship has hit and obstacle, if it is then game over!
            for (int i = 0; i < obstacles.length; i++) {
                if (obstacles[i].getStatus()) {
                    if (obstacles[i].getRect().contains(ship.getA().x, ship.getA().y)) {
                        shipExplodes();
                    } else if (obstacles[i].getRect().contains(ship.getB().x, ship.getB().y)) {
                        shipExplodes();
                    } else if (obstacles[i].getRect().contains(ship.getC().x, ship.getC().y)) {
                        shipExplodes();
                    } else if (obstacles[i].getRect().contains(ship.getD().x, ship.getD().y)) {
                        shipExplodes();
                    }
                }

            }
        }

        float pathWidth=0;
        float pathHeight = 0;
        float movePathToSide=0;
        if(ship.getSnowboardPos()==0){
            pathWidth=ship.getWidth()/14;
            movePathToSide=-(ship.getWidth()/5);
            pathHeight=tileHeight*8;
        }else if(ship.getSnowboardPos()==1){
            pathWidth=ship.getWidth()/10;
            movePathToSide=-(ship.getWidth()/8);
            pathHeight=tileHeight*6;
        }else if(ship.getSnowboardPos()==2){
            pathWidth=ship.getWidth()/5;
            movePathToSide=(ship.getWidth()/8);
            pathHeight=tileHeight*2;
        }else if(ship.getSnowboardPos()==3){
            pathWidth=ship.getWidth()/10;
            movePathToSide=(ship.getWidth()/7);
            pathHeight=tileHeight*6;
        }else if(ship.getSnowboardPos()==4) {
            pathWidth = ship.getWidth() / 14;
            movePathToSide = (ship.getWidth() /4);
            pathHeight=tileHeight*8;
        }
        float centreRectHeight=ship.getCentre().y-(ship.getHeight()/3);
        RectF centreRect=new RectF(ship.getCentre().x-pathWidth+movePathToSide, centreRectHeight,
                ship.getCentre().x+pathWidth+movePathToSide,ship.getCentre().y-pathHeight);
        int [] columnsArray = new int[columns];

        for (int i = 0; i < tileMap.length; i++) {
            for (int j=0;j<tileMap[1].length;j++) {
                Tile t = (Tile) tileMap[i][j];

                t.update(speed, fps);

                RectF tileRect = new RectF(t.getTileX(), t.getTileY(),
                        t.getTileX() + tileWidth, t.getTileY() + tileHeight);
                if (RectF.intersects(tileRect, centreRect)) {
                    t.setType(1);
                }

               if (t.getType()==0) {
                    //prevent array out of bounds...
                    if (j != 0 && j < tileMap[i].length - 2) {//and detect if there's a gap in rows...
                        if (tileMap[i][j - 1].getType() == 1 && tileMap[i][j + 1].getType() == 1
                                && tileMap[i][j + 2].getType() == 1) {
                            t.setType(1);
                        }
                    }  else if (j==rows-1 && tileMap[i][0].getType()==1 && (tileMap[i][rows-2].getType()==1)
                            && (tileMap[i][rows-3].getType()==1)){
                        t.setType(1);
                    } else if (j==rows-2 && tileMap[i][rows-1].getType()==1 && (tileMap[i][rows-3].getType()==1)
                            && (tileMap[i][rows-4].getType()==1)){
                        t.setType(1);
                    }
                }
                if(t.getType()==1 && t.getTileY()>tileHeight*18){
                    columnsArray[i]=1;
                }
            }
        }

         ArrayList<Integer>missedColumns = detectGaps(columnsArray, columns);


        for (int i =0;i<missedColumns.size();i++) {
            for (int j=0;j<rows;j++){//for each tile in the column -1
                int c =missedColumns.get(i);
                if (tileMap[c+1][j].getType()==1 || tileMap[c-1][j].getType()==1){
                    tileMap[c][j].setType(1);
                }
            }
        }

        //update the animations
        if (textAnimation1.getStatus()){
            textAnimation1.update(fps);
        }
        if (textAnimation2.getStatus()){
            textAnimation2.update(fps);
        }
        if (textAnimation3.getStatus()){
            textAnimation3.update(fps);
        }
        if(!explosionAnimation1.getStopAnimation()) {
            explosionAnimation1.update();
        }
        if (!explosionAnimation2.getStopAnimation()) {
            explosionAnimation2.update();
        }

        //if the game and ship exploding has finished, end the game
        if (endGame && explosionAnimation2.getStopAnimation()){
            lostGame();
        }


    }

  private ArrayList detectGaps(int[] columnsOrRows, int rowOrColumn ){
      ArrayList<Integer>tempMissedRowsOrColumns = new ArrayList<Integer>();
      ArrayList<Integer>missedRowsOrColumns = new ArrayList<Integer>();
      boolean encounterdOne=false;
      boolean encounterdZero=false;
      int lastOneRow = 0;
      for (int i=0;i<rowOrColumn;i++){
          if (columnsOrRows[i]==1){
               lastOneRow = i;
              if(encounterdOne&&encounterdZero){
                  if (tempMissedRowsOrColumns.size()<10) {//safegaurd against it detecting the natural gap that occurs in rows
                      for (int f : tempMissedRowsOrColumns) {
                          missedRowsOrColumns.add(0, f);
                      }
                  }
                  tempMissedRowsOrColumns.clear();
                  encounterdZero=false;
              }
              encounterdOne=true;
          }else if (columnsOrRows[i]==0){
              if(encounterdOne==true){
                  tempMissedRowsOrColumns.add(tempMissedRowsOrColumns.size(),i);
              }
              if (!encounterdZero && encounterdOne){//if it's the first zero row, meaning the one before it is a one...

              }
              encounterdZero=true;
          }
      }
      return missedRowsOrColumns;
  }

    private void draw(){
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background
            canvas.drawBitmap(backOne.getBitmap(), backOne.getX(), backOne.getY(), paint);
            canvas.drawBitmap(backTwo.getBitmap(), backTwo.getX(), backTwo.getY(), paint);

            paint.setColor(Color.argb(255,218,237,249));
            for (int i = 0; i < tileMap.length; i++) {
                for (int j=0; j<tileMap[1].length;j++) {
                    if (tileMap[i][j]!=null) {
                        Tile t = (Tile) tileMap[i][j];
                        if (t.getType() == 1) {
                            canvas.drawRect(t.getTileX(), t.getTileY(), t.getTileX() + tileWidth,
                                    t.getTileY() + tileHeight, paint);
                        }
                    }
                }
            }


            // Choose the brush color for drawing
            paint.setColor(Color.BLACK);

            //draw the warning sign
            if (warningSign.getStatus()) {
                canvas.drawBitmap(warningSign.getBitmap(), warningSign.getX(), warningSign.getY(), paint);
            }
            //draw the obstacles
            for(int i = 0; i < obstacles.length; i++){
                if(obstacles[i].getStatus()) {
                    canvas.drawBitmap(obstacles[i].getBitmap(),obstacles[i].getX(),obstacles[i].getY(), paint);
                }
            }

            //draw the path of the ship


            paint.setStrokeWidth(2*(ship.getWidth()/3));
            paint.setStyle(Paint.Style.STROKE);
            //switch between which path is being drawn, to make a seemless transition bewtween the paths

        /*   if (pathSwitchCounter == 0 || pathSwitchCounter == 1) {
               canvas.drawPath(shipPath1, paint);
           } else if (pathSwitchCounter == 2 || pathSwitchCounter == 3) {
               canvas.drawPath(shipPath2, paint);
           }*/


            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(3);
            paint.setColor(Color.BLACK);

            // Draw the players bullet if active
            if(bullet.getStatus()){
                canvas.drawBitmap(bullet.getBitmap(),bullet.getX(),bullet.getY(),paint);
            }

            // Draw the gate if active
            if(gate.getStatus()){
                canvas.drawBitmap(gate.getBitmap(),gate.getX(),gate.getY(),paint);
            }


            //draw the enemies
            for(int i = 0; i < enemies.length; i++){
                if(enemies[i].getStatus()) {
                    canvas.drawBitmap(enemies[i].getBitmap(),enemies[i].getX(),enemies[i].getY(),paint);
                }
            }


            //draw the ship
            if (!endGame) {
                if (!isInvincible) {
                    canvas.drawBitmap(ship.getSnowboardPic(), ship.getMatrix(), paint);
                    canvas.drawBitmap(ship.getSnowmanPic(), ship.getMatrix(), paint);
                } else {  //if invincible...
                    if (flashingSolid) {
                        canvas.drawBitmap(ship.getSnowboardPic(), ship.getMatrix(), paint);
                        canvas.drawBitmap(ship.getSnowmanPic(), ship.getMatrix(), paint);
                    } else {
                        paint.setAlpha(60);
                        canvas.drawBitmap(ship.getSnowboardPic(), ship.getMatrix(), paint);
                        canvas.drawBitmap(ship.getSnowmanPic(), ship.getMatrix(), paint);
                        paint.setAlpha(255);
                    }
                    if (!textAnimation3.getStatus()) {
                        textAnimation3.startAnimation();
                    }
                    paint.setTextSize(textAnimation3.getSize());
                    paint.setAlpha(textAnimation3.getAlpha());
                    canvas.drawText("Invincibility!", textAnimation3.getX(), textAnimation3.getY(), paint);
                    paint.setAlpha(255);
                }
            }

            // Update all the invader's bullets if active
            for(int i = 0; i < enemyBullets.length; i++){
                if(enemyBullets[i].getStatus()) {
                    canvas.drawBitmap(enemyBullets[i].getBitmap(),enemyBullets[i].getX(),enemyBullets[i].getY(),paint);
                }
            }

            //draw the explosion for the invaders
            if (!explosionAnimation1.getStopAnimation()){
                canvas.drawBitmap(explosionAnimation1.getSpritesheetBitmap(),explosionAnimation1.getFrameToDraw(),
                explosionAnimation1.getWhereToDraw(),paint);
            }

            //draw the explosion for the ship
            if (!explosionAnimation2.getStopAnimation()){
                canvas.drawBitmap(explosionAnimation2.getSpritesheetBitmap(),explosionAnimation2.getFrameToDraw(),
                        explosionAnimation2.getWhereToDraw(),paint);
            }


            // Draw the score
            // Change the brush color
            paint.setColor(Color.argb(170, 17, 104, 203));
            paint.setTextSize(screenY / 38);
            canvas.drawBitmap(scorePic, screenY / 32, (screenY / 40), paint);
            canvas.drawText(" " + score, screenY / 16, (screenY / 20), paint);
            canvas.drawBitmap(speedPic, screenX - screenX / 3, (screenY / 40), paint);
            canvas.drawText(" " + speed, (screenX - screenX / 3) + (screenY / 32), (screenY / 20), paint);
            canvas.drawBitmap(pausePic, screenX / 2, (screenY / 40), paint);

            //display instructions for touching to play game
            paint.setColor(Color.BLACK);
            if (beginGame == true && paused == true){
                canvas.drawText("Level one", (screenX/3),(screenY/2), paint);
                canvas.drawText("Press anywhere on the screen to start", (screenX/6),(screenY/2+50), paint);

            }

            //draw next level instructions
            if (levelNo!=1) {
                if (screenLengthsTravelled == 0) {
                    //only draw while the animation is active
                    if (textAnimation1.getStatus()==false){ //if it's not started, start the animation
                        textAnimation1.startAnimation();
                    }
                    paint.setTextSize(textAnimation1.getSize());
                    canvas.drawText("Level cleared!", textAnimation1.getX(), textAnimation1.getY(), paint);
                    turnOnAnimation1 = true;
                }
                if (screenLengthsTravelled == 1) {
                    if (turnOnAnimation1) {  //if it's the first time here, turn on the animation
                        textAnimation1.setInactive();
                        textAnimation1.startAnimation();
                    }
                    turnOnAnimation1 = false;
                    paint.setTextSize(textAnimation1.getSize());
                    canvas.drawText("Level " + levelNo + ":",
                            textAnimation1.getX(), textAnimation1.getY(), paint);
                    canvas.drawText(levelDescription,
                            textAnimation1.getX(), textAnimation1.getY()+(screenY/8), paint);
                }
                if (screenLengthsTravelled == 2){
                    textAnimation1.setInactive();
                }
            }
            //draw the text for bonus points for speed
            if (speedBonus){
                //only draw while the animation is active
                if (playSpeedAnimation){ //if it's not started, start the animation
                    textAnimation2.startAnimation();
                    playSpeedAnimation=false;
                }
            }
            if (textAnimation2.getStatus()) {
                paint.setTextSize(textAnimation2.getSize());
                paint.setAlpha(textAnimation2.getAlpha());
                canvas.drawText("Speed bonus!", textAnimation2.getX(), textAnimation2.getY(), paint);
            }
            paint.setAlpha(255);
            paint.setTextSize(screenY / 30);

            if (!beginGame&&paused&&ableToUnpause){
                canvas.drawText("Paused, touch to resume",screenX/8,screenY/2,paint);
            }

            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }

    }

    protected void shipExplodes(){
        explosionAnimation2.setStopAnimation(false);
        explosionAnimation2.setX(ship.getB().x);
        explosionAnimation2.setY(ship.getB().y);
        endGame=true;
        speed=0;
    }

    protected void lostGame() {
        timesPlayed++;
        SharedPreferences.Editor gameEdit = prefs.edit();
        gameEdit.putInt("timesPlayedGame",timesPlayed);
        gameEdit.commit();

        paused = true;
        ableToUnpause=false;
         final boolean beatHighScore = saveHighScore();
        //call the method in the main activity to display the end of game screen
        ((DownhillGameActivity) getContext()).callGameOverScreen(score, beatHighScore);

    }

    public void shareIt() {
    //sharing implementation here
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Look, I scored some points on a game. Wow.");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I just got "+ score + " points on Frosty, a super fun snowboarding game. " +
                "Think you can do better? Download Frosty today at http://play.google.com/store/apps/details?id=com.newvoyage.game.frosty");
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public boolean saveHighScore() {
        SharedPreferences scorePrefs = context.getSharedPreferences(difficultyString, Context.MODE_PRIVATE);
        int [] scoreList = new int[10];
        int scorePos = 0;
        boolean newHighScore=false;
        int savedPastScore=0;
        SharedPreferences.Editor edit = scorePrefs.edit();
        for (int pastScore : scoreList){
            String scorePosString = "score"+Integer.toString(scorePos);
            pastScore = scorePrefs.getInt(scorePosString, 0);
            if (score > pastScore && newHighScore==false) {
                savedPastScore=pastScore;
                //edit the current position in the array
                edit.putInt(scorePosString, score);
                edit.commit();
                newHighScore = true;
            } else if (newHighScore){//if a new high score has been reached, and the rest of the list needs to be shifted down...
                edit.putInt(scorePosString, savedPastScore);
                edit.commit();
                savedPastScore=pastScore;
            }
            scorePos++;
        }

       return newHighScore;
    }


    public void pause() {
        senSensorManager.unregisterListener(this);
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
        paused=true;

    }

    public void resume() {
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_GAME);
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:
                if (ableToUnpause) {
                    paused=false;
                }
                beginGame = false;

                bullet.shoot (ship.getCentre().x,(screenY/8),bullet.DOWN);

                if (display_dialog_on_touch && explosionAnimation2.getStopAnimation() ){
                    lostGame();
                }

                if ((motionEvent.getX()>(screenX/2)&&motionEvent.getX()<((screenX/2)+pauseButtonWidth))
                        &&(motionEvent.getY()<(screenY/40)+pauseButtonHeight)){ //if the pause button is pressed...
                    paused=true;
                }

        }

        return true;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
       // Log.i("changed", "mSensorEventListener.onAccuracyChanged()");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(justStarted) {
             tiltx = event.values[0];
        }
        justStarted = false;
        tiltx = tiltx + alpha * (event.values[0] - tiltx);
        tiltx = previousTiltX-tiltx*SENSITIVITY;
        previousTiltX=tiltx;

        float degreeX = (tiltx/6)*90;

        float newShipAngle;
        if (degreeX<0){
            newShipAngle=90+Math.abs(degreeX);
        }else{
            newShipAngle=90-degreeX;
        }
        if (newShipAngle>180){
            newShipAngle=180;
        }else if (newShipAngle<0){
            newShipAngle=0;
        }

        //set the new angle of the ship to be the angle the device is held at

        ship.setFacingAngle(newShipAngle);


    }



}
