package ours.shifumissage;

import java.util.concurrent.ThreadLocalRandom;

public class Crypthor {
    private int key;
    private String msg;
    final int minOffset = 97;
    final int majOffset = 65;

    public Crypthor (){
        this.key = this.genCesarKey();
    }

    public String cryptCesar(String msg, int key){
        int letter;
        int offset = minOffset; // minuscules
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < msg.length(); i ++){
            letter = (int) msg.charAt(i) - offset;
            letter = (letter + key)%26 + offset;
            res.append((char) letter);
        }
        return res.toString();
    }

    public String decryptCesar(String msg, int key){
        int letter;
        int offset = minOffset;
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < msg.length(); i ++){
            letter = (int) msg.charAt(i) - offset;
            letter = (letter - key + 26)%26 + offset;
            res.append((char) letter);
        }
        return res.toString();
    }

    private int genCesarKey(){
        // new standard way to gen randint
        int randomNum = ThreadLocalRandom.current().nextInt(0, 25 + 1);
        return randomNum;
    }



    //____________________getters & setters_____________________

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
