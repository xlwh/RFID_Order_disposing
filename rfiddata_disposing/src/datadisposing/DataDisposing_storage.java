/*
 * 2012-11-7
 * 
 * 功能：设备入库
 *      判断设备入库\出库并修改数据库
 *  2012-11-10     
 *      将入库、出库信息显示在出入库信息显示界面
 *      同时可将出入库信息存放到txtInformation/storageInformation.txt里
 *      
 * */



package datadisposing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import showFrame.StorageShow;
import showFrame.StorageShow_V2;
import txtShow.TxtShow;

import common.Database;

public class DataDisposing_storage {
	//public static String fileadderess ="";
	private static long interval =60;         //时间间隔设定为60秒，两次读入时间差大于60秒看做为两次不同的进/出
	private static String txtAddress = "txtInformation/storageInformation.txt";//存放出入库信息
	
	Vector<String> v_eqpId =null;
	Vector<String> v_readingTime = null;
	Vector<Integer> v_flag = null;

	String sql = null;
	
	StorageShow_V2 storageshow ;
	Vector<String> v = new Vector<String>();
	/*
	 * 函数入库参数：读取文件的路径fileaddress(String)
	 * 
	 * 函数功能：将txt文件里的内容按行读出，将一行信息并按','分隔后存入到数组里，即可将一条读写器信息获得
	 * */
	public void readeTxt(String fileadderss){
		
		storageshow = new StorageShow_V2();                                               //实例化仓库出入信息界面
		storageshow.setBounds(100,100,660,700);
		storageshow.getJlb_state().setText("读取状态");
		storageshow.setVisible(true);
		
		TxtShow txtShow = new TxtShow();                                                //将入库信息存入txt方便管理员查看
		
		String s = new String();                     									//存放从txt里读取的行信息
		v_eqpId = new Vector<String>();													//存放标签
		v_readingTime = new Vector<String>();											//读入时间
		v_flag = new Vector<Integer>();													//设备在库标志位

		//String txt = "flag,读入的标签号,读取时间"+"\r\n";	
		
	while(true){
		try { 
				BufferedReader input = new BufferedReader(new FileReader(fileadderss)); //读取流
				input.readLine();                     									//从第二行开始读取并存放到数组里
				while((s = input.readLine())!=null){  									//判断是否读到了最后一行
					String info[] = s.split(",");    									//将行信息以','为分隔符存入数组
					// 对读取到的标签号info[1]的解析  -------未完成
					String eqpFlag = deleteBlank(info[0]);                             //设备标志位
					String eqpId = deleteBlank(info[1]);                               //设备序列号
					String eqpType = null;
					String eqpTime =info[2];
					if(eqpId.substring(0,1).equals("A")){eqpType="主机";
					System.out.println("**************************************主机");}
					else if(eqpId.substring(0,1).equals("B")){eqpType="显示器";
					System.out.println("**************************************显示器");}
					else if(eqpId.substring(0,1).equals("C")){eqpType="其它";
					System.out.println("**************************************其它");}
				
					
					/*
					 * 扩展功能
					 * 
					 * 判断设备是否为库间移动？-----查看move表（表里含有该设备序列号？）
					 *              若是则   判断设备是否为合法移动-----判断目的地址是否为当前地址
					 *                     非法则   修改alarm_sign标志位为1，同时报警
					 * 
					 * */
					
					
					//设备非库间移动
					
					
					
					/*
					 * 判断该条信息是否已经处理过
					 * 如果文本里的flag为"0"表明还未处理
					 * 如为处理过则进行处理，否则判断下一条标签信息
					 * */
					
					if (eqpFlag.equals("0"))	{
						
						/*
						 * 判断是否为该读写器读入的第一条设备信息
						 * 如果是则直接将该条信息存入数据库
						 * 否则判断是否为已经读入过的标签信息
						 * */
						if(v_eqpId.size() == 0){

							
							
							
							v_eqpId.add(eqpId);
							v_readingTime.add(eqpTime);
							v_flag.add(1);
							
							
							//----添加数据库插入语句
							sql = "insert into equipment(eqp_Id,eqp_type,eqp_boughttime) values('"+eqpId + "','" + eqpType + "',to_date('" + eqpTime + "','yyyy-mm-dd hh24:mi:ss'))";
							
							System.out.println("设备"+eqpId+"入库");
							v = new Vector<String>();                                //出入库信息显示界面
							v.add(eqpId);v.add("入库");v.add(eqpTime);
							storageshow.updateTable(v);
							
							String str = eqpId + " " + "入库" + " "+eqpTime +"\r\n";
							txtShow.writeStr(txtAddress,str);
							
							String sql1 = "insert into route(eqp_Id,movetime,action) values('"+eqpId +"',to_date('"+eqpTime+"','yyyy-MM-DD hh24:mi:ss'),'进入-仓库')";
							
							
							
							Database db = new Database();
							db.updateDB(sql);
							db.updateDB(sql1);
							db.close();
							
							
							
							
							
							
						}
						
						/*
						 * 判断是否为已经读入过的标签信息
						 * 如果是继续判断是否和已经读入过的标签信息为不同读入过程（不同的进/出过程）
						 *       如果为不同过程则修改数据库
						 *       否则将该条信息标记为已读/删除
						 * 如果为新读入标签信息，将标签信息插入数据库      
						 * */
						else if(checkEqpId(eqpId)){  //设备号已在数组里存在，即为已读入过的标签信息
							System.out.println("设备序列号已经读入过");
							
							if(checkReadingTime(v_readingTime.get(v_eqpId.indexOf(eqpId)),eqpTime)){//时间差大于60秒
//								System.out.println("时间差大于60秒，表示第二次读到，更改设备在库标志位");
								
								//修改v_readingTime
								v_readingTime.set(v_eqpId.indexOf(eqpId), eqpTime);
								int i = v_flag.get(v_eqpId.indexOf(eqpId));       //在库标志位
								i = -i;											  //标志位取反
								v_flag.set(v_eqpId.indexOf(eqpId), i);
								
								//更新数据库
								if(i>0){
									sql = "update storage set eqp_in = 1 where eqp_Id = '" + eqpId +"'";
									
									System.out.println("设备"+eqpId+"入库");
									v = new Vector<String>();                                //存放每一条读入的信息
									v.add(eqpId);v.add("入库");v.add(eqpTime);
									storageshow.updateTable(v);
									
									
									String sql1 = "insert into route(eqp_Id,movetime,action) values('"+eqpId +"',to_date('"+eqpTime+"','yyyy-MM-DD hh24:mi:ss'),'进入-仓库')";
									Database db1=new Database();
									db1.updateDB(sql1);
									db1.close();
									
									
									
									String str = eqpId + " " + "入库" +" "+ eqpTime +"\r\n";
									txtShow.writeStr(txtAddress,str);
									
								}
								if(i<0){
									sql = "update storage set eqp_in = 0 where eqp_Id = '" + eqpId +"'";
									
									System.out.println("设备"+eqpId+"出库");
									v = new Vector<String>();                                //存放每一条读入的信息
									v.add(eqpId);v.add("出库");v.add(eqpTime);
									storageshow.updateTable(v);
									
									String str = eqpId + " " + "出库" + " "+eqpTime +"\r\n";
									txtShow.writeStr(txtAddress,str);
									
									//插入route表
									String sql1 = "insert into route(eqp_Id,movetime,action) values('"+eqpId +"',to_date('"+eqpTime+"','yyyy-MM-DD hh24:mi:ss'),'出库')";
									System.out.println("2013-06-21日输出："+sql1);
									Database db = new Database();
									db.updateDB(sql1);
									db.close();
									
								}
								Database db = new Database();
								db.updateDB(sql);
								db.close();

								
							}
							else{
//								System.out.println("时间差小于60秒，表示为同一次进/出);

							}
						}
						else{
//							System.out.println("读入序列号为新读入，将序列号添加进数组，并进行数据库操作");
							v_eqpId.add(eqpId);
							v_readingTime.add(eqpTime);
							v_flag.add(1);
							
							//----添加数据库插入语句
							sql = "insert into equipment(eqp_Id,eqp_type,eqp_boughttime) values('"+eqpId + "','" + eqpType + "',to_date('" + eqpTime + "','yyyy-mm-dd hh24:mi:ss'))";
//							System.out.println("sql语句为：" + sql);
							
							System.out.println("设备"+eqpId+"入库");
							v = new Vector<String>();                                //存放每一条读入的信息
							v.add(eqpId);v.add("入库");v.add(eqpTime);
							storageshow.updateTable(v);
							
							
							String sql1 = "insert into route(eqp_Id,movetime,action) values('"+eqpId +"',to_date('"+eqpTime+"','yyyy-MM-DD hh24:mi:ss'),'进入-仓库')";
							Database db1=new Database();
							db1.updateDB(sql1);
							db1.close();
							
							
							
							String str = eqpId + " " + "入库" +" "+ eqpTime +" "+"\r\n";
							txtShow.writeStr(txtAddress,str);
							
							Database db = new Database();
							db.updateDB(sql);
							db.close();
						}
						
						
						
//						System.out.println("入库的设备序列号为："+v_eqpId);
//						System.out.println("入库的设备读入时间："+v_readingTime);
						
						
						
					}
				} 
				
				/*
				 * 清空文本文件
				 * */
				BufferedWriter output = new BufferedWriter(new FileWriter(fileadderss));
		        output.write("");
				input.close(); 
				output.close();
				} catch (Exception e) {} 
	}
		}
		
