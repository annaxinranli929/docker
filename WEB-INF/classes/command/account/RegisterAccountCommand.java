package command.account;

import command.AbstractCommand;
import logic.ResponseContext;
import logic.RequestContext;

public class RegisterAccountCommand extends AbstractCommand {
    public ResponseContext execute(ResponseContext resc){

        RequestContext reqc = getRequestContext();

        // 初回表示（未送信）
        if(reqc.getParameter("firstName") == null){
            resc.setTarget("register_account");
            return resc;
        }

        String firstName = reqc.getParameter("firstName")[0];
        String lastName  = reqc.getParameter("lastName")[0];
        String password  = reqc.getParameter("password")[0];
        String email     = reqc.getParameter("email")[0];

        // ===== 項目別エラーチェック =====
        if(firstName == null || firstName.isEmpty()){
            resc.setResult("姓を入力してください");
            resc.setTarget("register_account");
            return resc;
        }

        if(lastName == null || lastName.isEmpty()){
            resc.setResult("名を入力してください");
            resc.setTarget("register_account");
            return resc;
        }

        if(password == null || password.isEmpty()){
            resc.setResult("パスワードを入力してください");
            resc.setTarget("register_account");
            return resc;
        }

        if(password.length() < 8){
            resc.setResult("パスワードは8文字以上で入力してください");
            resc.setTarget("register_account");
            return resc;
        }

        if(email == null || email.isEmpty()){
            resc.setResult("メールアドレスを入力してください");
            resc.setTarget("register_account");
            return resc;
        }

        // ===== ここまで来たら成功（仮） =====
        resc.setTarget("register_success");
        return resc;
    }
}
