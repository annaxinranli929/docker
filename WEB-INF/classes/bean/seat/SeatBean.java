package bean.seat;

import bean.Bean;

public class SeatBean implements Bean {
    private int seatId;
    private String seatName;
    private boolean reserved;

    public SeatBean(int seatId, String seatName, boolean reserved) {
        this.seatId = seatId;
        this.seatName = seatName;
        this.reserved = reserved;
    }

    public int getSeatId() { return seatId; }
    public String getSeatName() { return seatName; }
    public boolean isReserved() { return reserved; }
}