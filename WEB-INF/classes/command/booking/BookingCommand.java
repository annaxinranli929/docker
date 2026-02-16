package command.booking;

import java.util.HashMap;
import java.util.Map;

import logic.RequestContext;
import logic.ResponseContext;

import bean.booking.BookingBean;
import bean.account.AccountBean;
import bean.price.PriceBean;
import bean.card.CardBean;
import command.AbstractCommand;
import dao.AbstractDao;
import dao.ConnectionManager;
import dao.price.PriceDao;
import dao.card.CardDao;
import logic.MailUtil; 
import logic.QrUtil;
import logic.TicketTokenUtil;

public class BookingCommand extends AbstractCommand<BookingBean> {

    @Override
    public ResponseContext execute(ResponseContext resc) {

        Map<String, Object> results = new HashMap<>();
        RequestContext reqc = getRequestContext();

        // schedule / seat チェック 
        String[] scheduleIdArr = reqc.getParameter("scheduleId");
        String[] seatIds = reqc.getParameter("seatIds");

        if (scheduleIdArr == null || scheduleIdArr.length == 0) {
            results.put("bookingResult", false);
            results.put("error", "scheduleIdがありません");
            resc.setResult(results);
            resc.setTarget("booking_result");
            return resc;
        }

        if (seatIds == null || seatIds.length == 0) {
            results.put("bookingResult", false);
            results.put("error", "座席が選択されていません");
            resc.setResult(results);
            resc.setTarget("booking_result");
            return resc;
        }
        
        

        int scheduleId = Integer.parseInt(scheduleIdArr[0]);

        // 席ごとの料金区分
      // 席ごとの料金区分
        Map<String, Integer> seatPriceIdMap = new HashMap<>();

        for (String seatId : seatIds) {
            String[] arr = reqc.getParameter("priceId_" + seatId);
            String v = first(arr);               
            if (v == null) v = "";
            v = v.trim();

            
            if (v.isEmpty() || "0".equals(v)) {
                results.put("bookingResult", false);
                results.put("error", "料金区分が未選択の座席があります");
                resc.setResult(results);
                resc.setTarget("booking_result");
                return resc;
            }

            try {
                seatPriceIdMap.put(seatId, Integer.parseInt(v));
            } catch (NumberFormatException nfe) {
                results.put("bookingResult", false);
                results.put("error", "料金区分の値が不正です");
                resc.setResult(results);
                resc.setTarget("booking_result");
                return resc;
            }
        }


        //支払方法
        String[] paymentMethodArr = reqc.getParameter("paymentMethod");
        if (paymentMethodArr == null || paymentMethodArr.length == 0) {
            results.put("bookingResult", false);
            results.put("error", "支払方法が選択されていません");
            resc.setResult(results);
            resc.setTarget("booking_result");
            return resc;
        }
        String paymentMethod = paymentMethodArr[0];

        AccountBean account = (AccountBean) reqc.getSessionAttribute("account");

        if (account == null) {
            results.put("bookingResult", false);
            results.put("error", "ログインしてください");
            resc.setResult(results);
            resc.setTarget("booking_result");
            return resc;
        }
        int userId = account.getUserId();

    
        AbstractDao<BookingBean> bookingDao = getDao();
        var cm = ConnectionManager.getInstance();
        var con = cm.getConnection();

        bookingDao.setConnection(con);
        PriceDao priceDao = new PriceDao();
        priceDao.setConnection(con);

        cm.beginTransaction();
        try {
            // totalprice
            int totalPrice = 0;
            for (int priceId : seatPriceIdMap.values()) {
                totalPrice += priceDao.findPriceById(priceId);
            }

            BookingBean bookingInfo = new BookingBean();
            bookingInfo.setUserId(userId);
            bookingInfo.setScheduleId(scheduleId);
            bookingInfo.setTotalPrice(totalPrice);
            bookingInfo.setSeatIds(seatIds);
            bookingInfo.setSeatNames(reqc.getParameter("seatNames"));
            bookingInfo.setSeatPriceIdMap(seatPriceIdMap); 

            boolean ok = bookingDao.insert(bookingInfo);
            if (!ok) {
                throw new RuntimeException("予約に失敗しました");
            }


            if ("CARD".equals(paymentMethod)) {

    String cardChoice = first(reqc.getParameter("cardChoice")); // "SAVED_1" or "NEW" or null

    if (cardChoice == null || cardChoice.isBlank()) {
        throw new RuntimeException("カードを選択してください");
    }

    if ("NEW".equals(cardChoice)) {

        boolean save = (reqc.getParameter("saveCard") != null);

        if (save) {
            String brand  = first(reqc.getParameter("newBrand"));
            String number = first(reqc.getParameter("newCardNumber"));
            String mm     = first(reqc.getParameter("newExpMonth"));
            String yy     = first(reqc.getParameter("newExpYear"));

            if (brand == null || number == null || mm == null || yy == null
                    || brand.isBlank() || number.isBlank() || mm.isBlank() || yy.isBlank()) {
                throw new RuntimeException("カード情報が不足しています");
            }

            String digits = number.replaceAll("\\s+", "");
            String last4 = (digits.length() >= 4) ? digits.substring(digits.length() - 4) : digits;

            int expMonth = Integer.parseInt(mm);
            int expYear  = Integer.parseInt(yy);

            CardBean cb = new CardBean();
            cb.setUserId(userId);
            cb.setBrand(brand);
            cb.setLast4(last4);
            cb.setExpMonth(expMonth);
            cb.setExpYear(expYear);
            cb.setDefault(false);

            CardDao cardDao = new CardDao();
            cardDao.setConnection(con);
            cardDao.insert(cb);
                }

            
        } else if (cardChoice.startsWith("SAVED_")) {
                // 保存済みカード → 今は何もしないでOK
        } else {
                throw new RuntimeException("カード選択の値が不正です");
        }
            System.out.println("[DEBUG] payment=" + paymentMethod
            + ", cardChoice=" + first(reqc.getParameter("cardChoice"))
            + ", saveCard=" + reqc.getParameter("saveCard"));
        }

            cm.commit();
            System.out.println("[MAIL] to=" + account.getEmail());


            
  try {
                String toEmail = account.getEmail();
                String userName = account.getLastName() + " " + account.getFirstName();
                String[] seatNamesArr = reqc.getParameter("seatNames");
                String seatText = (seatNamesArr == null) ? "" : String.join(", ", seatNamesArr);

                String token = TicketTokenUtil.newToken(40);

                String qrText =
                    "NEOcinema TICKET\n"
                  + "NAME: " + userName + "\n"
                  + "SEAT: " + seatText + "\n"
                  + "PRICE: " + totalPrice + "円\n"
                  + "PAY: " + paymentMethod + "\n"
                  + "TOKEN: " + token;

                byte[] qrBytes = QrUtil.makePng(qrText, 320);

                MailUtil.sendBookingMailWithQr(
                    toEmail,
                    "【NEOcinema】予約完了のお知らせ（QRチケット）",
                    userName + " 様\n\nご予約が完了しました。\n"
                    + "座席: " + seatText + "\n"
                    + "合計金額: " + totalPrice + "円\n"
                    + "支払方法: " + paymentMethod + "\n\n"
                    + "QRチケットを添付しております。\n"
                    + "当日は受付にてご提示ください。\n\nNEOcinema",
                    qrBytes
                );
            } catch (Exception ignore) {}

            results.put("bookingData", bookingInfo);
            results.put("paymentMethod", paymentMethod);
            results.put("bookingResult", true);

        } catch (RuntimeException e) {
            cm.rollback();
            results.put("bookingResult", false);
            results.put("error", e.getMessage());
        } finally {
            try { cm.closeConnection(); } catch (Exception ignore) {}
        }

        resc.setResult(results);
        resc.setTarget("booking_result");
        return resc;
    }

    private String first(String[] arr) {
        return (arr != null && arr.length > 0) ? arr[0] : null;
    }
}
