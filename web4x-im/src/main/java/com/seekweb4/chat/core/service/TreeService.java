package com.seekweb4.chat.core.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seekweb4.chat.common.utils.Reflections;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.TreeEntity;
import com.seekweb4.chat.core.persistence.TreeMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service基类
 *
 * @author lixinapp
 * @version 2017-05-16
 */
@Transactional(readOnly = true)
public abstract class TreeService<D extends TreeMapper<T>, T extends TreeEntity<T>> extends CrudService<D, T> {

	@Transactional(readOnly = false)
	public void save(T entity) {

		@SuppressWarnings("unchecked")
		Class<T> entityClass = Reflections.getClassGenricType(getClass(), 1);

		// 如果没有设置父节点，则代表为跟节点，有则获取父节点实体
		if (entity.getParent() == null || StringUtils.isBlank(entity.getParentId()) || "0".equals(entity.getParentId())) {
			entity.setParent(null);
		} else {
			entity.setParent(super.get(entity.getParentId()));
		}
		if (entity.getParent() == null) {
			T parentEntity = null;
			try {
				parentEntity = entityClass.getConstructor(String.class).newInstance("0");
			} catch (Exception e) {
				throw new ServiceException(e);
			}
			entity.setParent(parentEntity);
			entity.getParent().setParentIds(StringUtils.EMPTY);
		}

		// 获取修改前的parentIds，用于更新子节点的parentIds
		String oldParentIds = entity.getParentIds();

		// 设置新的父节点串
		entity.setParentIds(entity.getParent().getParentIds() + entity.getParent().getId() + ",");

		// 保存或更新实体
		super.save(entity);

		// 更新子节点 parentIds
		T o = null;
		try {
			o = entityClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		o.setParentIds("%," + entity.getId() + ",%");
		List<T> list = mapper.findByParentIdsLike(o);
		for (T e : list) {
			if (e.getParentIds() != null && oldParentIds != null) {
				e.setParentIds(e.getParentIds().replace(oldParentIds, entity.getParentIds()));
				preUpdateChild(entity, e);
				mapper.updateParentIds(e);
			}
		}

	}

	/**
	 * 预留接口，用户更新子节前调用
	 *
	 * @param childEntity
	 */
	protected void preUpdateChild(T entity, T childEntity) {

	}

	public List<T> getChildren(String parentId) {
		return mapper.getChildren(parentId);
	}

	/**
	 * 以root为根节点, 将allList从线性列表转为树形列表
	 *
	 * @param root 根节点, 为空抛出空指针异常
	 * @param allList 所有需要参与构造为树的列表
	 * @param extId 需要排除在树之外的节点(子节点一并被排除)
	 * @return java.util.List<T>
	 * @Author 滕鑫源
	 * @Date 2020/10/23 17:04
	 **/
	public List<T> formatListToTree(T root, List<T> allList, String extId) {
		String rootId = root.getId();

		// 最终的树形态
		List<T> trees = Lists.newArrayList();

		// 把需要构造树的所有列表, 根据以父id作为key, 整理为列表
		Map<String, List<T>> treeMap = Maps.newHashMap();
		for (T entity : allList) {
			List<T> entities = treeMap.get(entity.getParent().getId());
			if (entities == null) {
				entities = Lists.newLinkedList();
			}

			// 剔除排除项, 构造treeMap, 转递归为线性操作
			if (StringUtils.isBlank(extId) ||  (!extId.equals(entity.getId()) && entity.getParentIds().indexOf("," + extId + ",") == -1)){
				entities.add(entity);
				treeMap.put(entity.getParent().getId(), entities);
			}

		}

		// 没有给定的子树, 返回空树
		if (treeMap.get(rootId) == null || treeMap.get(rootId).isEmpty()) {
			return trees;
		}

		// 开始递归格式化
		List<T> children = treeMap.get(rootId);
		for (T parent : children) {
			formatFillChildren(parent, treeMap);
			trees.add(parent);
		}
		if (StringUtils.equals(rootId, "0")) {
			return children;
		} else {
			root.setChildren(trees);
			return Lists.newArrayList(root);
		}
	}

	/**
	 * 从treeMap中取出子节点填入parent, 并递归此操作
	 *
	 * @param parent
	 * @param treeMap
	 * @return void
	 * @Author 滕鑫源
	 * @Date 2020/9/30 16:33
	 **/
	private void formatFillChildren(T parent, Map<String, List<T>> treeMap) {
		List<T> children = treeMap.get(parent.getId());
		parent.setChildren(children);
		if (children != null && !children.isEmpty()) {
			for (T child : children) {
				formatFillChildren(child, treeMap);
			}
		}
	}
}
