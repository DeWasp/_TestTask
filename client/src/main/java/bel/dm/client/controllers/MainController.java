package bel.dm.client.controllers;

import bel.dm.client.classes.MyAccountService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class MainController {

    /**
     * List of rCount threads
     */
    private final ArrayList<Thread> rCountThreadsList = new ArrayList<>();
    /**
     * List of wCount threads
     */
    private final ArrayList<Thread> wCountThreadsList = new ArrayList<>();
    /**
     * RMI client side
     */
    private final MyAccountService myAccountService = new MyAccountService();

    /** Field fill flag */
    private Boolean rSubmitted = false;
    /** Field fill flag */
    private Boolean wSubmitted = false;
    /** Obtained amount of wCount threads*/
    private Integer rWorkers = 0;
    /** Obtained amount of wCount threads*/
    private Integer wWorkers = 0;
    /** Obtained idList*/
    private String[] ids;

    public Button stop;
    public Button submitRCount;
    public Button submitWCount;
    public Button submitIdsList;

    public TextField wCount;
    public TextField rCount;
    public TextField idList;

    public Label error;
    public Label rVelocity;
    public Label rRequests;
    public Label wRequests;
    public Label wVelocity;

    /** Statistics updater*/
    private final Timeline statisticsWorker = new Timeline(
            new KeyFrame(Duration.seconds(0), evt -> {
                rVelocity.setText(String.valueOf(myAccountService.getRVelocity()));
                rRequests.setText(String.valueOf(myAccountService.getRRequestAmount()));
                wVelocity.setText(String.valueOf(myAccountService.getWVelocity()));
                wRequests.setText(String.valueOf(myAccountService.getWRequestAmount()));
            }),
            new KeyFrame(Duration.seconds(1))
    );


    @FXML
    protected void submitRCount() {
        if (Integer.parseInt(rCount.getText()) < 1) return;
        rSubmitted = true;
        rWorkers = Integer.parseInt(rCount.getText());
        if (rSubmitted && wSubmitted) submitIdsList.setDisable(false);
        submitRCount.setDisable(true);
    }

    @FXML
    protected void submitLCount() {
        if (Integer.parseInt(wCount.getText()) < 1) return;
        wSubmitted = true;
        wWorkers = Integer.parseInt(wCount.getText());
        if (rSubmitted && wSubmitted) submitIdsList.setDisable(false);
        submitWCount.setDisable(true);
    }

    @FXML
    protected void submitIdList() {
        try {
            ids = idList.getText().split(",");
            if (Arrays.stream(ids).anyMatch(number ->
                    !number.matches("\\d+"))) {
                error.setText("Список ключей содержит неприемлимые символы");
                return;
            }
            submitIdsList.setDisable(true);
            error.setText("");
            if (!startRWorkers()) {
                error.setText("Не удалось запустить потоки rCount");
                cancelAllThreads();
                return;
            }
            if (!startWWorkers()) {
                error.setText("Не удалось запустить потоки lCount");
                cancelAllThreads();
                return;
            }
            statisticsWorker.setCycleCount(Animation.INDEFINITE);
            statisticsWorker.play();
            stop.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Creates several threads to invoke getAmount method corresponding to rWorkers value*/
    private boolean startRWorkers() {
        try {
            for (int i = 0; i < rWorkers; i++) {
                Thread thread = new Thread(() -> {
                    while (!Thread.interrupted()) {
                        Long amount = myAccountService
                                .getAmount(
                                        Integer.parseInt(ids[new Random().nextInt(ids.length)]));
                        System.out.println("receiving: " + amount + "|___GET___|");
                    }
                });
                rCountThreadsList.add(thread);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /** Creates several threads to invoke addAmount method corresponding to wWorkers value*/
    private boolean startWWorkers() {
        try {
            for (int i = 0; i < wWorkers; i++) {
                Thread thread = new Thread(() -> {
                    while (!Thread.interrupted()) {
                        Long newAmount = new Random().nextLong((100 + 100) + 1) - 100;
                        System.out.println("|____ADD____|adding: " + newAmount);
                        myAccountService
                                .addAmount(
                                        Integer.parseInt(
                                                ids[new Random().nextInt(ids.length)]), newAmount
                                );
                    }
                });
                wCountThreadsList.add(thread);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /** Cancels all threads*/
    private void cancelAllThreads() {
        try {
            for (Thread thread : rCountThreadsList) {
                thread.interrupt();
            }
            rCountThreadsList.clear();
            for (Thread thread : wCountThreadsList) {
                thread.interrupt();
            }
            wCountThreadsList.clear();
            statisticsWorker.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void stop() {
        submitRCount.setDisable(false);
        submitWCount.setDisable(false);
        cancelAllThreads();
        rSubmitted = false;
        wSubmitted = false;
        stop.setVisible(false);
    }

    @FXML
    protected void reset() {
        myAccountService.reset();
        Platform.runLater(() -> {
            rVelocity.setText(String.valueOf(myAccountService.getRVelocity()));
            rRequests.setText(String.valueOf(myAccountService.getRRequestAmount()));
            wVelocity.setText(String.valueOf(myAccountService.getWVelocity()));
            wRequests.setText(String.valueOf(myAccountService.getWRequestAmount()));
        });
    }
}