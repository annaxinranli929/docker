package command.account;

import command.AbstractCommand;
import logic.ResponseContext;

public class SendUpdatePasswordCommand extends AbstractCommand {

    public ResponseContext execute(ResponseContext resc) {

        resc.setTarget("update_password");
        return resc;
    }
}
