package FinalSocket;

import java.io.Serializable;

public class Message implements Serializable {
    public int mes_id;
    public String time;
    public String tag;
    public String content;

    public Message(int mes_id, String time, String tag, String content) {
        this.mes_id = mes_id;
        this.time = time;
        this.tag = tag;
        this.content = content;
    }

    @Override
    public String toString() {
        return mes_id + " " + time + " " + tag + " " + content;
    }
}
