/*
 * 该类实现了数据库的连接，查询，和更新等操作
 * 
 * 该类仅仅在服务器端使用
 * */



package common;


import java.sql.*;
import java.io.*;
import java.util.*;

public class Database {
	
	
	  public Connection conn = null;
	  public Statement stmt = null;
	  public ResultSet rs = null;
	  private  String databaseInitFilePath ="";
	  private  String propFileName = "/common/databaseInitFilePath.properties";  	//指定资源文件保存的位置
	  private  Properties prop = new Properties();
	  private  String dbClassName ="";
	  private  String dbUrl ="";
	  private  String dbUser = "";
	  private  String dbPwd = "";
 
  
  public Database(){
	    try {
	    	InputStream in=getClass().getResourceAsStream(propFileName);
	    	prop.load(in);
	    	databaseInitFilePath = prop.getProperty("databaseInitFilePath");
	        in=getClass().getResourceAsStream(databaseInitFilePath);
	        prop.load(in);									          //通过输入流对象加载Properties文件
	        dbClassName = prop.getProperty("DB_CLASS_NAME");          //获取数据库驱动
	        dbUrl = prop.getProperty("DB_URL",dbUrl);
	        dbUser=prop.getProperty("DB_USER",dbUser);
	        dbPwd=prop.getProperty("DB_PWD",dbPwd);
	        System.out.println("尝试申请一个数据库连接");
	    	}catch (Exception e) {System.err.println(e.getMessage());}//输出异常信息
	    	
	    try {
	      Class.forName(dbClassName).newInstance();
	      System.out.println("加载数据库驱动"+dbClassName+"成功！");
	    }
	    catch (Exception e) {
	    	System.out.println("加载数据库驱动"+dbClassName+"失败！");
	    	System.err.println(e.getMessage());
	    }
	    
	    
	    try {
	       conn = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
	       System.out.println("成功申请了一个数据库连接");
	      }
	      catch (Exception e) {
	    	  System.out.println("数据库连接失败！");
	    	  System.err.println(e.getMessage());
	      }
	    
	    if (conn == null) {
	      System.err.println(
	          "警告: DbConnectionManager.getConnection() 获得数据库链接失败.\r\n\r\n链接类型:" +
	          dbClassName + "\r\n链接位置:" + dbUrl + "\r\n用户/密码" + dbUser + "/" +
	          dbPwd);
	    }
	  } 
  
  /*
   *功能：执行查询语句
   */
  public ResultSet  searchDB(String sql) {
    try {
      stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
      rs = stmt.executeQuery(sql);
    }
    catch (SQLException ex) {
      System.err.println(ex.getMessage());
    }
	return rs;
  }

  /*
   *功能:执行更新操作
   */
  public int updateDB(String sql) {
    int result = 0;
    try {
    	 stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
         result = stmt.executeUpdate(sql);;
      }catch (SQLException e) {
    	System.out.println("错误："+result);
    	System.err.println(e.getMessage());
      result = 0;
    }
    try {
      stmt.close();
    }catch (SQLException e) {
    	System.err.println(e.getMessage());
    }
    return result;
  }



  /*
   *功能:关闭数据库的连接
   */
  public void close() {
		try {							//捕捉异常
			if (rs != null) {				//当ResultSet对象的实例rs不为空时
				rs.close();					//关闭ResultSet对象
			}
			if (stmt != null) {				//当Statement对象的实例stmt不为空时
				stmt.close();				//关闭Statement对象
			}
			if (conn != null) {				//当Connection对象的实例conn不为空时
				conn.close();
				//关闭Connection对象
			}
			System.out.println("成功关闭数据库连接！");
		    } catch (Exception e) {
			System.out.println("关闭数据库连接失败！");
			e.printStackTrace(System.err);	}//输出异常信息
	    } 

public static void main(String args[]) throws SQLException{
	Database db = new Database();
	//String sql = "select user_name,pwd from SCOTT.users where SCOTT.USERS.USER_ID = '1221101'";
	
//	String sql = "insert into SCOTT.users(user_id,user_name,pwd) values ('122110123','张凤杰','12')";
//	int i = db.updateDB(sql);
//System.out.println(i);
	
    String sql = "select * from nls_database_parameters";
	db.searchDB(sql);
	while(db.rs.next()){
		System.out.println(db.rs.getString(1));
	}
}
}
