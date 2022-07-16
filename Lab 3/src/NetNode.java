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
    static int msg_lost = 0; // did not reach destination
    int port;
    char name;
    DatagramSocket rcv_socket;
    ArrayList<Integer> known_nodes;
    ArrayList<Routing_Table_Entry> routing_table;
    Random rand;
    int subnet = 0xFFFFFF00;

    public NetNode(int ID) {
        name = id_to_name(ID);
        port = 9900 + ID;
        rand = new Random();
        System.out.println("Creating a new NetNode " + name);
        try {
            known_nodes = new ArrayList<Integer>(26);
            routing_table = new ArrayList<Routing_Table_Entry>(26);
            rcv_socket = new DatagramSocket(port);  // create socket at local port
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    static char id_to_name(int id) {
        return (char) ('A' + id);
    }

    static byte name_to_id(char name) {
        return (byte) (name - 'A');
    }

    public void run() {
        byte[] buffer = new byte[1024];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        System.out.println("NetNode " + name + " thread started");
        Message msg;
        // init_routing_table();
        while (true) { // loop forever
            try {
                rcv_socket.receive(request);
                // System.out.println("Node " + this.name + " message received");
                msg = new Message(request.getData());
                msg.time_to_live--;
                if (msg.type == Message.ROUTING_MSG) {
                    System.out.println("received routing msg: " + msg);
                    if (msg.dst != name || !is_router()) continue;
                    // it's a router receiving a routing update
                    msg_terminated++;
                    update_routing_table(msg.src(), msg.payload);
                    continue;
                }
                if (msg.dst == msg.src()) {
                    System.out.println("Node " + this.name + " is now terminated");
                    break;
                }
                if (msg.dst == name) {
                    System.out.println(msg + " is terminated at Node " + this.name + " reached its destination");
                    msg_terminated++;
                } else if (msg.time_to_live == 0) {
                    System.out.println(msg + " is terminated at Node " + this.name + " expired time_to_live");
                    msg_terminated++;
                    msg_lost++;
                } else {
                    int next_node = get_next_node(name_to_id(msg.dst));
                    // System.out.println("next hop is Node " + id_to_name(next_node));
                    if (next_node < 0) {
                        System.out.println(msg + " is terminated at Node " + this.name + " due to no available gateway1");
                    } else {
                        byte[] msg_buffer = msg.get_bytes(this.name);
                        DatagramPacket relay_msg = new DatagramPacket(msg_buffer, msg_buffer.length, InetAddress.getLocalHost(), 9900 + next_node);
                        sleep(rand.nextInt(1000));
                        System.out.println(msg + " is relayed at Node " + this.name + " to " + id_to_name(next_node));
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

    public int id() {
        return port - 9900;
    }

    public void add_node(int ID) {
        known_nodes.add(ID);
    }

    public int get_next_node(int dst_id) {
        int val = -1;

        if (is_in_my_sub_net(dst_id)) {
            // Yay, the next node is in my subnet, just return that node ID directly
            val = dst_id;
        }
        else if (is_router()) {
            // Oh, I'm a router, so the destination might be handled by some node in the routing table
            for (Routing_Table_Entry entry : routing_table) {
                if (is_in_subnet(entry, dst_id)) {
                    val = entry.next_hop - 'A';
                    break;
                }
            }
        }
        else {
            // Let the sender send to the default gateway, which will handle the next
            val = gateway();
        }

        return val;
    }

    public void send_msg(int to_node, int msg_size) {
        Message msg = new Message(this.name, id_to_name(to_node), msg_size);
        int next_node = get_next_node(to_node);
        Boolean thread_terminating_msg = false;
        System.out.println("next hop is Node " + id_to_name(next_node));
        if (to_node + 9900 == port) {
            next_node = to_node; // thread terminating message
            thread_terminating_msg = true;
        }
        if (next_node < 0) {
            System.out.println(msg + " is terminated at Node " + this.name + " due to no available gateway2");
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

    private Boolean is_in_subnet(Routing_Table_Entry entry, int id) {
        int router_id = name_to_id(entry.destination);
        // System.out.println("is_in_subnet: src = " + name + ", dst = " + id_to_name(id) + ", table entry: " + entry);
        // System.out.println("(id & entry.subnet) = " + String.format("0x%X", (id & entry.subnet)));
        // System.out.println("router_id = " + String.format("0x%X", router_id));
        return ((id & entry.subnet) == router_id);
    }

    private Boolean is_in_my_sub_net(int _id) {
        return (id() & subnet) == (_id & subnet);
    }

    private Boolean is_router() {
        return id() == (id() & subnet);
    }

    private int gateway() {
        return (id() & subnet);
    }

    private void add_routing_table_entry(Routing_Table_Entry entry) {
        routing_table.add(entry);
        System.out.println("Node" + name + " added: " + entry);
    }

    private void update_routing_table(char src, byte[] routing_msg_payload) {
        int num_entries = routing_msg_payload.length / Routing_Table_Entry.ENTRY_SIZE;
        Boolean updated = false;
        System.out.println("routing_msg_payload.length = " + routing_msg_payload.length + " number of routing entries = " + num_entries);
        for (int e = 0, indx = 0; e < num_entries; e++, indx += Routing_Table_Entry.ENTRY_SIZE) {
            Routing_Table_Entry entry = new Routing_Table_Entry(routing_msg_payload, indx);
            System.out.println("recovered entry #" + e + ", " + entry);
            byte hop_cost = (byte) (entry.hop_cost + 1); // it came from a neighbour node
            Boolean found = false;

            for (Routing_Table_Entry tableEntry: routing_table) {
                if (tableEntry.destination == entry.destination) { // try to find an existing route to dst
                    if (hop_cost < entry.hop_cost) {
                        entry.hop_cost = hop_cost;
                        entry.next_hop = src;
                        updated = true;
                    }
                    found = true;
                    break;
                }
            }

            if (!found) { // add a new entry to that destination using this source as my next hop
                entry.hop_cost = hop_cost;
                entry.next_hop = src;
                add_routing_table_entry(entry);
                updated = true;
            }
        }
        if (updated) broadcast_routing_table(); // send updated table to my neighbours
    }

    public void init_routing_table() {
        Routing_Table_Entry entry;
        if (!is_router()) {
            return; // only routers has routing table
        }
        entry = new Routing_Table_Entry(name, name, (byte) 0, (byte) subnet);   // local network
        add_routing_table_entry(entry);
        System.out.println(entry);
    }

    private byte[] get_routing_table_bytes() {
        byte[] bytes = new byte[routing_table.size() * Routing_Table_Entry.ENTRY_SIZE];
        byte[] entry_bytes;
        int indx = 0;
        for (Routing_Table_Entry entry : routing_table) {
            entry_bytes = entry.get_bytes();
            for (byte b : entry_bytes) {
                bytes[indx++] = b;
            }
        }
        return bytes;
    }

    private void broadcast_routing_table() {
        byte[] payload = get_routing_table_bytes();
        Message msg;
        for (int node : known_nodes) {
            if (!is_in_my_sub_net(node)) {
                msg = new Message(name, id_to_name(node), Message.ROUTING_MSG, payload);
                try {
                    byte[] msg_buffer = msg.get_bytes();
                    DatagramPacket relay_msg = new DatagramPacket(msg_buffer, msg_buffer.length, InetAddress.getLocalHost(), 9900 + node);
                    rcv_socket.send(relay_msg);
                    sleep(100 + rand.nextInt(500));
                    msg_sent++;
                } catch (SocketException e) {
                    System.out.println("Socket: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO: " + e.getMessage());
                } catch (InterruptedException e) {
                    System.out.println("Interrupted: " + e.getMessage());
                }
            }
        }
    }

    public void trigger_routing() {
        broadcast_routing_table();
    }

    public void display_routing_table() {
        if (!is_router()) return; // only routes have routing table
        System.out.println("===================================================");
        System.out.println("Roting Table for Node" + name);
        for (Routing_Table_Entry entry : routing_table) {
            System.out.println(entry);
        }
        System.out.println("===================================================");
    }

    public Boolean done_routing_table() {
        Boolean done = true;
        if (is_router()) {
            char[] routers = {'A', 'E', 'I', 'Q', 'U', 'Y', 'Z'};
            for (char r : routers) {
                Boolean found = false;
                for (Routing_Table_Entry entry : routing_table) {
                    if (entry.destination == r) {
                        found = true;
                        break;
                    }
                }
                if (!found) done = false;
            }
        }
        return done;
    }
}
