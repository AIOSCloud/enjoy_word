package e.word.net.view;

import com.alibaba.fastjson.JSON;
import e.word.net.common.CardType;
import e.word.net.common.Common;
import e.word.net.component.JCard;
import e.word.net.model.Card;
import e.word.net.model.Event;
import e.word.net.model.User;
import e.word.net.utils.MyWebSocketClient;
import e.word.net.utils.Time;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

//房间
public class RoomPage extends JFrame implements ActionListener {
    Logger logger = Logger.getLogger(RoomPage.class);
    String uri = "ws://localhost:8090/websocket";
    MyWebSocketClient ws;
    public User user;
    public List<User> users;
    public Container container = null; //面板容器
    public JMenuItem start, exit, about; //界面上面的按钮
    public JButton[] landlord = new JButton[2]; //抢地主，抢，不抢的按钮
    public JButton[] publishCard = new JButton[2]; //出牌　出，不出的按钮
    public JTextField time[] = new JTextField[3];
    public JCard jCards[] = new JCard[54];
    public List<JCard> lordList = new ArrayList<JCard>();
    public List<JCard>[] players = new ArrayList[3];
    public List<JCard>[] shows = new ArrayList[3];
    //地主标签
    public JLabel lord;
    public int lordFlag;
    public int turn;
    public int mine;
    // TODO: 2020/3/18 跟牌的上家
    public int showIndex;
    public int lastShowIndex;
    public boolean isRun = true;
    public Time t;

    public RoomPage(User user) {
        this.user = user;
        wesocket();
        createLink();
        //界面出事化
        Init();
        // 设置菜单按钮
        setMenu();
        //设置页面信息
        setPage();
        setImage();
        // TODO: 2020/3/17 链接初始化
        // TODO: 2020/3/17 牌面初始化
        CardInit();
        // TODO: 2020/3/17创建房间
        createRoom();
        // 设置当前面板可见
        this.setVisible(true);
    }

    public void wesocket() {
        try {
            ws = new MyWebSocketClient(new URI(uri), this);
            ws.connect();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("用户客户端接入失败");
        }
    }

    public void createLink() {
        while (!ws.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            second(1);
            logger.debug("链接初始化中，请稍后.....");
        }
        Event event = new Event();
        event.setUser(user);
        event.setType("建立链接");
        ws.send(JSON.toJSONString(event));
    }

    //界面初始化
    public void Init() {
        this.setTitle("互联世界");
        this.setSize(830, 620);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        container = this.getContentPane();
        container.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container.setBackground(new Color(0, 112, 26));

        for (int i = 0; i < 3; i++) {
            players[i] = new ArrayList<>();
            shows[i] = new ArrayList<>();
        }
    }

