import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class Main {
    public static void main(String[] args){
        System.out.println(args[0]);
        if(Objects.equals(args[0], "server")){
            try {
                Server server = new Server();
                Kryo kryo = server.getKryo();
                kryo.register(Integer[].class);
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
                client.start();
                client.connect(5000, "192.168.0.165", 54555, 54777);
                Robot robot = new Robot();
                while (true){
                    try{
                        Thread.sleep(10);
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
