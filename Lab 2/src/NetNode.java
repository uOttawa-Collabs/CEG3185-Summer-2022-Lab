import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;

class NetNode extends Thread {
    static int msg_terminated = 0;
    static int msg_sent = 0;
    int port;
    char name;
    DatagramSocket rcv_socket;
    ArrayList<Integer> known_nodes;
    Random rand;

    public NetNode(int ID) {
        name = (char) ('A' + ID);
        port = 9900 + ID;
        rand = new Random();
        System.out.println("Creating a new NetNode " + name);
        try {
            known_nodes = new ArrayList<Integer>(26);
            rcv_socket = new DatagramSocket(port);  // create socket at local port
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        byte[] buffer = new byte[1024];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        System.out.println("NetNode " + name + " thread started");
        Message msg;
        while (true) { // loop forever
            try {
                rcv_socket.receive(request);
                // System.out.println("Node " + this.name + " message received");
                msg = new Message(request.getData());
                msg.time_to_live--;
                if (msg.dst == msg.src()) {
                    System.out.println("Node " + this.name + " is now terminated");
                    break;
                }
                if (msg.time_to_live == 0 || msg.dst == name) {
                    // System.out.print ( msg + " is terminated at Node " + this.name + "\t" + msg.payload_toString() );
                    System.out.println(msg + " is terminated at Node " + this.name);
                    msg_terminated++;
                } else {
                    int next_node = get_random_node();
                    if (next_node < 0) {
                        System.out.println(msg + " is terminated at Node " + this.name);
                    } else {
                        byte[] msg_buffer = msg.get_bytes(this.name);
                        DatagramPacket relay_msg = new DatagramPacket(msg_buffer, msg_buffer.length, InetAddress.getLocalHost(), 9900 + next_node);
                        sleep(rand.nextInt(1000));
                        // System.out.print ("Node " + this.name + " relaying " + msg + " to Node" + ('A' + next_node) + "\n");
                        System.out.println(msg + " is relayed at Node " + this.name);
                        rcv_socket.send(relay_msg);
                    }
                }
            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e.getMessage());
            }
        } // loop forever
        rcv_socket.close();
    }

    public void add_node(int ID) {
        known_nodes.add(ID);
    }

    public int get_random_node() {
        int val = -1;
        int size = known_nodes.size();
        if (size == 1) val = known_nodes.get(0);
        else if (size > 1) {
            val = known_nodes.get(rand.nextInt(size));
        }
        return val;
    }

    public void send_msg(int to_node, int msg_size) {
        Message msg = new Message(this.name, (char) ('A' + to_node), msg_size);
        int next_node = get_random_node();
        Boolean thread_terminating_msg = false;
        if (to_node + 9900 == port) {
            next_node = to_node; // thread terminating message
            thread_terminating_msg = true;
        }
        if (next_node < 0) {
            System.out.println(msg + " is terminated at Node " + this.name);
            return;
        }
        try {
            byte[] msg_buffer = msg.get_bytes();
            DatagramPacket relay_msg = new DatagramPacket(msg_buffer, msg_buffer.length, InetAddress.getLocalHost(), 9900 + next_node);
            rcv_socket.send(relay_msg);
            if (!thread_terminating_msg) System.out.println("Node " + this.name + " message sent: " + msg);
            msg_sent++;
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
}
