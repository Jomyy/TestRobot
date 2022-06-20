import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class Main {
    public static void main(String[] args){

        if(args[0] == "server"){
            try {
                Server server = new Server();
                server.start();
                server.bind(54555, 54777);
                server.addListener(new Listener() {
                    public void received (Connection connection, Object object) {
                        if (object instanceof Integer[]) {
                            Integer[] request = (Integer[])object;
                            System.out.println(request[1]);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try{
                Client client = new Client();
                client.start();
                client.connect(5000, "192.168.0.165", 54555, 54777);

                while (true){
                    try{
                        Thread.sleep(10);
                        Integer[] pos = {1,1};
                        client.sendUDP(pos);
                    }catch(Exception e){

                    }


                }


            }catch(Exception e){

            }

        }

    }
}
