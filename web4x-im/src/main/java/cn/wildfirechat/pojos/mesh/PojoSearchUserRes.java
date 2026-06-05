package cn.wildfirechat.pojos.mesh;

import cn.wildfirechat.pojos.InputOutputUserInfo;

import java.util.List;

public class PojoSearchUserRes {
    public List<InputOutputUserInfo> userInfos;
    public String keyword;

    public List<InputOutputUserInfo> getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(List<InputOutputUserInfo> userInfos) {
        this.userInfos = userInfos;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
