package com.seekweb4.chat.agora.controller.v2;

import com.seekweb4.chat.agora.bean.dto.R;
import com.seekweb4.chat.agora.bean.dto.TokenDto;
import com.seekweb4.chat.agora.bean.req.TokenV2Req;
import com.seekweb4.chat.agora.service.ITokenV2Service;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * Token管理控制器 V2版本
 * 
 * <p>该控制器提供各种类型Token的生成服务，支持Agora平台的身份认证和权限控制。</p>
 * <p>Token是访问Agora服务的必要凭证，用于确保通信安全和权限管理。</p>
 * 
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>生成标准RTC Token - 用于实时音视频通信</li>
 *   <li>生成Token006 - 最新版本的RTC Token</li>
 *   <li>生成IM Token - 用于即时消息服务</li>
 * </ul>
 * 
 * <p><strong>Token类型说明：</strong></p>
 * <ul>
 *   <li><strong>RTC Token：</strong>用于音视频通话、直播等实时通信场景</li>
 *   <li><strong>Token006：</strong>增强版Token，支持更多功能和更强的安全性</li>
 *   <li><strong>IM Token：</strong>用于聊天室、私聊等即时消息功能</li>
 * </ul>
 * 
 * <p><strong>API版本：</strong>v2</p>
 * <p><strong>路径前缀：</strong>/v2</p>
 * <p><strong>响应格式：</strong>JSON</p>
 * 
 * <p><strong>安全说明：</strong></p>
 * <ul>
 *   <li>Token具有时效性，过期后需重新生成</li>
 *   <li>AppCert是敏感信息，需妥善保管</li>
 *   <li>不同场景需使用对应类型的Token</li>
 * </ul>
 * 
 * @author UIKit Team
 * @version 2.0
 * @since 2.0
 */
@Validated
@RestController
@RequestMapping(value = "/v2", produces = MediaType.APPLICATION_JSON_VALUE)
public class TokenV2Controller {
    
    /**
     * Token服务接口，处理各种Token的生成逻辑
     */
    @Resource
    private ITokenV2Service tokenService;

    /**
     * 生成标准RTC Token
     * 
     * <p>生成用于实时音视频通信的标准Token，适用于基础的RTC场景。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/token/generate</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>适用场景：</strong></p>
     * <ul>
     *   <li>1对1音视频通话</li>
     *   <li>多人视频会议</li>
     *   <li>直播推拉流</li>
     *   <li>屏幕共享</li>
     * </ul>
     * 
     * <p><strong>请求参数说明：</strong></p>
     * <ul>
     *   <li><strong>appId：</strong>应用ID，在Agora控制台获取</li>
     *   <li><strong>appCert：</strong>应用证书，用于Token签名</li>
     *   <li><strong>channelName：</strong>频道名称，同一频道内的用户可以互相通信</li>
     *   <li><strong>userId：</strong>用户ID，频道内的唯一标识</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "appId": "your_app_id",
     *   "appCert": "your_app_certificate",
     *   "channelName": "test_channel",
     *   "userId": "user123"
     * }
     * </pre>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "token": "006ICAgIANx...",
     *     "expire": 1694160000
     *   },
     *   "success": true
     * }
     * </pre>
     * 
     * @param tokenReq Token生成请求对象，包含生成Token所需的参数
     * @return 统一响应对象，成功时包含生成的Token信息
     */
    @PostMapping("/token/generate")
    @ResponseBody
    public R<TokenDto> generate(@Validated @RequestBody TokenV2Req tokenReq) {
        return R.success(tokenService.generateToken(tokenReq.getAppId(), tokenReq.getAppCert(), tokenReq.getChannelName(), tokenReq.getUserId()));
    }

    /**
     * 生成Token006（增强版Token）
     * 
     * <p>生成最新版本的RTC Token，提供更强的安全性和更多功能支持。</p>
     * <p>相比标准Token，Token006支持更细粒度的权限控制和更长的有效期。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/token006/generate</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>Token006优势：</strong></p>
     * <ul>
     *   <li>支持动态权限控制</li>
     *   <li>更强的防伪造能力</li>
     *   <li>支持服务端踢人功能</li>
     *   <li>可设置用户属性权限</li>
     * </ul>
     * 
     * <p><strong>使用场景：</strong></p>
     * <ul>
     *   <li>企业级视频会议</li>
     *   <li>在线教育平台</li>
     *   <li>大型直播活动</li>
     *   <li>需要严格权限控制的应用</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "appId": "your_app_id",
     *   "appCert": "your_app_certificate",
     *   "channelName": "premium_channel",
     *   "userId": "premium_user123"
     * }
     * </pre>
     * 
     * @param tokenReq Token生成请求对象，包含生成Token006所需的参数
     * @return 统一响应对象，成功时包含生成的Token006信息
     * @throws Exception 当生成过程中出现异常时抛出，如参数无效、证书错误等
     */
    @PostMapping("/token006/generate")
    @ResponseBody
    public R<TokenDto> generateToken006(@Validated @RequestBody TokenV2Req tokenReq) throws Exception {
        return R.success(tokenService.generateToken006(tokenReq.getAppId(), tokenReq.getAppCert(), tokenReq.getChannelName(), tokenReq.getUserId()));
    }

    /**
     * 生成即时消息Token
     * 
     * <p>生成用于Agora Chat即时消息服务的Token，用于聊天室、私聊等IM场景。</p>
     * <p>IM Token与RTC Token相互独立，分别用于不同的服务模块。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/im/token</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>IM功能支持：</strong></p>
     * <ul>
     *   <li>单聊消息发送</li>
     *   <li>群组聊天管理</li>
     *   <li>聊天室功能</li>
     *   <li>消息历史查询</li>
     *   <li>用户状态管理</li>
     * </ul>
     * 
     * <p><strong>应用场景：</strong></p>
     * <ul>
     *   <li>社交应用的聊天功能</li>
     *   <li>直播间弹幕互动</li>
     *   <li>在线客服系统</li>
     *   <li>游戏内聊天系统</li>
     *   <li>协作办公应用</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "appId": "your_app_id",
     *   "appCert": "your_app_certificate", 
     *   "channelName": "chat_room_001",
     *   "userId": "chat_user123"
     * }
     * </pre>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "token": "007eJxTYJBS...",
     *     "expire": 1694160000
     *   },
     *   "success": true
     * }
     * </pre>
     * 
     * @param tokenReq Token生成请求对象，包含生成IM Token所需的参数
     * @return 统一响应对象，成功时包含生成的IM Token信息
     * @throws Exception 当生成过程中出现异常时抛出，如网络错误、权限不足等
     */
    @PostMapping("/im/token")
    @ResponseBody
    public R<TokenDto> generateIMToken(@Validated @RequestBody TokenV2Req tokenReq) throws Exception {
        return R.success(tokenService.generateToken006(tokenReq.getAppId(), tokenReq.getAppCert(), tokenReq.getChannelName(), tokenReq.getUserId()));
    }
}