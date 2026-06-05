package cn.wildfirechat.pojos.mesh;

import cn.wildfirechat.pojos.PojoGroupInfo;
import cn.wildfirechat.pojos.PojoGroupMember;

import java.util.List;

public class PojoGroupUpdated {
    public List<String> domainIds;
    public PojoGroupInfo groupInfo;
    public List<PojoGroupMember> members;

    public List<String> getDomainIds() {
        return domainIds;
    }

    public void setDomainIds(List<String> domainIds) {
        this.domainIds = domainIds;
    }

    public PojoGroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(PojoGroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public List<PojoGroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<PojoGroupMember> members) {
        this.members = members;
    }
}
