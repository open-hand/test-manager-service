package io.choerodon.test.manager.api.dto;

/**
 * Created by zongw.lee@gmail.com on 05/11/2018
 */
public class ExcelWebSocketMessageDTO {
    private Double rate;

    private Long historyId;

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }
}
