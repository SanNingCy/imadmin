package com.seekweb4.chat.core.persistence.interceptor;

import com.seekweb4.chat.common.utils.Reflections;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.persistence.dialect.Dialect;
import com.seekweb4.chat.core.persistence.dialect.db.*;
import com.seekweb4.chat.core.persistence.dialect.db.MySQLDialect;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * 数据库分页插件，只拦截查询语句.
 * @version 2016-8-28
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class PaginationInterceptor extends BaseInterceptor {

    private static final long serialVersionUID = 1L;

    protected Dialect getDialect() {
        return new MySQLDialect();
        /*Dialect dialect = null;
        String dbType ;
        try{
            dbType = SpringContextHolder.getBean(DatabaseIdProvider.class).getDatabaseId(SpringContextHolder.getBean(DataSource.class));
        }catch (Exception e){
            dbType = "mysql";
        }

        if("mysql".equals(dbType)){
            dialect = new MySQLDialect();
        }else if ("db2".equals(dbType)){
            dialect = new DB2Dialect();
        }else if("derby".equals(dbType)){
            dialect = new DerbyDialect();
        }else if("h2".equals(dbType)){
            dialect = new H2Dialect();
        }else if("hsql".equals(dbType)){
            dialect = new HSQLDialect();
        }else if("oracle".equals(dbType)){
            dialect = new OracleDialect();
        }else if("postgre".equals(dbType)){
            dialect = new PostgreSQLDialect();
        }else if("mssql".equals(dbType) || "sqlserver".equals(dbType)){
            dialect = new SQLServerDialect();
        }else if("sybase".equals(dbType)){
            dialect = new SybaseDialect();
        }
        if (dialect == null) {
            throw new RuntimeException("mybatis dialect error.");
        }
        return dialect;*/
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];
        BoundSql boundSql = args.length == 6 ? (BoundSql) args[5] : mappedStatement.getBoundSql(parameter);
        Object parameterObject = boundSql.getParameterObject();

        Page<Object> page = null;
        if (parameterObject != null) {
            page = convertParameter(parameterObject, page);
        }

        if (page != null && page.getPageSize() != -1) {
            if (StringUtils.isBlank(boundSql.getSql())) {
                return null;
            }
            String originalSql = boundSql.getSql().trim();
            Dialect dialect = getDialect();
            page.setCount(SQLHelper.getCount(originalSql, null, mappedStatement, parameterObject, boundSql, log, dialect));
            String pageSql = SQLHelper.generatePageSql(originalSql, page, dialect);

            RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
            args[2] = rowBounds;
            BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql,
                    boundSql.getParameterMappings(), boundSql.getParameterObject());
            if (Reflections.getFieldValue(boundSql, "metaParameters") != null) {
                MetaObject mo = (MetaObject) Reflections.getFieldValue(boundSql, "metaParameters");
                Reflections.setFieldValue(newBoundSql, "metaParameters", mo);
            }
            MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
            args[0] = newMs;
            if (args.length == 6) {
                args[5] = newBoundSql;
                Executor executor = (Executor) invocation.getTarget();
                args[4] = executor.createCacheKey(newMs, parameter, rowBounds, newBoundSql);
            }
        }
        return invocation.proceed();
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null) {
            for (String keyProperty : ms.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        return builder.build();
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
