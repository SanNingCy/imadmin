package com.seekweb4.chat.modules.redPacketTransaction.service;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.grouphongbao.entity.GroupHongbao;
import com.seekweb4.chat.modules.grouphongbao.service.GroupHongbaoService;
import com.seekweb4.chat.modules.hongbao.entity.Hongbao;
import com.seekweb4.chat.modules.hongbao.service.HongbaoService;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.redPacketTransaction.vo.RedPacketTransactionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 红包交易记录统一Service
 * 整合单聊红包和群聊红包记录
 * @author system
 * @version 2025-12-08
 */
@Service
@Transactional(readOnly = true)
public class RedPacketTransactionService {

	@Autowired
	private HongbaoService hongbaoService;
	
	@Autowired
	private GroupHongbaoService groupHongbaoService;

	/**
	 * 根据ID查询红包交易记录
	 * @param id 记录ID
	 * @param packetType 红包类型（single-单聊红包, group-群聊红包）
	 * @return 红包交易记录VO
	 */
	public RedPacketTransactionVO get(String id, String packetType) {
		if (StringUtils.isBlank(id) || StringUtils.isBlank(packetType)) {
			return null;
		}
		
		RedPacketTransactionVO vo = null;
		
		switch (packetType) {
			case "single":
				Hongbao hongbao = hongbaoService.get(id);
				if (hongbao != null) {
					vo = convertToVO(hongbao);
					vo.setPacketType("single");
					vo.setPacketTypeName("单聊红包");
				}
				break;
			case "group":
				GroupHongbao groupHongbao = groupHongbaoService.get(id);
				if (groupHongbao != null) {
					vo = convertToVO(groupHongbao);
					vo.setPacketType("group");
					vo.setPacketTypeName("群聊红包");
				}
				break;
		}
		
		return vo;
	}

