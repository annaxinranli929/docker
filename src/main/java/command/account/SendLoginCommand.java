package command.account;

import command.AbstractCommand;
import logic.ResponseContext;

public class SendLoginCommand extends AbstractCommand {
    public ResponseContext execute(ResponseContext resc) {

        resc.setTarget("login");
        return resc;

    }
}