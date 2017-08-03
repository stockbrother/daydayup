package daydayup.openstock.database;

import daydayup.jdbc.ConnectionProvider;
import daydayup.openstock.RtException;
import org.h2.Driver;
import org.h2.api.ErrorCode;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class H2ConnectionPool implements ConnectionProvider {
    private static final Logger LOG = LoggerFactory.getLogger(H2ConnectionPool.class);

    private static class PooledJdbcConnection extends JdbcConnection {

        private boolean isClosed;
        H2ConnectionPool pool;

        public PooledJdbcConnection(H2ConnectionPool pool, JdbcConnection conn) {
            super(conn);
            this.pool = pool;
        }

        @Override
        public synchronized void close() throws SQLException {
            pool.close(this);
        }

        @Override
        public synchronized boolean isClosed() throws SQLException {
            return isClosed || super.isClosed();
        }

        @Override
        protected synchronized void checkClosed(boolean write) {
            if (isClosed) {
                throw DbException.get(ErrorCode.OBJECT_CLOSED);
            }
            super.checkClosed(write);
        }
    }

    private void close(PooledJdbcConnection con) {
        synchronized (this) {
            if (con.isClosed) {
                //ignore for closed twice
                return;
            }
            //do close it.
            try {
                con.rollback();
                //setAutoCommit(true);
            } catch (SQLException e) {
                // ignore
            }

            //recycle this connection.
            this.cachedConnectionList.add(con);
        }

    }

    private int maxConnections = 100;
    private int connections;
    private List<PooledJdbcConnection> cachedConnectionList = new ArrayList<>();
    private String url;
    private String user;
    private char[] pass;

    public H2ConnectionPool(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass == null ? null : pass.toCharArray();
//        try {
//            Class.forName("org.h2.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new RtException(e);
//        }
    }

    @Override
    public Connection openConnection() throws SQLException {
        synchronized (this) {
            if (this.cachedConnectionList.isEmpty()) {
                if (this.connections > this.maxConnections) {
                    throw new RtException("max connections exceed:" + this.maxConnections);
                }
                JdbcConnection con = getJdbcConnection();
                con.setAutoCommit(false);
                //LOG.trace("connection opened,it may be closed later without notified.");
                PooledJdbcConnection rt = new PooledJdbcConnection(this, con);
                this.connections++;
                return rt;
            } else {
                PooledJdbcConnection rt = this.cachedConnectionList.remove(0);
                rt.isClosed = false;//reopen it.
                return rt;
            }
        }
    }

    private JdbcConnection getJdbcConnection()
            throws SQLException {

        Properties info = new Properties();

        info.setProperty("user", user);
        info.put("password", pass);
        Connection conn = Driver.load().connect(url, info);
        if (conn == null) {
            throw new SQLException("No suitable driver found for " + url,
                    "08001", 8001);
        } else if (!(conn instanceof JdbcConnection)) {
            throw new SQLException(
                    "Connecting with old version is not supported: " + url,
                    "08001", 8001);
        }
        return (JdbcConnection) conn;
    }

    public static ConnectionProvider newInstance(String dbUrl, String user, String pass) {

        LOG.info("connection pool created");
        return new H2ConnectionPool(dbUrl, user, pass);
    }

    @Override
    public void dispose() {

        LOG.info("connection pool closed");//
    }

}
