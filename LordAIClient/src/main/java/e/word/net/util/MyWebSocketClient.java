package e.word.net.util;

import com.alibaba.fastjson.JSON;
import e.word.net.common.*;
import e.word.net.model.Card;
import e.word.net.model.Event;
import e.word.net.model.Message;
import e.word.net.model.User;
import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.rmi.MarshalException;
import java.util.List;

public class MyWebSocketClient extends WebSocketClient {
    Logger logger = Logger.getLogger(MyWebSocketClient.class);

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.debug("------onOpen---------");
    }

    @Override
    public void onMessage(String message) {
        logger.debug("机器人出牌I......" + message);
        // TODO: 2020/3/17 接受服务端发送的消息
        Event event = JSON.parseObject(message, Event.class);
        if (event.getType().equals("建立链接。。。。。。")) {
            logger.debug("建立连接。。。。。。");
        } else if (event.getType().equals("发牌")) {
            logger.debug("发牌......");
            // TODO: 2020/3/17 根据牌来抢地主
        } else if (event.getType().equals("地主")) {
            // TODO: 2020/3/18 获取地主信息 
            logger.debug("抢地主......");
            // TODO: 2020/3/18 如果自己是地主，那么出牌
            //获取自己的位置
            int mineIndex = event.getIndex();
            //获取地主的位置
            int lordIndex = event.getLordIndex();
            if (mineIndex == lordIndex) {
                //如果自己位置与地主的位置是一样的，那么需要出牌
                boolean follow = false;
                AIPlayer player = new AIPlayer(event.getPlayers(), event.getShows(), mineIndex, lordIndex, false);
                List<Card> cards = player.play();
                second(30);
                //出牌
                Event result = new Event();
                result.setType("出牌");
                result.setShows(cards);
                result.setIndex(mineIndex);
                result.setUser(event.getUser());
                result.setShowIndex(mineIndex);
                result.setPlay(true);
                result.setPlayIndex(mineIndex);
                this.send(JSON.toJSONString(result));
                logger.debug("机器人是用户:" + mineIndex);
            }
        } else if (event.getType().equals("出牌")) {
            second(30);
            int turn = event.getTurn();
            int mineIndex = event.getIndex();
            int lordIndex = event.getLordIndex();
            int showIndex = event.getShowIndex();
            boolean follow = false;
            //如果轮到自己出牌
            if (showIndex != mineIndex) {
                follow = true;
            }
            User user = event.getUser();
            // TODO: 2020/3/18 轮到自己出牌
            List<Card> players = event.getPlayers();
            List<Card> shows = event.getShows();
            AIPlayer player = new AIPlayer(players, shows, mineIndex, lordIndex, follow);
            List<Card> cards = player.play();
            logger.debug("出牌张数:" + cards.size());
            //出牌
            Event result = new Event();
            result.setType("出牌");
            result.setUser(user);
            result.setIndex(mineIndex);
            if (cards.size() > 0) {
                //出牌
                result.setShows(cards);
                result.setShowIndex(mineIndex);
                result.setPlay(true);
                result.setPlayIndex(mineIndex);
            } else {
                //没出牌
                result.setShows(event.getShows());
                result.setShowIndex(event.getShowIndex());
                result.setPlay(false);
                result.setPlayIndex(mineIndex);
            }
            this.send(JSON.toJSONString(result));
        }
    }

    public void second(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (Exception e) {
            logger.debug("线程休眠失败......");
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.debug(s);
        logger.debug("------onClose---------");
    }

    @Override
    public void onError(Exception e) {
        logger.debug(e);
        logger.debug("服务链接失败");
    }
}
