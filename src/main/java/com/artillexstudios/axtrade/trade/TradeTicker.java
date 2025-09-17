package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axtrade.AxTrade;
import com.artillexstudios.axtrade.request.Request;
import com.artillexstudios.axtrade.request.Requests;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;
import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;
import static com.artillexstudios.axtrade.trade.Trade.checkItem;

public class TradeTicker {

    public static void start() {
        Scheduler.get().runTimer(scheduledTask -> {
            for (Trade trade : Trades.getTrades()) {
                if (trade.isEnded()) continue;
                if (!(trade.player1.hasConfirmed() && trade.player2.hasConfirmed())) continue;

                if (trade.player1.getConfirmed() == 1) {
                    trade.complete();

                    Bukkit.getScheduler().runTaskLater(AxTrade.plugin, () -> {
                        for (ItemStack item : trade.player1.getPlayer().getInventory()) {
                            checkItem(trade.player1.getPlayer(), item);
                        }
                        for (ItemStack item : trade.player2.getPlayer().getInventory()) {
                            checkItem(trade.player2.getPlayer(), item);
                        }
                    }, 1L);



                    continue;
                }

                trade.player1.tick();
                trade.player2.tick();

            }

            final Iterator<Request> iterator = Requests.getRequests().iterator();
            while (iterator.hasNext()) {
                Request request = iterator.next();
                if (System.currentTimeMillis() - request.getTime() <= CONFIG.getInt("trade-request-expire-seconds", 60) * 1_000L) continue;
                if (request.isActive()) {
                    MESSAGEUTILS.sendLang(request.getSender(), "request.expired", Map.of("%player%", request.getReceiver().getName()));
                }
                iterator.remove();
            }


        }, 20, 20);
    }
}
