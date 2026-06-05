package com.seekweb4.chat.modules.sys.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.DateUtils;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.common.utils.excel.ExportExcel;
import com.seekweb4.chat.common.utils.excel.ImportExcel;
import com.seekweb4.chat.modules.sys.entity.Post;
import com.seekweb4.chat.modules.sys.service.PostService;

/**
 * 岗位Controller
 */
@RestController
@RequestMapping(value = "/sys/post")
public class PostController extends BaseController {

	@Autowired
	private PostService postService;

	@ModelAttribute
	public Post get(@RequestParam(required=false) String id) {
		Post entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = postService.get(id);
		}
		if (entity == null){
			entity = new Post();
		}
		return entity;
	}

	/**
	 * 岗位列表数据
	 */
	@RequiresPermissions("sys:post:list")
	@GetMapping("list")
	public AjaxJson list(Post post, HttpServletRequest request, HttpServletResponse response) {
		Page<Post> page = postService.findPage(new Page<Post>(request, response), post);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取岗位数据
	 */
	@RequiresPermissions(value={"sys:post:view","sys:post:add","sys:post:edit"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(Post post) {
		return AjaxJson.success().put("post", post);
	}

	/**
	 * 保存岗位
	 */
	@RequiresPermissions(value={"sys:post:add","sys:post:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(Post post, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(post);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		postService.save(post);//保存
		return AjaxJson.success("保存岗位成功");
	}


	/**
	 * 批量删除岗位
	 */
	@RequiresPermissions("sys:post:del")
	@DeleteMapping("delete")
	public AjaxJson delete(String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			postService.delete(postService.get(id));
		}
		return AjaxJson.success("删除岗位成功");
	}

	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("sys:post:export")
    @GetMapping("export")
    public AjaxJson exportFile(Post post, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "岗位"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Post> page = postService.findPage(new Page<Post>(request, response, -1), post);
    		new ExportExcel("岗位", Post.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出岗位记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sys:post:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Post> list = ei.getDataList(Post.class);
			for (Post post : list){
				try{
					postService.save(post);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条岗位记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条岗位记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入岗位失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入岗位数据模板
	 */
	@RequiresPermissions("sys:post:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "岗位数据导入模板.xlsx";
    		List<Post> list = Lists.newArrayList();
    		new ExportExcel("岗位数据", Post.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}