	/*
	 * 函数入口参数：设备序列号eqpId，读取时间readingTime
	 * 
	 * 函数功能：判断入口参数eqpId在入库设备数组v_eqpId里存在
	 *          若存在返回true；
	 * */
	public boolean checkEqpId(String eqpId){
		boolean flag = false;
	
		for(int i = 0;i<v_eqpId.size();i++){
			String Id = v_eqpId.get(i);
			if(Id.equals(eqpId)){                 //数组里已经存在
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	/*
	 * 函数输入参数：数组里存放的一个设备序列号vReadingTime， 从文本中读取的设备序列号readingTime
	 * 
	 * 函数功能：计算同一个设备两次读入的时间差，
	 *  		若时间差大于60秒，则返回true
	 * */
	public boolean checkReadingTime(String vReadingTime,String readingTime){
		boolean flag = false;
		long between = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
		    java.util.Date d1 =  df.parse(vReadingTime);
		    java.util.Date d2 = df.parse(readingTime);
		    between=(d2.getTime()-d1.getTime())/1000;//除以1000是为了转换成秒
//		    System.out.println("时间差为：" + between);
		}catch (Exception e){}
		
		if (between > interval){flag = true;}

		return flag;
	}
	
	/*
	 * 函数入口参数：字符串initialStr
	 * 函数功能：取出输入字符串中的空格，包括前后的空格
	 * 函数返回值：不不包含空格的字符串
	 * */
	public String deleteBlank(String initialStr){
		return initialStr.replaceAll(" ", "");
	}
	
	
	
	public static void main(String[] args) {
		DataDisposing_storage dd = new DataDisposing_storage();
		dd.readeTxt("d:\\storageInformation.txt");

		
		
	}

}