    /**
     * 菜单按钮布局
     */
    public void setMenu() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu game = new JMenu("开始");
        JMenu help = new JMenu("帮助");
        start = new JMenuItem("开始");
        exit = new JMenuItem("退出");
        about = new JMenuItem("关于");
        start.addActionListener(this);
        exit.addActionListener(this);
        about.addActionListener(this);
        game.add(start);
        game.add(exit);
        help.add(about);
        jMenuBar.add(game);
        jMenuBar.add(help);
        this.setJMenuBar(jMenuBar);
    }

    public void setPage() {
        landlord[0] = new JButton("抢地主");
        landlord[1] = new JButton("不 抢");
        publishCard[0] = new JButton("出牌");
        publishCard[1] = new JButton("不要");
        for (int i = 0; i < 2; i++) {
            publishCard[i].setBounds(320 + i * 100, 400, 60, 20);
            landlord[i].setBounds(320 + i * 100, 400, 75, 20);
            container.add(landlord[i]);
            landlord[i].addActionListener(this);
            landlord[i].setVisible(false);
            container.add(publishCard[i]);
            publishCard[i].setVisible(false);
            publishCard[i].addActionListener(this);
        }
        for (int i = 0; i < 3; i++) {
            time[i] = new JTextField("倒计时:");
            time[i].setVisible(false);
            container.add(time[i]);
        }
        time[0].setBounds(140, 230, 60, 20);
        time[1].setBounds(374, 360, 60, 20);
        time[2].setBounds(620, 230, 60, 20);
    }

    public void setImage() {
        lord = new JLabel(new ImageIcon(ClassLoader.getSystemResource("images/card/dizhu.gif")));
        lord.setVisible(false);
        lord.setSize(40, 40);
        container.add(lord);
    }

    public void CardInit() {
        int count = 0;
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 13; j++) {
                if ((i == 5) && (j > 2)) {
                    break;
                } else {
                    Card card = new Card(i, j);
                    jCards[count] = new JCard(card, false);
                    jCards[count].setLocation(350 + i * 5, 50);
                    container.add(jCards[count]);
                    count++;
                }
            }
        }
    }

    public void createRoom() {
        logger.debug("创建房间");
        second(3);
        while (!ws.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            second(1);
            logger.debug("链接初始化中，请稍后.....");
        }
        logger.debug("创建房间请求......" + user);
        Event event = new Event();
        event.setUser(user);
        event.setOnline(false);
        event.setType("创建房间");
        ws.send(JSON.toJSONString(event));
    }

    public void second(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (Exception e) {
            logger.debug("线程休眠失败......");
        }
    }

    // 设定地主
    public void setLord(int i) {
        Point point = new Point();
        if (i == 1)// 我是地主
        {
            point.x = 80;
            point.y = 430;
        }
        if (i == 0) {
            point.x = 80;
            point.y = 20;
        }
        if (i == 2) {
            point.x = 700;
            point.y = 20;
        }
        lord.setLocation(point);
        lord.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == landlord[0]) {
            t.isRun = false;
            landlord[0].setVisible(false);
            landlord[1].setVisible(false);
            time[1].setText("抢地主");
            // TODO: 2020/3/17 抢地主
            Event event = new Event();
            event.setType("抢地主");
            event.setLord(true);
            event.setUser(user);
            ws.send(JSON.toJSONString(event));
        }
        if (e.getSource() == landlord[1]) {
            t.isRun = false;
            landlord[0].setVisible(false);
            landlord[1].setVisible(false);
            time[1].setText("不抢");
            // TODO: 2020/3/17 不抢
            Event event = new Event();
            event.setType("抢地主");
            event.setLord(false);
            event.setUser(user);
            ws.send(JSON.toJSONString(event));
        }
        if (e.getSource() == publishCard[0]) {
            // TODO: 2020/3/18 出牌
            logger.debug("出牌......");
            // TODO: 2020/3/18 不要
            t.isRun = false;
            // 出牌
            List<JCard> cards = new ArrayList<JCard>();
            for (int i = 0; i < players[mine].size(); i++) {
                JCard card = players[1].get(i);
                if (card.isClicked()) {
                    cards.add(card);
                }
            }
            int flag = 0;
            logger.info("开始判断是否可以出牌......");
            if (turn == mine && showIndex == mine) {
                //轮到自己出牌 并且上次是自己出牌
                if (Common.jugdeType(cards) != CardType.c0) {
                    flag = 1;
                }
            } else {
                List<JCard> currentList = shows[showIndex];
                flag = Common.checkCards(cards, currentList);
            }
            //可以出牌
            logger.debug("判断是否可以出牌:" + flag);
            if (flag == 1) {
                shows[mine].clear();
                shows[mine].addAll(cards);
                players[mine].removeAll(cards);
                //定位出牌
                Point point = new Point();
                point.x = (770 / 2) - (shows[mine].size() + 1) * 15 / 2;
                point.y = 300;
                for (int i = 0, len = shows[mine].size(); i < len; i++) {
                    JCard card = shows[1].get(i);
                    Common.move(card, card.getLocation(), point);
                    point.x += 15;
                }
                //重新理牌
                Common.rePosition(this, players[mine], 1);

                //出牌
                Event result = new Event();
                result.setType("出牌");
                result.setIndex(mine);
                result.setUser(user);
                result.setShows(Common.getCards(cards));
                result.setShowIndex(mine);
                result.setPlay(true);
                result.setPlayIndex(mine);
                ws.send(JSON.toJSONString(result));

                time[mine].setVisible(false);
                publishCard[0].setVisible(false);
                publishCard[1].setVisible(false);

                shows[(mine + 1) % 3].clear();
                time[(mine + 1) % 3].setVisible(true);
                t = new Time(this, ws, true, false);
                t.start();
                logger.debug("机器人是用户:" + mine);
            } else {
                JOptionPane.showMessageDialog(this, "请正确出牌......");
            }

        }
        if (e.getSource() == publishCard[1]) {
            logger.debug("出牌......");
            // TODO: 2020/3/18 不要
            t.isRun = false;
            turn = (turn + 1) % 3;
            time[mine].setText("不要");

            //出牌
            Event result = new Event();
            result.setType("出牌");
            result.setUser(user);
            //没出牌
            result.setShows(Common.getCards(shows[showIndex]));
            result.setShowIndex(showIndex);
            result.setIndex(mine);
            result.setPlay(false);
            result.setPlayIndex(mine);
            ws.send(JSON.toJSONString(result));

            time[mine].setVisible(false);
            publishCard[0].setVisible(false);
            publishCard[1].setVisible(false);

            shows[(turn + 1) % 3].clear();
            time[(mine + 1) % 3].setVisible(true);
            time[(turn + 2) % 3].setVisible(false);
            t = new Time(this, ws, true, false);
            t.start();
        }
    }
}
