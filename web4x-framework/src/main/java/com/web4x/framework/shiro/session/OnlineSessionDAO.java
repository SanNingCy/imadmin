package com.web4x.framework.shiro.session;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import com.web4x.common.core.session.OnlineSession;
import com.web4x.common.enums.OnlineStatus;
import com.web4x.framework.manager.AsyncManager;
import com.web4x.framework.manager.factory.AsyncFactory;
import com.web4x.framework.shiro.service.SysShiroService;

/**
 * 针对自定义的ShiroSession的db操作
 * 
 * @author web4x
 */
public class OnlineSessionDAO extends EnterpriseCacheSessionDAO
{
    /**
     * 同步session到数据库的周期 单位为毫秒（默认1分钟）
     */
    @Value("${shiro.session.dbSyncPeriod:1}")
    private int dbSyncPeriod;

    /**
     * 上次同步数据库的时间戳
     */
    private static final String LAST_SYNC_DB_TIMESTAMP = OnlineSessionDAO.class.getName() + "LAST_SYNC_DB_TIMESTAMP";

    @Autowired
    private SysShiroService sysShiroService;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    public OnlineSessionDAO()
    {
        super();
    }

    public OnlineSessionDAO(long expireTime)
    {
        super();
    }

    /**
     * 根据会话ID获取会话
     *
     * @param sessionId 会话ID
     * @return ShiroSession
     */
    @Override
    protected Serializable doCreate(Session session)
    {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId)
    {
        Object session = redisTemplate.opsForValue().get(getSessionKey(sessionId));
        if (session instanceof Session)
        {
            return (Session) session;
        }
        Session dbSession = sysShiroService.getSession(sessionId);
        if (dbSession != null)
        {
            saveSession(dbSession);
        }
        return dbSession;
    }

    @Override
    public void update(Session session) throws UnknownSessionException
    {
        super.update(session);
        saveSession(session);
    }

    /**
     * 更新会话；如更新会话最后访问时间/停止会话/设置超时时间/设置移除属性等会调用
     */
    public void syncToDb(OnlineSession onlineSession)
    {
        Date lastSyncTimestamp = (Date) onlineSession.getAttribute(LAST_SYNC_DB_TIMESTAMP);
        if (lastSyncTimestamp != null)
        {
            boolean needSync = true;
            long deltaTime = onlineSession.getLastAccessTime().getTime() - lastSyncTimestamp.getTime();
            if (deltaTime < dbSyncPeriod * 60 * 1000)
            {
                // 时间差不足 无需同步
                needSync = false;
            }
            // isGuest = true 访客
            boolean isGuest = onlineSession.getUserId() == null || onlineSession.getUserId() == 0L;

            // session 数据变更了 同步
            if (!isGuest && onlineSession.isAttributeChanged())
            {
                needSync = true;
            }

            if (!needSync)
            {
                return;
            }
        }
        // 更新上次同步数据库时间
        onlineSession.setAttribute(LAST_SYNC_DB_TIMESTAMP, onlineSession.getLastAccessTime());
        // 更新完后 重置标识
        if (onlineSession.isAttributeChanged())
        {
            onlineSession.resetAttributeChanged();
        }
        AsyncManager.me().execute(AsyncFactory.syncSessionToDb(onlineSession));
    }

    /**
     * 当会话过期/停止（如用户退出时）属性等会调用
     */
    @Override
    protected void doDelete(Session session)
    {
        OnlineSession onlineSession = (OnlineSession) session;
        if (null == onlineSession)
        {
            return;
        }
        onlineSession.setStatus(OnlineStatus.off_line);
        redisTemplate.delete(getSessionKey(session.getId()));
        sysShiroService.deleteSession(onlineSession);
    }

    private void saveSession(Session session)
    {
        if (session == null || session.getId() == null)
        {
            return;
        }
        long timeout = session.getTimeout();
        String key = getSessionKey(session.getId());
        if (timeout > 0)
        {
            redisTemplate.opsForValue().set(key, session, timeout, TimeUnit.MILLISECONDS);
        }
        else
        {
            redisTemplate.opsForValue().set(key, session);
        }
    }

    private String getSessionKey(Serializable sessionId)
    {
        return "web4x:session:" + sessionId;
    }
}