	/**
	 * 查询所有类型的红包交易记录（分页）
	 * @param page 分页对象
	 * @param packetType 红包类型（可选，single-单聊红包, group-群聊红包）
	 * @param userId 用户ID（可选，用于筛选特定用户的记录）
	 * @param uIdno 发红包用户的IDNO（可选）
	 * @param uNickname 发红包用户的昵称（可选）
	 * @param uid2Idno 接收红包用户的IDNO（可选，仅用于单聊红包）
	 * @param uid2Nickname 接收红包用户的昵称（可选，仅用于单聊红包）
	 * @param beginCreateDate 开始创建时间（可选）
	 * @param endCreateDate 结束创建时间（可选）
	 * @return 分页结果
	 */
	public Page<RedPacketTransactionVO> findPage(Page<RedPacketTransactionVO> page, String packetType, String userId, String uIdno, String uNickname, String uid2Idno, String uid2Nickname, Date beginCreateDate, Date endCreateDate) {
		List<RedPacketTransactionVO> allList = new ArrayList<>();
		
		// 查询单聊红包记录
		if (StringUtils.isBlank(packetType) || "single".equals(packetType)) {
			Hongbao queryHongbao = new Hongbao();
			Member u = new Member();
			if (StringUtils.isNotBlank(userId)) {
				u.setId(userId);
			}
			if (StringUtils.isNotBlank(uIdno)) {
				u.setIdno(uIdno);
			}
			if (StringUtils.isNotBlank(uNickname)) {
				u.setNickname(uNickname);
			}
			queryHongbao.setU(u);
			if (StringUtils.isNotBlank(uid2Idno) || StringUtils.isNotBlank(uid2Nickname)) {
				Member uid2 = new Member();
				if (StringUtils.isNotBlank(uid2Idno)) {
					uid2.setIdno(uid2Idno);
				}
				if (StringUtils.isNotBlank(uid2Nickname)) {
					uid2.setNickname(uid2Nickname);
				}
				queryHongbao.setUid2(uid2);
			}
			// 设置时间范围
			if (beginCreateDate != null || endCreateDate != null) {
				// 由于Hongbao实体没有beginCreateDate和endCreateDate字段，我们需要在Service层过滤
				List<Hongbao> hongbaoList = hongbaoService.findList(queryHongbao);
				for (Hongbao hongbao : hongbaoList) {
					// 时间过滤
					if (beginCreateDate != null && endCreateDate != null) {
						if (hongbao.getCreateDate() != null && 
							(hongbao.getCreateDate().before(beginCreateDate) || hongbao.getCreateDate().after(endCreateDate))) {
							continue;
						}
					} else if (beginCreateDate != null) {
						if (hongbao.getCreateDate() != null && hongbao.getCreateDate().before(beginCreateDate)) {
							continue;
						}
					} else if (endCreateDate != null) {
						if (hongbao.getCreateDate() != null && hongbao.getCreateDate().after(endCreateDate)) {
							continue;
						}
					}
					
					RedPacketTransactionVO vo = convertToVO(hongbao);
					vo.setPacketType("single");
					vo.setPacketTypeName("单聊红包");
					allList.add(vo);
				}
			} else {
				List<Hongbao> hongbaoList = hongbaoService.findList(queryHongbao);
				for (Hongbao hongbao : hongbaoList) {
					RedPacketTransactionVO vo = convertToVO(hongbao);
					vo.setPacketType("single");
					vo.setPacketTypeName("单聊红包");
					allList.add(vo);
				}
			}
		}
		
		// 查询群聊红包记录
		if (StringUtils.isBlank(packetType) || "group".equals(packetType)) {
			GroupHongbao queryGroupHongbao = new GroupHongbao();
			Member u = new Member();
			if (StringUtils.isNotBlank(userId)) {
				u.setId(userId);
			}
			if (StringUtils.isNotBlank(uIdno)) {
				u.setIdno(uIdno);
			}
			if (StringUtils.isNotBlank(uNickname)) {
				u.setNickname(uNickname);
			}
			queryGroupHongbao.setU(u);
			// 设置时间范围
			queryGroupHongbao.setBeginCreateDate(beginCreateDate);
			queryGroupHongbao.setEndCreateDate(endCreateDate);
			List<GroupHongbao> groupHongbaoList = groupHongbaoService.findList(queryGroupHongbao);
			for (GroupHongbao groupHongbao : groupHongbaoList) {
				RedPacketTransactionVO vo = convertToVO(groupHongbao);
				vo.setPacketType("group");
				vo.setPacketTypeName("群聊红包");
				allList.add(vo);
			}
		}
		
		// 按创建时间倒序排序
		allList.sort(Comparator.comparing(RedPacketTransactionVO::getCreateDate, Comparator.nullsLast(Comparator.reverseOrder())));
		
		// 手动分页
		int total = allList.size();
		int pageNo = page.getPageNo();
		int pageSize = page.getPageSize();
		int start = (pageNo - 1) * pageSize;
		int end = Math.min(start + pageSize, total);
		
		List<RedPacketTransactionVO> pageList = new ArrayList<>();
		if (start < total) {
			pageList = allList.subList(start, end);
		}
		
		page.setList(pageList);
		page.setCount(total);
		
		return page;
	}

	/**
	 * 将Hongbao转换为VO
	 */
	private RedPacketTransactionVO convertToVO(Hongbao hongbao) {
		RedPacketTransactionVO vo = new RedPacketTransactionVO();
		vo.setId(hongbao.getId());
		vo.setU(hongbao.getU());
		vo.setUid2(hongbao.getUid2());
		vo.setMoney(hongbao.getMoney());
		vo.setInfo(hongbao.getInfo());
		vo.setTuiTime(hongbao.getTuiTime());
		vo.setShouTime(hongbao.getShouTime());
		vo.setCreateDate(hongbao.getCreateDate());
		return vo;
	}

	/**
	 * 将GroupHongbao转换为VO
	 */
	private RedPacketTransactionVO convertToVO(GroupHongbao groupHongbao) {
		RedPacketTransactionVO vo = new RedPacketTransactionVO();
		vo.setId(groupHongbao.getId());
		vo.setU(groupHongbao.getU());
		vo.setGroup(groupHongbao.getGroup());
		vo.setType(groupHongbao.getType());
		vo.setMoney(groupHongbao.getMoney());
		vo.setInfo(groupHongbao.getInfo());
		vo.setCount(groupHongbao.getCount());
		vo.setSycount(groupHongbao.getSycount());
		vo.setTuiTime(groupHongbao.getTuiTime());
		vo.setCreateDate(groupHongbao.getCreateDate());
		return vo;
	}
}

