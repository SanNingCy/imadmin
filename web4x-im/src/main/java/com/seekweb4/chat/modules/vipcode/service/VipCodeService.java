package com.seekweb4.chat.modules.vipcode.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.seekweb4.chat.api.config.AssetConfig;
import com.seekweb4.chat.api.constant.WXRequestConstant;
import com.seekweb4.chat.api.utils.HttpUtil;
import com.seekweb4.chat.api.utils.sign.ImToLqApiSignUtils;
import com.seekweb4.chat.api.utils.sign.SignUtil;
import com.seekweb4.chat.common.utils.DateUtil;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.delayedQueue.RedisDelayedQueue;
import com.seekweb4.chat.delayedQueue.VipEndDelayedQueueListener;
import com.seekweb4.chat.dto.response.HttpResponseDTO;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.vipcode.constant.ChainBridgeException;
import com.seekweb4.chat.modules.vipcode.constant.ChainBridgeResult;
import com.seekweb4.chat.modules.vipcode.dto.SyncFromImItemDTO;
import com.seekweb4.chat.modules.vipcode.dto.SyncFromImRequestDTO;
import com.seekweb4.chat.modules.vipcode.dto.SyncFromImSignDTO;
import com.seekweb4.chat.modules.vipcode.entity.VipCode;
import com.seekweb4.chat.modules.vipcode.mapper.VipCodeMapper;

/**
 * 会员码Service
 * @author lixinapp
 * @version 2025-03-24
 */
@Service
@Transactional(readOnly = true)
public class VipCodeService extends CrudService<VipCodeMapper, VipCode> {

	private static final Logger log = LoggerFactory.getLogger(VipCodeService.class);
	private static final int MAX_SYNC_BATCH_SIZE = 500;
	private static final int MAX_GENERATE_BATCH_SIZE = 10_000;

	/** 用于将 data 序列化为稳定 JSON 参与签名（与链桥约定一致） */
	private static final ObjectMapper STABLE_JSON_MAPPER = new ObjectMapper()
			.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
			.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
			.setSerializationInclusion(JsonInclude.Include.NON_NULL);

	@Autowired
	private MemberService memberService;
	@Autowired
	private RedisDelayedQueue redisDelayedQueue;
	@Autowired
	private WXRequestConstant wxRequestConstant;
	@Autowired
	private AssetConfig assetConfig;
	public VipCode get(String id) {
		VipCode vipCode = super.get(id);
		// 确保 VipCode 有 u 字段，即使未兑换
		if (vipCode != null && vipCode.getU() == null) {
			vipCode.setU(new Member());
		}
		return vipCode;
	}
	
	public List<VipCode> findList(VipCode vipCode) {
		List<VipCode> list = super.findList(vipCode);
		// 确保每个 VipCode 都有 u 字段，即使未兑换
		if (list != null) {
			for (VipCode code : list) {
				if (code.getU() == null) {
					code.setU(new Member());
				}
			}
		}
		return list;
	}
	
	public Page<VipCode> findPage(Page<VipCode> page, VipCode vipCode) {
		Page<VipCode> result = super.findPage(page, vipCode);
		// 确保每个 VipCode 都有 u 字段，即使未兑换
		if (result != null && result.getList() != null) {
			for (VipCode code : result.getList()) {
				if (code.getU() == null) {
					code.setU(new Member());
				}
			}
		}
		return result;
	}
	
	@Transactional(readOnly = false)
	public void save(VipCode vipCode) {
		super.save(vipCode);
	}
	@Transactional(readOnly = false)
	public void delete(VipCode vipCode) {
		super.delete(vipCode);
	}

	/**
	 * 兑换
	 * @param vipCode
	 */
	@Transactional(readOnly = false)
	public void duihuan(VipCode vipCode,Member member) {
		super.save(vipCode);
		if("1".equals(member.getIsvip())){
			member.setViptime(DateUtil.addDays(member.getViptime(),vipCode.getDay()));
		}else {
			member.setIsvip("1");
			member.setViptime(DateUtil.addDays(new Date(),vipCode.getDay()));
		}
		memberService.save(member);
		redisDelayedQueue.addQueueDays(member.getId(),vipCode.getDay(), VipEndDelayedQueueListener.class);
	}


	/**
	 * 调用链桥接口获取卡密类型
	 */
	public Object getKeyCardTypeFromChainBridge(){
		try {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("appId", "im_admin");
			params.put("nonce", UUID.randomUUID().toString().replace("-", ""));
			params.put("timestamp", System.currentTimeMillis() / 1000);

			String signContent = SignUtil.buildSignContent(params);
			String sign = SignUtil.sign(signContent, assetConfig.getPrivateKey());
			params.put("sign", sign);

			String json = new ObjectMapper().writeValueAsString(params);
			String url = wxRequestConstant.getKeyCardTypeUrl();
			log.info("获取链桥卡密类型请求url:{}", url);
			log.info("获取链桥卡密类型请求参数:{}", json);

			String response = HttpUtil.postJson(url, json);
			log.info("获取链桥卡密类型响应:{}", response);

			HttpResponseDTO httpResponse = new ObjectMapper().readValue(response, HttpResponseDTO.class);
			if (httpResponse.getCode() == null || httpResponse.getCode() != 0) {
				throw new RuntimeException("链桥获取卡密类型失败: " + (httpResponse.getMsg() != null ? httpResponse.getMsg() : "未知错误"));
			}
			return httpResponse.getData();
		}catch (Exception e){
			log.error("获取链桥卡密类型异常,异常信息为:{}", e.getMessage());
		}
		return null;
	}

