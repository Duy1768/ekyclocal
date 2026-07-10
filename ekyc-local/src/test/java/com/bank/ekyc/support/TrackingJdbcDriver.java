package com.bank.ekyc.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public final class TrackingJdbcDriver implements Driver {

    public static final String URL_PREFIX = "jdbc:tracking:";

    private static final AtomicInteger CREATED_CONNECTIONS = new AtomicInteger();
    private static final AtomicInteger CLOSED_CONNECTIONS = new AtomicInteger();
    private static final AtomicInteger OPEN_CONNECTIONS = new AtomicInteger();

    static {
        try {
            DriverManager.registerDriver(new TrackingJdbcDriver());
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void reset() {
        CREATED_CONNECTIONS.set(0);
        CLOSED_CONNECTIONS.set(0);
        OPEN_CONNECTIONS.set(0);
    }

    public static int createdConnections() {
        return CREATED_CONNECTIONS.get();
    }

    public static int closedConnections() {
        return CLOSED_CONNECTIONS.get();
    }

    public static int openConnections() {
        return OPEN_CONNECTIONS.get();
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        int id = CREATED_CONNECTIONS.incrementAndGet();
        OPEN_CONNECTIONS.incrementAndGet();

        InvocationHandler handler = new TrackingConnectionHandler(id);
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                handler);
    }

    @Override
    public boolean acceptsURL(String url) {
        return url != null && url.startsWith(URL_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger("TrackingJdbcDriver");
    }

    private static final class TrackingConnectionHandler implements InvocationHandler {

        private final int id;
        private final AtomicBoolean closed = new AtomicBoolean(false);

        private TrackingConnectionHandler(int id) {
            this.id = id;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            if ("close".equals(methodName)) {
                if (closed.compareAndSet(false, true)) {
                    OPEN_CONNECTIONS.decrementAndGet();
                    CLOSED_CONNECTIONS.incrementAndGet();
                }
                return null;
            }

            if ("isClosed".equals(methodName)) {
                return closed.get();
            }

            if ("isValid".equals(methodName)) {
                return !closed.get();
            }

            if ("createStatement".equals(methodName)) {
                return createStatementProxy();
            }

            if ("prepareStatement".equals(methodName)) {
                return createPreparedStatementProxy();
            }

            if ("setAutoCommit".equals(methodName)
                    || "commit".equals(methodName)
                    || "rollback".equals(methodName)
                    || "clearWarnings".equals(methodName)
                    || "setReadOnly".equals(methodName)
                    || "setCatalog".equals(methodName)
                    || "setSchema".equals(methodName)
                    || "setClientInfo".equals(methodName)
                    || "setTransactionIsolation".equals(methodName)
                    || "setHoldability".equals(methodName)
                    || "setNetworkTimeout".equals(methodName)
                    || "abort".equals(methodName)
                    || "beginRequest".equals(methodName)
                    || "endRequest".equals(methodName)) {
                return null;
            }

            if ("getAutoCommit".equals(methodName)) {
                return true;
            }

            if ("isReadOnly".equals(methodName)) {
                return false;
            }

            if ("getTransactionIsolation".equals(methodName)) {
                return Connection.TRANSACTION_READ_COMMITTED;
            }

            if ("getWarnings".equals(methodName)) {
                return null;
            }

            if ("getSchema".equals(methodName)) {
                return null;
            }

            if ("getCatalog".equals(methodName)) {
                return null;
            }

            if ("getClientInfo".equals(methodName)) {
                return new Properties();
            }

            if ("getNetworkTimeout".equals(methodName)) {
                return 0;
            }

            if ("unwrap".equals(methodName)) {
                Class<?> targetType = (Class<?>) args[0];
                if (targetType.isInstance(proxy)) {
                    return proxy;
                }
                throw new SQLException("Unsupported unwrap type: " + targetType.getName());
            }

            if ("isWrapperFor".equals(methodName)) {
                Class<?> targetType = (Class<?>) args[0];
                return targetType.isInstance(proxy);
            }

            if ("toString".equals(methodName)) {
                return "TrackingConnection-" + id;
            }

            return defaultValue(method.getReturnType());
        }
    }

    private static Statement createStatementProxy() {
        InvocationHandler handler = new TrackingStatementHandler();
        return (Statement) Proxy.newProxyInstance(
                Statement.class.getClassLoader(),
                new Class<?>[]{Statement.class},
                handler);
    }

    private static PreparedStatement createPreparedStatementProxy() {
        InvocationHandler handler = new TrackingStatementHandler();
        return (PreparedStatement) Proxy.newProxyInstance(
                PreparedStatement.class.getClassLoader(),
                new Class<?>[]{PreparedStatement.class},
                handler);
    }

    private static ResultSet createResultSetProxy() {
        InvocationHandler handler = new TrackingResultSetHandler();
        return (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class<?>[]{ResultSet.class},
                handler);
    }

    private static final class TrackingStatementHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            if ("close".equals(methodName)
                    || "cancel".equals(methodName)
                    || "clearWarnings".equals(methodName)
                    || "setFetchDirection".equals(methodName)
                    || "setFetchSize".equals(methodName)
                    || "setMaxFieldSize".equals(methodName)
                    || "setMaxRows".equals(methodName)
                    || "setPoolable".equals(methodName)
                    || "closeOnCompletion".equals(methodName)) {
                return null;
            }

            if ("execute".equals(methodName)) {
                return false;
            }

            if ("executeUpdate".equals(methodName)) {
                return 1;
            }

            if ("executeLargeUpdate".equals(methodName)) {
                return 1L;
            }

            if ("executeQuery".equals(methodName)) {
                return createResultSetProxy();
            }

            if ("getResultSet".equals(methodName)) {
                return createResultSetProxy();
            }

            if ("getUpdateCount".equals(methodName)) {
                return 0;
            }

            if ("getConnection".equals(methodName)) {
                return null;
            }

            if ("isClosed".equals(methodName)) {
                return false;
            }

            if ("isPoolable".equals(methodName)) {
                return false;
            }

            if ("getMoreResults".equals(methodName)) {
                return false;
            }

            if ("unwrap".equals(methodName)) {
                return proxy;
            }

            if ("isWrapperFor".equals(methodName)) {
                return false;
            }

            return defaultValue(method.getReturnType());
        }
    }

    private static final class TrackingResultSetHandler implements InvocationHandler {

        private boolean firstRow = true;
        private boolean closed;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            if ("close".equals(methodName)) {
                closed = true;
                return null;
            }

            if ("next".equals(methodName)) {
                if (closed) {
                    return false;
                }
                boolean hasRow = firstRow;
                firstRow = false;
                return hasRow;
            }

            if ("wasNull".equals(methodName)) {
                return false;
            }

            if ("getString".equals(methodName)) {
                return "1";
            }

            if ("getInt".equals(methodName)) {
                return 1;
            }

            if ("getLong".equals(methodName)) {
                return 1L;
            }

            if ("getObject".equals(methodName)) {
                return 1;
            }

            if ("isClosed".equals(methodName)) {
                return closed;
            }

            if ("unwrap".equals(methodName)) {
                Class<?> targetType = (Class<?>) args[0];
                if (targetType.isInstance(proxy)) {
                    return proxy;
                }
                throw new SQLException("Unsupported unwrap type: " + targetType.getName());
            }

            if ("isWrapperFor".equals(methodName)) {
                Class<?> targetType = (Class<?>) args[0];
                return targetType.isInstance(proxy);
            }

            return defaultValue(method.getReturnType());
        }
    }

    private static Object defaultValue(Class<?> returnType) {
        if (returnType == void.class) {
            return null;
        }
        if (returnType == boolean.class) {
            return false;
        }
        if (returnType == byte.class) {
            return (byte) 0;
        }
        if (returnType == short.class) {
            return (short) 0;
        }
        if (returnType == int.class) {
            return 0;
        }
        if (returnType == long.class) {
            return 0L;
        }
        if (returnType == float.class) {
            return 0F;
        }
        if (returnType == double.class) {
            return 0D;
        }
        if (returnType == char.class) {
            return '\0';
        }
        return null;
    }
}
