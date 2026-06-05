package com.seekweb4.chat.modules.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seekweb4.chat.core.persistence.TreeEntity;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import java.util.List;
/**
 * 菜单Entity
 * @author lixinapp
 * @version 2016-05-15
 */
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler"})
public class Menu extends TreeEntity<Menu> {

	private static final long serialVersionUID = 1L;
//	private String href; 	// 链接
	private String path; 	// 链接
	private String target; 	// 目标（ mainFrame、_blank、_self、_parent、_top）
	private String icon; 	// 图标
	private String isShow; 	// 是否在菜单中显示（1：显示；0：不显示）
	private String type; //按钮类型
	private String permission; // 权限标识
	private List<DataRule> dataRuleList;
	private  String affix;

	private String userId;

	private String component; // 组件路径
	private String iframeLink; // iframe链接
	private String externalLink; // 外部链接

	public Menu(){
		super();
		this.sort = 30;
		this.isShow = "1";
		this.type="1";
	}

	public Menu(String id){
		super(id);
	}

	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	@Length(min=1, max=2000)
	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	@Length(min=1, max=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	@Length(min=0, max=2000)
//	public String getHref() {
//		return href;
//	}
//
//	public void setHref(String href) {
//		this.href = href;
//	}


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Length(min=0, max=20)
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	@Length(min=0, max=100)
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@NotNull
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Length(min=1, max=1)
	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	@Length(min=0, max=200)
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}

	@JsonIgnore
	public static void sortList(List<Menu> list, List<Menu> sourcelist, String parentId, boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			Menu e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						Menu child = sourcelist.get(j);
						if (child.getParent()!=null && child.getParent().getId()!=null
								&& child.getParent().getId().equals(e.getId())){
							sortList(list, sourcelist, e.getId(), true);
							break;
						}
					}
				}
			}
		}
	}

	@JsonIgnore
	public static String getRootId(){
		return "1";
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return name;
	}

	public List<DataRule> getDataRuleList() {
		return dataRuleList;
	}

	public void setDataRuleList(List<DataRule> dataRuleList) {
		this.dataRuleList = dataRuleList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAffix() {
		return affix;
	}

	public void setAffix(String affix) {
		this.affix = affix;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getIframeLink() {
		return iframeLink;
	}

	public void setIframeLink(String iframeLink) {
		this.iframeLink = iframeLink;
	}

	public String getExternalLink() {
		return externalLink;
	}

	public void setExternalLink(String externalLink) {
		this.externalLink = externalLink;
	}
}
