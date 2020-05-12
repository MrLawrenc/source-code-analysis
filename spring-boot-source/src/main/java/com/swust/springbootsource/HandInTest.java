package com.swust.springbootsource;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * FenQianHero class
 *
 * @author nanj
 * @date 2018/1/19
 */
public class HandInTest {

    Robot clickRobot = null;

    public HandInTest() {
        try {
            clickRobot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    public void click(int x, int y) {
        clickRobot.mouseMove(x, y);
        clickRobot.mousePress(KeyEvent.BUTTON1_MASK);
        clickRobot.mouseRelease(KeyEvent.BUTTON1_MASK);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("click:" + x + ":" + y);
    }

    public void rightClick(int x, int y) {
        clickRobot.mouseMove(x, y);
        clickRobot.mousePress(KeyEvent.BUTTON3_MASK);
        clickRobot.mouseRelease(KeyEvent.BUTTON3_MASK);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("click:" + x + ":" + y);
    }


    public static void main(String[] args) {
        // 这里停一下
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HandInTest handInTest = new HandInTest();
/*
        handInTest.click(960, 300);
        handInTest.click(960, 400);
        handInTest.click(960, 500);
        handInTest.click(960, 600);
        handInTest.click(960, 700);
        handInTest.click(960, 800);
        handInTest.click(960, 900);
*/


        handInTest.rightClick(400, 800);
        handInTest.rightClick(600, 800);
        handInTest.rightClick(800, 600);
        handInTest.rightClick(960, 900);
        handInTest.rightClick(600, 300);
        handInTest.rightClick(700, 200);
        handInTest.rightClick(1000, 900);
        handInTest.rightClick(60, 900);
    }
}