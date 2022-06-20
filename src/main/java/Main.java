import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import data.InputInfos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class Main {
    public static Boolean[]before ={ false,false} ;

    public static Integer[] screenSize = {GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().width,GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().height};
    public static Integer[] otherScreenSize = {0,0};
    public static void main(String[] args){
        System.out.println(args[0]);
        if(Objects.equals(args[0], "server")){
            try {
                Server server = new Server();
                Kryo kryo = server.getKryo();

                kryo.register(InputInfos.class);
                kryo.register(Boolean[].class);
                kryo.register(Integer[].class);
                server.start();
                server.bind(54555, 54777);
                Robot robot = new Robot();
                Robot robot1 = new Robot();

                server.addListener(new Listener() {
                    @Override
                    public void connected(Connection connection) {
                        System.out.println("INPU");
                        super.connected(connection);
                    }

                    public void received (Connection connection, Object object) {
                        if(object instanceof Integer[]){
                            otherScreenSize = (Integer[]) object;
                        }
                        if (object instanceof InputInfos) {

                            InputInfos request = (InputInfos) object;
                            robot.mouseMove(map(request.mousePos[0],0,screenSize[0],0,otherScreenSize[0]),map(request.mousePos[1],0,screenSize[1],0,otherScreenSize[1]));

                            if(request.mouseClick[0] && !before[0]){
                                robot1.mousePress(InputEvent.BUTTON1_MASK);

                            }
                            if(!request.mouseClick[0] && before[0]){
                                robot1.mouseRelease(InputEvent.BUTTON1_MASK);
                            }
                            if(request.mouseClick[1] && !before[1]){
                                robot1.mousePress(InputEvent.BUTTON2_MASK);


                            }
                            if(!request.mouseClick[1] && before[1]){
                                robot1.mouseRelease(InputEvent.BUTTON2_MASK);

                            }
                            before = request.mouseClick;
                        }
                    }
                });

                JFrame frame = new JFrame("Chat Server");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    public void windowClosed (WindowEvent evt) {
                        server.stop();
                    }
                });
                frame.getContentPane().add(new JLabel("Close to stop the chat server."));
                frame.setSize(320, 200);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            } catch (Exception e) {

                e.printStackTrace();
            }

        }else{
            try{
                InputInfos infos = new InputInfos();

                Client client = new Client();

                Kryo kryo = client.getKryo();
                kryo.register(InputInfos.class);
                kryo.register(Boolean[].class);
                kryo.register(Integer[].class);

                client.start();
                client.connect(5000, "192.168.0.173", 54555, 54777);
                client.sendTCP(screenSize);
                MouseAdapter adapter = new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(e.getButton() == 1){
                            infos.mouseClick[0] = true;
                        }
                        if(e.getButton() == 2){
                            infos.mouseClick[1] = true;
                        }
                        super.mousePressed(e);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if(e.getButton() == 1){
                            infos.mouseClick[0] = false;
                        }
                        if(e.getButton() == 2){
                            infos.mouseClick[1] = false;
                        }
                        super.mouseReleased(e);
                    }
                };
                JFrame frame = new JFrame("Chat Server");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    public void windowClosed (WindowEvent evt) {
                        client.stop();
                    }
                });
                frame.addMouseListener(adapter);
                frame.getContentPane().add(new JLabel("Close to stop the chat server."));
                frame.setSize(320, 200);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                while (true){
                    try{
                        Thread.sleep(1);
                        Point info = MouseInfo.getPointerInfo().getLocation();
                        infos.mousePos[0] = info.x;
                        infos.mousePos[1] = info.y;
                        System.out.println(info.x);
                        client.sendUDP(infos);

                    }catch(Exception e){

                    }


                }


            }catch(Exception e){

            }

        }

    }
    public static int map(int x,int in_min,int in_max,int out_min,int out_max){
        return ((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }
}
