import java.util.Random;

public class Net_Console {
    private static final NetNode[] node_array;
    private static final Random randomizer;
    private static int orphan_msg_cnt = 0;

    static {
        node_array = new NetNode[26]; // creating nodes A .. Z
        for (int i = 0; i < node_array.length; i++) {
            node_array[i] = new NetNode(i);
        }
        randomizer = new Random();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            set_connections();
            send_messages();
            node_array[15].send_msg(25, 25);
            orphan_msg_cnt++;
        } else {
            int task = args[0].charAt(0) - '0';
            switch (task) {
                case 1:
                    task1();
                    break;
                case 2:
                    task2();
                    break;
                case 3:
                    task3();
                    break;
                default:
                    System.out.println("Usage: java Net_Console [<task#>]");
            }
        }

        check_messages();
        terminate_nodes();

    }

    private static void set_connections() {
    }

    private static void send_messages() {
    }

    private static void terminate_nodes() {
        for (int i = 0; i < node_array.length; i++) {
            node_array[i].send_msg(i, 0);
        }
    }

    private static void check_messages() {
        while (NetNode.msg_sent != (NetNode.msg_terminated + orphan_msg_cnt)) {
            System.out.println("msg_sent " + NetNode.msg_sent + "  msg_terminated " + NetNode.msg_terminated + " ###### ");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e.getMessage());
            }
        }
    }

    // your tasks here
    private static void task1() {
        orphan_msg_cnt += 5;
        // From A -> H
        System.out.println("Creating daisy chain connection from A -> H");
        for (char startNode = 'A'; startNode < 'H'; ++startNode) {
            node_array[startNode - 'A'].add_node(startNode - 'A' + 1);
        }

        // From I -> P
        System.out.println("Creating daisy chain connection from I -> P");
        for (char startNode = 'I'; startNode < 'P'; ++startNode) {
            node_array[startNode - 'A'].add_node(startNode - 'A' + 1);
        }

        // From R -> Y
        System.out.println("Creating daisy chain connection from R -> Y");
        for (char startNode = 'R'; startNode < 'Y'; ++startNode) {
            node_array[startNode - 'A'].add_node(startNode - 'A' + 1);
        }

        SourceDestinationPairStruct[] array = {new SourceDestinationPairStruct() {{
            source = 'A';
            destination = 'H';
        }}, new SourceDestinationPairStruct() {{
            source = 'B';
            destination = 'D';
        }}, new SourceDestinationPairStruct() {{
            source = 'I';
            destination = 'P';
        }}, new SourceDestinationPairStruct() {{
            source = 'K';
            destination = 'R';
        }}, new SourceDestinationPairStruct() {{
            source = 'Y';
            destination = 'T';
        }}, new SourceDestinationPairStruct() {{
            source = 'R';
            destination = 'Z';
        }}, new SourceDestinationPairStruct() {{
            source = 'C';
            destination = 'C';
        }}, new SourceDestinationPairStruct() {{
            source = 'B';
            destination = 'G';
        }}};

        for (SourceDestinationPairStruct e : array) {
            // Random from 10 to 40
            int messageSize = randomizer.nextInt(31) + 10;

            System.out.printf("Sending from node %c to node %c with message size = %d\n", e.source, e.destination, messageSize);
            node_array[e.source - 'A'].send_msg(e.destination - 'A', messageSize);
        }
    }

    private static void task2() {
        orphan_msg_cnt += 4;

        // From A -> Z
        System.out.println("Creating daisy chain connection from A -> Z");
        for (char startNode = 'A'; startNode < 'Z'; ++startNode) {
            node_array[startNode - 'A'].add_node(startNode - 'A' + 1);
        }

        SourceDestinationPairStruct[] array = {new SourceDestinationPairStruct() {{
            source = 'A';
            destination = 'Z';
        }}, new SourceDestinationPairStruct() {{
            source = 'B';
            destination = 'D';
        }}, new SourceDestinationPairStruct() {{
            source = 'I';
            destination = 'P';
        }}, new SourceDestinationPairStruct() {{
            source = 'K';
            destination = 'R';
        }}, new SourceDestinationPairStruct() {{
            source = 'Y';
            destination = 'T';
        }}, new SourceDestinationPairStruct() {{
            source = 'R';
            destination = 'Z';
        }}, new SourceDestinationPairStruct() {{
            source = 'C';
            destination = 'C';
        }}, new SourceDestinationPairStruct() {{
            source = 'B';
            destination = 'G';
        }}};

        for (SourceDestinationPairStruct e : array) {
            // Random from 10 to 40
            int messageSize = randomizer.nextInt(31) + 10;

            System.out.printf("Sending from node %c to node %c with message size = %d\n", e.source, e.destination, messageSize);
            node_array[e.source - 'A'].send_msg(e.destination - 'A', messageSize);
        }
    }

    private static void task3() {
        orphan_msg_cnt += 3;

        SourceDestinationPairStruct[] connectionArray = {new SourceDestinationPairStruct() {{
            source = 'A';
            destination = 'B';
        }}, new SourceDestinationPairStruct() {{
            source = 'A';
            destination = 'C';
        }}, new SourceDestinationPairStruct() {{
            source = 'A';
            destination = 'D';
        }}, new SourceDestinationPairStruct() {{
            source = 'B';
            destination = 'E';
        }}, new SourceDestinationPairStruct() {{
            source = 'B';
            destination = 'F';
        }}, new SourceDestinationPairStruct() {{
            source = 'B';
            destination = 'G';
        }}, new SourceDestinationPairStruct() {{
            source = 'C';
            destination = 'F';
        }}, new SourceDestinationPairStruct() {{
            source = 'C';
            destination = 'G';
        }}, new SourceDestinationPairStruct() {{
            source = 'C';
            destination = 'H';
        }}, new SourceDestinationPairStruct() {{
            source = 'D';
            destination = 'G';
        }}, new SourceDestinationPairStruct() {{
            source = 'D';
            destination = 'H';
        }}, new SourceDestinationPairStruct() {{
            source = 'D';
            destination = 'I';
        }}, new SourceDestinationPairStruct() {{
            source = 'E';
            destination = 'J';
        }}, new SourceDestinationPairStruct() {{
            source = 'E';
            destination = 'K';
        }}, new SourceDestinationPairStruct() {{
            source = 'E';
            destination = 'L';
        }}, new SourceDestinationPairStruct() {{
            source = 'F';
            destination = 'K';
        }}, new SourceDestinationPairStruct() {{
            source = 'F';
            destination = 'L';
        }}, new SourceDestinationPairStruct() {{
            source = 'F';
            destination = 'M';
        }}, new SourceDestinationPairStruct() {{
            source = 'G';
            destination = 'L';
        }}, new SourceDestinationPairStruct() {{
            source = 'G';
            destination = 'M';
        }}, new SourceDestinationPairStruct() {{
            source = 'G';
            destination = 'N';
        }}, new SourceDestinationPairStruct() {{
            source = 'H';
            destination = 'M';
        }}, new SourceDestinationPairStruct() {{
            source = 'H';
            destination = 'N';
        }}, new SourceDestinationPairStruct() {{
            source = 'H';
            destination = 'O';
        }}, new SourceDestinationPairStruct() {{
            source = 'I';
            destination = 'N';
        }}, new SourceDestinationPairStruct() {{
            source = 'I';
            destination = 'O';
        }}, new SourceDestinationPairStruct() {{
            source = 'I';
            destination = 'P';
        }}, new SourceDestinationPairStruct() {{
            source = 'J';
            destination = 'Q';
        }}, new SourceDestinationPairStruct() {{
            source = 'K';
            destination = 'Q';
        }}, new SourceDestinationPairStruct() {{
            source = 'L';
            destination = 'Q';
        }}, new SourceDestinationPairStruct() {{
            source = 'K';
            destination = 'R';
        }}, new SourceDestinationPairStruct() {{
            source = 'L';
            destination = 'R';
        }}, new SourceDestinationPairStruct() {{
            source = 'M';
            destination = 'R';
        }}, new SourceDestinationPairStruct() {{
            source = 'L';
            destination = 'S';
        }}, new SourceDestinationPairStruct() {{
            source = 'M';
            destination = 'S';
        }}, new SourceDestinationPairStruct() {{
            source = 'N';
            destination = 'S';
        }}, new SourceDestinationPairStruct() {{
            source = 'M';
            destination = 'T';
        }}, new SourceDestinationPairStruct() {{
            source = 'N';
            destination = 'T';
        }}, new SourceDestinationPairStruct() {{
            source = 'O';
            destination = 'T';
        }}, new SourceDestinationPairStruct() {{
            source = 'N';
            destination = 'U';
        }}, new SourceDestinationPairStruct() {{
            source = 'O';
            destination = 'U';
        }}, new SourceDestinationPairStruct() {{
            source = 'P';
            destination = 'U';
        }}, new SourceDestinationPairStruct() {{
            source = 'Q';
            destination = 'V';
        }}, new SourceDestinationPairStruct() {{
            source = 'R';
            destination = 'V';
        }}, new SourceDestinationPairStruct() {{
            source = 'S';
            destination = 'V';
        }}, new SourceDestinationPairStruct() {{
            source = 'R';
            destination = 'W';
        }}, new SourceDestinationPairStruct() {{
            source = 'S';
            destination = 'W';
        }}, new SourceDestinationPairStruct() {{
            source = 'T';
            destination = 'W';
        }}, new SourceDestinationPairStruct() {{
            source = 'S';
            destination = 'X';
        }}, new SourceDestinationPairStruct() {{
            source = 'T';
            destination = 'X';
        }}, new SourceDestinationPairStruct() {{
            source = 'U';
            destination = 'X';
        }}, new SourceDestinationPairStruct() {{
            source = 'V';
            destination = 'Y';
        }}, new SourceDestinationPairStruct() {{
            source = 'W';
            destination = 'Y';
        }}, new SourceDestinationPairStruct() {{
            source = 'X';
            destination = 'Y';
        }}, new SourceDestinationPairStruct() {{
            source = 'Y';
            destination = 'Z';
        }}};

        // Create connection
        for (SourceDestinationPairStruct e : connectionArray) {
            System.out.printf("Creating connection %c -> %c\n", e.source, e.destination);
            node_array[e.source - 'A'].add_node(e.destination - 'A');
        }


        SourceDestinationPairStruct[] messageArray = {new SourceDestinationPairStruct() {{
            source = 'A';
            destination = 'Z';
        }}, new SourceDestinationPairStruct() {{
            source = 'B';
            destination = 'Z';
        }}, new SourceDestinationPairStruct() {{
            source = 'C';
            destination = 'Z';
        }}, new SourceDestinationPairStruct() {{
            source = 'K';
            destination = 'Z';
        }}, new SourceDestinationPairStruct() {{
            source = 'Y';
            destination = 'Z';
        }}, new SourceDestinationPairStruct() {{
            source = 'G';
            destination = 'C';
        }}, new SourceDestinationPairStruct() {{
            source = 'N';
            destination = 'M';
        }}, new SourceDestinationPairStruct() {{
            source = 'E';
            destination = 'U';
        }}};

        for (SourceDestinationPairStruct e : messageArray) {
            // Random from 10 to 40
            int messageSize = randomizer.nextInt(31) + 10;

            System.out.printf("Sending from node %c to node %c with message size = %d\n", e.source, e.destination, messageSize);
            node_array[e.source - 'A'].send_msg(e.destination - 'A', messageSize);
        }
    }

    private static class SourceDestinationPairStruct {
        public char source;
        public char destination;
    }
}
