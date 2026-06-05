package com.seekweb4.chat.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.sys.entity.DataRule;
import com.seekweb4.chat.modules.sys.entity.Menu;
import com.seekweb4.chat.modules.sys.mapper.MenuMapper;
import com.seekweb4.chat.modules.sys.service.DataRuleService;
import com.seekweb4.chat.modules.sys.service.MenuService;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 数据权限Controller
 * @author lgf
 * @version 2017-04-02
 */
@RestController
@RequestMapping("/sys/dataRule")
public class DataRuleController extends BaseController {

	@Autowired
	private DataRuleService dataRuleService;

	@Autowired
	private MenuService menuService;

	@Autowired
	private MenuMapper menuMapper;

	@ModelAttribute
	public DataRule get(@RequestParam(required=false) String id) {
		DataRule entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = dataRuleService.get(id);
		}
		if (entity == null){
			entity = new DataRule();
		}
		return entity;
	}


	/**
	 * 数据权限列表数据
	 */
	@GetMapping("list")
	public AjaxJson data(DataRule dataRule, HttpServletRequest request, HttpServletResponse response) {
		Page<DataRule> page = dataRuleService.findPage(new Page<DataRule>(request, response), dataRule);
		return AjaxJson.success().put("page", page);
	}

	/**
	 * 查看，增加，编辑数据权限表单页面
	 */
	@GetMapping("queryById")
	public AjaxJson queryById(DataRule dataRule) {
		return AjaxJson.success().put("dataRule", dataRule);
	}

	/**
	 * 保存数据权限
	 */
	@PostMapping("save")
	public AjaxJson save(DataRule dataRule, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(dataRule);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}

        dataRuleService.save(dataRule);//保存
		//清除缓存
		UserUtils.removeCache(UserUtils.CACHE_DATA_RULE_LIST);
		return AjaxJson.success("保存数据权限成功!");
	}

	/**
	 * 删除数据权限
	 */
	@DeleteMapping("delete")
	public AjaxJson delete(DataRule dataRule) {
		dataRuleService.delete(dataRule);
		//清除缓存
		UserUtils.removeCache(UserUtils.CACHE_DATA_RULE_LIST);
		return AjaxJson.success("删除数据权限成功");
	}


    public List<Map<String, Object>> getTreeMenu(List<Map<String, Object>> list, String extId, String isShowHid) {
        List rootTree = Lists.newArrayList();
        Menu menu = menuService.get("1");
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", menu.getId());
        map.put("name", menu.getName());
        map.put("parentId", "0");
        rootTree.add( getChildOfTree(map, list, extId, isShowHid));
        return rootTree;
    }

    private  Map<String, Object> getChildOfTree(Map<String, Object> menuItem,  List<Map<String, Object>> menuList, String extId, String isShowHide) {
//        menuItem.setChildren(Lists.newArrayList());
        menuItem.put("children", Lists.newArrayList());
        for (Map<String, Object> child : menuList) {
            if (StringUtils.isBlank(extId) || (extId != null && !extId.equals(child.get("id").toString()) && child.get("parentIds").toString().indexOf("," + extId + ",") == -1)) {
                if (isShowHide != null && isShowHide.equals("0") && child.get("isShow").toString().equals("0")) {
                    continue;
                }
                if (child.get("parentId").equals(menuItem.get("id"))) {
                    ((List)menuItem.get("children")).add(getChildOfTree(child, menuList, extId, isShowHide));
                }
            }
        }
        return menuItem;
    }

	/**
	 * isShowHide是否显示隐藏菜单
	 * @param extId
	 * @return
	 */
	@RequiresPermissions("user")
	@GetMapping("treeData")
	public AjaxJson treeData(@RequestParam(required=false) String extId,@RequestParam(required=false) String isShowHide) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Menu> list =  menuMapper.findAllDataRuleList(new Menu());
		HashSet<String> existIdSet = new HashSet<String>();
		for (int i=0; i<list.size(); i++){
			Menu menu = list.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(menu.getId()) && menu.getParentIds().indexOf(","+extId+",")==-1)){
				if(isShowHide != null && isShowHide.equals("0") && menu.getIsShow().equals("0")){
					continue;
				}

				Map<String, Object> map = Maps.newHashMap();
				map.put("id", menu.getId());
				map.put("name", menu.getName());
                map.put("parentId", menu.getParentId());
                map.put("parentIds", menu.getParentIds());
				if(StringUtils.isNotBlank(menu.getIcon())){
					map.put("icon", menu.getIcon());
				}

				boolean existDataRule = false;
				List<DataRule> dataRuleList = menu.getDataRuleList();
				for(DataRule dataRule : dataRuleList){
					Map<String, Object> dataRuleMap = Maps.newHashMap();
					dataRuleMap.put("id", "dataRule-" + dataRule.getId());
					dataRuleMap.put("name", dataRule.getName());
					dataRuleMap.put("parentId", dataRule.getMenuId());
                    dataRuleMap.put("parentIds", dataRule.getMenuId());
					mapList.add(dataRuleMap);
					existDataRule = true;
				}
				if(existDataRule){
					if(!existIdSet.contains(menu.getId())){
						mapList.add(map);
						existIdSet.add(menu.getId());
					}

					String[] parentIds =( menu.getParentIds()== null? new String[0] : menu.getParentIds().split(","));
					for(String parentId : parentIds){
						if(!existIdSet.contains(parentId)){
							existIdSet.add(parentId);
							Menu parentMenu = menuService.get(parentId);
							if(parentMenu == null){
								continue;
							}
							Map<String, Object> parentMenuMap = Maps.newHashMap();
							parentMenuMap.put("id", parentId);
							parentMenuMap.put("name", parentMenu.getName());
                            parentMenuMap.put("parentId", parentMenu.getParentId());
                            parentMenuMap.put("parentIds", parentMenu.getParentIds());
							mapList.add(parentMenuMap);

						}
					}

				}

			}
		}

        List rootTree = getTreeMenu(mapList, extId, isShowHide);
        return AjaxJson.success().put("treeData",rootTree);
	}




}
