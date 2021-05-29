package app.nehc.batterytool.bean;

public class FuncItem {
    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public int getFuncId() {
        return funcId;
    }

    public void setFuncId(int funcId) {
        this.funcId = funcId;
    }

    public boolean isSwitchItem() {
        return isSwitchItem;
    }

    public void setSwitchItem(boolean switchItem) {
        isSwitchItem = switchItem;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
    private String funcName;
    private int funcId;
    private boolean isSwitchItem;
    private boolean isEnable;

    @Override
    public String toString() {
        return "FuncItem{" +
                "funcName='" + funcName + '\'' +
                ", funcId=" + funcId +
                ", isSwitchItem=" + isSwitchItem +
                ", isEnable=" + isEnable +
                '}';
    }
}