	/**
	 * 校验同步数据：每条数据的 code、day、type 不能为空；类型不能为空
	 */
	private String validateSyncData(List<SyncFromImItemDTO> dataList) {
		for (int i = 0; i < dataList.size(); i++) {
			SyncFromImItemDTO item = dataList.get(i);
			String code = item.getCode() != null ? item.getCode().trim() : "";
			if (StringUtils.isBlank(code)) {
				return "第" + (i + 1) + "条数据的 code 不能为空";
			}
			if (item.getDay() == null) {
				return code + " 会员天数不能为空";
			}
			if (item.getType() == null) {
				return code + " 类型不能为空";
			}
		}
		return null;
	}

	/**
	 * 校验待同步的会员码在库中不能已是「已同步」状态（sync_status=1 不能重复同步）
	 */
	private String validateNotAlreadySynced(List<SyncFromImItemDTO> dataList) {
		List<String> alreadySyncedCodes = new ArrayList<>();
		for (SyncFromImItemDTO item : dataList) {
			if (StringUtils.isBlank(item.getCode())) continue;
			VipCode exist = mapper.findUniqueByProperty("code", item.getCode());
			if (exist != null && "1".equals(exist.getSyncStatus())) {
				alreadySyncedCodes.add(item.getCode());
			}
		}
		if (!alreadySyncedCodes.isEmpty()) {
			return "以下会员码已同步成功，不能重复同步：" + String.join("、", alreadySyncedCodes);
		}
		return null;
	}

	/**
	 * 构建同步到链桥请求体
	 */
	private Map<String, Object> buildSyncFromImBody(SyncFromImRequestDTO request) throws Exception {
		String appId = "im_admin";
		String nonce = UUID.randomUUID().toString().replace("-", "");
		long timestamp = System.currentTimeMillis() / 1000;

		// data 序列化为稳定 JSON 字符串参与签名
		String dataJson = STABLE_JSON_MAPPER.writeValueAsString(request.getData());

		// 参与签名的参数：appId、nonce、timestamp、data（dataJson）
		Map<String, Object> signParams = new LinkedHashMap<>();
		signParams.put("appId", appId);
		signParams.put("nonce", nonce);
		signParams.put("timestamp", timestamp);
		signParams.put("data", dataJson);

		String signContent = ImToLqApiSignUtils.buildSignContent(signParams);
		String sign = ImToLqApiSignUtils.sign(signContent, assetConfig.getPrivateKey());

		// sign 对象
		Map<String, Object> signObj = new LinkedHashMap<>();
		signObj.put("appId", appId);
		signObj.put("nonce", nonce);
		signObj.put("timestamp", timestamp);
		signObj.put("sign", sign);

		// 最终请求体：sign 在前，data 为原数组
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("sign", signObj);
		body.put("data", request.getData());
		return body;
	}

	/**
	 * 查询未同步且已填写类型的会员码（用于点击「同步」时自动提交到链桥）
	 */
	public List<VipCode> findUnsyncedWithType() {
		List<VipCode> list = mapper.findUnsyncedWithTypeLimit(MAX_SYNC_BATCH_SIZE);
		return list != null ? list : new ArrayList<>();
	}

	/**
	 * 根据未同步且已填类型的会员码构建同步请求（供 syncFromIm 无参数时使用）
	 */
	public SyncFromImRequestDTO buildSyncRequestFromUnsynced() {
		List<VipCode> list = findUnsyncedWithType();
		List<SyncFromImItemDTO> dataList = new ArrayList<>();
		for (VipCode v : list) {
			if (dataList.size() >= MAX_SYNC_BATCH_SIZE) {
				break;
			}
			SyncFromImItemDTO item = new SyncFromImItemDTO();
			item.setCode(v.getCode());
			item.setDay(v.getDay());
			item.setIdNo(null);
			try {
				item.setType(Integer.parseInt(v.getType().trim()));
			} catch (NumberFormatException e) {
				log.warn("会员码 type 非数字，跳过: code={}, type={}", v.getCode(), v.getType());
				continue;
			}
			dataList.add(item);
		}
		SyncFromImRequestDTO request = new SyncFromImRequestDTO();
		request.setData(dataList);
		return request;
	}

