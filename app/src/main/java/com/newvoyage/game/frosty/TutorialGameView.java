package com.newvoyage.game.frosty;

/**
 * Created by phil on 4/21/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;


/**
 * Created by Phil on 12/18/2015.
 */
public class TutorialGameView extends SurfaceView implements Runnable, SensorEventListener {

    Context context;

    // This is our thread
    private Thread gameThread = null;

    // Our SurfaceHolder to lock the surface before we draw our graphics
    private SurfaceHolder ourHolder;

    // A boolean which we will set and unset
    // when the game is running- or not.
    private volatile boolean playing;

    // Game is paused at the start
    private boolean paused = true;

    private boolean turnedOffGameText;

    //vairable that stops players playing exiting the popup dialouge at game over and unpausing the game
    private boolean ableToUnpause;

    // A Canvas and a Paint object
    private Canvas canvas;
    private Paint paint;
    private Background backOne;
    private Background backTwo;

    //loads the animations
    private SpritesheetAnimation explosionAnimation1;
    private SpritesheetAnimation explosionAnimation2;
    private SpritesheetAnimation snowboardAnimation;

    // This variable tracks the game frame rate
    private long fps;

    // This is used to help calculate the fps
    private long timeThisFrame;

    //the speed the background goes past at
    private int speed;

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
    private boolean makeGate;

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

    private boolean pausedLastUpdate = false;
    private boolean cueNextText;
    private boolean firstUpdateCycle;

    // The score, and how often it is updated
    int score = 0;
    int scoreInterval=100;
    long lastScoreTime;
    boolean speedBonus;
    boolean playSpeedAnimation;
    boolean turnOnAnimation1;
    private String levelDescription;
    private long startSpeedTime;
    private boolean justStartedSpeed;
    private boolean endGame;

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
    private Enemy[] enemies = new Enemy[2];
    private boolean startEnemyLevel;
   private boolean createdEnemies = false;

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

    private boolean canStartNextAnimation;

    //variable for the values from tilting the device
    float tiltx;

    //tells instructional dialog if game is yet to begin
    private  boolean beginGame;
    private boolean display_dialog_on_touch = false;

    //variables for smoothing out the turning
    private static float alpha = 0.5f;
    private static final float SENSITIVITY = 1f/2;
    private SharedPreferences prefs;

    //the text animation objects
    private Text textAnimation1;
    private Text textAnimation2;
    private Text textAnimation3;

    //the path tiles
    private Tile [][] tileMap;
    //private ArrayList<Tile> tilearray = new ArrayList<Tile>();
    private int rows;
    private int columns;
    private int tileWidth;
    private float tileHeight;

    //the images!!!
    private Bitmap snowmanPic;
    private Bitmap snowboardPicMaster;
    private Bitmap treePic1;
    private Bitmap treePic2;
    private Bitmap snowball;
    private Bitmap enemyPic;
    private Bitmap signPic;
    private  Bitmap explosionPic;
    private Bitmap backgroundpic1;
    private Bitmap scorePic;
    private Bitmap speedPic;
    private Bitmap gatePic;
    private Bitmap pausePic;


    // When the we initialize (call new()) on gameView
// This special constructor method runs
    public TutorialGameView(Context context, int x, int y, Handler handler) {

        // The next line of code asks the
        // SurfaceView class to set up our object.
        // How kind.
        super(context);

        // Make a globally available copy of the context so we can use it in another method
        this.context = context;

        prefs = context.getSharedPreferences("samoanewzealand", Context.MODE_PRIVATE);

        if( prefs.contains("sensitivity")) {
            float tempsens = 0;
            tempsens = prefs.getInt("sensitivity", 0);
            if (tempsens<1){tempsens=1;
            }
            tempsens=5-tempsens;
            tempsens=-tempsens;
            alpha=0.5f +(tempsens/30);

        }else {
            Log.d("sensitivity", "= normal");
        }

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;

        senSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);

        //load the tree pictures

