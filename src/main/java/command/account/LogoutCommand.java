package command.account;

import logic.ResponseContext;
import logic.RequestContext;
import command.AbstractCommand;

public class LogoutCommand extends AbstractCommand {
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();

        // 現在のセッションからログアウト前にメッセージをセット
        reqc.setSessionAttribute("logoutMessage", "ログアウトしました！");

        // セッション破棄
        reqc.invalidateSession();

        // リダイレクト先で新しいセッションを作り、メッセージを引き継ぐ
        // ここではトップページにリダイレクト
        resc.setRedirect(true);
        resc.setTargetURI(".");

        return resc;
    }
}
