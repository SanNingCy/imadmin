package cn.wildfirechat.pojos.mesh;

import cn.wildfirechat.pojos.PojoGroupMember;

import java.util.List;

public class PojoAddGroupMember {
    public String domainId;
    public String operator;
    public String group_id;
    public List<PojoGroupMember> members;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public List<PojoGroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<PojoGroupMember> members) {
        this.members = members;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }
}
