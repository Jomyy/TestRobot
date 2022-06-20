import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class Main {
    public static void main(String[] args){
        System.out.println(args[0]);
        if(Objects.equals(args[0], "server")){
            try {
                Server server = new Server();
                Kryo kryo = server.getKryo();
                kryo.register(Integer[].class);
                kryo.register(Boolean[].class);
                server.start();
                server.bind(54555, 54777);
                Robot robot = new Robot();
                server.addListener(new Listener() {
                    public void received (Connection connection, Object object) {
                        if (object instanceof Integer[]) {
                            Integer[] request = (Integer[])object;
                            robot.mouseMove(request[0],request[1]);
                            System.out.println(request[1]);
                        }
                        if(object instanceof Boolean[]){
                            Boolean[] request = (Boolean[]) object;
                            if(request[0]){
                                robot.mousePress(0);
                                robot.mouseRelease(0);
                            }
                            if(request[1]){
                                robot.mousePress(1);
                                robot.mouseRelease(1);

                            }

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
                Client client = new Client();
                Kryo kryo = client.getKryo();
                kryo.register(Integer[].class);
                kryo.register(Boolean[].class);

                client.start();
                client.connect(5000, "192.168.0.165", 54555, 54777);
                Robot robot = new Robot();
                MouseAdapter adapter = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(e.getButton() == 0){
                            client.sendUDP(new Boolean[]{true,false});
                        }
                        if(e.getButton() == 1){
                            client.sendUDP(new Boolean[]{false,true});
                        }
                        super.mouseClicked(e);
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

                        client.sendUDP(new Integer[]{info.x, info.y});
                    }catch(Exception e){

                    }


                }


            }catch(Exception e){

            }

        }

    }
}
