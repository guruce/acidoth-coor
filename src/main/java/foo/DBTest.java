package foo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

public class DBTest{

    int foo = -1;
	private String appName = "mytransaction: ";
    // value stored in DB

    public void init(String completion) {
        try{
            Context ctx = new InitialContext();

            // JDBC stuff
            DataSource ds =
                (DataSource)ctx.lookup("java:comp/env/jdbc/myDB");
            ensureTableExists(ds);

            UserTransaction ut = (UserTransaction)ctx.lookup("java:comp/UserTransaction");

            Connection conn = ds.getConnection();

            System.out.println(appName + "<<< beginning the transaction >>>");
            ut.begin();

             // JDBC statements
             Statement stmt = conn.createStatement();
             ResultSet rst =
                 stmt.executeQuery("select id, foo from testdata");
             if(rst.next()) {
                 foo=rst.getInt(2);
             }
             System.out.println(appName + "foo = "+ foo +" (before completion)");

             PreparedStatement pstmt = conn.prepareStatement("update testdata set foo=? where id=1");
             pstmt.setInt(1,++foo);
             pstmt.executeUpdate();

              if (completion != null && completion.equals("commit")) {
                  System.out.println(appName + "<<< committing the transaction >>>");
                  ut.commit();
              } else {
                  System.out.println(appName + "<<< rolling back the transaction >>>");
                  ut.rollback();
              }

             // we set foo to the value stored in the DB
             rst =
                 stmt.executeQuery("select id, foo from testdata");
             if(rst.next()) {
                 foo=rst.getInt(2);
             }
             System.out.println("foo = "+ foo +" (after completion)");

             conn.close();
             System.out.println(appName + "<<< done >>>");
        }catch(Exception e) {
            System.out.print(appName + "DBTest >> ");
            e.printStackTrace();
        }
    }

    public String getFoo() { return ""+foo; }
    
    private void ensureTableExists(DataSource ds) throws Exception {
        Connection c = ds.getConnection();
        
        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate("create table testdata (id integer not null primary key, foo integer)");
            stmt.executeUpdate("insert into testdata values(1, 1)");
            stmt.close();
            c.commit();
            System.out.println(appName + "<<< created table >>>");
        }
        catch (SQLException ex) {
            // ignore
        }
        
        c.close();
    }
}