	/**
	 * 安全的同步到链桥调用（含 DB 更新 sync_status，需写事务）
	 */
	@Transactional(readOnly = false)
	public ChainBridgeResult<Object> syncToChainBridgeSafe(SyncFromImRequestDTO request) {
		if (request == null || request.getData() == null || request.getData().isEmpty()) {
			return ChainBridgeResult.failure("同步数据 data 不能为空");
		}
		if (request.getData().size() > MAX_SYNC_BATCH_SIZE) {
			return ChainBridgeResult.failure("单次最多同步" + MAX_SYNC_BATCH_SIZE + "条数据，请分批同步");
		}
		String validateErr = validateSyncData(request.getData());
		if (validateErr != null) {
			return ChainBridgeResult.failure(validateErr);
		}
		String alreadySyncedErr = validateNotAlreadySynced(request.getData());
		if (alreadySyncedErr != null) {
			return ChainBridgeResult.failure(alreadySyncedErr);
		}
		try {
			Object data = syncToChainBridgeEnhanced(request);
			return ChainBridgeResult.success(data);
		} catch (ChainBridgeException e) {
			log.error("链桥同步业务异常, error:{}", e.getMessage(), e);
			return ChainBridgeResult.failure(e.getMessage());
		} catch (Exception e) {
			log.error("链桥同步系统异常", e);
			return ChainBridgeResult.failure("系统异常，请联系客服");
		}
	}

	/**
	 * 批量插入会员码（用于批量生成），单次最多 10000 条
	 */
	@Transactional(readOnly = false)
	public int insertBatchForGenerate(List<VipCode> list) {
		if (list == null || list.isEmpty()) return 0;
		if (list.size() > MAX_GENERATE_BATCH_SIZE) {
			throw new IllegalArgumentException("单次最多生成" + MAX_GENERATE_BATCH_SIZE + "条会员码，请分批生成");
		}
		for (VipCode v : list) {
			// 生成 ID / createDate / updateDate 等
			v.preInsert();
		}
		mapper.insertBatch(list);
		return list.size();
	}

	/**
	 * 增强的同步到链桥方法：请求链桥后根据 data.failCount/items 判断真实成功/失败，并更新 DB sync_status
	 */
	private Object syncToChainBridgeEnhanced(SyncFromImRequestDTO request) throws Exception {
		Map<String, Object> body = buildSyncFromImBody(request);
		String json = new ObjectMapper().writeValueAsString(body);
		String url = wxRequestConstant.getSyncFromTypeUrl();
		log.info("会员码同步链桥请求url:{}", url);
		log.info("会员码同步链桥请求体:{}", json);

		String response = HttpUtil.postJson(url, json);
		log.info("会员码同步链桥响应:{}", response);

		HttpResponseDTO httpResponse = new ObjectMapper().readValue(response, HttpResponseDTO.class);
		if (httpResponse.getCode() == null || httpResponse.getCode() != 0) {
			throw new ChainBridgeException("链桥同步失败: " + (httpResponse.getMsg() != null ? httpResponse.getMsg() : "未知错误"));
		}

		// 链桥 HTTP 200 且 code=0 时，根据 data.items 按条判断：成功的更新 sync_status=1，失败的更新 sync_status=0（原方式保留）
		Object data = httpResponse.getData();
		if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> dataMap = (Map<String, Object>) data;
			List<String> successCodes = new ArrayList<>();
			List<String> failCodes = new ArrayList<>();
			List<String> failCodeReasons = new ArrayList<>(); // 每个元素为 "reason (code: xxx)"
			collectSuccessAndFailFromItems(dataMap, successCodes, failCodes, failCodeReasons);

			// 成功的更新为 1，失败的更新为 0（原来的更新状态方式保留）
			if (!successCodes.isEmpty()) {
				mapper.updateSyncStatusByCodes(successCodes, "1");
			}
			if (!failCodes.isEmpty()) {
				mapper.updateSyncStatusByCodes(failCodes, "0");
			}
			if (!failCodeReasons.isEmpty()) {
				String detail = String.join("; ", failCodeReasons);
				throw new ChainBridgeException("同步失败：" + detail);
			}
		}

		return data;
	}

	private static int toInt(Object v, int def) {
		if (v == null) return def;
		if (v instanceof Number) return ((Number) v).intValue();
		return def;
	}

	/** 从链桥 data.items 中分离成功 code、失败 code，失败项拼成 "reason (code: xxx)" 便于定位 */
	@SuppressWarnings("unchecked")
	private void collectSuccessAndFailFromItems(Map<String, Object> dataMap, List<String> successCodes, List<String> failCodes, List<String> failCodeReasons) {
		Object itemsObj = dataMap.get("items");
		if (!(itemsObj instanceof List)) return;
		List<?> items = (List<?>) itemsObj;
		for (Object o : items) {
			if (!(o instanceof Map)) continue;
			Map<String, Object> item = (Map<String, Object>) o;
			Object codeObj = item.get("code");
			String code = codeObj != null ? codeObj.toString() : "";
			Boolean status = (Boolean) item.get("status");
			if (Boolean.TRUE.equals(status)) {
				if (!code.isEmpty()) successCodes.add(code);
			} else {
				if (!code.isEmpty()) failCodes.add(code);
				Object reasonObj = item.get("reason");
				String reason = reasonObj != null ? reasonObj.toString() : "未知原因";
				failCodeReasons.add(reason + " (code: " + code + ")");
			}
		}
	}
}