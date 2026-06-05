package com.seekweb4.chat.core.persistence.interceptor;

import com.seekweb4.chat.common.utils.Reflections;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.persistence.dialect.Dialect;
import com.seekweb4.chat.core.persistence.dialect.db.*;
import com.seekweb4.chat.core.persistence.dialect.db.MySQLDialect;
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
@Intercepts({@Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
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

        final MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        
//        //拦截需要分页的SQL
////        if (mappedStatement.getId().matches(_SQL_PATTERN)) {
//        if (StringUtils.indexOfIgnoreCase(mappedStatement.getId(), _SQL_PATTERN) != -1) {
            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Object parameterObject = boundSql.getParameterObject();

            //获取分页参数对象
            Page<Object> page = null;
            if (parameterObject != null) {
                page = convertParameter(parameterObject, page);
            }

            //如果设置了分页对象，则进行分页
            if (page != null && page.getPageSize() != -1) {

            	if (StringUtils.isBlank(boundSql.getSql())){
                    return null;
                }
                String originalSql = boundSql.getSql().trim();

            	Dialect dialect = getDialect();

                //得到总记录数
                page.setCount(SQLHelper.getCount(originalSql, null, mappedStatement, parameterObject, boundSql, log, dialect));
                //分页查询 本地化对象 修改数据库注意修改实现
                String pageSql = SQLHelper.generatePageSql(originalSql, page, dialect);

//                if (log.isDebugEnabled()) {
//                    log.debug("PAGE SQL:" + StringUtils.replace(pageSql, "\n", ""));
//                }
                invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
                BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
                //解决MyBatis 分页foreach 参数失效 start
                if (Reflections.getFieldValue(boundSql, "metaParameters") != null) {
                    MetaObject mo = (MetaObject) Reflections.getFieldValue(boundSql, "metaParameters");
                    Reflections.setFieldValue(newBoundSql, "metaParameters", mo);
                }
                //解决MyBatis 分页foreach 参数失效 end
                MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));

                invocation.getArgs()[0] = newMs;
            }
//        }
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
