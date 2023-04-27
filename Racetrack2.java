
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.*;

public class Racetrack2 extends JPanel implements KeyListener {
    private static final int WIDTH = 850;
    private static final int HEIGHT = 650;
    private static final int KART_WIDTH = 50;
    private static final int KART_HEIGHT = 30;
    private static final int MAX_SPEED = 100;
    private static final int SPEED_INCREMENT = 10;
    private static final double ANGLE_INCREMENT = Math.PI / 8;
    private static final double MAX_ANGLE = 2 * Math.PI;
    private static final int COLLISION_THRESHOLD = 10;
    private Image kartImage1;
    private Image kartImage2;
    private int kart1X;
    private int kart1Y;
    private int kart2X;
    private int kart2Y;
    private double kart1Angle;
    private double kart2Angle;
    private int kart1Speed;
    private int kart2Speed;
    private boolean[] kart1 = new boolean[8];
    private boolean[] kartHelper = new boolean[8];

    public Racetrack2() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.green);
        kartImage1 = Toolkit.getDefaultToolkit().getImage("./public/kart1.jpg");
        kartImage2 = Toolkit.getDefaultToolkit().getImage("./public/kart2.jpg");
        kart1X = 400;
        kart1Y = 550;
        kart2X = 450;
        kart2Y = 550;
        kart1Angle = 0;
        kart2Angle = 0;
        kart1Speed = 0;
        kart2Speed = 0;
        kart1[0] = false;
        kart1[1] = false;
        kart1[2] = false;
        kart1[3] = false;
        kart1[4] = false;
        kart1[5] = false;
        kart1[6] = false;
        kart1[7] = false;
        setFocusable(true);
        addKeyListener(this);
        Timer timer = new Timer(20, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                kart1 = fetchLatestBooleanArrayFromServer();
                updateKarts();
                checkCollisions();
                repaint();
            }
        });
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.drawRect(50, 100, 750, 500);
        g.drawRect(150, 200, 550, 300);
        g.setColor(Color.yellow);
        g.drawRect(100, 150, 650, 400);
        g.setColor(Color.white);
        g.drawLine(425, 500, 425, 600);
        drawKart(g, kartImage1, kart1X, kart1Y, kart1Angle);
        drawKart(g, kartImage2, kart2X, kart2Y, kart2Angle);
    }

    private void drawKart(Graphics g, Image kartImage, int x, int y, double angle) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.rotate(angle, x + KART_WIDTH / 2, y + KART_HEIGHT / 2);
        g2d.drawImage(kartImage, x, y, KART_WIDTH, KART_HEIGHT, this);
        g2d.rotate(-angle, x + KART_WIDTH / 2, y + KART_HEIGHT / 2);
    }

    private void updateKarts() {
        // Update kart 1 speed and angle based on user input
        if (kart1[0]) {
            kart1Angle -= ANGLE_INCREMENT;
            if (kart1Angle < 0) {
                kart1Angle += MAX_ANGLE;
            }
        }
        if (kart1[1]) {
            kart1Angle += ANGLE_INCREMENT;
            if (kart1Angle >= MAX_ANGLE) {
                kart1Angle -= MAX_ANGLE;
            }
        }
        if (kart1[2]) {
            kart1Speed += SPEED_INCREMENT;
            if (kart1Speed > MAX_SPEED) {
                kart1Speed = MAX_SPEED;
            }
        }
        if (kart1[3]) {
            kart1Speed -= SPEED_INCREMENT;
            if (kart1Speed < 0) {
                kart1Speed = 0;
            }
        }

        // Update kart 2 speed and angle based on user input
        if (kart1[4]) {
            kart2Angle -= ANGLE_INCREMENT;
            if (kart2Angle < 0) {
                kart2Angle += MAX_ANGLE;
            }
        }
        if (kart1[5]) {
            kart2Angle += ANGLE_INCREMENT;
            if (kart2Angle >= MAX_ANGLE) {
                kart2Angle -= MAX_ANGLE;
            }
        }
        if (kart1[6]) {
            kart2Speed += SPEED_INCREMENT;
            if (kart2Speed > MAX_SPEED) {
                kart2Speed = MAX_SPEED;
            }
        }
        if (kart1[7]) {
            kart2Speed -= SPEED_INCREMENT;
            if (kart2Speed < 0) {
                kart2Speed = 0;
            }
        }

        // Move kart 1 based on current speed and angle
        int dx1 = (int) Math.round(kart1Speed * Math.sin(kart1Angle));
        int dy1 = (int) Math.round(kart1Speed * Math.cos(kart1Angle));
        kart1X += dx1;
        kart1Y -= dy1;

        // Move kart 2 based on current speed and angle
        int dx2 = (int) Math.round(kart2Speed * Math.sin(kart2Angle));
        int dy2 = (int) Math.round(kart2Speed * Math.cos(kart2Angle));
        kart2X += dx2;
        kart2Y -= dy2;

        // Check if karts go out of bounds and wrap them around if necessary
        if (kart1X < -KART_WIDTH) {
            kart1X = WIDTH;
        } else if (kart1X > WIDTH) {
            kart1X = -KART_WIDTH;
        }
        if (kart1Y < -KART_HEIGHT) {
            kart1Y = HEIGHT;
        } else if (kart1Y > HEIGHT) {
            kart1Y = -KART_HEIGHT;
        }
        if (kart2X < -KART_WIDTH) {
            kart2X = WIDTH;
        } else if (kart2X > WIDTH) {
            kart2X = -KART_WIDTH;
        }
        if (kart2Y < -KART_HEIGHT) {
            kart2Y = HEIGHT;
        } else if (kart2Y > HEIGHT) {
            kart2Y = -KART_HEIGHT;
        }
    }

    // auto move car and bounderies are the frame itself
    private void checkCollisions() {
        int kart1CenterX = kart1X + KART_WIDTH / 2;
        int kart1CenterY = kart1Y + KART_HEIGHT / 2;
        int kart2CenterX = kart2X + KART_WIDTH / 2;
        int kart2CenterY = kart2Y + KART_HEIGHT / 2;
        int distance = (int) Math
                .sqrt(Math.pow(kart1CenterX - kart2CenterX, 2) + Math.pow(kart1CenterY - kart2CenterY, 2));

        // Check if either kart has collided with the boundary
        // System.out.println("x: " + kart1X + " y: " + kart1Y);
        if (kart1X < 40 || kart1X + KART_WIDTH > 800 || kart1Y < 95 || kart1Y + KART_HEIGHT > 600) {
            kart1Speed = 0;
        }
        if (kart2X < 40 || kart2X + KART_WIDTH > 800 || kart2Y < 95 || kart2Y + KART_HEIGHT > 600) {
            kart2Speed = 0;
        }

        // Check if both karts have collided
        if (distance <= COLLISION_THRESHOLD + COLLISION_THRESHOLD) {
            JOptionPane.showMessageDialog(this, "Game Over! Both karts have collided!");
            System.exit(0);
        }
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                kartHelper = kart1;
                kartHelper[4] = true;
                sendBooleanArrayToServer(kartHelper);
                break;
            case KeyEvent.VK_D:
                kartHelper = kart1;
                kartHelper[5] = true;
                sendBooleanArrayToServer(kartHelper);
                break;
            case KeyEvent.VK_W:
                kartHelper = kart1;
                kartHelper[6] = true;
                sendBooleanArrayToServer(kartHelper);
                break;
            case KeyEvent.VK_S:
                kartHelper = kart1;
                kartHelper[7] = true;
                sendBooleanArrayToServer(kartHelper);
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                kartHelper = kart1;
                kartHelper[4] = false;
                sendBooleanArrayToServer(kartHelper);
                break;
            case KeyEvent.VK_D:
                kartHelper = kart1;
                kartHelper[5] = false;
                sendBooleanArrayToServer(kartHelper);
                break;
            case KeyEvent.VK_W:
                kartHelper = kart1;
                kartHelper[6] = false;
                sendBooleanArrayToServer(kartHelper);
                break;
            case KeyEvent.VK_S:
                kartHelper = kart1;
                kartHelper[7] = false;
                sendBooleanArrayToServer(kartHelper);
                break;
        }
    }

    public void sendBooleanArrayToServer(boolean[] arr) {
        try (Socket socket = new Socket("localhost", 4444)) { // replace localhost and 1234 with your server's IP
                                                              // address and port number
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            outputStream.writeObject("update"); // send the request to update the array to the server
            outputStream.writeObject(arr); // send the boolean array to the server

            String response = (String) inputStream.readObject(); // read the response from the server

            // print the response
            System.out.println(response);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean[] fetchLatestBooleanArrayFromServer() {
        boolean[] latestArray = null;
        try (Socket socket = new Socket("localhost", 4444)) { // replace localhost and 1234 with your server's IP
                                                              // address and port number
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            outputStream.writeObject("getLatestArray"); // send the request to get the latest array from the server

            latestArray = (boolean[]) inputStream.readObject(); // read the latest array from the server
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return latestArray;
    }

    // public void sendBooleanArrayToServer(boolean[] arr) {
    // try (Socket socket = new Socket("localhost", 4444)) { // replace localhost
    // and 1234 with your server's IP
    // // address and port number
    // ObjectOutputStream outputStream = new
    // ObjectOutputStream(socket.getOutputStream());
    // ObjectInputStream inputStream = new
    // ObjectInputStream(socket.getInputStream());

    // outputStream.writeObject(arr); // send the boolean array to the server

    // boolean[] response = (boolean[]) inputStream.readObject(); // read the
    // response from the server

    // // print the response
    // kart1 = response;
    // System.out.println("Received response from server: ");
    // for (int i = 0; i < response.length; i++) {
    // System.out.print(response[i] + " ");
    // }
    // } catch (IOException | ClassNotFoundException e) {
    // e.printStackTrace();
    // }
    // }

    public void keyTyped(KeyEvent e) {
    }
}
