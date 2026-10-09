package com.shego.util;
import java.sql.*;
public final class DBUtil { private DBUtil(){} private static String p(String key,String def){String v=System.getProperty(key); return v==null||v.trim().isEmpty()?System.getenv().getOrDefault(key.toUpperCase().replace('.','_'),def):v;}
 public static Connection getConnection() throws SQLException { String url=p("shego.db.url","jdbc:sqlserver://localhost:1433;databaseName=SheGo;encrypt=false"); String user=p("shego.db.user","sa"); String password=p("shego.db.password","change-me"); return DriverManager.getConnection(url,user,password); }
 public static void close(AutoCloseable... resources){for(AutoCloseable r:resources) if(r!=null) try{r.close();}catch(Exception ignored){}}
}
