import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class Message implements Serializable {
    static final byte NORMAL_MSG = 0;
    static final byte ROUTING_MSG = 1;
    static int sequence_number = 0;
    final int INIT_TIME_TO_LIVE = 10;
    byte time_to_live;
    byte msg_id;
    byte type;
    byte payload_size; // in 32b word, 4 bytes each
    char dst;
    String path;
    byte[] payload;

    public Message(char _src, char _dst, int size) {
        Random rand = new Random();
        byte[] tmp = "-start of message:".getBytes();

        payload = new byte[size + tmp.length];
        rand.nextBytes(payload);
        for (int i = 0; i < tmp.length; i++) payload[i] = tmp[i];
        path = String.valueOf(_src);
        dst = _dst;
        time_to_live = INIT_TIME_TO_LIVE;
        msg_id = (byte) (sequence_number & 0xFF);
        type = NORMAL_MSG;
        payload_size = (byte) ((payload.length + 3) / 4);
        sequence_number++;
    }

    public Message(char _src, char _dst, byte _type, byte[] _payload) {
        Random rand = new Random();

        payload = _payload;
        path = String.valueOf(_src);
        dst = _dst;
        time_to_live = INIT_TIME_TO_LIVE;
        msg_id = (byte) (sequence_number & 0xFF);
        type = _type;
        payload_size = (byte) ((payload.length + 3) / 4);
        sequence_number++;
    }

    public Message(byte[] bytes) { // unpack message
        int indx, payload_sz;
        time_to_live = bytes[0];
        msg_id = bytes[1];
        type = bytes[2];
        payload_size = bytes[3];
        payload_sz = payload_size & 0xFF;
        dst = (char) bytes[4];
        indx = 5;
        int path_size = INIT_TIME_TO_LIVE - time_to_live + 1;
        // if (path_size == 0) path_size = 1;
        byte[] path_bytes = Arrays.copyOfRange(bytes, indx, indx + path_size);
        payload = Arrays.copyOfRange(bytes, indx + path_size, indx + path_size + (payload_sz * 4));
        path = new String(path_bytes);
    }

    private byte[] header_2_bytes() {
        int offset = 5;
        byte[] header = new byte[offset + path.length()];
        byte[] path_bytes = path.getBytes();
        header[0] = time_to_live;
        header[1] = msg_id;
        header[2] = type;
        header[3] = payload_size;
        header[4] = (byte) dst;
        for (int i = 0; i < path_bytes.length; i++) header[offset++] = path_bytes[i];
        return header;
    }

    public byte[] get_bytes() { // pack message to bytes
        byte[] header = header_2_bytes();
        byte[] bytes = new byte[payload.length + header.length];
        int indx = 0;
        for (int i = 0; i < header.length; i++) bytes[indx++] = header[i];
        for (int i = 0; i < payload.length; i++) bytes[indx++] = payload[i];
        return bytes;
    }

    public byte[] get_bytes(char router) { // pack message to byte and insert router as source
        path = router + path;
        return get_bytes();
    }

    public String toString() {
        int id = msg_id & 0xFF;
        String val = ("msg#" + id + ", time_to_live=" + time_to_live);
        switch (type) {
            case NORMAL_MSG:
                val += ", type: NORMAL_MSG";
                break;
            case ROUTING_MSG:
                val += ", type: ROUTING_MSG";
                break;
        }
        val += ", dst: " + dst;
        val += ", path: " + path;
        return val;
    }

    public char src() {
        return path.charAt(0);
    }
}