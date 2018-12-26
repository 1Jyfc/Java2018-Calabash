package other;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import battleArray.*;
import creatures.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Field {

    private final int MAX_X = 14;
    private final int MAX_Y = 7;
    private final int IMAGE_SIZE = 100;
    private final int OFFSET_X = 50;
    private final int OFFSET_Y = 25;
    private final int MAX_TIME = 119;

    private baseArray yourArray;
    private baseArray enemyArray;
    private ArrayList<ArrayList<Creature>> creatureList;
    private ArrayList<ArrayList<Integer>> tombList;
    private ArrayList<ArrayList<Bullet>> bulletList;

    private ArrayList<Calabash> calabashes;
    private Grandpa grandpa;
    private Scorpion scorpion;
    private Snake snake;
    private ArrayList<Minion> minions;

    private Image blood = new Image("/image/blood.png");

    @FXML
    private Canvas battleField;
    @FXML
    private Accordion mainAccordion;
    @FXML
    private Label label1;
    @FXML
    private Label label2;
    @FXML
    private Label label3;
    @FXML
    private Label winLabel;
    @FXML
    private Label loseLabel;
    @FXML
    private Label drawLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Button startWithChoice;
    @FXML
    private Button startRand;
    @FXML
    private Button lastBattle;
    @FXML
    private Button readFile;
    @FXML
    private Button saveBattle;
    @FXML
    private Button about;
    @FXML
    private Button exit;

    private GraphicsContext gc;

    private Timeline timeline;
    private AnimationTimer animationTimer;
    private Timeline replayTime;
    private AnimationTimer replayTimer;

    private Map<String, Integer> mMap;
    private List<String> arrays;

    private ArrayList<Thread> threads;

    private int ifWin;
    private ArrayList<byte[]> record;

    private AudioClip audioClip;

    private long startTime;

    public void init() {
        audioClip = new AudioClip(getClass().getResource("/music/main.mp3").toString());
        audioClip.setCycleCount(10);
        audioClip.play();

        mainAccordion.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                // e.consume(); //blocks all key bingdings
                mainAccordion.requestFocus();
                KeyCode code = e.getCode();
                if (code == KeyCode.SPACE) {
                    startRandomGame();
                }
            }
        });

        winLabel.setVisible(false);
        loseLabel.setVisible(false);
        drawLabel.setVisible(false);
        timeLabel.setVisible(false);
        lastBattle.setVisible(false);
        saveBattle.setVisible(false);

        threads = new ArrayList<>();

        gc = battleField.getGraphicsContext2D();
        Image background = new Image("/image/background.png");
        gc.drawImage(background, 0, 0);

        arrays = new ArrayList<>();
        arrays.add("随机");
        arrays.add("鹤翼");
        arrays.add("雁行");
        arrays.add("冲轭");
        arrays.add("长蛇");
        arrays.add("鱼鳞");
        arrays.add("方元");
        arrays.add("偃月");
        arrays.add("锋矢");

        mMap = new HashMap<>();
        mMap.put("随机", 0);
        mMap.put("鹤翼", 1);
        mMap.put("雁行", 2);
        mMap.put("冲轭", 3);
        mMap.put("长蛇", 4);
        mMap.put("鱼鳞", 5);
        mMap.put("方元", 6);
        mMap.put("偃月", 7);
        mMap.put("锋矢", 8);
    }

    @FXML
    public void aboutButtonOnMouseClicked() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("关于...");
        about.setHeaderText(null);
        about.setContentText("Calabash VS Scorpion V1.0\n开发者：JYF");
        about.showAndWait();
    }

    @FXML
    public void exitButtonOnMouseClicked() {
        Platform.exit();
    }

    @FXML
    public void startGameWithChoice() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(arrays.get(0), arrays);
        dialog.setTitle("选择阵型");
        dialog.setHeaderText("战斗开始前，请为葫芦娃们做些准备吧！");
        dialog.setContentText("选择布阵方式：");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            this.startGame(mMap.get(result.get()));
        }
    }

    @FXML
    public void startRandomGame() {
        this.startGame(0);
    }

    @FXML
    public void onReadFileClicked() {
        Stage newStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择复盘文件");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("文本文档", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(newStage);

        if (selectedFile == null)
            return;

        try {
            record = new ArrayList<>();
            int length = MAX_X * MAX_Y * 2;
            FileReader fr = new FileReader(selectedFile);
            ifWin = fr.read();
            byte b;
            while ((b = (byte) fr.read()) != -1) {
                byte[] frame = new byte[length];
                frame[0] = b;
                for (int i = 1; i < length; i++)
                    frame[i] = (byte) fr.read();
                record.add(frame);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(ifWin);
        System.out.println(record.size());
        replayGame();
    }

    @FXML
    public void onSaveBattleClicked() {
        Stage newStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存复盘文件");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("文本文档", "*.txt"));
        File savedFile = fileChooser.showSaveDialog(newStage);
        if (savedFile != null) {
            saveFile(savedFile);
        }
    }

    @FXML
    public void onLastBattleClicked() {
        replayGame();
    }

    private void saveFile(File file) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write((byte) ifWin);
            for (byte[] bytes : record) {
                for (byte b : bytes) {
                    fw.write(b);
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void replayGame() {
        mainAccordion.setVisible(false);
        label1.setVisible(false);
        label2.setVisible(false);
        label3.setVisible(false);
        winLabel.setVisible(false);
        loseLabel.setVisible(false);
        drawLabel.setVisible(false);
        timeLabel.setVisible(false);

        Random random = new Random();
        audioClip.stop();
        audioClip = new AudioClip(getClass().getResource("/music/battle" + Integer.toString(random.nextInt(3) + 1) + ".mp3").toString());
        audioClip.setCycleCount(10);
        audioClip.play();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                replayTime = new Timeline();
                replayTime.setCycleCount(30);
                replayTime.setAutoReverse(true);
                replayTimer = new AnimationTimer() {
                    int i = 0;

                    @Override
                    public void handle(long now) {
                        if (i == record.size())
                            finishGame(ifWin, true);
                        else {
                            System.out.println(i);
                            playFrame(i);
                            i++;
                        }
                    }
                };
                replayTime.play();
                replayTimer.start();
            }
        }, 1000);
    }

    private void playFrame(int i) {
        byte[] frame = record.get(i);
        /*for (int y = 0; y < MAX_Y; y++) {
            for (int x = 0; x < MAX_X; x++) {
                int location = (x * MAX_Y + y) * 2;
                System.out.print((char)frame[location]);
                System.out.print((char)frame[location + 1]);
            }
            System.out.print('\n');
        }
        System.out.print('\n');*/
        Image background = new Image("/image/background.png");
        gc.drawImage(background, 0, 0);
        for (int x = 0; x < MAX_X; x++) {
            for (int y = 0; y < MAX_Y; y++) {
                int location = (x * MAX_Y + y) * 2;

                byte b1 = frame[location];

                int type = (b1 >> 4), tomb = ((b1 >> 2) & 3), bullet = (b1 & 3);
                if (type > 5)
                    type -= 13;
                switch (tomb) {
                    case 1:
                        gc.drawImage(new Image("/image/enemytomb.png"),
                                IMAGE_SIZE * x + OFFSET_X, IMAGE_SIZE * y + OFFSET_Y, 100, 100);
                        break;
                    case 2:
                        gc.drawImage(new Image("/image/yourtomb.png"),
                                IMAGE_SIZE * x + OFFSET_X, IMAGE_SIZE * y + OFFSET_Y, 100, 100);
                        break;
                    default:
                        break;
                }
                switch (type) {
                    case -1:
                        drawCreature("/image/calabash_1st.png", x, y, (double) frame[location + 1]);
                        break;
                    case -2:
                        drawCreature("/image/calabash_2nd.png", x, y, (double) frame[location + 1]);
                        break;
                    case -3:
                        drawCreature("/image/calabash_3rd.png", x, y, (double) frame[location + 1]);
                        break;
                    case -4:
                        drawCreature("/image/calabash_4th.png", x, y, (double) frame[location + 1]);
                        break;
                    case -5:
                        drawCreature("/image/calabash_5th.png", x, y, (double) frame[location + 1]);
                        break;
                    case -6:
                        drawCreature("/image/calabash_6th.png", x, y, (double) frame[location + 1]);
                        break;
                    case -7:
                        drawCreature("/image/calabash_7th.png", x, y, (double) frame[location + 1]);
                        break;
                    case 2:
                        drawCreature("/image/grandpa.png", x, y, (double) frame[location + 1]);
                        break;
                    case 3:
                        drawCreature("/image/scorpion.png", x, y, (double) frame[location + 1]);
                        break;
                    case 4:
                        drawCreature("/image/snake.png", x, y, (double) frame[location + 1]);
                        break;
                    case 5:
                        drawCreature("/image/minion.png", x, y, (double) frame[location + 1]);
                        break;
                }
                switch (bullet) {
                    case 1:
                        gc.drawImage(new Image("/image/lightning.png"),
                                IMAGE_SIZE * x + OFFSET_X, IMAGE_SIZE * y + OFFSET_Y, 100, 100);
                        break;
                    case 2:
                        gc.drawImage(new Image("/image/fireball.png"),
                                IMAGE_SIZE * x + OFFSET_X, IMAGE_SIZE * y + OFFSET_Y, 100, 100);
                        break;
                    default:
                        break;
                }


                /*switch ((char) frame[location]) {
                    case 'A':
                        drawCreature("/image/calabash_1st.png", x, y, (double) frame[location + 1]);
                        break;
                    case 'B':
                        drawCreature("/image/calabash_2nd.png", x, y, (double) frame[location + 1]);
                        break;
                    case 'C':
                        drawCreature("/image/calabash_3rd.png", x, y, (double) frame[location + 1]);
                        break;
                    case 'D':
                        drawCreature("/image/calabash_4th.png", x, y, (double) frame[location + 1]);
                        break;
                    case 'E':
                        drawCreature("/image/calabash_5th.png", x, y, (double) frame[location + 1]);
                        break;
                    case 'F':
                        drawCreature("/image/calabash_6th.png", x, y, (double) frame[location + 1]);
                        break;
                    case 'G':
                        drawCreature("/image/calabash_7th.png", x, y, (double) frame[location + 1]);
                        break;
                    case 'O':
                        drawCreature("/image/grandpa.png", x, y, (double) frame[location + 1]);
                        break;
                    case 'X':
                        drawCreature("/image/scorpion.png", x, y, (double) frame[location + 1]);
                        break;
                    case 'Y':
                        drawCreature("/image/snake.png", x, y, (double) frame[location + 1]);
                        break;
                    case 'Z':
                        drawCreature("/image/minion.png", x, y, (double) frame[location + 1]);
                        break;
                    case '#':
                        switch (frame[location + 1]) {
                            case 'l':
                                gc.drawImage(new Image("/image/lightning.png"),
                                        IMAGE_SIZE * x + OFFSET_X, IMAGE_SIZE * y + OFFSET_Y, 100, 100);
                                break;
                            case 'r':
                                gc.drawImage(new Image("/image/fireball.png"),
                                        IMAGE_SIZE * x + OFFSET_X, IMAGE_SIZE * y + OFFSET_Y, 100, 100);
                                break;
                            case 'e':
                                gc.drawImage(new Image("/image/enemytomb.png"),
                                        IMAGE_SIZE * x + OFFSET_X, IMAGE_SIZE * y + OFFSET_Y, 100, 100);
                                break;
                            case 'y':
                                gc.drawImage(new Image("/image/yourtomb.png"),
                                        IMAGE_SIZE * x + OFFSET_X, IMAGE_SIZE * y + OFFSET_Y, 100, 100);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }*/
            }
        }
    }

    private void drawCreature(String path, int x, int y, double blood) {
        gc.drawImage(new Image(path), IMAGE_SIZE * x + OFFSET_X, IMAGE_SIZE * y + OFFSET_Y);
        gc.drawImage(new Image("/image/blood.png"),
                IMAGE_SIZE * x + OFFSET_X, IMAGE_SIZE * y + OFFSET_Y,
                blood, 5);
    }

    private void startGame(int array) {
        Random random = new Random();
        if (array == 0) {
            array = random.nextInt(8) + 1;
        }
        System.out.println("选择阵型" + Integer.toString(array));

        int enemy = random.nextInt(8) + 1;

        switch (array) {
            case 1:
                yourArray = new HeYi(true);
                break;
            case 2:
                yourArray = new YanXing(true);
                break;
            case 3:
                yourArray = new ChongE(true);
                break;
            case 4:
                yourArray = new ChangShe(true);
                break;
            case 5:
                yourArray = new YuLin(true);
                break;
            case 6:
                yourArray = new FangYuan(true);
                break;
            case 7:
                yourArray = new YanYue(true);
                break;
            case 8:
                yourArray = new FengShi(true);
                break;
            default:
                break;
        }

        switch (enemy) {
            case 1:
                enemyArray = new HeYi(false);
                break;
            case 2:
                enemyArray = new YanXing(false);
                break;
            case 3:
                enemyArray = new ChongE(false);
                break;
            case 4:
                enemyArray = new ChangShe(false);
                break;
            case 5:
                enemyArray = new YuLin(false);
                break;
            case 6:
                enemyArray = new FangYuan(false);
                break;
            case 7:
                enemyArray = new YanYue(false);
                break;
            case 8:
                enemyArray = new FengShi(false);
                break;
            default:
                break;
        }

        mainAccordion.setVisible(false);
        label1.setVisible(false);
        label2.setVisible(false);
        label3.setVisible(false);
        winLabel.setVisible(false);
        loseLabel.setVisible(false);
        drawLabel.setVisible(false);

        setInitialArray();
        repainting();
        runGame();
    }

    private void setInitialArray() {
        calabashes = new ArrayList<>(7);
        minions = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            calabashes.add(new Calabash(i + 1, yourArray.calabashLocation.get(i), this));
            minions.add(new Minion(enemyArray.minionLocation.get(i), this));
        }
        grandpa = new Grandpa(yourArray.grandpaLocation, this);
        scorpion = new Scorpion(enemyArray.scorpionLocation, this);
        snake = new Snake(enemyArray.snakeLocation, this);

        creatureList = new ArrayList<>();
        tombList = new ArrayList<>();
        bulletList = new ArrayList<>();
        for (int i = 0; i < MAX_X; i++) {
            ArrayList<Creature> creatures = new ArrayList<>();
            ArrayList<Integer> integers = new ArrayList<>();
            ArrayList<Bullet> bullets = new ArrayList<>();
            for (int j = 0; j < MAX_Y; j++) {
                creatures.add(new Creature());
                integers.add(0);
                bullets.add(new Bullet());
            }
            creatureList.add(creatures);
            tombList.add(integers);
            bulletList.add(bullets);
        }

        creatureList.get(grandpa.getLocation().getX()).set(grandpa.getLocation().getY(), grandpa);
        creatureList.get(scorpion.getLocation().getX()).set(scorpion.getLocation().getY(), scorpion);
        creatureList.get(snake.getLocation().getX()).set(snake.getLocation().getY(), scorpion);
        for (int i = 0; i < 7; i++) {
            creatureList.get(calabashes.get(i).getLocation().getX()).set(calabashes.get(i).getLocation().getY(), calabashes.get(i));
            creatureList.get(minions.get(i).getLocation().getX()).set(minions.get(i).getLocation().getY(), minions.get(i));
        }

        record = new ArrayList<>();
        addRecord();
    }

    private void addRecord() {
        byte[] info = new byte[MAX_X * MAX_Y * 2];
        for (int x = 0; x < MAX_X; x++) {
            for (int y = 0; y < MAX_Y; y++) {
                int location = (x * MAX_Y + y) * 2;
                Creature creature = creatureList.get(x).get(y);

                int id = creature.getType();
                if (id < 0)
                    id += 13;
                int tomb = tombList.get(x).get(y);
                int bullet;
                Bullet b = bulletList.get(x).get(y);
                if (b.getAttack() == 0)
                    bullet = 0;
                else if (b.isType())
                    bullet = 1;
                else
                    bullet = 2;

                info[location] = (byte) ((id << 4) + (tomb << 2) + bullet);

                if (creature.getType() != 0)
                    info[location + 1] = (byte) (100 * creature.getHealth() / creature.getMaxHealth());
                else
                    info[location + 1] = 0;

                /*switch (creature.getType()) {
                    case -1:
                        info[location] = 'A';
                        break;
                    case -2:
                        info[location] = 'B';
                        break;
                    case -3:
                        info[location] = 'C';
                        break;
                    case -4:
                        info[location] = 'D';
                        break;
                    case -5:
                        info[location] = 'E';
                        break;
                    case -6:
                        info[location] = 'F';
                        break;
                    case -7:
                        info[location] = 'G';
                        break;
                    case 2:
                        info[location] = 'O';
                        break;
                    case 3:
                        info[location] = 'X';
                        break;
                    case 4:
                        info[location] = 'Y';
                        break;
                    case 5:
                        info[location] = 'Z';
                        break;
                    case 0:
                        info[location] = '#';
                        break;
                    default:
                        break;
                }
                if (creature.getType() == 0) {
                    Bullet bullet = bulletList.get(x).get(y);
                    if (bullet.getAttack() != 0) {
                        if (bullet.isType())
                            info[location + 1] = 'l';
                        else
                            info[location + 1] = 'r';
                    } else {
                        if (tombList.get(x).get(y) == 1)
                            info[location + 1] = 'e';
                        else if (tombList.get(x).get(y) == 2)
                            info[location + 1] = 'y';
                        else
                            info[location + 1] = '#';
                    }
                } else {
                    info[location + 1] = (byte) (100 * creature.getHealth() / creature.getMaxHealth());
                }*/
            }
        }
        record.add(info);
    }

    private int isFinish() {
        long nowTime = System.currentTimeMillis() / 1000;
        if (nowTime - startTime > MAX_TIME)
            return 3;

        boolean yoursAlive = grandpa.isAlive();
        boolean enemyAlive = scorpion.isAlive() || snake.isAlive();
        for (Calabash calabash : calabashes)
            yoursAlive |= calabash.isAlive();
        for (Minion minion : minions)
            enemyAlive |= minion.isAlive();
        if (yoursAlive && enemyAlive) {
            return 0;
        } else if (yoursAlive) {
            return 1;
        } else {
            return 2;
        }
    }

    private void finishGame(int winner, boolean ifReplay) {
        if (winner == 0)
            return;

        for (Thread thread : threads) {
            thread.interrupt();
        }

        if(!ifReplay) {
            timeline.stop();
            animationTimer.stop();
        }
        else {
            replayTime.stop();
            replayTimer.stop();
        }

        mainAccordion.setVisible(true);
        lastBattle.setVisible(true);
        saveBattle.setVisible(true);

        audioClip.stop();

        if (winner == 1) {
            winLabel.setVisible(true);
            audioClip = new AudioClip(getClass().getResource("/music/win.mp3").toString());
        } else if (winner == 2) {
            loseLabel.setVisible(true);
            audioClip = new AudioClip(getClass().getResource("/music/lose.mp3").toString());
        } else {
            drawLabel.setVisible(true);
            audioClip = new AudioClip(getClass().getResource("/music/draw.mp3").toString());
        }
        ifWin = winner;

        audioClip.setCycleCount(10);
        audioClip.play();
    }

    private void repainting() {
        gc = battleField.getGraphicsContext2D();
        Image background = new Image("/image/background.png");
        gc.drawImage(background, 0, 0);

        for (int i = 0; i < MAX_X; i++) {
            for (int j = 0; j < MAX_Y; j++) {
                if (tombList.get(i).get(j) == 1) {
                    gc.drawImage(new Image("/image/enemytomb.png"),
                            IMAGE_SIZE * i + OFFSET_X,
                            IMAGE_SIZE * j + OFFSET_Y,
                            100, 100);
                } else if (tombList.get(i).get(j) == 2) {
                    gc.drawImage(new Image("/image/yourtomb.png"),
                            IMAGE_SIZE * i + OFFSET_X,
                            IMAGE_SIZE * j + OFFSET_Y,
                            100, 100);
                }
            }
        }

        /*for(int y = 0; y < MAX_Y; y++) {
            for (int x = 0; x < MAX_X; x++) {
                Bullet bullet = bulletList.get(x).get(y);
                if(bullet.getAttack() == 0) {
                    System.out.print("_");
                }
                else if(bullet.isType())
                    System.out.print("T");
                else
                    System.out.print("F");
            }
            System.out.print('\n');
        }
        System.out.print('\n');*/

        for (int i = 0; i < MAX_Y; i++) {
            if (calabashes.get(i).isAlive()) {
                gc.drawImage(calabashes.get(i).getImage(),
                        IMAGE_SIZE * calabashes.get(i).getLocation().getX() + OFFSET_X,
                        IMAGE_SIZE * calabashes.get(i).getLocation().getY() + OFFSET_Y);
                gc.drawImage(blood,
                        IMAGE_SIZE * calabashes.get(i).getLocation().getX() + OFFSET_X,
                        IMAGE_SIZE * calabashes.get(i).getLocation().getY() + OFFSET_Y,
                        (double) (100 * calabashes.get(i).getHealth() / calabashes.get(i).getMaxHealth()), 5);
            }
            if (minions.get(i).isAlive()) {
                gc.drawImage(minions.get(i).getImage(),
                        IMAGE_SIZE * minions.get(i).getLocation().getX() + OFFSET_X,
                        IMAGE_SIZE * minions.get(i).getLocation().getY() + OFFSET_Y);
                gc.drawImage(blood,
                        IMAGE_SIZE * minions.get(i).getLocation().getX() + OFFSET_X,
                        IMAGE_SIZE * minions.get(i).getLocation().getY() + OFFSET_Y,
                        (double) (100 * minions.get(i).getHealth() / minions.get(i).getMaxHealth()), 5);
            }
        }

        if (grandpa.isAlive()) {
            gc.drawImage(grandpa.getImage(),
                    IMAGE_SIZE * grandpa.getLocation().getX() + OFFSET_X,
                    IMAGE_SIZE * grandpa.getLocation().getY() + OFFSET_Y);
            gc.drawImage(blood,
                    IMAGE_SIZE * grandpa.getLocation().getX() + OFFSET_X,
                    IMAGE_SIZE * grandpa.getLocation().getY() + OFFSET_Y,
                    (double) (100 * grandpa.getHealth() / grandpa.getMaxHealth()), 5);
        }
        if (scorpion.isAlive()) {
            gc.drawImage(scorpion.getImage(),
                    IMAGE_SIZE * scorpion.getLocation().getX() + OFFSET_X,
                    IMAGE_SIZE * scorpion.getLocation().getY() + OFFSET_Y);
            gc.drawImage(blood,
                    IMAGE_SIZE * scorpion.getLocation().getX() + OFFSET_X,
                    IMAGE_SIZE * scorpion.getLocation().getY() + OFFSET_Y,
                    (double) (100 * scorpion.getHealth() / scorpion.getMaxHealth()), 5);
        }
        if (snake.isAlive()) {
            gc.drawImage(snake.getImage(),
                    IMAGE_SIZE * snake.getLocation().getX() + OFFSET_X,
                    IMAGE_SIZE * snake.getLocation().getY() + OFFSET_Y);
            gc.drawImage(blood,
                    IMAGE_SIZE * snake.getLocation().getX() + OFFSET_X,
                    IMAGE_SIZE * snake.getLocation().getY() + OFFSET_Y,
                    (double) (100 * snake.getHealth() / snake.getMaxHealth()), 5);
        }

        for (int i = 0; i < MAX_X; i++) {
            for (int j = 0; j < MAX_Y; j++) {
                Bullet bullet = bulletList.get(i).get(j);
                if (bullet.getAttack() != 0) {
                    gc.drawImage(bullet.getBulletImage(),
                            IMAGE_SIZE * i + OFFSET_X,
                            IMAGE_SIZE * j + OFFSET_Y,
                            100, 100);
                }
            }
        }

        timeLabel.setText("时间：" + Integer.toString((int) (System.currentTimeMillis() / 1000 - startTime)) + "s");
    }

    private void runGame() {
        Random random = new Random();
        audioClip.stop();
        audioClip = new AudioClip(getClass().getResource("/music/battle" + Integer.toString(random.nextInt(3) + 1) + ".mp3").toString());
        audioClip.setCycleCount(10);
        audioClip.play();

        timeLabel.setVisible(true);
        timeLabel.setText("时间：0s");

        startTime = System.currentTimeMillis() / 1000;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeline = new Timeline();
                timeline.setCycleCount(100);
                timeline.setAutoReverse(true);
                animationTimer = new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        bulletTurn();
                        repainting();
                        addRecord();
                        finishGame(isFinish(), false);
                    }
                };
                timeline.play();
                animationTimer.start();

                /*bulletTime = new Timeline();
                bulletTime.setCycleCount(1000);
                bulletTime.setAutoReverse(true);
                bulletTimer = new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        bulletTurn();
                    }
                };
                bulletTime.play();
                bulletTimer.start();*/

                threads = new ArrayList<>();
                for (Calabash calabash : calabashes) {
                    threads.add(new Thread(calabash));
                }
                for (Minion minion : minions) {
                    threads.add(new Thread(minion));
                }
                threads.add(new Thread(grandpa));
                threads.add(new Thread(scorpion));
                threads.add(new Thread(snake));

                for (Thread thread : threads)
                    thread.start();
            }
        }, 1000);
    }

    private void bulletTurn() {
        for (int x = 0; x < MAX_X; x++) {
            for (int y = 0; y < MAX_Y; y++) {
                bulletList.get(x).get(y).move();
            }
        }
        for (int x = 0; x < MAX_X; x++) {
            for (int y = 0; y < MAX_Y; y++) {
                bulletList.get(x).get(y).setMoveable(true);
            }
        }
    }

    public Creature getUnit(int x, int y) {
        return creatureList.get(x).get(y);
    }

    public void clearUnit(int x, int y) {
        creatureList.get(x).set(y, new Creature());
    }

    public void setUnit(int x, int y, Creature unit) {
        creatureList.get(x).set(y, unit);
    }

    public Creature getUnit(Location location) {
        return creatureList.get(location.getX()).get(location.getY());
    }

    public void clearUnit(Location location) {
        creatureList.get(location.getX()).set(location.getY(), new Creature());
    }

    public void setUnit(Location location, Creature unit) {
        creatureList.get(location.getX()).set(location.getY(), unit);
    }

    public void buildTomb(int x, int y, boolean type) {
        if (type)
            tombList.get(x).set(y, 1);
        else
            tombList.get(x).set(y, 2);
    }

    public void addBullet(int x, int y, Bullet bullet) {
        bulletList.get(x).set(y, bullet);
    }

    public void bulletHit(int x, int y) {
        bulletList.get(x).set(y, new Bullet());
    }

    public Bullet getBullet(int x, int y) {
        return bulletList.get(x).get(y);
    }

    public void battle(int x, int y) {
        gc = battleField.getGraphicsContext2D();
        Image battle = new Image("/image/battle.png");
        gc.drawImage(battle, x, y, 100, 100);
    }
}
