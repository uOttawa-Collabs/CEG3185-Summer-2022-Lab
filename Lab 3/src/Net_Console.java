import java.util.Random;

public class Net_Console {
    private static final NetNode[] node_array;
    private static final int orphan_msg_cnt = 0;

    static {
        node_array = new NetNode[26]; // creating nodes A .. Z
        for (int i = 0; i < node_array.length; i++) {
            node_array[i] = new NetNode(i);
        }
    }

    public static void main(String[] args) {
        set_connections();
        node_array[0].trigger_routing();
        check_messages();
        for (int i = 0; i < node_array.length; i++) {
            node_array[i].display_routing_table();
        }
        send_messages();
        check_messages();
        terminate_nodes();
        System.out.println("##################################################");
        System.out.println("msg_lost " + NetNode.msg_lost);
        System.out.println("##################################################");

    }

    private static void set_connections() {
        int[] subnets = {4, 4, 8, 4, 4, 1, 1};
        int[] routers = {0, 4, 8, 16, 20, 24, 25};
        int start_indx = 0;
        int end_indx = 0;
        int subnet_mask;
        for (int subnet : subnets) {
            start_indx = end_indx;
            end_indx += subnet;
            subnet_mask = 0xFFFFFFFF ^ (subnet - 1);
            // fully connect nodes in a subnet
            for (int i = start_indx; i < end_indx; i++) {
                System.out.println("subnet: " + subnet + ", subnet_mask: " + String.format("0x%X", subnet_mask));
                node_array[i].subnet = subnet_mask;
                for (int j = start_indx; j < end_indx; j++) {
                    if (i == j) continue;
                    node_array[i].add_node(j);
                }
                node_array[i].init_routing_table();
            }
        }
        // connect routers - bidirectional
        node_array[routers[5]].add_node(routers[6]);
        node_array[routers[6]].add_node(routers[5]);  // 5 - 6
        node_array[routers[0]].add_node(routers[5]);
        node_array[routers[5]].add_node(routers[0]);  // 5 - 0
        node_array[routers[1]].add_node(routers[5]);
        node_array[routers[5]].add_node(routers[1]);  // 5 - 1
        node_array[routers[3]].add_node(routers[6]);
        node_array[routers[6]].add_node(routers[3]);  // 6 - 3
        node_array[routers[4]].add_node(routers[6]);
        node_array[routers[6]].add_node(routers[4]);  // 6 - 4
        node_array[routers[0]].add_node(routers[2]);
        node_array[routers[2]].add_node(routers[0]);  // 0 - 2
    }

    private static void send_messages() {
        System.out.println("##################################################");
        Random rand = new Random();
        int src, dst;
        int sz;
        for (int i = 0; i < 100; i++) {
            src = rand.nextInt(26);
            do {
                dst = rand.nextInt(26);
            } while (src == dst); // make sure src & dst are different
            sz = rand.nextInt(500);
            node_array[src].send_msg(dst, sz);
        }
        System.out.println("##################################################");
    }

    private static void terminate_nodes() {
        for (int i = 0; i < node_array.length; i++) {
            node_array[i].send_msg(i, 0);
        }
    }

    private static void check_messages() {
        System.out.println("##################################################");
        System.out.println("###### Check that all messages sent are received ######");
        Boolean done_routing = false;
        while (NetNode.msg_sent != (NetNode.msg_terminated + orphan_msg_cnt) || !done_routing) {
            System.out.println(" ###### " + "msg_sent " + NetNode.msg_sent + "  msg_terminated " + NetNode.msg_terminated + " done_routing " + done_routing + " ###### ");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e.getMessage());
            }
            done_routing = true; // assume done till proven wrong
            for (int i = 0; i < node_array.length; i++) {
                done_routing &= node_array[i].done_routing_table();
            }
            System.out.println(" done_routing " + done_routing);
        }
        System.out.println(" ###### " + "msg_sent " + NetNode.msg_sent + "  msg_terminated " + NetNode.msg_terminated + " orphan_msg_cnt " + orphan_msg_cnt + " ###### ");
        System.out.println("###### All messages sent are received ######");
        System.out.println("##################################################");
    }

    // your tasks here
    private static void task1() {
    }

    private static void task2() {
    }

    private static void task3() {
    }

}