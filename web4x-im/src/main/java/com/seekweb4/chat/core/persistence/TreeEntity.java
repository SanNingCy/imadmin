package com.seekweb4.chat.core.persistence;

import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.Reflections;
import com.seekweb4.chat.common.utils.StringUtils;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 数据Entity类
 *
 * @author lixinapp
 * @version 2017-05-16
 */
public abstract class TreeEntity<T> extends DataEntity<T> {

	private static final long serialVersionUID = 1L;

	protected T parent;    // 父级编号
	protected String parentIds; // 所有父级编号
	protected String name;    // 名称
	protected Integer sort;        // 排序
	private List<T> children = Lists.newArrayList();    // 父级菜单

	public TreeEntity() {
		super();
		this.sort = 30;
	}

	public TreeEntity(String id) {
		super(id);
	}

	/**
	 * 父对象，只能通过子类实现，父类实现mybatis无法读取
	 *
	 * @return
	 */
	public abstract T getParent();

	/**
	 * 父对象，只能通过子类实现，父类实现mybatis无法读取
	 *
	 * @return
	 */
	public abstract void setParent(T parent);

	@Length(min = 1, max = 2000)
	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	@Length(min = 1, max = 100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}


	public String getParentId() {
		String id = null;
		if (parent != null) {
			id = (String) Reflections.getFieldValue(parent, "id");
		}
		return StringUtils.isNotBlank(id) ? id : "0";
	}


	public List<T> getChildren() {
		return children;
	}

	public void setChildren(List<T> children) {
		this.children = children;
	}

}
