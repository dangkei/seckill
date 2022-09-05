package cn.dangkei.enums;

/**
 * 使用枚举表述常量数据
 */
public enum SecKillStatEnum {
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KILL(-1,"重复秒杀"),
    INNER_ERROR(-2,"系统异常"),
    DATE_REWRITE(-3,"数据篡改");

    private int stat;
    private String statInfo;

    SecKillStatEnum(int stat, String statInfo) {
        this.stat = stat;
        this.statInfo = statInfo;
    }

    public static SecKillStatEnum stateOf(int index){
        for(SecKillStatEnum statEnum:values()){
            if(statEnum.getStat()==index){
                return statEnum;
            }
        }
        return  null;
    }

    public int getStat() {
        return stat;
    }

    public String getStatInfo() {
        return statInfo;
    }
}
