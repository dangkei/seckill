package org.seckill.dto;

import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SecKillStatEnum;

/**
 * 秒杀返回结果
 */
public class SecKillExecution    {

    private long seckillId;
    private int state;
    private String stateInfo;
    private SuccessKilled successKilled;

    public SecKillExecution(long seckillId, SecKillStatEnum statEnum) {
        this.seckillId = seckillId;
        this.state = statEnum.getStat();
        this.stateInfo = statEnum.getStatInfo();
    }

    public SecKillExecution(long seckillId, SecKillStatEnum statEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = statEnum.getStat();
        this.stateInfo = statEnum.getStatInfo();
        this.successKilled = successKilled;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SecKillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
