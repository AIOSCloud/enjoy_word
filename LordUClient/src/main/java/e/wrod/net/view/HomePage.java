package e.wrod.net.view;

import e.wrod.net.model.Room;
import e.wrod.net.model.User;
import e.wrod.net.component.MusicPanel;
import e.wrod.net.utils.MyWebSocketClient;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 斗地主首页
 */
public class HomePage extends JFrame implements ActionListener {
    private final Logger logger = Logger.getLogger(HomePage.class);
    private User user;
    private Container container = null; //面板容器
    private MusicPanel musicPanel;
    JMenuItem offline, online, exit, about; //界面上面的按钮
    String home = "http://localhost:18090/home";

    HomePage(User user) {
        this.user = user;
        //界面出事化
        Init();
        // 设置菜单按钮
        setMenu();
        //设置用户首次进入界面的动画
        setHomePage();
        // 设置当前面板可见
        this.setVisible(true);
    }

    //界面初始化
    public void Init() {
        this.setTitle("互联世界");
        this.setSize(1024, 548);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        container = this.getContentPane();
        container.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 菜单按钮布局
     */
    public void setMenu() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu game = new JMenu("开始");
        JMenu help = new JMenu("帮助");
        offline = new JMenuItem("单机");
        online = new JMenuItem("在线");
        exit = new JMenuItem("退出");
        about = new JMenuItem("关于");
        offline.addActionListener(this);
        online.addActionListener(this);
        exit.addActionListener(this);
        about.addActionListener(this);
        game.add(offline);
        game.add(online);
        game.add(exit);
        help.add(about);
        jMenuBar.add(game);
        jMenuBar.add(help);
        this.setJMenuBar(jMenuBar);
    }

    public void setHomePage() {
        try {
            musicPanel = new MusicPanel("images/chat/first.jpg", null);
            //musicPanel.loopMusic();
            musicPanel.setSize(1024, 548);
            musicPanel.setVisible(true);
            container.add(musicPanel);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("首页初始化失败:" + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == offline) {
            //单机对战
            new OffLinePage();
        }
        if (e.getSource() == online) {
            // TODO: 2020/3/12 在线对战
        }
        if (e.getSource() == about) {
            //关于
            JOptionPane.showMessageDialog(this, "互联世界");
        }
        if (e.getSource() == exit) {
            //退出
            this.dispose();
            System.exit(0);
        }
    }
}
