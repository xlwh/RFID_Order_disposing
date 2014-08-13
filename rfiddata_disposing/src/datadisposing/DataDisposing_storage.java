/*
 * 2012-11-7
 * 
 * ���ܣ��豸���
 *      �ж��豸���\���Ⲣ�޸����ݿ�
 *  2012-11-10     
 *      ����⡢������Ϣ��ʾ�ڳ������Ϣ��ʾ����
 *      ͬʱ�ɽ��������Ϣ��ŵ�txtInformation/storageInformation.txt��
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
	private static long interval =60;         //ʱ�����趨Ϊ60�룬���ζ���ʱ������60�뿴��Ϊ���β�ͬ�Ľ�/��
	private static String txtAddress = "txtInformation/storageInformation.txt";//��ų������Ϣ
	
	Vector<String> v_eqpId =null;
	Vector<String> v_readingTime = null;
	Vector<Integer> v_flag = null;

	String sql = null;
	
	StorageShow_V2 storageshow ;
	Vector<String> v = new Vector<String>();
	/*
	 * ��������������ȡ�ļ���·��fileaddress(String)
	 * 
	 * �������ܣ���txt�ļ�������ݰ��ж�������һ����Ϣ����','�ָ�����뵽��������ɽ�һ����д����Ϣ���
	 * */
	public void readeTxt(String fileadderss){
		
		storageshow = new StorageShow_V2();                                               //ʵ�����ֿ������Ϣ����
		storageshow.setBounds(100,100,660,700);
		storageshow.getJlb_state().setText("��ȡ״̬");
		storageshow.setVisible(true);
		
		TxtShow txtShow = new TxtShow();                                                //�������Ϣ����txt�������Ա�鿴
		
		String s = new String();                     									//��Ŵ�txt���ȡ������Ϣ
		v_eqpId = new Vector<String>();													//��ű�ǩ
		v_readingTime = new Vector<String>();											//����ʱ��
		v_flag = new Vector<Integer>();													//�豸�ڿ��־λ

		//String txt = "flag,����ı�ǩ��,��ȡʱ��"+"\r\n";	
		
	while(true){
		try { 
				BufferedReader input = new BufferedReader(new FileReader(fileadderss)); //��ȡ��
				input.readLine();                     									//�ӵڶ��п�ʼ��ȡ����ŵ�������
				while((s = input.readLine())!=null){  									//�ж��Ƿ���������һ��
					String info[] = s.split(",");    									//������Ϣ��','Ϊ�ָ�����������
					// �Զ�ȡ���ı�ǩ��info[1]�Ľ���  -------δ���
					String eqpFlag = deleteBlank(info[0]);                             //�豸��־λ
					String eqpId = deleteBlank(info[1]);                               //�豸���к�
					String eqpType = null;
					String eqpTime =info[2];
					if(eqpId.substring(0,1).equals("A")){eqpType="����";
					System.out.println("**************************************����");}
					else if(eqpId.substring(0,1).equals("B")){eqpType="��ʾ��";
					System.out.println("**************************************��ʾ��");}
					else if(eqpId.substring(0,1).equals("C")){eqpType="����";
					System.out.println("**************************************����");}
				
					
					/*
					 * ��չ����
					 * 
					 * �ж��豸�Ƿ�Ϊ����ƶ���-----�鿴move�����ﺬ�и��豸���кţ���
					 *              ������   �ж��豸�Ƿ�Ϊ�Ϸ��ƶ�-----�ж�Ŀ�ĵ�ַ�Ƿ�Ϊ��ǰ��ַ
					 *                     �Ƿ���   �޸�alarm_sign��־λΪ1��ͬʱ����
					 * 
					 * */
					
					
					//�豸�ǿ���ƶ�
					
					
					
					/*
					 * �жϸ�����Ϣ�Ƿ��Ѿ������
					 * ����ı����flagΪ"0"������δ����
					 * ��Ϊ���������д��������ж���һ����ǩ��Ϣ
					 * */
					
					if (eqpFlag.equals("0"))	{
						
						/*
						 * �ж��Ƿ�Ϊ�ö�д������ĵ�һ���豸��Ϣ
						 * �������ֱ�ӽ�������Ϣ�������ݿ�
						 * �����ж��Ƿ�Ϊ�Ѿ�������ı�ǩ��Ϣ
						 * */
						if(v_eqpId.size() == 0){

							
							
							
							v_eqpId.add(eqpId);
							v_readingTime.add(eqpTime);
							v_flag.add(1);
							
							
							//----������ݿ�������
							sql = "insert into equipment(eqp_Id,eqp_type,eqp_boughttime) values('"+eqpId + "','" + eqpType + "',to_date('" + eqpTime + "','yyyy-mm-dd hh24:mi:ss'))";
							
							System.out.println("�豸"+eqpId+"���");
							v = new Vector<String>();                                //�������Ϣ��ʾ����
							v.add(eqpId);v.add("���");v.add(eqpTime);
							storageshow.updateTable(v);
							
							String str = eqpId + " " + "���" + " "+eqpTime +"\r\n";
							txtShow.writeStr(txtAddress,str);
							
							String sql1 = "insert into route(eqp_Id,movetime,action) values('"+eqpId +"',to_date('"+eqpTime+"','yyyy-MM-DD hh24:mi:ss'),'����-�ֿ�')";
							
							
							
							Database db = new Database();
							db.updateDB(sql);
							db.updateDB(sql1);
							db.close();
							
							
							
							
							
							
						}
						
						/*
						 * �ж��Ƿ�Ϊ�Ѿ�������ı�ǩ��Ϣ
						 * ����Ǽ����ж��Ƿ���Ѿ�������ı�ǩ��ϢΪ��ͬ������̣���ͬ�Ľ�/�����̣�
						 *       ���Ϊ��ͬ�������޸����ݿ�
						 *       ���򽫸�����Ϣ���Ϊ�Ѷ�/ɾ��
						 * ���Ϊ�¶����ǩ��Ϣ������ǩ��Ϣ�������ݿ�      
						 * */
						else if(checkEqpId(eqpId)){  //�豸��������������ڣ���Ϊ�Ѷ�����ı�ǩ��Ϣ
							System.out.println("�豸���к��Ѿ������");
							
							if(checkReadingTime(v_readingTime.get(v_eqpId.indexOf(eqpId)),eqpTime)){//ʱ������60��
//								System.out.println("ʱ������60�룬��ʾ�ڶ��ζ����������豸�ڿ��־λ");
								
								//�޸�v_readingTime
								v_readingTime.set(v_eqpId.indexOf(eqpId), eqpTime);
								int i = v_flag.get(v_eqpId.indexOf(eqpId));       //�ڿ��־λ
								i = -i;											  //��־λȡ��
								v_flag.set(v_eqpId.indexOf(eqpId), i);
								
								//�������ݿ�
								if(i>0){
									sql = "update storage set eqp_in = 1 where eqp_Id = '" + eqpId +"'";
									
									System.out.println("�豸"+eqpId+"���");
									v = new Vector<String>();                                //���ÿһ���������Ϣ
									v.add(eqpId);v.add("���");v.add(eqpTime);
									storageshow.updateTable(v);
									
									
									String sql1 = "insert into route(eqp_Id,movetime,action) values('"+eqpId +"',to_date('"+eqpTime+"','yyyy-MM-DD hh24:mi:ss'),'����-�ֿ�')";
									Database db1=new Database();
									db1.updateDB(sql1);
									db1.close();
									
									
									
									String str = eqpId + " " + "���" +" "+ eqpTime +"\r\n";
									txtShow.writeStr(txtAddress,str);
									
								}
								if(i<0){
									sql = "update storage set eqp_in = 0 where eqp_Id = '" + eqpId +"'";
									
									System.out.println("�豸"+eqpId+"����");
									v = new Vector<String>();                                //���ÿһ���������Ϣ
									v.add(eqpId);v.add("����");v.add(eqpTime);
									storageshow.updateTable(v);
									
									String str = eqpId + " " + "����" + " "+eqpTime +"\r\n";
									txtShow.writeStr(txtAddress,str);
									
									//����route��
									String sql1 = "insert into route(eqp_Id,movetime,action) values('"+eqpId +"',to_date('"+eqpTime+"','yyyy-MM-DD hh24:mi:ss'),'����')";
									System.out.println("2013-06-21�������"+sql1);
									Database db = new Database();
									db.updateDB(sql1);
									db.close();
									
								}
								Database db = new Database();
								db.updateDB(sql);
								db.close();

								
							}
							else{
//								System.out.println("ʱ���С��60�룬��ʾΪͬһ�ν�/��);

							}
						}
						else{
//							System.out.println("�������к�Ϊ�¶��룬�����к���ӽ����飬���������ݿ����");
							v_eqpId.add(eqpId);
							v_readingTime.add(eqpTime);
							v_flag.add(1);
							
							//----������ݿ�������
							sql = "insert into equipment(eqp_Id,eqp_type,eqp_boughttime) values('"+eqpId + "','" + eqpType + "',to_date('" + eqpTime + "','yyyy-mm-dd hh24:mi:ss'))";
//							System.out.println("sql���Ϊ��" + sql);
							
							System.out.println("�豸"+eqpId+"���");
							v = new Vector<String>();                                //���ÿһ���������Ϣ
							v.add(eqpId);v.add("���");v.add(eqpTime);
							storageshow.updateTable(v);
							
							
							String sql1 = "insert into route(eqp_Id,movetime,action) values('"+eqpId +"',to_date('"+eqpTime+"','yyyy-MM-DD hh24:mi:ss'),'����-�ֿ�')";
							Database db1=new Database();
							db1.updateDB(sql1);
							db1.close();
							
							
							
							String str = eqpId + " " + "���" +" "+ eqpTime +" "+"\r\n";
							txtShow.writeStr(txtAddress,str);
							
							Database db = new Database();
							db.updateDB(sql);
							db.close();
						}
						
						
						
//						System.out.println("�����豸���к�Ϊ��"+v_eqpId);
//						System.out.println("�����豸����ʱ�䣺"+v_readingTime);
						
						
						
					}
				} 
				
				/*
				 * ����ı��ļ�
				 * */
				BufferedWriter output = new BufferedWriter(new FileWriter(fileadderss));
		        output.write("");
				input.close(); 
				output.close();
				} catch (Exception e) {} 
	}
		}
		
	/*
	 * ������ڲ������豸���к�eqpId����ȡʱ��readingTime
	 * 
	 * �������ܣ��ж���ڲ���eqpId������豸����v_eqpId�����
	 *          �����ڷ���true��
	 * */
	public boolean checkEqpId(String eqpId){
		boolean flag = false;
	
		for(int i = 0;i<v_eqpId.size();i++){
			String Id = v_eqpId.get(i);
			if(Id.equals(eqpId)){                 //�������Ѿ�����
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	/*
	 * ��������������������ŵ�һ���豸���к�vReadingTime�� ���ı��ж�ȡ���豸���к�readingTime
	 * 
	 * �������ܣ�����ͬһ���豸���ζ����ʱ��
	 *  		��ʱ������60�룬�򷵻�true
	 * */
	public boolean checkReadingTime(String vReadingTime,String readingTime){
		boolean flag = false;
		long between = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
		    java.util.Date d1 =  df.parse(vReadingTime);
		    java.util.Date d2 = df.parse(readingTime);
		    between=(d2.getTime()-d1.getTime())/1000;//����1000��Ϊ��ת������
//		    System.out.println("ʱ���Ϊ��" + between);
		}catch (Exception e){}
		
		if (between > interval){flag = true;}

		return flag;
	}
	
	/*
	 * ������ڲ������ַ���initialStr
	 * �������ܣ�ȡ�������ַ����еĿո񣬰���ǰ��Ŀո�
	 * ��������ֵ�����������ո���ַ���
	 * */
	public String deleteBlank(String initialStr){
		return initialStr.replaceAll(" ", "");
	}
	
	
	
	public static void main(String[] args) {
		DataDisposing_storage dd = new DataDisposing_storage();
		dd.readeTxt("d:\\storageInformation.txt");

		
		
	}

}
