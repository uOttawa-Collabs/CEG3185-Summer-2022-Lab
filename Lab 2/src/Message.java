import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class Message implements Serializable {
    static int sequence_number = 0;
    final int INIT_TIME_TO_LIVE = 10;
    byte time_to_live;
    byte msg_id;
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
        sequence_number++;
    }

    public Message(byte[] bytes) { // unpack message
        time_to_live = bytes[0];
        msg_id = bytes[1];
        int path_size = INIT_TIME_TO_LIVE - time_to_live;
        if (path_size == 0) path_size = 1;
        byte[] slice = Arrays.copyOfRange(bytes, 3, 3 + path_size);
        payload = Arrays.copyOfRange(bytes, 3 + slice.length, bytes.length + 1);
        path = new String(slice);
        dst = (char) bytes[2];
    }

    public byte[] get_bytes() { // pack message to bytes
        byte[] bytes = new byte[payload.length + 3 + path.length()];
        byte[] path_bytes = path.getBytes();
        bytes[2] = (byte) dst;
        bytes[1] = msg_id;
        bytes[0] = time_to_live;
        int indx = 3;
        for (int i = 0; i < path_bytes.length; i++) bytes[indx++] = path_bytes[i];
        for (int i = 0; i < payload.length; i++) bytes[indx++] = payload[i];
        return bytes;
    }

    public byte[] get_bytes(char router) { // pack message to byte and insert router as source
        byte[] bytes = new byte[payload.length + 4 + path.length()];
        byte[] path_bytes = path.getBytes();
        bytes[3] = (byte) router;
        bytes[2] = (byte) dst;
        bytes[1] = msg_id;
        bytes[0] = time_to_live;
        int indx = 4;
        for (int i = 0; i < path_bytes.length; i++) bytes[indx++] = path_bytes[i];
        for (int i = 0; i < payload.length; i++) bytes[indx++] = payload[i];
        return bytes;
    }

    public String toString() {
        String val = ("msg#" + msg_id + ", time_to_live=" + time_to_live);
        val += ", dst: " + dst;
        val += ", path: " + path;
        return val;
    }

    public char src() {
        return path.charAt(0);
    }
}