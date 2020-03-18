package e.word.net.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Event {
    String type;
    int status;
    boolean next;
    boolean isLord;
    boolean online;
    int lordIndex;
    int turn;
    User user;
    List<User> users;
    List<Card> lordList;
    // TODO: 2020/3/17 用户牌 
    List<Card> players;
    // TODO: 2020/3/17 用户出牌
    List<Card> shows;

}