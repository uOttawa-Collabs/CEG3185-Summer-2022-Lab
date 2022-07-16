class Routing_Table_Entry {
    static final int ENTRY_SIZE = 4; // 4 bytes
    char destination;
    char next_hop;
    byte hop_cost;
    byte subnet;

    public Routing_Table_Entry(char dst, char _next_hop, byte cost, byte _subnet) {
        destination = dst;
        next_hop = _next_hop;
        hop_cost = cost;
        subnet = _subnet;
    }

    public Routing_Table_Entry(byte[] bytes, int start_indx) {
        destination = (char) bytes[start_indx];
        next_hop = (char) bytes[start_indx + 1];
        hop_cost = bytes[start_indx + 2];
        subnet = bytes[start_indx + 3];
    }

    public byte[] get_bytes() {
        byte[] bytes = new byte[ENTRY_SIZE];
        bytes[0] = (byte) destination;
        bytes[1] = (byte) next_hop;
        bytes[2] = hop_cost;
        bytes[3] = subnet;
        return bytes;
    }

    public String toString() {
        String str = ("Routing_Table_Entry: dst: " + destination + ", next_hop: " + next_hop + ", subnet: " + String.format("0x%X", subnet) + ", cost: " + hop_cost);
        return str;
    }
}