        treePic1 = BitmapFactory.decodeResource(getResources(), R.drawable.tree1);
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
        backgroundpic1 = BitmapFactory.decodeResource(getResources(),R.drawable.tilebackgroundcolours);
        //rezise background picture

        backgroundpic1 = Bitmap.createScaledBitmap(backgroundpic1,
                (int) (screenX),
                (int) (screenY),
                false);

        //set up the two background images so they are on top of each other
        backOne = new Background(0,0,screenX,screenY,context, backgroundpic1);
        backTwo = new Background(0, backOne.getImageHeight(), screenX, screenY, context, backgroundpic1);

        //load the snowball pic
        snowball = BitmapFactory.decodeResource(getResources(),R.drawable.snowball);

        //load the enemy pic
        enemyPic = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);

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


        prepareLevel();

    }



    public void prepareLevel(){

        speed = 100;
        distanceTravelled=0;
        backgroundState=NONE;
        backgroundStateOrder = new int[10];
        levelNo = 1;
        screenLengthsTravelled=0;
        creatingObstacles=0;
        noMoreObstacles=false;
        makeEnemies=false;
        invincibleStartTime=0;
        isInvincible=false;
        justBecomeInvincible=false;
        startEnemyLevel = false;
        gradient = 10;
        //start the first tunnel at a random coordinate
        tunnelX=0;
        tunnelMovingRight=true;
        obstacleWidth=screenX/20;
        activeObs=0;
        bulletNumber=0;
        score=0;
        speedBonus = false;
        playSpeedAnimation = true;
        densityUnit=screenY/100;
        textAnimation1 = new Text(screenX, screenY, 1);
        textAnimation2 = new Text(screenX, screenY, 2);
        textAnimation3 = new Text(screenX, screenY,4);
        turnOnAnimation1=true;
        levelDescription=new String();
        ableToUnpause=true;
        display_dialog_on_touch=false;
        flashingSolid = false;
        flashingTime=300;
        flashingStartTime=0;
        makeGate=false;
        tunnelWidth=(screenX/2)+(screenX/6);
        canStartNextAnimation = true;
        cueNextText = true;
        turnedOffGameText=true;

        // Here we will initialize all the game objects

        for (int i = 0; i<obstacles.length; i++){
            obstacles[i].clearObs();
        }

        for(int i = 0; i < enemies.length; i++){
            enemies[i] = new Enemy(screenX, screenY, i, enemyPic);
        }

        // Prepare the players bullet
        bullet = new Bullet(screenY, screenX, snowball, 0);

        // Initialize the enemies bullets array
        for(int i = 0; i < enemyBullets.length; i++){
            enemyBullets[i] = new Bullet(screenY, screenX, snowball, 0);
        }

        //make a warning sign
        warningSign = new WarningSign(screenX,screenY,signPic);


        // Make a new player ship
        ship = new Ship(context, screenX, screenY, speed, snowmanPic,snowboardPicMaster,0);

        //make a gate
        gate = new Gate(screenX,screenY,gatePic);


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
        //make new animations
        explosionAnimation1=new SpritesheetAnimation(explosionPic,4,200,screenX/6,screenX/6);
        explosionAnimation2=new SpritesheetAnimation(explosionPic,4,200,screenX/6,screenX/6);


        justStartedSpeed = true;

        previousTiltX=0;
        justStarted=true;
        beginGame = true;
        endGame=false;
        setLevel();
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
                    score=score+1;
                    if(speed>2500){
                        //rewarded for going faster!
                        score=score+5;
                        speedBonus = true;
                    } else{
                        speedBonus = false;
                        //can play the speed bonus animation again once speed has dipped below the bonus speed
                        playSpeedAnimation = true;
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

        if (tunnelMovingRight==true){
            tunnelX=tunnelX+(obstacleWidth/4);
        }else{
            tunnelX=tunnelX-(obstacleWidth/4);
        }
        if((tunnelX+obstacleWidth+tunnelWidth)>screenX){ //if at the right hand side of the screen
            tunnelMovingRight=false;
        }
        if (tunnelX<0){
            tunnelMovingRight=true;
        }
    }

    private void setLevel(){
        screenLengthsTravelled=0;

        if (levelNo==1){
            ship.setGradient(gradient);
            levelDescription = "Tilt your device to the left to turn left!";
        }else if (levelNo==2){
              levelDescription = "Great! Now tilt your device to the right to turn right!";
        } else if (levelNo==3){
            levelDescription = "Notice how turning slows you down? Try to go as slow as you can by turning";
        }else if (levelNo==4){
            levelDescription = "Good job! Now go as fast as you can by travelling in a straight line!";
        }else if (levelNo==5){
            levelDescription = "Now let's see if you can steer around these trees. Go through the flags to gain temporary invincibility!";
        }else if (levelNo==6){
            levelDescription = "Cool. Time for a tunnel! Remember, the faster you go, the more points you get.";
        }else if (levelNo==7){
            startEnemyLevel = true;
            levelDescription = "Touch the screen to shoot the spaceships!";
        }else if (levelNo==8){
            makeEnemies = false;
            lostGame();
        }
        if (!beginGame) {
            if (canStartNextAnimation) {
                ((TutorialActivity) getContext()).callGameText(beginGame, levelDescription);
                turnedOffGameText=false;
            } else {
                ((TutorialActivity) getContext()).turnOffGameText();
                cueNextText = true;
                turnedOffGameText=true;
            }
        }
    }


    private void update(){

        //calculate when the ship has done a full screen length, and update the background state

        if (levelNo==6) {   //if it's the tunnel part of the tutorial...
            distanceTravelled = distanceTravelled + (densityUnit * (speed / 20)) / fps;
            if (screenY - distanceTravelled < 0) {
                distanceTravelled = 0;
                screenLengthsTravelled++;
                if (screenLengthsTravelled >= 10) {

                    levelNo++;
                    setLevel();
                    screenLengthsTravelled = 0;
                }

                //set the background state to empty at the beginning to make time to prepare for the tunnel
                if(screenLengthsTravelled>2) {
                    backgroundState = TUNNEL;
                }//stop the tunnel to make time to prepare for enemies
                else if (screenLengthsTravelled>=8){
                    backgroundState=NONE;
                } else if (screenLengthsTravelled<2){
                    backgroundState=SPARSE;
                }

              /*  Log.d("background", "backgroundstate" + backgroundState);
                Log.d("screen", "screenlengths" + screenLengthsTravelled);
                Log.d("level", "levelNo" + levelNo);*/
            }
        }

        if (levelNo==1){
            updateDistanceTravelled();
            if (ship.getCentre().x<screenX/6){
                levelNo++;
                setLevel();
            }
          //  Log.d("level", "levelNo" + levelNo);
        } else if (levelNo==2){
            updateDistanceTravelled();
            if (ship.getCentre().x>(screenX-screenX/6)){
                levelNo++;
                setLevel();
            }
          //  Log.d("level", "levelNo" + levelNo);
        }else if (levelNo==3){
            updateDistanceTravelled();
            //if the player goes below a certain speed for a satisfactory amount of time, they have
            //passed this stage of the tutorial
            long timeSlowHappening = 0;
            if (speed<1000){
                if (justStartedSpeed){
                    startSpeedTime = System.currentTimeMillis();
                    justStartedSpeed=false;
                }else {
                    timeSlowHappening = System.currentTimeMillis() - startSpeedTime;
                    if (timeSlowHappening>3000) {
                     //   Log.d("time", ""+ timeSlowHappening);
                        levelNo++;
                        setLevel();
                    }
                }
            }else{
                justStartedSpeed=true;
            }
           // Log.d("level", "levelNo" + levelNo);
        }else if (levelNo==4){
            updateDistanceTravelled();
            //if the player goes above a certain speed for a satisfactory amount of time, they have
            //passed this stage of the tutorial
            long timeFastHappening = 0;
            if (speed>1700){
                if (justStartedSpeed){
                    startSpeedTime = System.currentTimeMillis();
                    justStartedSpeed=false;
                }else {
                    timeFastHappening = System.currentTimeMillis() - startSpeedTime;
                    if (timeFastHappening>2000) {
                     //   Log.d("time", ""+ timeFastHappening);
                        levelNo++;
                        setLevel();
                    }
                }
            }else{
                justStartedSpeed=true;
            }
           // Log.d("level", "levelNo" + levelNo);
        }else if (levelNo == 5){
            if (isInvincible){
                backgroundState=DENSE;
            }else {
                backgroundState = SPARSE;
            }
            distanceTravelled = distanceTravelled + (densityUnit * (speed / 20)) / fps;
            if (screenY - distanceTravelled < 0) {
                distanceTravelled = 0;
                screenLengthsTravelled++;
                if (screenLengthsTravelled<5) {
                    makeGate = true;
                }
                if (screenLengthsTravelled>6){
                    levelNo++;
                    setLevel();
                }
            }

        }
        else if (levelNo==7){  //when both the enemies are dead, they player has completed the tutorial
            int deadEnemies=0;
            distanceTravelled = distanceTravelled + (densityUnit * (speed / 20)) / fps;
            if (screenY - distanceTravelled < 0) {
                distanceTravelled = 0;
                screenLengthsTravelled++;
            }
            if (screenLengthsTravelled>1) {
                makeEnemies = true;
            }
            if(!startEnemyLevel && screenLengthsTravelled>3) {
                for (Enemy e : enemies) {
                    if (e.getStatus() == false) {
                        deadEnemies++;
                    }
                }
                if (deadEnemies == enemies.length) {
                    levelNo++;
                    setLevel();
                }
            }

            backgroundState=NONE;
          //  Log.d("level", "levelNo" + levelNo);
        }



        //generate or update obstacles

        int createdSecondTunnelObs = 0;
        creatingObstacles=0;


        for(int i = 0; i < obstacles.length; i++){
            if (obstacles[i].getClearedBottomY()==false && obstacles[i].getStatus()){
                creatingObstacles++;
            }
        }



        for(int i = 0; i < obstacles.length; i++){
            if (obstacles[i].getStatus() == false &&noMoreObstacles==false) {
                //if the obstacle is not active, and not too many obstacles currently being generated...

                //generate obstacles
                if (backgroundState==SPARSE) {  //if sparse...
                    if(activeObs<4) {                                                //if not already dense enough...
                        obstacles[i].createSparse(screenX, screenY);
                    }
                } else if (backgroundState==WARNINGSIGNS) { //if pre-tunnel...
                    if (activeObs < 4) {
                        obstacles[i].createWarning(screenX, screenY);
                    }
                } else if (backgroundState==NONE){ //if pre-dense...
                    //do nothing
                }else if (backgroundState==DENSE) {    //if dense...
                    if (activeObs < 8) {
                        obstacles[i].createDense(screenX, screenY);
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
            if (levelNo==5&&screenLengthsTravelled==1){
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
        if (gate.getStatus()==false && makeGate){
            gate.createGate(screenX, makeGate);
            for (int i = 0; i < obstacles.length; i++){
                if (RectF.intersects(gate.getRect(), obstacles[i].getRect()) && obstacles[i].getStatus()==true) {   //if it intersects and is not itself
                    gate.setInactive();
                }
            }
            makeGate=false;
        } else if (gate.getStatus()){
            gate.update(fps,speed);
            if(gate.getImpactPointY()<0){
                gate.setInactive();
            }
        }

        //update the enemies

        for(int i = 0; i < enemies.length; i++){
            if (enemies[i].getStatus() == false &&makeEnemies==true) {
                //create an enemy if it's not active and are at the right level
                enemies[i].create(screenX, screenY, startEnemyLevel);
                createdEnemies=true;
            } else if (enemies[i].getStatus() == true &&makeEnemies==true){    //code for updating existing enemies, and firing bullets
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
        if (createdEnemies) {
            startEnemyLevel = false;
        }

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

        // Has an enemy bullet hit the ship
        if (!isInvincible) {
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

        //check if the ship has hit and obstacle, if it is then game over!
        if (!isInvincible) {
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

        ArrayList<Integer> missedColumns = detectGaps(columnsArray, columns);


        for (int i =0;i<missedColumns.size();i++) {
            for (int j=0;j<rows;j++){//for each tile in the column -1
                int c =missedColumns.get(i);
                if (tileMap[c+1][j].getType()==1 || tileMap[c-1][j].getType()==1){
                    tileMap[c][j].setType(1);
                }
            }
        }

        //update the text
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

   private void updateDistanceTravelled(){
        distanceTravelled = distanceTravelled + (densityUnit * (speed / 20)) / fps;
        if (screenY - distanceTravelled < 0) {
            distanceTravelled = 0;
            screenLengthsTravelled++;
        }
    }

    private void draw(){
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();


            // Draw the background
            canvas.drawBitmap(backOne.getBitmap(), backOne.getX(), backOne.getY(), paint);
            canvas.drawBitmap(backTwo.getBitmap(), backTwo.getX(), backTwo.getY(), paint);

            // Choose the brush color for drawing
            paint.setColor(Color.BLACK);

            //draw the obstacles
            for(int i = 0; i < obstacles.length; i++){
                if(obstacles[i].getStatus()) {
                    canvas.drawBitmap(obstacles[i].getBitmap(),obstacles[i].getX(),obstacles[i].getY(), paint);
                }
            }

            //draw the path of the ship

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(255, 218, 237, 249));
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

            //draw the warning sign
            if (warningSign.getStatus()) {
                canvas.drawBitmap(warningSign.getBitmap(), warningSign.getX(), warningSign.getY(), paint);
            }

            //draw the ship
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
            canvas.drawText(" " + speed, (screenX-screenX/3)+(screenY/32), (screenY/20), paint);
            canvas.drawBitmap(pausePic, screenX / 2, (screenY / 40), paint);



            //display instructions for touching to play game
            paint.setColor(Color.BLACK);
            if (beginGame == true && paused == true){
                ((TutorialActivity) getContext()).callGameText(beginGame,"Press anywhere on the screen to start");
                turnedOffGameText=false;
            }

          //draw next level instructions
            if (paused==false) {
                if (cueNextText  && canStartNextAnimation) {
                    ((TutorialActivity) getContext()).callGameText(beginGame, levelDescription);
                    turnedOffGameText = false;
                    canStartNextAnimation = false;
                    cueNextText=false;
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
               ((TutorialActivity) getContext()).setGameTextFromGameView("Game paused, touch to continue.");
                pausedLastUpdate=true;
            } else if (pausedLastUpdate && !beginGame && !paused){
               ((TutorialActivity) getContext()).setGameTextFromGameView(levelDescription);
                pausedLastUpdate=false;
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
        paused = true;
        ableToUnpause=false;
        //if the next level text is displaying....
        ((TutorialActivity) getContext()).turnOffGameText();
        turnedOffGameText=true;
        //call the method in the main activity to display the end of game screen
        if (levelNo!=8) {
            ((TutorialActivity) getContext()).callGameOverScreen();
        } else{//if finished tutorial
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("notFirstPlaythru", true);
            edit.commit();
            ((TutorialActivity) getContext()).callFinishedTutorialScreen();
        }

    }

    public void setCanStartNextAnimation(boolean animationFinished){
        if (animationFinished){
            canStartNextAnimation=true;
        }
    }



    public void pause() {
        senSensorManager.unregisterListener(this);
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
          //  Log.e("Error:", "joining thread");
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
                    paused = false;
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
     //   Log.i("changed", "mSensorEventListener.onAccuracyChanged()");
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

        float degreeX = (tiltx/7)*90;

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

